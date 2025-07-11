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

    /**
     * Проверяет, поддерживает ли команда переданный текст.
     * @param commandText текст команды
     * @return true, если команда поддерживается
     */
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
                /addElement <название> — добавить корневой элемент
                /addElement <родитель> <дочерний> — добавить подкатегорию
                /removeElement <название> — удалить категорию и её потомков
                /viewTree — показать дерево категорий
                /help — список команд
                """;
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(helpText)
                .build();
    }
}
