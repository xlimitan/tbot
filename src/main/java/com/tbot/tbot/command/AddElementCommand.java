package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class AddElementCommand implements BotCommand {

    public AddElementCommand(CategoryService service) {
        this.service = service;
    }

    private final CategoryService service;

    @Override
    public boolean supports(String commandText) {
        return commandText.startsWith("/addElement");
    }

    @Override
    public SendMessage handle(Message message) {
        String[] parts = message.getText().split(" ");
        String response;
        if (parts.length == 2) {
            response = service.addCategory(parts[1], null);
        } else if (parts.length == 3) {
            response = service.addCategory(parts[2], parts[1]);
        } else {
            response = "Неверный формат. Используйте: /addElement <родитель> <дочерний>";
        }

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(response)
                .build();
    }
}
