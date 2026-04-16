package hr.tvz.vibecheck.service.follow_request;

import hr.tvz.vibecheck.dto.request.FollowRequestRequest;
import hr.tvz.vibecheck.dto.response.FollowActionResponse;
import hr.tvz.vibecheck.entity.FollowRequest;
import hr.tvz.vibecheck.entity.Follows;
import hr.tvz.vibecheck.entity.User;
import hr.tvz.vibecheck.enums.FollowActionResult;
import hr.tvz.vibecheck.enums.FollowRequestStatus;
import hr.tvz.vibecheck.enums.ProfileVisibility;
import hr.tvz.vibecheck.exception.DuplicateFollowException;
import hr.tvz.vibecheck.exception.DuplicateFollowRequestException;
import hr.tvz.vibecheck.repository.follow_request.FollowRequestRepository;
import hr.tvz.vibecheck.repository.follows.FollowsRepository;
import hr.tvz.vibecheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowRequestService {
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;
    private final FollowsRepository followsRepository;

    public FollowActionResponse createFollowRequestOrFollow(FollowRequestRequest followRequest) {
        User sender = userRepository.findById(followRequest.senderId()).orElseThrow(()
                -> new RuntimeException("User not found"));

        User receiver = userRepository.findById(followRequest.receiverId()).orElseThrow(()
                -> new RuntimeException("User not found"));

        if (followsRepository.existsByUser1_IdUserAndUser2_IdUser(sender.getIdUser(), receiver.getIdUser())) {
            throw new DuplicateFollowException("Follow for " + receiver.getUsername() + " already exists");
        }

        if (receiver.getVisibility() == ProfileVisibility.PUBLIC) {
            Follows newFollow = Follows.builder()
                            .user1(sender)
                            .user2(receiver)
                            .build();
            followsRepository.save(newFollow);
            return new FollowActionResponse(FollowActionResult.FOLLOWING);
        }
        else {
            Optional<FollowRequest> existingRequestOpt =
                    followRequestRepository.findBySender_IdUserAndReceiver_IdUser(sender.getIdUser(), receiver.getIdUser());

            if (existingRequestOpt.isPresent()) {
                FollowRequest existingReq = existingRequestOpt.get();

                if (existingReq.getStatus().equals(FollowRequestStatus.DECLINED)) {
                    existingReq.setStatus(FollowRequestStatus.PENDING);
                    followRequestRepository.save(existingReq);
                    return new FollowActionResponse(FollowActionResult.PENDING);
                }
                else {
                    throw new DuplicateFollowRequestException("Follow request for user " + receiver.getUsername() + " already exists");
                }
            }
            else {
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




}
