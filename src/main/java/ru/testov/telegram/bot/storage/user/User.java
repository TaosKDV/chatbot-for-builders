package ru.testov.telegram.bot.storage.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import ru.testov.telegram.bot.storage.house.House;

public class User {

    @Getter
    @Setter
    private String userName;

    @Getter
    @Setter
    private String chatId;

    @Getter
    @Setter
    private String status;

    @Getter
    @Setter
    private Map<String, Integer> activeStep;

    @Setter
    private List<House> houseList;

    public List<House> getHouseList() {
        return Objects.requireNonNullElseGet(this.houseList, ArrayList::new);
    }

    public User(String chatId, String userName, String status, List<House> houseList) {
        this.userName = userName;
        this.chatId = chatId;
        this.status = status;
        this.houseList = houseList;
    }
}
