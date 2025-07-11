package com.tbot.tbot.controller;

import com.tbot.tbot.command.CommandDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Telegram-бот для работы с категориями.
 * Обрабатывает входящие сообщения и делегирует команды диспетчеру.
 */
@Component
public class CategoryBot extends TelegramLongPollingBot {

    /**
     * Диспетчер команд, обрабатывающий сообщения пользователей.
     */
    private final CommandDispatcher dispatcher;

    /**
     * Конструктор бота.
     * @param dispatcher диспетчер команд
     */
    public CategoryBot(CommandDispatcher dispatcher) {
        super(new DefaultBotOptions());
        this.dispatcher = dispatcher;
    }

    /**
     * Обработка входящих обновлений от Telegram.
     * @param update объект обновления
     */
    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, что сообщение текстовое
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Получаем ответ от диспетчера команд
            SendMessage response = dispatcher.dispatch(update.getMessage());
            try {
                // Отправляем ответ пользователю
                execute(response);
            } catch (TelegramApiException e) {
                // Логируем ошибку отправки сообщения
                e.printStackTrace();
            }
        }
    }

    /**
     * Имя бота, отображаемое в Telegram.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "CategoryTreeBot";
    }

    /**
     * Токен для доступа к Telegram Bot API.
     * @return токен бота
     */
    @Override
    public String getBotToken() {
        return "7995618135:AAHBhJjK1kK65DlhMCPBotuV_hkmqo7AqLc";
    }
}