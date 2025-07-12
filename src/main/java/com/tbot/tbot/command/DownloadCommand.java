package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Команда для скачивания категорий в виде Excel-файла.
 * При получении команды /download информирует пользователя о начале формирования файла.
 */
@Component
public class DownloadCommand implements BotCommand {

    @Autowired
    private CategoryService categoryService;

    @Override
    public boolean supports(String text) {
        return "/download".equals(text);
    }

    /**
     * Обрабатывает сообщение пользователя.
     * Отправляет уведомление о начале формирования Excel-файла.
     * @param message сообщение Telegram
     * @return ответ пользователю
     */
    @Override
    public SendMessage handle(Message message) {
        // Сообщение пользователю о начале формирования файла
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Формирую Excel-файл с категориями. Пожалуйста, подождите...")
                .build();
    }
}
