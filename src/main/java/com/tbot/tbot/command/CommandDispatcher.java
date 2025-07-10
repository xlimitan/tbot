package com.tbot.tbot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class CommandDispatcher {

    private final List<BotCommand> commands;

    public CommandDispatcher(List<BotCommand> commands) {
        this.commands = commands;
    }

    public SendMessage dispatch(Message message) {
        String text = message.getText();

        for (BotCommand cmd : commands) {
            if (cmd.supports(text)) {
                return cmd.handle(message);
            }
        }

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Неизвестная команда. Используйте /help.")
                .build();
    }
}