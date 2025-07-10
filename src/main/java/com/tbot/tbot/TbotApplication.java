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

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(TbotApplication.class, args);

		CategoryBot bot = context.getBean(CategoryBot.class);
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(bot);
			System.out.println("Бот успешно зарегистрирован!");
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
