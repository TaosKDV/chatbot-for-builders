package ru.testov.telegram.bot.storage.house.inspection;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class InspectionTypes {

    @Getter
    @Setter
    private String typeName;

    @Getter
    @Setter
    private int typeId;


    @Getter
    @Setter
    private List<Stage> stageList;

    /***
     *
     * @param stageId ID этапа работ
     * @return
     */
    public Stage getStage(int stageId) {
        for (Stage stage : stageList) {
            if (stage.getStageId() == stageId) {
                return stage;
            }
        }
        return null;
    }
}
