package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ViewTreeCommand implements BotCommand {

    public ViewTreeCommand(CategoryService service) {
        this.service = service;
    }

    private final CategoryService service;

    @Override
    public boolean supports(String commandText) {
        return commandText.equals("/viewTree");
    }

    @Override
    public SendMessage handle(Message message) {
        String tree = service.getCategoryTree();
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(tree)
                .build();
    }
}