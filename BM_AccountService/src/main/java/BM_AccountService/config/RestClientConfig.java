package BM_AccountService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(@Value("${nginx.url}") String nginxUrl) {
        return RestClient.builder().baseUrl(nginxUrl).build();
    }
}