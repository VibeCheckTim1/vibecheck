package hr.tvz.vibecheck.api.account.service;

import hr.tvz.vibecheck.api.account.dto.FollowStatsResponse;
import hr.tvz.vibecheck.api.account.entity.FollowRequest;
import hr.tvz.vibecheck.api.account.service.interfaces.FollowRequestActionHandler;
import hr.tvz.vibecheck.api.account.service.interfaces.FollowRequestCommandService;
import hr.tvz.vibecheck.api.account.service.interfaces.FollowRequestQueryService;
import hr.tvz.vibecheck.api.security.enums.FollowActionResult;
import hr.tvz.vibecheck.api.security.enums.FollowRequestStatus;
import hr.tvz.vibecheck.api.user.entity.User;
import hr.tvz.vibecheck.api.user.repository.UserRepository;
import hr.tvz.vibecheck.api.security.dto.FollowRequestActionRequest;
import hr.tvz.vibecheck.api.security.dto.FollowRequestRequest;
import hr.tvz.vibecheck.api.account.dto.FollowActionResponse;
import hr.tvz.vibecheck.api.account.dto.FollowRequestResponse;
import hr.tvz.vibecheck.api.account.entity.Follows;
import hr.tvz.vibecheck.exception.custom.*;
import hr.tvz.vibecheck.api.account.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.api.account.repository.follows.FollowsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowRequestService implements FollowRequestCommandService, FollowRequestQueryService {
    private static final String USER_NOT_FOUND_PREFIX = "User with ID ";
    private static final String NOT_FOUND_SUFFIX = " not found";

    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;
    private final FollowsRepository followsRepository;
    private final List<FollowRequestActionHandler> actionHandlers;

    @Override
    @Transactional
    public FollowActionResponse createFollowRequestOrFollow(Long senderId, FollowRequestRequest followRequest) {
        User sender = findUserById(senderId);
        User receiver = findUserById(followRequest.receiverId());

        validateFollowCanBeCreated(sender, receiver);

        if (!receiver.isPrivate()) {
            createFollow(sender, receiver);
            return new FollowActionResponse(FollowActionResult.FOLLOWING);
        }
        createOrReactivatePendingFollowRequest(sender, receiver);

        return new FollowActionResponse(FollowActionResult.PENDING);
    }

    private void createOrReactivatePendingFollowRequest(User sender, User receiver) {
        Optional<FollowRequest> existingRequestOpt =
                followRequestRepository.findBySender_IdUserAndReceiver_IdUser(sender.getIdUser(), receiver.getIdUser());

        if (existingRequestOpt.isPresent()) {
            reactivateDeclinedRequestOrThrow(existingRequestOpt.get(), receiver);
            return;
        }

        FollowRequest newFollowRequest = FollowRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FollowRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        followRequestRepository.save(newFollowRequest);
    }

    private void reactivateDeclinedRequestOrThrow(FollowRequest existingRequest, User receiver) {
        if (existingRequest.getStatus() != FollowRequestStatus.DECLINED) {
            throw new DuplicateFollowRequestException("Follow request for user " + receiver.getUsername() + " already exists");
        }

        existingRequest.setStatus(FollowRequestStatus.PENDING);
        followRequestRepository.save(existingRequest);
    }

    private void createFollow(User sender, User receiver) {
        Follows newFollow = Follows.builder()
                .user1(sender)
                .user2(receiver)
                .build();
        followsRepository.save(newFollow);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException(USER_NOT_FOUND_PREFIX + userId + NOT_FOUND_SUFFIX));
    }

    private void validateFollowCanBeCreated(User sender, User receiver) {
        if (sender.getIdUser().equals(receiver.getIdUser())) {
            throw new SelfFollowException("You cannot follow yourself");
        }

        if (followsRepository.findByUser1_IdUserAndUser2_IdUser(sender.getIdUser(), receiver.getIdUser()).isPresent()) {
            throw new DuplicateFollowException("Follow for " + receiver.getUsername() + " already exists");
        }
    }

    @Override
    public void cancelFollowRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElseThrow(()
                -> new UserNotFoundException(USER_NOT_FOUND_PREFIX + senderId + NOT_FOUND_SUFFIX));

        User receiver = userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException(USER_NOT_FOUND_PREFIX + receiverId + NOT_FOUND_SUFFIX));

        FollowRequest existingReq = followRequestRepository.findBySender_IdUserAndReceiver_IdUser
                (sender.getIdUser(), receiver.getIdUser()).orElseThrow(()
                -> new FollowRequestNotFoundException("Follow request for user " + sender.getUsername() + NOT_FOUND_SUFFIX));


        if (!existingReq.getStatus().equals(FollowRequestStatus.PENDING)) {
            throw new NotPendingStatusException("You cannot delete a follow request that is not \"Pending\"");
        }

        followRequestRepository.delete(existingReq);
    }

    @Transactional
    @Override
    public void unfollow(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElseThrow(()
                -> new UserNotFoundException(USER_NOT_FOUND_PREFIX + senderId + NOT_FOUND_SUFFIX));

        User receiver = userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException(USER_NOT_FOUND_PREFIX + receiverId + NOT_FOUND_SUFFIX));

        Follows followToDelete = followsRepository.findByUser1_IdUserAndUser2_IdUser
                (sender.getIdUser(), receiver.getIdUser()).orElseThrow(() ->
                new FollowNotFoundException("Follow for user " + sender.getUsername() + NOT_FOUND_SUFFIX));

        Optional<FollowRequest> existingFollowReq = followRequestRepository.findBySender_IdUserAndReceiver_IdUser
                (sender.getIdUser(), receiver.getIdUser());

        existingFollowReq.ifPresent(followRequestRepository::delete);

        followsRepository.delete(followToDelete);

    }


    @Override
    public FollowActionResponse getFollowStatus(Long senderId, Long receiverId) {
        if (isFollowing(senderId, receiverId)) {
            return new FollowActionResponse(FollowActionResult.FOLLOWING);
        }

        return followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId)
                .map(this::mapFollowRequestToActionResponse)
                .orElseGet(() -> new FollowActionResponse(FollowActionResult.FOLLOW));
    }

    private boolean isFollowing(Long senderId, Long receiverId) {
        return followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId).isPresent();
    }

    private FollowActionResponse mapFollowRequestToActionResponse(FollowRequest followRequest) {
        if (followRequest.getStatus() == FollowRequestStatus.DECLINED) {
            return new FollowActionResponse(FollowActionResult.FOLLOW);
        }

        return new FollowActionResponse(FollowActionResult.PENDING);
    }


    @Transactional
    @Override
    public void acceptOrDeclineFollowRequest(Long requestId, Long receiverId, FollowRequestActionRequest userResponse) {
        String actionName = userResponse.action().name().toLowerCase();

        User receiver = userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException(USER_NOT_FOUND_PREFIX + receiverId + NOT_FOUND_SUFFIX));

        FollowRequest existingReq = followRequestRepository.findById(requestId).orElseThrow(() ->
                new FollowRequestNotFoundException("Follow request for user " + receiver.getUsername() + NOT_FOUND_SUFFIX));

        if (!existingReq.getReceiver().getIdUser().equals(receiverId)) {
            throw new NotYourRequestException("You cannot " + actionName + " someone else's follow request");
        }

        if (existingReq.getStatus() != FollowRequestStatus.PENDING) {
            throw new NotPendingStatusException("You cannot " + actionName + " a follow request that is not \"Pending\"");
        }

        FollowRequestActionHandler handler = actionHandlers.stream()
                .filter(candidate -> candidate.action() == userResponse.action())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported follow request action"));

        handler.handle(existingReq, receiver);
    }

    @Override
    public List<FollowRequestResponse> getAllFollowRequests(Long receiverId) {
        userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException(USER_NOT_FOUND_PREFIX + receiverId + NOT_FOUND_SUFFIX));

        return followRequestRepository.findAllByReceiverId(receiverId);

    }

    @Override
    public FollowStatsResponse getFollowStats(Long userId) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        long followersCount = followsRepository.countByUser2_IdUser(userId);
        long followingCount = followsRepository.countByUser1_IdUser(userId);

        return new FollowStatsResponse(followersCount, followingCount);

    }


}
