package ru.testov.telegram.bot;

import java.util.Map;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotLaunch {
    private static final Map<String, String> getenv = System.getenv();

    public static void main(String[] args) {
        try {
            TelegramBotsApi tba = new TelegramBotsApi(DefaultBotSession.class);
            //раскомитить при запуске не через idea
            //tba.registerBot(new Bot(getenv.get("BOT_NAME"), getenv.get("BOT_TOKEN")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
