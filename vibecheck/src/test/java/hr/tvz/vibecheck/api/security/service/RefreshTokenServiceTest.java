package hr.tvz.vibecheck.api.security.service;

import hr.tvz.vibecheck.api.security.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenServiceTest {

    @Test
    void deleteExpiredOrRevokedTokensDeletesMatchingTokens() {
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        RefreshTokenService service = new RefreshTokenService(refreshTokenRepository);

        when(refreshTokenRepository.deleteExpiredOrRevokedBefore(any(LocalDateTime.class)))
                .thenReturn(3);

        int deletedCount = service.deleteExpiredOrRevokedTokens();

        assertThat(deletedCount).isEqualTo(3);
        verify(refreshTokenRepository).deleteExpiredOrRevokedBefore(any(LocalDateTime.class));
    }
}
