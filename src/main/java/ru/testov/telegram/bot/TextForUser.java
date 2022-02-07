package ru.testov.telegram.bot;

public class TextForUser {

    public static final String SORRY_I_DO_NOT_UNDERSTAND_YOU = "Простите, я не понимаю Вас.\nВозможно, Вам поможет /help";

    public static final String CREATE_NEW_OBJECT = "Создать новый объект";

    public static String helloNewUser(String name) {
        return """
            Привет, ${name}!
            Я помогу тебе оценить устройство систем утепления фасадов!
            Для начала работы давай заведем объект, подлежащий оценке.
                        
            Введите название объекта (пример «ЖК Патриот»)"""
            .replace("${name}", name);
    }

    public static String helloOldUser(String name) {
        return """
            Привет, ${name}!
            Выбери объект для проверки или создай новый."""
            .replace("${name}", name);
    }

}
