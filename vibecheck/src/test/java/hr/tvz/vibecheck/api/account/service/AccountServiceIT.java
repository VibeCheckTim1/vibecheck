package hr.tvz.vibecheck.api.account.service;

import hr.tvz.vibecheck.api.account.dto.ChangePasswordRequestDto;
import hr.tvz.vibecheck.api.account.dto.UpdateAccountRequestDto;
import hr.tvz.vibecheck.exception.custom.InvalidPasswordException;
import hr.tvz.vibecheck.exception.custom.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(properties = {
        "spring.liquibase.contexts=integration",
        "spring.datasource.url=jdbc:tc:postgresql:16:///vibecheck",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
@AutoConfigureMockMvc
@Transactional
class AccountServiceIT {

    @Autowired
    AccountService accountService;

    @MockitoBean
    CloudinaryService cloudinaryService;

    @MockitoBean
    MailService mailService;

    @Test
    @DisplayName("updateAccount - uspješno ažurira korisnika")
    void updateAccount_withValidData_updatesUser() {
        var request = new UpdateAccountRequestDto("NovoIme", "NovoPrezime", "Nova bio", false);

        assertThatCode(() -> accountService.updateAccount(1L, request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("updateAccount - baca UserNotFoundException za nepostojećeg korisnika")
    void updateAccount_withInvalidUserId_throwsUserNotFoundException() {
        var request = new UpdateAccountRequestDto("NovoIme", "NovoPrezime", "Nova bio", false);

        assertThatThrownBy(() -> accountService.updateAccount(999L, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("changePassword - uspješno mijenja lozinku")
    void changePassword_withValidOldPassword_changesPassword() {
        var request = new ChangePasswordRequestDto("1234", "novaLozinka123");

        assertThatCode(() -> accountService.changePassword(1L, request))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("changePassword - baca InvalidPasswordException za pogrešnu staru lozinku")
    void changePassword_withWrongOldPassword_throwsInvalidPasswordException() {
        var request = new ChangePasswordRequestDto("pogresnaLozinka", "novaLozinka123");

        assertThatThrownBy(() -> accountService.changePassword(1L, request))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("deleteAccount - uspješno briše korisnika")
    void deleteAccount_withValidUserId_deletesUser() {
        assertThatCode(() -> accountService.deleteAccount(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteAccount - baca UserNotFoundException za nepostojećeg korisnika")
    void deleteAccount_withInvalidUserId_throwsUserNotFoundException() {
        assertThatThrownBy(() -> accountService.deleteAccount(999L))
                .isInstanceOf(UserNotFoundException.class);
    }
}