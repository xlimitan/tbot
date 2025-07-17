package com.tbot.tbot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Команда для отображения справки.
 * Отправляет пользователю список доступных команд и их описание.
 */
@Component
public class HelpCommand implements BotCommand {

    @Override
    public boolean supports(String commandText) {
        return commandText.equals("/help");
    }

    /**
     * Обрабатывает команду и возвращает сообщение со справкой.
     * @param message входящее сообщение пользователя
     * @return сообщение со списком команд
     */
    @Override
    public SendMessage handle(Message message) {
        String helpText = """
            Список доступных команд:
            /addElement категория — добавить категорию
            /addElement категория подкатегория — добавить подкатегорию
            /removeElement категория — удалить категорию и её подкатегории
            /viewTree — показать дерево категорий
            /download — скачать категории (Excel)
            /upload — загрузить категории (Excel)
            /help — справка
            """;
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(helpText)
                .build();
    }
}
