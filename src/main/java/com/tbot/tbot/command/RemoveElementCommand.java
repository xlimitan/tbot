package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class RemoveElementCommand implements BotCommand {
    public RemoveElementCommand(CategoryService service) {
        this.service = service;
    }

    private final CategoryService service;

    @Override
    public boolean supports(String commandText) {
        return commandText.startsWith("/removeElement");
    }

    @Override
    public SendMessage handle(Message message) {
        String[] parts = message.getText().split(" ");
        String response;

        if (parts.length == 2) {
            response = service.removeCategory(parts[1]);
        } else {
            response = "Неверный формат. Используйте: /removeElement <название категории>";
        }

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(response)
                .build();
    }
}
