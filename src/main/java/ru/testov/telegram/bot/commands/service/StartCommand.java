package ru.testov.telegram.bot.commands.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.client.Status;
import ru.testov.telegram.bot.commands.Command;
import ru.testov.telegram.bot.storage.DBJson;

import static ru.testov.telegram.bot.TextForUser.helloNewUser;
import static ru.testov.telegram.bot.TextForUser.helloOldUser;
import static ru.testov.telegram.bot.client.Status.ADD_NAME_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.NEW;
import static ru.testov.telegram.bot.client.Status.STARTED;
import static ru.testov.telegram.bot.commands.SendMessageUtil.getSendMessage;

/**
 * Команда "Старт"
 */
public class StartCommand extends Command {

    private Logger logger = LoggerFactory.getLogger(StartCommand.class);

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    public void commandProcessing(AbsSender absSender, Client client) {
        Status status = client.getStatus();
        if (status == NEW) {
            logger.info("Запущен процес добавления дома пользователю ID - " + client.getChatId()
                + " статус - " + client.getStatus());
            client.setStatus(ADD_NAME_TO_NEW_HOUSE);
            DBJson.saveUser(client);
            try {
                absSender.execute(getSendMessage(client, helloNewUser(client.getUserName())));
            } catch (Exception e) {
                logger.error(String.format("Ошибка %s. При отправке ответа.", e.getMessage()));
                e.printStackTrace();
            }
        }
        if (status == STARTED) {
            try {
                absSender.execute(getSendMessage(client, getHouseListKeyAndNewObjectCreation(client),
                    helloOldUser(client.getUserName())));
            } catch (Exception e) {
                logger.error(String.format("Ошибка %s. При отправке ответа.", e.getMessage()));
                e.printStackTrace();
            }
        }

    }
}
