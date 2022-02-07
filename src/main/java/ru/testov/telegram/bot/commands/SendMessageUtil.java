package ru.testov.telegram.bot.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.testov.telegram.bot.client.Client;

public class SendMessageUtil {

    //Есть праметр sendMessage.enableMarkdown(true); непонятно зачем он нужен

    public static SendMessage getSendMessage(Client client, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(client.getChatId());
        sendMessage.setText(text);
        return sendMessage;
    }

    public static SendMessage getSendMessage(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        return sendMessage;
    }

    public static SendMessage getSendMessage(Message message, ReplyKeyboard replyKeyboard, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setText(text);
        return sendMessage;
    }

    public static SendMessage getSendMessage(Client client, ReplyKeyboard replyKeyboard, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(client.getChatId());
        sendMessage.setReplyMarkup(replyKeyboard);
        sendMessage.setText(text);
        return sendMessage;
    }

    public static SendMessage getSendMessageAndKeyboardRemove(Client client, String text) {
        return getSendMessage(client, new ReplyKeyboardRemove(true), text);//очистак клавиатуры
    }
}
