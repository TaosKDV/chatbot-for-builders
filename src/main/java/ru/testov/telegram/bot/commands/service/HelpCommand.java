package ru.testov.telegram.bot.commands.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.commands.Command;

import static ru.testov.telegram.bot.commands.SendMessageUtil.getSendMessage;

/**
 * Команда "Помощь"
 */
public class HelpCommand extends Command {

    private Logger logger = LoggerFactory.getLogger(HelpCommand.class);

    public HelpCommand(String identifier, String description) {
        super(identifier, description);
    }

    public void commandProcessing(AbsSender absSender, Client client) {
        try {
            absSender.execute(getSendMessage(client, "Извините, данный функционал еще не реализован."));
        } catch (

            Exception e) {
            logger.error(String.format("Ошибка %s. При отправке ответа.", e.getMessage()));
            e.printStackTrace();
        }
    }

}