package ru.testov.telegram.bot.commands.keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

public class ReplyKeyboardMarkupUtil {

    @Getter
    private ReplyKeyboardMarkup replyKeyboardMarkup;

    public ReplyKeyboardMarkupUtil() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("✅");
        keyboardRow.add("Не осмотрено");
        keyboardRow.add("❌");
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        this.replyKeyboardMarkup = keyboardMarkup;
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
