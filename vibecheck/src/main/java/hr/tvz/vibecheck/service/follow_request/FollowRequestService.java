package hr.tvz.vibecheck.service.follow_request;

import hr.tvz.vibecheck.dto.request.FollowRequestActionRequest;
import hr.tvz.vibecheck.dto.request.FollowRequestRequest;
import hr.tvz.vibecheck.dto.response.FollowActionResponse;
import hr.tvz.vibecheck.dto.response.FollowRequestResponse;
import hr.tvz.vibecheck.entity.FollowRequest;
import hr.tvz.vibecheck.entity.Follows;
import hr.tvz.vibecheck.entity.User;
import hr.tvz.vibecheck.enums.FollowActionResult;
import hr.tvz.vibecheck.enums.FollowRequestStatus;
import hr.tvz.vibecheck.enums.FollowRequestUserResponse;
import hr.tvz.vibecheck.enums.ProfileVisibility;
import hr.tvz.vibecheck.exception.*;
import hr.tvz.vibecheck.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowRequestService {
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;
    private final FollowsRepository followsRepository;

    @Transactional
    public FollowActionResponse createFollowRequestOrFollow(Long senderId, FollowRequestRequest followRequest) {
        User sender = userRepository.findById(senderId).orElseThrow(()
                -> new UserNotFoundException("User with ID " + senderId + " not found"));

        User receiver = userRepository.findById(followRequest.receiverId()).orElseThrow(()
                -> new UserNotFoundException("User with ID " + followRequest.receiverId() + " not found"));

        if (sender.getIdUser().equals(receiver.getIdUser())) {
            throw new SelfFollowException("You cannot follow yourself");
        }

        if (followsRepository.findByUser1_IdUserAndUser2_IdUser(sender.getIdUser(), receiver.getIdUser()).isPresent()) {
            throw new DuplicateFollowException("Follow for " + receiver.getUsername() + " already exists");
        }

        if (receiver.getVisibility() == ProfileVisibility.PUBLIC) {
            Follows newFollow = Follows.builder()
                    .user1(sender)
                    .user2(receiver)
                    .build();
            followsRepository.save(newFollow);
            return new FollowActionResponse(FollowActionResult.FOLLOWING);
        } else {
            Optional<FollowRequest> existingRequestOpt =
                    followRequestRepository.findBySender_IdUserAndReceiver_IdUser(sender.getIdUser(), receiver.getIdUser());

            if (existingRequestOpt.isPresent()) {
                FollowRequest existingReq = existingRequestOpt.get();

                if (existingReq.getStatus() == FollowRequestStatus.DECLINED) {
                    existingReq.setStatus(FollowRequestStatus.PENDING);
                    followRequestRepository.save(existingReq);
                    return new FollowActionResponse(FollowActionResult.PENDING);
                } else {
                    throw new DuplicateFollowRequestException("Follow request for user " + receiver.getUsername() + " already exists");
                }
            } else {
                FollowRequest newFollowRequest = FollowRequest.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .status(FollowRequestStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build();
                followRequestRepository.save(newFollowRequest);
                return new FollowActionResponse(FollowActionResult.PENDING);
            }

        }
    }

    public void cancelFollowRequest(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElseThrow(()
                -> new UserNotFoundException("User with ID " + senderId + " not found"));

        User receiver = userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException("User with ID " + receiverId + " not found"));

        FollowRequest existingReq = followRequestRepository.findBySender_IdUserAndReceiver_IdUser
                (sender.getIdUser(), receiver.getIdUser()).orElseThrow(()
                -> new FollowRequestNotFoundException("Follow request for user " + sender.getUsername() + " not found"));


        if (!existingReq.getStatus().equals(FollowRequestStatus.PENDING)) {
            throw new NotPendingStatusException("You cannot delete a follow request that is not \"Pending\"");
        }

        followRequestRepository.delete(existingReq);
    }

    @Transactional
    public void unfollow(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId).orElseThrow(()
                -> new UserNotFoundException("User with ID " + senderId + " not found"));

        User receiver = userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException("User with ID " + receiverId + " not found"));

        Follows followToDelete = followsRepository.findByUser1_IdUserAndUser2_IdUser
                (sender.getIdUser(), receiver.getIdUser()).orElseThrow(() ->
                new FollowNotFoundException("Follow for user " + sender.getUsername() + " not found"));

        Optional<FollowRequest> existingFollowReq = followRequestRepository.findBySender_IdUserAndReceiver_IdUser
                (sender.getIdUser(), receiver.getIdUser());

        existingFollowReq.ifPresent(followRequestRepository::delete);

        followsRepository.delete(followToDelete);

    }

    public FollowActionResponse getFollowStatus(Long senderId, Long receiverId) {
        Optional<Follows> followExistsOpt =
                followsRepository.findByUser1_IdUserAndUser2_IdUser(senderId, receiverId);

        if (followExistsOpt.isPresent()) {
            return new FollowActionResponse(FollowActionResult.FOLLOWING);
        }

        Optional<FollowRequest> followRequestExistsOtp =
                followRequestRepository.findBySender_IdUserAndReceiver_IdUser(senderId, receiverId);

        if (followRequestExistsOtp.isPresent()) {
            FollowRequest followRequest = followRequestExistsOtp.get();

            if (followRequest.getStatus() == FollowRequestStatus.DECLINED) {
                return new FollowActionResponse(FollowActionResult.FOLLOW);
            }
            return new FollowActionResponse(FollowActionResult.PENDING);
        }
        return new FollowActionResponse(FollowActionResult.FOLLOW);
    }


    @Transactional
    public void acceptOrDeclineFollowRequest(Long requestId, Long receiverId, FollowRequestActionRequest userResponse) {
        String actionName = userResponse.action().name().toLowerCase();

        User receiver = userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException("User with ID " + receiverId + " not found"));

        FollowRequest existingReq = followRequestRepository.findById(requestId).orElseThrow(() ->
                new FollowRequestNotFoundException("Follow request for user " + receiver.getUsername() + " not found"));

        if (!existingReq.getReceiver().getIdUser().equals(receiverId)) {
            throw new NotYourRequestException("You cannot " + actionName + " someone else's follow request");
        }

        if (existingReq.getStatus() != FollowRequestStatus.PENDING) {
            throw new NotPendingStatusException("You cannot " + actionName + " a follow request that is not \"Pending\"");
        }


        if (userResponse.action() == FollowRequestUserResponse.ACCEPT) {
            boolean alreadyFollowing = followsRepository
                    .findByUser1_IdUserAndUser2_IdUser(existingReq.getSender().getIdUser(), receiver.getIdUser())
                    .isPresent();

            if (alreadyFollowing) {
                throw new DuplicateFollowException("User already follows this profile");
            }

            //existingReq.setStatus(FollowRequestStatus.ACCEPTED);
            followRequestRepository.delete(existingReq);

            Follows newFollow = Follows.builder()
                    .user1(existingReq.getSender())
                    .user2(receiver)
                    .build();

            followsRepository.save(newFollow);
        }
        else if (userResponse.action() == FollowRequestUserResponse.DECLINE) {
            existingReq.setStatus(FollowRequestStatus.DECLINED);
            //followRequestRepository.delete(existingReq);
        }


    }


    public List<FollowRequestResponse> getAllFollowRequests(Long receiverId) {
        userRepository.findById(receiverId).orElseThrow(()
                -> new UserNotFoundException("User with ID " + receiverId + " not found"));

        return followRequestRepository.findAllByReceiverId(receiverId);

    }


}
