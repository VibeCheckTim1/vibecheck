package hr.tvz.vibecheck.projections;

import java.time.LocalDateTime;

public interface UserStateResponse {
    Long getIdUser();
    String getFirstName();
    String getLastName();
    String getAvatarUrl();
    String getBio();
    String getVisibility();
    String getUsername();
    String getEmail();
    LocalDateTime getTstamp();
}
