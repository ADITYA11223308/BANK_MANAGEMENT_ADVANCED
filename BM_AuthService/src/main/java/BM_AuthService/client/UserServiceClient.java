package BM_AuthService.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import BM_AuthService.dto.AuthUserResponse;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public AuthUserResponse getUserForAuthentication(String email) {

        return restClient.get()
                .uri("/users/auth/{email}", email)
                .retrieve()
                .body(AuthUserResponse.class);
    }
}