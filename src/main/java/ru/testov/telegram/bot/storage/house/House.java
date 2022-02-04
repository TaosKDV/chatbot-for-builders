package ru.testov.telegram.bot.storage.house;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class House {

    @Getter
    @Setter
    private String houseName;

    @Getter
    @Setter
    private String houseAddress;

    public static String getHouseListString(List<House> houseList) {
        List<String> stringList = new ArrayList<>();
        if (houseList == null) {
            return "";
        }
        for (House house : houseList) {
            String sb = "{\"houseName\":\"" + house.getHouseName()
                + "\",\"houseAddress\":\"" + house.getHouseAddress() + "\"}";
            stringList.add(sb);
        }
        return String.join(",", stringList);
    }
}
