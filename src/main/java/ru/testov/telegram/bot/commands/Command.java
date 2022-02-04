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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.storage.DBJson;
import ru.testov.telegram.bot.storage.House;

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
     * Формирование клавиатуры (под сообщением) с объектами
     */
    public static InlineKeyboardMarkup getInlineKeyboard(Client client) {
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        for (House house : client.getHouseList()) {
            InlineKeyboardButton button = new InlineKeyboardButton(house.getHouseName());
            button.setCallbackData(house.getHouseName());
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            lists.add(listButton);
        }
        {
            InlineKeyboardButton button = new InlineKeyboardButton("\uD83C\uDFE0\n Создать новый объект");
            button.setCallbackData("Создать новый объект");
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            lists.add(listButton);
        }
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(lists);
        return keyboard;
    }




   /* void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName) {
        //Отправка несколькоих строк клавиатуры
        // ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        //        KeyboardRow keyboardFirstRow = new KeyboardRow();
        //        KeyboardRow keyboardSecondRow = new KeyboardRow();
        //        keyboardMarkup.setSelective(true);
        //        keyboardMarkup.setResizeKeyboard(true);
        //        keyboardMarkup.setOneTimeKeyboard(false);
        //        keyboardFirstRow.add("1");
        //        keyboardFirstRow.add("2");
        //        keyboardSecondRow.add("3");
        //        keyboardSecondRow.add("4");
        //        keyboardRows.add(keyboardFirstRow);
        //        keyboardRows.add(keyboardSecondRow);
        //        keyboardMarkup.setKeyboard(keyboardRows);

        keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
//        keyboardRows.clear();
//        keyboardFirstRow.clear();
//        keyboardSecondRow.clear();
//        keyboardRow.add("✅");
//        keyboardRow.add("Не осмотрено");
//        keyboardRow.add("❌");
        keyboardRow.add("\uD83C\uDFE0\n Добавить объект");
        keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));

        //KeyboardRow keyboardRow = new KeyboardRow();
        //            keyboardMarkup.setSelective(true);
        //            keyboardMarkup.setResizeKeyboard(true);
        //            keyboardMarkup.setOneTimeKeyboard(false);
        ////        keyboardRows.clear();
        ////        keyboardFirstRow.clear();
        ////        keyboardSecondRow.clear();
        //            keyboardRow.add("\uD83C\uDFE0 Добавить объект");

        //группа картинок можно использовать только картинки из интернета (по ссылке)
//        MediaPhoto inputMediaPhoto_1 = new InputMediaPhoto();
//        inputMediaPhoto_1.setMedia("/Users/de.konovalov/IdeaProjects/sco/sco-innovations/TestBot/src/main/resources/1.png");
//        InputMediaPhoto inputMediaPhoto_2 = new InputMediaPhoto();
//        inputMediaPhoto_2.setMedia("/Users/de.konovalov/IdeaProjects/sco/sco-innovations/TestBot/src/main/resources/2.png");
//
//        ArrayList<InputMedia> inputMediaPhotoArrayList = new ArrayList<>();
//        inputMediaPhotoArrayList.add(inputMediaPhoto_1);
//        inputMediaPhotoArrayList.add(inputMediaPhoto_2);
//
//        SendMediaGroup sendMediaGroup = new SendMediaGroup();
//        sendMediaGroup.setChatId(chatId.toString());
//        sendMediaGroup.setMedias(inputMediaPhotoArrayList);

        //одна картинка
//        SendPhoto sendPhoto = new SendPhoto();
//        sendPhoto.setChatId(chatId.toString());
//        sendPhoto.setPhoto(new InputFile(
//            new File("/Users/de.konovalov/IdeaProjects/sco/sco-innovations/TestBot/src/main/resources/1.png")));
//        sendPhoto.setCaption("1");
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(getText());
        message.setReplyMarkup(keyboardMarkup);

        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s",
                e.getMessage(), commandName, userName));
            e.printStackTrace();
        }
//        sendPhoto.setPhoto(new InputFile(
//            new File("/Users/de.konovalov/IdeaProjects/sco/sco-innovations/TestBot/src/main/resources/2.png")));
//        try {
//            absSender.execute(sendPhoto);
//        } catch (TelegramApiException e) {
//            logger.error(String.format("Ошибка %s. Команда %s. Пользователь: %s",
//                e.getMessage(), commandName, userName));
//            e.printStackTrace();
//        }
    }*/
}