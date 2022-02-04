package ru.testov.telegram.bot.client;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.testov.telegram.bot.storage.house.House;


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
    private List<House> houseList;

    public Client(String chatId, String userName, Status status, List<House> houseList) {
        this.chatId = chatId;
        this.userName = userName;
        this.status = status;
        this.houseList = houseList;
    }

    public Client(Chat chat, User user, Status status, List<House> houseList) {
        this(chat.getId().toString(), getUserName(user), status, houseList);
    }

    public String addHouseName(String houseName) {
        logger.info("Добавление пользователю ID - " + chatId + " имя дома \"" + houseName + "\"");
        House house = new House();
        house.setHouseName(houseName);
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
        boolean houseFound = false;
        for (int i = 0; i < this.houseList.size(); i++) {
            House he = this.houseList.get(i);
            if (he.getHouseAddress() == null) {//проверяем наличие адреса, при отсутствии добавляем адрес
                houseFound = true;
                he.setHouseAddress(houseAddress);
                this.houseList.set(i, he);
            }
        }
        if (!houseFound) {
            logger.error("Дом без адреса не найден ID - " + chatId);
            return "Не получилось добавить адрес объекта! Попробуйте воспользоваться командой /start или /addNewHouse";
        }
        return null;
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

    public boolean findHouse(String houseName) {
        for (House house : houseList) {
            if (house.getHouseName().equals(houseName)) {
                return true;
            }
        }
        return false;
    }
}
