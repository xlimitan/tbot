package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Команда для удаления категории.
 * Обрабатывает команду /removeElement и удаляет указанную категорию.
 */
@Component
public class RemoveElementCommand implements BotCommand {

    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService service;

    /**
     * Конструктор команды.
     * @param service сервис категорий
     */
    public RemoveElementCommand(CategoryService service) {
        this.service = service;
    }

    /**
     * Проверяет, поддерживает ли команда переданный текст.
     * @param commandText текст команды
     * @return true, если команда поддерживается
     */
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
        // Если передано два слова — команда и название категории
        if (parts.length == 2) {
            // Удаляем категорию через сервис и получаем результат
            response = service.removeCategory(parts[1]);
            // В остальных случаях возвращаем сообщение об ошибке формата
        } else {
            response = "Неверный формат. Используйте: /removeElement <название категории>";
        }
        // Формируем и возвращаем ответное сообщение пользователю
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(response)
                .build();
    }
}
