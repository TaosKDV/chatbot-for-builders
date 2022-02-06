package ru.testov.telegram.bot.storage.house.inspection;

import lombok.Getter;
import lombok.Setter;

public class Step {

    @Getter
    @Setter
    private int id;

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private String pictureOk;

    @Getter
    @Setter
    private String pictureNok;

    @Getter
    @Setter
    private int percent;

    @Getter
    @Setter
    private Result result;
}
