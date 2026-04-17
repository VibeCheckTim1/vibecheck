package hr.tvz.vibecheck.service.follow_request;

import hr.tvz.vibecheck.dto.request.FollowRequestRequest;
import hr.tvz.vibecheck.dto.response.FollowActionResponse;
import hr.tvz.vibecheck.entity.FollowRequest;
import hr.tvz.vibecheck.entity.Follows;
import hr.tvz.vibecheck.entity.User;
import hr.tvz.vibecheck.enums.FollowActionResult;
import hr.tvz.vibecheck.enums.FollowRequestStatus;
import hr.tvz.vibecheck.enums.ProfileVisibility;
import hr.tvz.vibecheck.exception.*;
import hr.tvz.vibecheck.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowRequestService {
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;
    private final FollowsRepository followsRepository;

    public FollowActionResponse createFollowRequestOrFollow(Long senderId, FollowRequestRequest followRequest) {
        User sender = userRepository.findById(senderId).orElseThrow(()
                -> new UserNotFoundException("User not found"));

        User receiver = userRepository.findById(followRequest.receiverId()).orElseThrow(()
                -> new UserNotFoundException("User not found"));

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
                -> new FollowRequestNotFoundException("Follow request for user " + sender.getUsername() + " not found!"));


        if (!existingReq.getStatus().equals(FollowRequestStatus.PENDING)) {
            throw new NotPendingStatusException("You cannot delete a follow request that is not \"Pending\"");
        }

        followRequestRepository.delete(existingReq);
    }

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


}
