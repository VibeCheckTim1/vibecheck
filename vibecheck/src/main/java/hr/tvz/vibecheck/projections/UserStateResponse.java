package hr.tvz.vibecheck.projections;

import java.time.LocalDateTime;

public interface UserStateResponse {
    Long getIdUser();
    String getUsername();
    String getEmail();
    LocalDateTime getTstamp();
}
