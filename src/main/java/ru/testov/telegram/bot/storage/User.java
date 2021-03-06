package ru.testov.telegram.bot.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

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

    @Setter
    private List<House> houseList;

    public List<House> getHouseList() {
        return Objects.requireNonNullElseGet(this.houseList, ArrayList::new);
    }

    User(String chatId, String userName, String status, List<House> houseList) {
        this.userName = userName;
        this.chatId = chatId;
        this.status = status;
        this.houseList = houseList;
    }
}
