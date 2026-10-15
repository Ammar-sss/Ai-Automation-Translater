package com.just.AiAutomationTranslater.translatorservice.translation.client;

import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiRequest;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class N8nClient {

    private final RestClient restClient;

    public N8nClient(@Value("${n8n.webhook-url}") String url,
                     @Value("${n8n.secret}") String secret) {

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(60));

        this.restClient = RestClient.builder()
                .baseUrl(url)
                .requestFactory(factory)
                .defaultHeader("x-n8n-secret", secret)
                .build();
    }

    public AiResponse translate(AiRequest aiRequest) {
        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(aiRequest)
                .retrieve()
                .body(AiResponse.class);
    }
}