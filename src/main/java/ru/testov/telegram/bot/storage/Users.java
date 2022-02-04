package ru.testov.telegram.bot.storage;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Users {

    @Getter
    @Setter
    private List<User> userList;
}
