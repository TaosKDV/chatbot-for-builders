package ru.testov.telegram.bot.storage.house.inspection;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Stage {

    @Getter
    @Setter
    private String stageName;

    @Getter
    @Setter
    private int stageId;

    @Getter
    @Setter
    private String stagePercent;

    @Getter
    @Setter
    private List<Step> stepList;

    public Step getStep(int id) {
        for (Step step : stepList) {
            if (step.getId() == id) {
                return step;
            }
        }
        return null;
    }
}
