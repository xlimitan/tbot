package com.tbot.tbot;

import com.tbot.tbot.controller.CategoryBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TbotApplication {

	/**
	 * Точка входа в приложение.
	 * Инициализирует Spring-контекст, получает бин бота и регистрирует его в Telegram API.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		// Запускаем Spring Boot приложение и получаем контекст
		ConfigurableApplicationContext context = SpringApplication.run(TbotApplication.class, args);

		// Получаем бин Telegram-бота из контекста
		CategoryBot bot = context.getBean(CategoryBot.class);
		try {
			// Инициализируем API Telegram и регистрируем бота
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(bot);
			System.out.println("Бот успешно зарегистрирован!");
		} catch (TelegramApiException e) {
			// В случае ошибки выводим стек вызовов
			e.printStackTrace();
		}
	}
}
