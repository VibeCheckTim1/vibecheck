package hr.tvz.vibecheck.projections;

import java.time.LocalDateTime;

public interface UserStateResponse {
    Long getIdUser();
    String getFirstName();
    String getLastName();
    String getUsername();
    String getAvatarUrl();
    String getBio();
    Boolean getIsPrivate();
    String getEmail();
    LocalDateTime getTstamp();
}
