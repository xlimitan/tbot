package com.tbot.tbot.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CommandDispatcher.class);

    private final List<BotCommand> commands;

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
                try {
                // Если команда найдена — вызываем её обработчик и возвращаем результат
                return cmd.handle(message);
                } catch (Exception e) {
                    logger.error("Ошибка при обработке команды: {}", text, e);
                }
            }
        }
        // Ответ по умолчанию, если команда не найдена
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Неизвестная команда. Используйте /help.")
                .build();
    }
}