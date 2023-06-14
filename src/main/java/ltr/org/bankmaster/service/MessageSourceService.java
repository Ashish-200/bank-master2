package ltr.org.bankmaster.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static ltr.org.bankmaster.util.Constants.COMMON_SERVICE_NAME;
import static ltr.org.bankmaster.util.Constants.SERVICE_NAME;

@Slf4j
@Service
@CacheConfig(cacheNames = "npaMasterMessageSource")
public class MessageSourceService {
    @Value("${default.errorMsg.documentId}")
    private String defaultErrorMessageDocId;
    @Value("${spring.config.service.url}")
    private String configBaseUrl;

    @Cacheable(key = "#language +'_'+ #key")
    public String getMessageContent(String language, String key) {
        String messageNotFound = "No message found in config for Language :" + language + " and Code :" + key;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String url;
        if (StringUtils.isEmpty(language) || language.equalsIgnoreCase("en")) {
            url = configBaseUrl + (defaultErrorMessageDocId + "/" + (key.startsWith("common") ? COMMON_SERVICE_NAME : SERVICE_NAME));
        } else {
            url = configBaseUrl + (defaultErrorMessageDocId + "_" + language + "/" + (key.startsWith("common") ? COMMON_SERVICE_NAME : SERVICE_NAME));
        }
        String response = (restTemplate.getForObject(url, String.class));
        if (null == response) return messageNotFound;
        try {
            Map<String, String> propertiesMap = new ObjectMapper().readValue(response, new TypeReference<>() {
            });
            return propertiesMap.getOrDefault(key, messageNotFound);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }
}
