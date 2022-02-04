package ru.testov.telegram.bot.storage.house.inspection;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class Result {

    @Getter
    @Setter
    private LocalDateTime dateTime;

    @Getter
    @Setter
    private ResultValue resultValue;

    @Getter
    @Setter
    private String comment;

    @Getter
    @Setter
    private String photo;
}
