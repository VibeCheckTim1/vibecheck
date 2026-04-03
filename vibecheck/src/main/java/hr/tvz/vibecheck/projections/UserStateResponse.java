package hr.tvz.vibecheck.projections;

import hr.tvz.vibecheck.enums.ProfileVisibility;

import java.time.LocalDateTime;

public interface UserStateResponse {
    Long getIdUser();
    String getFirstName();
    String getLastName();
    String getUsername();
    String getAvatarUrl();
    String getBio();
    ProfileVisibility getVisibility();
    String getEmail();
    LocalDateTime getTstamp();
}
