package ru.testov.telegram.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.commands.Command;
import ru.testov.telegram.bot.commands.callback.query.CallbackQueryHandler;
import ru.testov.telegram.bot.commands.keyboard.InlineKeyboardMarkupUtil;
import ru.testov.telegram.bot.commands.service.StartCommand;
import ru.testov.telegram.bot.storage.DBJson;
import ru.testov.telegram.bot.storage.house.House;
import ru.testov.telegram.bot.storage.house.HouseStatus;
import ru.testov.telegram.bot.storage.house.inspection.Result;
import ru.testov.telegram.bot.storage.house.inspection.Stage;

import static ru.testov.telegram.bot.client.Status.ADD_ADDRESS_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.ADD_NAME_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.AUDIT;
import static ru.testov.telegram.bot.client.Status.CHOICE_OF_STAGE;
import static ru.testov.telegram.bot.client.Status.STARTED;
import static ru.testov.telegram.bot.storage.house.HouseStatus.ADD_STAGE;

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
            setAnswer(handler.processing(update.getCallbackQuery()));
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
            setAnswer(client, "Введите адрес объекта:");
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
            setAnswer(client, Command.getHouseListKey(client),
                "Объект успешно добавлен!\nТеперь нажми на него для начала проверки.");
            return;
        }
        if (client.getStatus() == AUDIT) {
            auditProcessing(client, text);
            return;
        }
        answer = "Простите, я не понимаю Вас.\nВозможно, Вам поможет /help";
        logger.info(String.format("Пользователь %s. Завершена обработка сообщения \"%s\", не являющегося командой",
            client.getUserName(), text));
        setAnswer(client, answer);
    }

    private void auditProcessing(Client client, String text) {
        House house = client.findHouse(HouseStatus.AUDIT);
        Map<String, Integer> activeStep = client.getActiveStep();
        Result result = new Result(text);
        //todo сделать разветвление в случае не успешного результата
        int stepId = activeStep.get("Step");
        house.getInspectionTypesList().get(activeStep.get("InspectionTypes"))
            .getStageList().get(activeStep.get("Stage"))
            .getStepList().get(stepId)
            .setResult(result);
        int stepListSize = house.getInspectionTypesList().get(activeStep.get("InspectionTypes"))
            .getStageList().get(activeStep.get("Stage"))
            .getStepList().size();
        if (stepListSize > stepId) {
            activeStep.replace("Step", stepId, stepId + 1);
        }
        //если равны, значит был последний шаг проверки
        if (stepListSize == activeStep.get("Step")) {
            List<Stage> stageList = Objects.requireNonNull(DBJson.getInspectionType(house.getInspectionTypesList()
                .get(activeStep.get("InspectionTypes")))).getStageList();//берем список этапов текущего типа работ
            List<String[]> list = new ArrayList<>();
            for (Stage stage : stageList) {
                String[] str = {stage.getStageName(), String.valueOf(stage.getStageId())};
                list.add(str);
            }
            house.setHouseStatus(ADD_STAGE);
            client.setHouse(house);
            client.setActiveStep(null);
            client.setStatus(CHOICE_OF_STAGE);
            DBJson.saveUser(client);
            setAnswerAndKeyboardRemove(client, "Проверка успешно завершена!");
            setAnswer(client, new InlineKeyboardMarkupUtil(list).getInlineKeyboardMarkup(),
                "Выбери следующий этап работ.");
        } else {
            client.setHouse(house);
            client.setActiveStep(activeStep);
            DBJson.saveUser(client);
            String answer = house.getInspectionTypesList().get(activeStep.get("InspectionTypes"))
                .getStageList().get(activeStep.get("Stage"))
                .getStepList().get(activeStep.get("Step")).getText();
            setAnswer(client, answer);
        }
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
     * Отправка ответа и удаление кастомной клавиатуры
     *
     * @param client данные клиента
     * @param text   текст ответа
     */
    private void setAnswerAndKeyboardRemove(Client client, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(client.getChatId());
        answer.setReplyMarkup(new ReplyKeyboardRemove(true));//очистак клавиатуры
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

    /**
     * Отправка ответа
     *
     * @param answer сообщение
     */
    private void setAnswer(SendMessage answer) {
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            logger.error(String.format("Ошибка %s. Сообщение, не являющееся командой.", e.getMessage()));
            e.printStackTrace();
        }
    }
}
