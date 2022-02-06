package ru.testov.telegram.bot.commands;


import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.commands.keyboard.InlineKeyboardMarkupUtil;
import ru.testov.telegram.bot.storage.DBJson;
import ru.testov.telegram.bot.storage.house.House;

/**
 * Суперкласс для команд
 */
public abstract class Command extends BotCommand {

    private Logger logger = LoggerFactory.getLogger(Command.class);

    @Getter
    @Setter
    private ReplyKeyboardMarkup keyboardMarkup;

    public Command(String identifier, String description) {
        super(identifier, description);
    }

    private void sendError(AbsSender absSender, Chat chat, String message) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(chat.getId().toString());
            sendMessage.setText(message);
            absSender.execute(sendMessage);
        } catch (Exception e) {
            logger.error(String.format("Ошибка %s. При отправке ответа.", e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Client client = DBJson.findClient(chat, user);
        logger.info(String.format("Пользователь ID - %s. Начато выполнение команды %s", client.getChatId(),
            this.getCommandIdentifier()));
        commandProcessing(absSender, client);
        logger.info(String.format("Пользователь ID - %s. Завершено выполнение команды %s", client.getChatId(),
            this.getCommandIdentifier()));
    }

    public abstract String getText();

    /**
     * Отправка ответа пользователю
     */
    public abstract void commandProcessing(AbsSender absSender, Client client);

    /**
     * Формирование клавиатуры со списком объектов
     */
    public static InlineKeyboardMarkup getHouseListKey(Client client) {
        List<String[]> list = new ArrayList<>();
        for (House house : client.getHouseList()) {
            String[] strings = {house.getHouseName(), house.getHouseName()};
            list.add(strings);
        }
        return new InlineKeyboardMarkupUtil(list).getInlineKeyboardMarkup();
    }

    /**
     * Формирование клавиатуры со списком объектов и кнопкой "Создать новый объект"
     */
    public static InlineKeyboardMarkup getHouseListKeyAndNewObjectCreation(Client client) {
        List<String[]> list = new ArrayList<>();
        for (House house : client.getHouseList()) {
            String[] strings = {house.getHouseName(), house.getHouseName()};
            list.add(strings);
        }
        String[] strings = {"\uD83C\uDFE0\n Создать новый объект", "Создать новый объект"};
        list.add(strings);
        return new InlineKeyboardMarkupUtil(list).getInlineKeyboardMarkup();
    }
}