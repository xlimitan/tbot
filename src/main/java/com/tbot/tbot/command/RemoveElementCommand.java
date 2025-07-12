package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Команда для удаления категории.
 * Обрабатывает команду /removeElement и удаляет указанную категорию.
 */
@Component
public class RemoveElementCommand implements BotCommand {

    private static final Logger logger = LoggerFactory.getLogger(RemoveElementCommand.class);

    private final CategoryService service;

    public RemoveElementCommand(CategoryService service) {
        this.service = service;
    }

    @Override
    public boolean supports(String commandText) {
        return commandText.startsWith("/removeElement");
    }

    /**
     * Обрабатывает команду удаления категории.
     * @param message входящее сообщение пользователя
     * @return сообщение с результатом удаления
     */
    @Override
    public SendMessage handle(Message message) {
        // Ожидается формат: /removeElement <название категории>
        String[] parts = message.getText().split(" ");
        String response;
        try {
        // Если передано два слова — команда и название категории
        if (parts.length == 2) {
            // Удаляем категорию через сервис и получаем результат
            response = service.removeCategory(parts[1]);
            // В остальных случаях возвращаем сообщение об ошибке формата
        } else {
            response = "Неверный формат. Используйте: /removeElement <название категории>";
        }
        } catch (Exception e) {
            logger.error("Ошибка при удалении категории", e);
            response = "Ошибка при удалении категории.";
        }
        // Формируем и возвращаем ответное сообщение пользователю
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(response)
                .build();
    }
}
