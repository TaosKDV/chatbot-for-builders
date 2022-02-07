package ru.testov.telegram.bot.commands.callback.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.client.Status;
import ru.testov.telegram.bot.commands.keyboard.InlineKeyboardMarkupUtil;
import ru.testov.telegram.bot.commands.keyboard.ReplyKeyboardMarkupUtil;
import ru.testov.telegram.bot.storage.DBJson;
import ru.testov.telegram.bot.storage.house.House;
import ru.testov.telegram.bot.storage.house.HouseStatus;
import ru.testov.telegram.bot.storage.house.inspection.InspectionTypes;
import ru.testov.telegram.bot.storage.house.inspection.Stage;
import ru.testov.telegram.bot.storage.house.inspection.Step;

import static ru.testov.telegram.bot.TextForUser.CREATE_NEW_OBJECT;
import static ru.testov.telegram.bot.TextForUser.SORRY_I_DO_NOT_UNDERSTAND_YOU;
import static ru.testov.telegram.bot.client.Status.ADD_NAME_TO_NEW_HOUSE;
import static ru.testov.telegram.bot.client.Status.AUDIT;
import static ru.testov.telegram.bot.client.Status.CHOICE_OF_STAGE;
import static ru.testov.telegram.bot.client.Status.NEW;
import static ru.testov.telegram.bot.client.Status.STARTED;
import static ru.testov.telegram.bot.commands.SendMessageUtil.getSendMessage;
import static ru.testov.telegram.bot.storage.house.HouseStatus.ADD_STAGE;

public class CallbackQueryHandler {

    private static Logger logger = LoggerFactory.getLogger(CallbackQueryHandler.class);

    private static final int INDEX = 0;//хардкод пока нет выбора вида работ

    public CallbackQueryHandler() {
    }

    public SendMessage processing(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String data = callbackQuery.getData();
        Status status = DBJson.getUserStatus(message.getChat());
        if (status == NEW) {
            return getSendMessage(message, SORRY_I_DO_NOT_UNDERSTAND_YOU);
        }
        Client client = DBJson.findClient(message.getChat());
        if (status == STARTED) {
            return statusProcessingStarted(message, data, client);
        }
        if (status == CHOICE_OF_STAGE) {
            return statusProcessingChoiceOfStage(message, data, client);
        }
        return getSendMessage(message, SORRY_I_DO_NOT_UNDERSTAND_YOU);
    }

    private SendMessage statusProcessingStarted(Message message, String data, Client client) {
        if (data.equals(CREATE_NEW_OBJECT)) {
            logger.info("Запущен процес добавления нового дома пользователю ID - " + client.getChatId()
                + " статус - " + client.getStatus());
            client.setStatus(ADD_NAME_TO_NEW_HOUSE);
            DBJson.saveUser(client);
            return getSendMessage(message,
                "Введите название объекта (пример «ЖК Патриот»)");//todo вынести текста в отделны класс
        }
        House house = client.findHouse(data);
 /*        Путь выбора типа области проверки (фасады/проводка/фундамент и т.д.)
            if (Objects.nonNull(house)) {
                if (house.getHouseStatus() == HouseStatus.NEW) {
                    house.setHouseStatus(ADD_INSPECTION_TYPES);
                    client.setStatus(CHOICE_OF_WORK_TYPE);
                    client.setHouse(house);
                    DBJson.saveUser(client);
                    List<InspectionTypes> insTypesList = DBJson.getInspectionTypesList();
                    List<String[]> list = new ArrayList<>();
                    for (InspectionTypes insTypes : insTypesList) {
                        String[] str = {insTypes.getTypeName(), insTypes.getTypeId()};
                        list.add(str);
                    }
                    return getSendMessage(message, new InlineKeyboardMarkupUtil(list).getInlineKeyboardMarkup(),
                        "Выбран объект \"" + data + "\"");
                }
            }*/
        if (Objects.isNull(house)) {
            return getSendMessage(message, SORRY_I_DO_NOT_UNDERSTAND_YOU);
        }
        if (house.getHouseStatus() == HouseStatus.NEW) {
            logger.info("Запущен процес выбора объекта пользователь ID - " + client.getChatId()
                + " статус - " + client.getStatus());
            InspectionTypes insTypes = DBJson.getInspectionTypesList().get(INDEX);
            List<Stage> stageList = insTypes.getStageList();
            InspectionTypes newInspectionTypes = new InspectionTypes();
            List<InspectionTypes> newInspectionTypesList = new ArrayList<>();
            {
                newInspectionTypes.setTypeName(insTypes.getTypeName());
                newInspectionTypes.setTypeId(insTypes.getTypeId());
                newInspectionTypesList.add(newInspectionTypes);
            }
            house.setHouseStatus(ADD_STAGE);
            house.setInspectionTypesList(newInspectionTypesList);
            client.setStatus(CHOICE_OF_STAGE);
            client.setHouse(house);
            DBJson.saveUser(client);
            List<String[]> list = new ArrayList<>();
            for (Stage stage : stageList) {
                String[] str = {stage.getStageName(), String.valueOf(stage.getStageId())};
                list.add(str);
            }
            return getSendMessage(message, new InlineKeyboardMarkupUtil(list).getInlineKeyboardMarkup(),
                "Выбран объект \"" + data + "\"\nВыбери этап работ для проверки.");
        }
        return getSendMessage(message, SORRY_I_DO_NOT_UNDERSTAND_YOU);
    }

    private SendMessage statusProcessingChoiceOfStage(Message message, String data, Client client) {
        HashMap<String, Integer> activeStep = new HashMap<>();
        logger.info("Запущен процес выбора этапа работ пользователь ID - " + client.getChatId()
            + " статус - " + client.getStatus());
        //ищем дом со статусом ADD_STAGE пользователя
        House house = client.findHouse(ADD_STAGE);
        //берем список типов проверок пользователя
        List<InspectionTypes> inspectionTypesList = house.getInspectionTypesList();
        //сейчас есть один тип, берем его у пользователя
        InspectionTypes inspectionType = inspectionTypesList.get(INDEX);
        activeStep.put("InspectionTypes", inspectionType.getTypeId());
        //получаем templates типа проверки
        InspectionTypes inspectionTypeTemplates = DBJson.getInspectionType(inspectionType);
        //Получаем выбранный этап работ
        Stage stageTemplates = Objects.requireNonNull(inspectionTypeTemplates).getStage(Integer.parseInt(data));
        activeStep.put("Stage", stageTemplates.getStageId());
        //берем список этапов работ пользователя
        List<Stage> stageList = inspectionType.getStageList();
        //если список пуст создаем новый
        if (Objects.isNull(stageList)) {
            stageList = new ArrayList<>();
            stageList.add(stageTemplates);//записываем пользователя сразу весть этап с шагами
            logger.info("Добавлен первый этап работ \"" + stageTemplates.getStageName() + "\" пользователь ID - "
                + client.getChatId());
        } else {
            boolean stageFound = false;
            //ищем выбранный этап работ
            for (Stage st : stageList) {
                if (st.getStageId() == Integer.parseInt(data)) {
                    stageFound = true;
                    break;
                }
            }
            //Если этапа нет добавляем его
            if (!stageFound) {
                stageList.add(stageTemplates);
                logger.info("Добавлен новый этап работ \"" + stageTemplates.getStageName() + "\" пользователь ID - "
                    + client.getChatId());
            }
        }
        //берем первый шаг проверки
        Step step = stageTemplates.getStepList().get(0);
        activeStep.put("Step", step.getId());
        //сохраняем список этапов
        inspectionType.setStageList(stageList);
        inspectionTypesList.set(INDEX, inspectionType);
        house.setHouseStatus(HouseStatus.AUDIT);
        house.setInspectionTypesList(inspectionTypesList);
        client.setStatus(AUDIT);
        client.setHouse(house);
        client.setActiveStep(activeStep);
        DBJson.saveUser(client);
        return getSendMessage(message, new ReplyKeyboardMarkupUtil().getReplyKeyboardMarkup(), step.getText());
    }
}
