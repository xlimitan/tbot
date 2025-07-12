package com.tbot.tbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;

/**
 * Конфигурационный класс для параметров Telegram-ботов.
 * Загружает данные из application.yml с префиксом "telegram".
 */
@Configuration
@ConfigurationProperties(prefix = "telegram")
@Data
public class BotConfig {

    /**
     * Список данных о ботах, определённых в конфигурации.
     */
    private List<BotData> bots;


    /**
     * Класс, представляющий данные одного Telegram-бота.
     */
    @Data
    public static class BotData {
        private String name;
        private String token;
    }
}
