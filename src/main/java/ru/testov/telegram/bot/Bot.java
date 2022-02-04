package ru.testov.telegram.bot;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.commands.Command;
import ru.testov.telegram.bot.commands.callback.query.CallbackQueryHandler;
import ru.testov.telegram.bot.commands.service.StartCommand;
import ru.testov.telegram.bot.storage.DBJson;

import static ru.testov.telegram.bot.client.Status.ADD_ADDRESS_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.ADD_NAME_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.STARTED;

public class Bot extends TelegramLongPollingBot {

    private Logger logger = LoggerFactory.getLogger(Bot.class);

    private final String BOT_NAME;

    private final String BOT_TOKEN;

    private CommandRegistry commandRegistry;

    public Bot(String botName, String botToken) {
        super(new DefaultBotOptions());
        logger.info("Конструктор суперкласса отработал");
        this.commandRegistry = new CommandRegistry(true, this::getBotUsername);
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        logger.info("Имя и токен присвоены");

        logger.info("Класс обработки сообщения, не являющегося командой, создан");

        register(new StartCommand("start", "Старт"));
        logger.info("Команда start создана");
//
//        register(new HelpCommand("help", "Помощь"));
//        logger.info("Команда help создана");
//
//        register(new SettingsCommand("settings", "Мои настройки"));
//        logger.info("Команда settings создана");

        logger.info("Запущен!");
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    public final boolean register(IBotCommand botCommand) {
        return commandRegistry.register(botCommand);
    }

    @Override
    public final void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.isCommand() && !filter(message)) {
                if (!commandRegistry.executeCommand(this, message)) {
                    //получили незарегистрированную команду
                    processInvalidCommandUpdate(update);
                }
                return;
            }
        } else if (update.hasCallbackQuery()) {
            //todo возможно стоит добавить проверку что сообщение пришло от самого бота
            CallbackQueryHandler handler = new CallbackQueryHandler();
            String text = handler.processing(update.getCallbackQuery());
            setAnswer(update.getCallbackQuery().getMessage(), text);
            return;
        }
        processNonCommandUpdate(update);
    }

    private boolean filter(Message message) {
        if (message.getFrom().getIsBot()) {
            logger.warn("Предупреждение. К боту обращается другой бот!\nUser:\n" + message.getFrom()
                + "\nChat:\n" + message.getChatId());
            setAnswer(message, "Извините, но я не работаю с ботами.");
            return true;
        }
        if (!message.getChat().isUserChat()) {
            logger.warn(
                "Предупреждение. К боту обращаются не из пользовательского чата!\nUser:\n" + message.getFrom()
                    + "\nChat:\n" + message.getChatId());
            setAnswer(message, "Извините, но я работаю только с пользовательскими чатами.");
            return true;
        }
        return false;
    }

    /**
     * Этот метод вызывается, когда пользователь отправляет незарегистрированную команду.
     */
    protected void processInvalidCommandUpdate(Update update) {
        processNonCommandUpdate(update);
    }

    /**
     * Ответ на запрос, не являющийся командой
     */
    public void processNonCommandUpdate(Update update) {
        Message message = update.getMessage();
        Client client = DBJson.findClient(message);
        Objects.nonNull(client);
        nonCommandProcessing(client, message.getText());
    }

    public void nonCommandProcessing(Client client, String text) {
        logger.info("Начата обработка сообщения \"" + text + "\", не являющегося командой от пользователя ID - "
            + client.getChatId() + " Статус - " + client.getStatus());
        String answer;
        if (client.getStatus() == ADD_NAME_TO_NEW_HOUSE) {
            answer = client.addHouseName(text);
            if (answer != null) {
                setAnswer(client, answer);
                return;
            }
            client.setStatus(ADD_ADDRESS_TO_NEW_HOUSE);
            DBJson.saveUser(client);
            setAnswer(client, "Введите адрес объекта");
            return;
        }
        if (client.getStatus() == ADD_ADDRESS_TO_NEW_HOUSE) {
            answer = client.addHouseAddress(text);
            if (answer != null) {
                setAnswer(client, answer);
                return;
            }
            client.setStatus(STARTED);
            DBJson.saveUser(client);
            setAnswer(client, Command.getInlineKeyboard(client),
                "Объект успешно добавлен!\n*Выберите объект из списка или добавьте новый* (обсудить необходимость этого текста)");
            return;
        }
        answer = "Простите, я не понимаю Вас.\nВозможно, Вам поможет /help";

        logger.info(String.format("Пользователь %s. Завершена обработка сообщения \"%s\", не являющегося командой",
            client.getUserName(), text));
        setAnswer(client, answer);
    }

    /**
     * Отправка ответа с экранной клавиатурой
     *
     * @param client               данные клиента
     * @param inlineKeyboardMarkup экранная клавиатура
     * @param text                 текст ответа
     */
    private void setAnswer(Client client, InlineKeyboardMarkup inlineKeyboardMarkup, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(client.getChatId());
        answer.setReplyMarkup(inlineKeyboardMarkup);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                client.getUserName()));
            e.printStackTrace();
        }
    }

    /**
     * Отправка ответа
     *
     * @param client данные клиента
     * @param text   текст ответа
     */
    private void setAnswer(Client client, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(client.getChatId());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                client.getUserName()));
            e.printStackTrace();
        }
    }

    /**
     * Отправка ответа
     *
     * @param message данные сообщения
     * @param text    текст ответа
     */
    private void setAnswer(Message message, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(message.getChatId().toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Сообщение, не являющееся командой. Пользователь: %s", e.getMessage(),
                message.getFrom()));
            e.printStackTrace();
        }
    }
}
