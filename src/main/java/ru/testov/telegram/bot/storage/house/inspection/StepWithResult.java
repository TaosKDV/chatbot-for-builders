package ru.testov.telegram.bot.storage.house.inspection;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class StepWithResult extends Step {

    @Getter
    @Setter
    private List<Result> resultList;
}
