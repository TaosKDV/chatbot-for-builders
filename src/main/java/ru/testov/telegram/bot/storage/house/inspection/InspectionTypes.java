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
    private String typeId;


    @Getter
    @Setter
    private List<Stage> stageList;
}
