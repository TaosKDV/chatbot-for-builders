package ru.testov.telegram.bot.client;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.testov.telegram.bot.storage.house.House;
import ru.testov.telegram.bot.storage.house.HouseStatus;
import ru.testov.telegram.bot.storage.house.inspection.Step;

import static ru.testov.telegram.bot.storage.house.HouseStatus.ADD_ADDRESS;
import static ru.testov.telegram.bot.storage.house.HouseStatus.NEW;


public class Client {

    private Logger logger = LoggerFactory.getLogger(Client.class);

    @Getter
    @Setter
    private String userName;

    @Setter
    private String chatId;

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    private Map<String, Integer> activeStep;

    @Getter
    @Setter
    private List<House> houseList;

    public Client(String chatId, String userName, Status status, List<House> houseList, Map<String, Integer> activeStep) {
        this.chatId = chatId;
        this.userName = userName;
        this.status = status;
        this.houseList = houseList;
        this.activeStep = activeStep;
    }

    public Client(Chat chat, User user, Status status, List<House> houseList, Map<String, Integer> activeStep) {
        this(chat.getId().toString(), getUserName(user), status, houseList, activeStep);
    }

    public String addHouseName(String houseName) {
        logger.info("Добавление пользователю ID - " + chatId + " имя дома \"" + houseName + "\"");
        House house = new House();
        house.setHouseName(houseName);
        house.setHouseStatus(ADD_ADDRESS);
        for (House he : this.houseList) {//проверяем уникальность названия
            if (he.getHouseName().equals(house.getHouseName())) {
                return "Объект с таким названием уже существует";
            }
        }
        this.houseList.add(house);
        return null;
    }

    public String addHouseAddress(String houseAddress) {
        logger.info("Добавление пользователю ID - " + chatId + " адреса дома \"" + houseAddress + "\"");
        for (int i = 0; i < this.houseList.size(); i++) {
            House he = this.houseList.get(i);
            //проверяем отсутствие адреса и статус дома
            if (he.getHouseAddress() == null && he.getHouseStatus() == ADD_ADDRESS) {
                he.setHouseAddress(houseAddress);
                he.setHouseStatus(NEW);
                this.houseList.set(i, he);
                return null;
            }
        }
        logger.error("Дом без адреса не найден ID - " + chatId);
        return "Не получилось добавить адрес объекта! Попробуйте воспользоваться командой /start или /addNewHouse";
    }


    /**
     * Формирование имени пользователя. Если заполнен никнейм, используем его. Если нет - используем фамилию и имя
     *
     * @param user пользователь
     */
    public static String getUserName(User user) {
        String name = "";
        if (user.getUserName() != null) {
            return user.getUserName();
        }
        if (user.getLastName() != null) {
            name = user.getLastName();
        }
        name += user.getFirstName();
        return name;
    }

    /**
     * Формирование имени пользователя
     *
     * @param msg сообщение
     */
    public static String getUserName(Message msg) {
        return getUserName(msg.getFrom());
    }

    /**
     * Формирование id пользователя
     *
     * @param msg сообщение
     */
    public static String getChatId(Message msg) {
        return msg.getChat().getId().toString();
    }

    public String getChatId() {
        return this.chatId;
    }

    public House findHouse(String houseName) {
        for (House house : houseList) {
            if (house.getHouseName().equals(houseName)) {
                return house;
            }
        }
        return null;
    }

    public House findHouse(HouseStatus houseStatus) {
        for (House house : houseList) {
            if (house.getHouseStatus() == houseStatus) {
                return house;
            }
        }
        return null;
    }

    public void setHouse(House house) {
        this.houseList.set(this.houseList.indexOf(house), house);
    }
}
