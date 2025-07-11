package com.tbot.tbot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

/**
 * Диспетчер команд Telegram-бота.
 * Перебирает зарегистрированные команды и вызывает обработчик подходящей.
 * Если команда не найдена — отправляет сообщение с подсказкой.
 */
@Component
public class CommandDispatcher {

    /**
     * Список доступных команд бота.
     */
    private final List<BotCommand> commands;

    /**
     * Конструктор диспетчера команд.
     * @param commands список команд
     */
    public CommandDispatcher(List<BotCommand> commands) {
        this.commands = commands;
    }

    /**
     * Обрабатывает входящее сообщение, определяет и вызывает соответствующую команду.
     * @param message входящее сообщение пользователя
     * @return ответное сообщение
     */
    public SendMessage dispatch(Message message) {
        // Получаем текст из входящего сообщения пользователя
        String text = message.getText();
        // Перебираем все зарегистрированные команды
        for (BotCommand cmd : commands) {
            // Проверяем, поддерживает ли команда данный текст
            if (cmd.supports(text)) {
                // Если команда найдена — вызываем её обработчик и возвращаем результат
                return cmd.handle(message);
            }
        }
        // Ответ по умолчанию, если команда не найдена
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Неизвестная команда. Используйте /help.")
                .build();
    }
}