package com.tbot.tbot.command;

import com.tbot.tbot.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Команда для добавления категории.
 * Обрабатывает команду /addElement и добавляет новую категорию или подкатегорию.
 */
@Component
public class AddElementCommand implements BotCommand {

    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService service;

    /**
     * Конструктор команды.
     * @param service сервис категорий
     */
    public AddElementCommand(CategoryService service) {
        this.service = service;
    }

    /**
     * Проверяет, поддерживает ли команда переданный текст.
     * @param commandText текст команды
     * @return true, если команда поддерживается
     */
    @Override
    public boolean supports(String commandText) {
        return commandText.startsWith("/addElement");
    }

    /**
     * Обрабатывает команду добавления категории.
     * @param message входящее сообщение пользователя
     * @return сообщение с результатом добавления
     */
    @Override
    public SendMessage handle(Message message) {
        // Разделяем текст сообщения на части по пробелу
        String[] parts = message.getText().split(" ");
        String response;
        // Если указано только название категории — добавляем корневую категорию
        if (parts.length == 2) {
            response = service.addCategory(parts[1], null);
            // Если указаны родитель и дочерняя категория — добавляем подкатегорию
        } else if (parts.length == 3) {
            response = service.addCategory(parts[2], parts[1]);
            // В остальных случаях — возвращаем сообщение об ошибке формата
        } else {
            response = "Неверный формат. Используйте: /addElement <родитель> <дочерний>";
        }
        // Формируем ответное сообщение
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(response)
                .build();
    }
}
