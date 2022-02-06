package ru.testov.telegram.bot.commands.keyboard;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class InlineKeyboardMarkupUtil {

    @Getter
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public InlineKeyboardMarkupUtil(List<String[]> listData) {
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        for (String[] data : listData) {
            InlineKeyboardButton button = new InlineKeyboardButton(data[0]);
            button.setCallbackData(data[1]);
            List<InlineKeyboardButton> listButton = new ArrayList<>();
            listButton.add(button);
            lists.add(listButton);
        }
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(lists);
        this.inlineKeyboardMarkup = keyboard;
    }
}
