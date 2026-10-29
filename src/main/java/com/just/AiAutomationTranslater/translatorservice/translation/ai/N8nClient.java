/*package com.just.AiAutomationTranslater.translatorservice.translation.AI;

import com.just.AiAutomationTranslater.translatorservice.exceptions.TranslationWorkflowException;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.AiResponse;
import com.just.AiAutomationTranslater.translatorservice.translation.dto.TranslateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
@Slf4j
public class N8nClient{

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

    public AiResponse translate(TranslateRequest translateRequest) {
        try {
            return restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(translateRequest)
                    .retrieve()
                    .body(AiResponse.class);
        } catch (HttpServerErrorException e) {
            log.error("n8n Workflow Execution Error: {}", e.getResponseBodyAsString(), e);
            throw new TranslationWorkflowException("Translation service failed in n8n workflow: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Failed to communicate with n8n webhook", e);
            throw new TranslationWorkflowException("Error communicating with translation workflow service.");
        }
    }
}
 */