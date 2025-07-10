package com.tbot.tbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Data
public class BotConfig {
    private List<BotData> bots;

    @Data
    public static class BotData {
        private String name;
        private String token;
    }
}
