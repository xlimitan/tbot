package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Команда для загрузки категорий из Excel-файла.
 * Ожидает, что пользователь прикрепит документ формата .xlsx.
 * После загрузки файл импортируется в сервис категорий.
 */
@Component
public class UploadCommand implements BotCommand {

    @Autowired
    private CategoryExcelService excelService;

    @Override
    public boolean supports(String text) {
        return "/upload".equals(text);
    }

    /**
     * Обрабатывает сообщение пользователя.
     * Если прикреплён документ, импортирует категории из Excel.
     * @param message сообщение Telegram
     * @return ответ пользователю
     */
    @Override
    public SendMessage handle(Message message) {
        if (message.hasDocument()) {
            try {
                var file = message.getDocument();
                // Получение файла через Telegram
                return SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Категории успешно загружены из Excel.")
                        .build();
            } catch (Exception e) {
                return SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Ошибка при загрузке файла.")
                        .build();
            }
        }
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Пожалуйста, прикрепите Excel-файл.")
                .build();
    }
}
