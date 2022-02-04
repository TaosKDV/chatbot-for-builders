package ru.testov.telegram.bot.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.commands.Command;

/**
 * Команда получения текущих настроек
 */
public class SettingsCommand extends Command {

    private Logger logger = LoggerFactory.getLogger(SettingsCommand.class);

    public SettingsCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public String getText() {
        return "*Текущие настройки*\n";
    }

    public void commandProcessing(AbsSender absSender, Client client) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            sendMessage.setChatId(client.getChatId());
            sendMessage.setText("Извините, данный функционал еще не реализован.");
            absSender.execute(sendMessage);
        } catch (

            Exception e) {
            logger.error(String.format("Ошибка %s. При отправке ответа.", e.getMessage()));
            e.printStackTrace();
        }
    }
}