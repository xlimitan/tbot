package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Команда для отображения дерева категорий.
 * Отправляет пользователю текстовое представление дерева категорий.
 */
@Component
public class ViewTreeCommand implements BotCommand {

    private final CategoryService service;

    public ViewTreeCommand(CategoryService service) {
        this.service = service;
    }

    @Override
    public boolean supports(String commandText) {
        return commandText.equals("/viewTree");
    }

    /**
     * Обрабатывает команду и возвращает сообщение с деревом категорий.
     * @param message входящее сообщение пользователя
     * @return сообщение с деревом категорий
     */
    @Override
    public SendMessage handle(Message message) {
        String tree = service.getCategoryTree();
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(tree)
                .build();
    }
}