package com.tbot.tbot.controller;

import com.tbot.tbot.command.CommandDispatcher;
import com.tbot.tbot.service.CategoryService;
import com.tbot.tbot.service.CategoryExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

/**
 * Telegram-бот для управления деревом категорий.
 * <p>
 * Поддерживает команды:
 * - /download: экспортирует дерево категорий в Excel.
 * - Загрузка Excel-файла: импортирует категории из файла.
 * <p>
 * Устойчив к ошибкам формата и загрузки файлов.
 */
@Component
public class CategoryBot extends TelegramLongPollingBot {

    private final CommandDispatcher dispatcher;
    private final CategoryService categoryService;
    private final CategoryExcelService categoryExcelService;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    public CategoryBot(CommandDispatcher dispatcher,
                       CategoryService categoryService,
                       CategoryExcelService categoryExcelService) {
        super(new DefaultBotOptions());
        this.dispatcher = dispatcher;
        this.categoryService = categoryService;
        this.categoryExcelService = categoryExcelService;
    }

    /**
     * Обрабатывает входящие обновления Telegram.
     * <p>
     * - Текстовые сообщения: передаёт в диспетчер команд.
     * - /download: отправляет Excel-файл с категориями.
     * - Документ .xlsx: импортирует категории из файла.
     */
    @Override
    public void onUpdateReceived(Update update) {
        // Проверяем, есть ли сообщение в обновлении
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.hasText()) {
                // Обработка текстовых команд
                var response = dispatcher.dispatch(message);
                try {
                    execute(response);
                    // Если команда /download, отправить Excel-файл
                    if ("/download".equals(message.getText())) {
                        // Получаем все категории
                        var categories = categoryService.getAllCategories();
                        // Экспортируем категории в Excel
                        var excel = categoryExcelService.exportCategories(categories);
                        // Создаём InputFile для отправки документа
                        var inputFile = new org.telegram.telegrambots.meta.api.objects.InputFile(
                                new java.io.ByteArrayInputStream(excel.toByteArray()), "categories.xlsx"
                        );
                        // Формируем сообщение с документом
                        var docMsg = org.telegram.telegrambots.meta.api.methods.send.SendDocument.builder()
                                .chatId(message.getChatId().toString())
                                .document(inputFile)
                                .caption("Дерево категорий в Excel")
                                .build();
                        // Отправляем Excel-файл пользователю
                        execute(docMsg);
                    }
                } catch (Exception e) {
                    // Логируем ошибку при обработке команды или отправке файла
                    e.printStackTrace();
                }
            } else if (message.hasDocument()) {
                // обработка загрузки Excel-файла
                var document = message.getDocument();
                // Проверяем, что файл имеет расширение .xlsx
                if (document.getFileName().endsWith(".xlsx")) {
                    try {
                        // Получаем файл с серверов Telegram
                        var file = execute(new org.telegram.telegrambots.meta.api.methods.GetFile(document.getFileId()));
                        var filePath = file.getFilePath();
                        // Формируем URL для скачивания файла
                        var url = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
                        // Открываем поток для чтения Excel-файла
                        try (InputStream inputStream = new java.net.URL(url).openStream()) {
                            // Импортируем категории из Excel
                            categoryExcelService.importCategories(inputStream);
                        }
                        // Отправляем сообщение об успешной загрузке
                        execute(org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text("Категории успешно загружены из Excel.")
                                .build());
                    } catch (Exception e) {
                        // В случае ошибки отправляем сообщение об ошибке
                        try {
                            execute(org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                                    .chatId(message.getChatId().toString())
                                    .text("Ошибка при загрузке файла.")
                                    .build());
                        } catch (TelegramApiException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                } else {
                    // Сообщение о неверном формате файла
                    try {
                        execute(org.telegram.telegrambots.meta.api.methods.send.SendMessage.builder()
                                .chatId(message.getChatId().toString())
                                .text("Пожалуйста, загрузите Excel-файл (.xlsx).")
                                .build());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "CategoryTreeBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}