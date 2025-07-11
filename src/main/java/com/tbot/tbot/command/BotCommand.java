package com.tbot.tbot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface BotCommand {
    boolean supports(String commandText);
    SendMessage handle(Message message);
}