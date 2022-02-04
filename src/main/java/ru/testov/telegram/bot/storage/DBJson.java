package ru.testov.telegram.bot.storage;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.testov.telegram.bot.client.Client;
import ru.testov.telegram.bot.client.Status;

import static ru.testov.telegram.bot.client.Status.NEW;

public class DBJson {

    private static Logger logger = LoggerFactory.getLogger(DBJson.class);

    private static final String USER_FILE = "src/main/resources/clients.json";

    public static Client findClient(Message message) {
        logger.info("Ищем пользователя через объект \"message\"");
        return findClient(Client.getChatId(message), Client.getUserName(message));
    }

    public static Client findClient(Chat chat, org.telegram.telegrambots.meta.api.objects.User user) {
        logger.info("Ищем пользователя через объекты \"Chat\" и \"User\"");
        return findClient(chat.getId().toString(), Client.getUserName(user));
    }

    public static Client findClient(Chat chat) {
        if (getUserStatus(chat) == NEW) {
            //todo придумать как ругнуться
        }
        return findClient(chat.getId().toString(), "");
    }

    //не далать публичным, использовать перегрузку метода
    private static Client findClient(String chatId, String userName) {
        logger.info("Ищем пользователя ID - " + chatId);
        Status status = getUserStatus(chatId);
        if (status == NEW) {
            logger.info("Сохраняем нового пользователя ID - " + chatId + " Статус - " + status);
            saveUser(chatId, userName, status, null);
            return new Client(chatId, userName, status, null);
        } else {
            logger.info("Берем пользователя из файла ID - " + chatId + " Статус - " + status);
            try (FileReader reader = new FileReader(USER_FILE)) {
                Users users = new Gson().fromJson(reader, Users.class);
                List<User> userList = users.getUserList();
                for (User user : userList) {
                    if (user.getChatId().equals(chatId)) {
                        return new Client(user.getChatId(), user.getUserName(),
                            Status.valueOf(user.getStatus()), user.getHouseList());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;//такого быть никогда не должно ищи косяк!
    }

    public static void saveUser(Client client) {
        Objects.nonNull(client.getStatus());
        saveUser(client.getChatId(), client.getUserName(), client.getStatus(), client.getHouseList());
    }

    //не далать публичным, использовать перегрузку метода
    private static void saveUser(String chatId, String userName, Status status, List<House> houseList) {
        logger.info("Сохранение пользователя ID - " + chatId + " Статус - " + status);
        Gson gson = new Gson();
        Users users = null;
        try (FileReader reader = new FileReader(USER_FILE)) {
            users = gson.fromJson(reader, Users.class);//беем из файла пользователей
            List<User> userList = users.getUserList();
            boolean userFound = false;
            for (int i = 0; i < userList.size(); i++) {
                User use = userList.get(i);
                if (use.getChatId().equals(chatId)) {
                    logger.info("Пользователь найден ID - " + chatId);
                    userFound = true;
                    logger.info("Обновляем статус с \"" + use.getStatus() + "\" на \"" + status + "\"");
                    use.setStatus(status.toString());//обновляем статус
                    logger.info("Обновляем список домов с [" + House.getHouseListString(use.getHouseList())
                        + "] на [" + House.getHouseListString(houseList) + "]");
                    use.setHouseList(houseList);//обновляем список домов
                    userList.set(i, use);//заменяем
                }
            }
            if (!userFound) {
                logger.info("Добавляем нового пользователя ID - " + chatId + " Статус - " + status);
                userList.add(new User(chatId, userName, status.toString(), houseList));//добавляем новый
            }
            users.setUserList(userList);
        } catch (FileNotFoundException e) {
            logger.info("Поймали FileNotFoundException создаем файл с пользователями!", e);
            createFileWithClients(new User(chatId, userName, status.toString(), houseList));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (users != null) {
            writer(users);
            logger.info("Сохранение данных пользователя завершено");
        }
    }

    public static Status getUserStatus(Chat chat) {
        return getUserStatus(chat.getId().toString());
    }

    private static Status getUserStatus(String chatId) {
        logger.info("Получаем статус по ID - " + chatId);
        try (FileReader reader = new FileReader(USER_FILE)) {
            Users users = new Gson().fromJson(reader, Users.class);
            List<User> userList = users.getUserList();
            for (User user : userList) {
                if (user.getChatId().equals(chatId)) {
                    return Status.valueOf(user.getStatus());
                }
            }
        } catch (Exception e) {
            logger.info("Поймали Exception ID - " + chatId + " Статус - " + NEW);
            return NEW;
        }
        logger.info("Ничего не нашли ID - " + chatId + " Статус - " + NEW);
        return NEW;
    }

    private static void createFileWithClients(User user) {
        logger.info("Начинаем создание файла с пользователями");
        Users users = new Users();
        users.setUserList(Collections.singletonList(user));
        writer(users);
        if (!new File(USER_FILE).exists()) {
            logger.error("Хмм файла нет( что-то явно идет не так!");
        }
        logger.info("Ура первый пользователь добавлен!");
    }

    private static void writer(Users users) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(USER_FILE)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
