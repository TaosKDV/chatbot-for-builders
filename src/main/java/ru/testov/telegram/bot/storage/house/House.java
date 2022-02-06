package ru.testov.telegram.bot.storage.house;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.testov.telegram.bot.storage.house.inspection.InspectionTypes;

public class House {

    @Getter
    @Setter
    private String houseName;

    @Getter
    @Setter
    private String houseAddress;

    @Getter
    @Setter
    private List<InspectionTypes> inspectionTypesList;

    @Getter
    @Setter
    private HouseStatus houseStatus;

    public InspectionTypes getInspectionTypes(int typeId) {
        for (InspectionTypes inspectionTypes : inspectionTypesList) {
            if(inspectionTypes.getTypeId() == typeId){
                return inspectionTypes;
            }
        }
        return null;
    }


    public static String getHouseListString(List<House> houseList) {
        List<String> stringList = new ArrayList<>();
        if (houseList == null) {
            return "";
        }
        for (House house : houseList) {
            String sb = "{\"houseName\":\"" + house.getHouseName()
                + "\",\"houseAddress\":\"" + house.getHouseAddress()
                + "\",\"houseStatus\":\"" + house.getHouseStatus() + "\"}";
            stringList.add(sb);
        }
        return String.join(",", stringList);
    }
}
