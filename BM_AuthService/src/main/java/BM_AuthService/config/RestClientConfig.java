package BM_AuthService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(@Value("${user-service.url}") String userServiceUrl) {

        return RestClient.builder().baseUrl(userServiceUrl).build();
    }
}