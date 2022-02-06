package ru.testov.telegram.bot.commands.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.client.Status;
import ru.testov.telegram.bot.commands.Command;
import ru.testov.telegram.bot.storage.DBJson;

import static ru.testov.telegram.bot.client.Status.ADD_NAME_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.NEW;
import static ru.testov.telegram.bot.client.Status.STARTED;

/**
 * Команда "Старт"
 */
public class StartCommand extends Command {

    private Logger logger = LoggerFactory.getLogger(StartCommand.class);

    public StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public String getText() {
        return "Привет!\nЕсли Вам нужна помощь, нажмите /help";
    }

    public void commandProcessing(AbsSender absSender, Client client) {
        Status status = client.getStatus();
        if (status == NEW) {
            logger.info("Запущен процес добавления дома пользователю ID - " + client.getChatId()
                + " статус - " + client.getStatus());
            client.setStatus(ADD_NAME_TO_NEW_HOUSE);
            DBJson.saveUser(client);
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(client.getChatId());
                sendMessage.setText(
                    "Привет, " + client.getUserName() + "!\nЯ помогу тебе оценить устройство систем утепления фасадов!\n"
                        + "Для начала работы давай заведем объект, подлежащий оценке.\n\nВведите название объекта (пример «ЖК Патриот»)");
                absSender.execute(sendMessage);
            } catch (Exception e) {
                logger.error(String.format("Ошибка %s. При отправке ответа.", e.getMessage()));
                e.printStackTrace();
            }
        }
        if (status == STARTED) {
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(client.getChatId());
                sendMessage
                    .setText("Привет, " + client.getUserName() + "!\nВыбери объект для проверки или создай новый.");
                sendMessage.setReplyMarkup(getHouseListKeyAndNewObjectCreation(client));
                absSender.execute(sendMessage);
            } catch (Exception e) {
                logger.error(String.format("Ошибка %s. При отправке ответа.", e.getMessage()));
                e.printStackTrace();
            }
        }

    }
}
