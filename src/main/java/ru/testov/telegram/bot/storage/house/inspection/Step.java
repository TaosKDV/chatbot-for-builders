package ru.testov.telegram.bot.storage.house.inspection;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Step {

    @Getter
    @Setter
    private String id;

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
    private List<Result> resultList;
}
