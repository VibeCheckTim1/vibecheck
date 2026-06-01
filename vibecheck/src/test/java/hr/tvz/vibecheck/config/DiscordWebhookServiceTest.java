package hr.tvz.vibecheck.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DiscordWebhookServiceTest {

    @Test
    void skipsNotificationWhenWebhookUrlIsBlank() {
        RestClient restClient = mock(RestClient.class);
        DiscordWebhookService service = new DiscordWebhookService(restClient, "", "VibeCheck");

        service.sendDailyServerStatus();

        verify(restClient, never()).post();
    }

    @Test
    void sendsDailyServerStatusToDiscordWebhook() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restClientBuilder).build();
        DiscordWebhookService service = new DiscordWebhookService(
                restClientBuilder.build(),
                "https://discord.test/webhook",
                "VibeCheck"
        );

        server.expect(requestTo("https://discord.test/webhook"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"content\":\"VibeCheck - server is up\"}"))
                .andRespond(withSuccess());

        service.sendDailyServerStatus();

        server.verify();
    }
}
