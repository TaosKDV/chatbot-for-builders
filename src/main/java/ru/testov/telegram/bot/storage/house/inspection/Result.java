package ru.testov.telegram.bot.storage.house.inspection;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

import static ru.testov.telegram.bot.storage.house.inspection.ResultValue.NOK;
import static ru.testov.telegram.bot.storage.house.inspection.ResultValue.OK;
import static ru.testov.telegram.bot.storage.house.inspection.ResultValue.SKIP;

public class Result {

    @Getter
    @Setter
    private String dateTime;

    @Getter
    @Setter
    private ResultValue resultValue;

    @Getter
    @Setter
    private String comment;

    @Getter
    @Setter
    private String photo;

    public Result(String text) {
        this.dateTime = LocalDateTime.now().toString();
        switch (text) {
            case "✅" -> this.resultValue = OK;
            case "Не осмотрено" -> this.resultValue = SKIP;
            case "❌" -> this.resultValue = NOK;
            default -> throw new RuntimeException();
        }
    }
}
