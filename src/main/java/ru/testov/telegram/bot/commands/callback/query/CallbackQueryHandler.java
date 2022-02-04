package ru.testov.telegram.bot.commands.callback.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.client.Status;
import ru.testov.telegram.bot.storage.DBJson;

import static ru.testov.telegram.bot.client.Status.ADD_NAME_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.STARTED;

public class CallbackQueryHandler {

    private static Logger logger = LoggerFactory.getLogger(CallbackQueryHandler.class);

    public CallbackQueryHandler() {
    }

    public String processing(CallbackQuery callbackQuery) {
        Status status = DBJson.getUserStatus(callbackQuery.getMessage().getChat());
        if (status != STARTED) {
            return "Простите, я не понимаю Вас.\nВозможно, Вам поможет /help";
        }
        Client client = DBJson.findClient(callbackQuery.getMessage().getChat());
        String data = callbackQuery.getData();
        if (data.equals("Создать новый объект")) {
            logger.info("Запущен процес добавления нового дома пользователю ID - " + client.getChatId()
                + " статус - " + client.getStatus());
            client.setStatus(ADD_NAME_TO_NEW_HOUSE);
            DBJson.saveUser(client);
            return "Введите название (индентификатор) объекта";
        } else if (client.findHouse(data)) {
            return "Выбран объект \"" + data + "\"";
        }
        return "Простите, я не понимаю Вас.\nВозможно, Вам поможет /help";
    }

}
