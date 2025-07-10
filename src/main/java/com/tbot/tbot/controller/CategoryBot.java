package com.tbot.tbot.controller;

import com.tbot.tbot.command.CommandDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CategoryBot extends TelegramLongPollingBot {

    private final CommandDispatcher dispatcher;

    public CategoryBot(CommandDispatcher dispatcher) {
        super(new DefaultBotOptions());
        this.dispatcher = dispatcher;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage response = dispatcher.dispatch(update.getMessage());
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "CategoryTreeBot";
    }

    @Override
    public String getBotToken() {
        return "7995618135:AAHBhJjK1kK65DlhMCPBotuV_hkmqo7AqLc"; // или получи из BotConfig
    }
}