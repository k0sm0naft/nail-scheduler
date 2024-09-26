package fern.nail.art.nailscheduler.telegram.handler.impl;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import fern.nail.art.nailscheduler.telegram.model.LoginUser;
import fern.nail.art.nailscheduler.telegram.model.RegisterUser;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Service
@RequiredArgsConstructor
public class FirstVisitHandler implements UpdateHandler {
    private final MessageService messageService;
    private final UserService userService;

    @Override
    public void handleUpdate(Update update, User user) {
        CallbackQuery query = update.getCallbackQuery();
        Message message = update.getMessage();
        user = userService.getTempUser(user);

        if (user instanceof RegisterUser registerUser && registerUser.getPassword() != null) {
            handleRegistration(update, registerUser);
            return;
        }

        if (user instanceof LoginUser loginUser && message != null) {
            handleAuthorization(update, loginUser);
            return;
        }


        if (query == null) {
            messageService.sendMenu(user.getTelegramId(), "Вы у нас впервые?\n"
                    + "Выберите действие:", getFirstButtons());
            return;
        }

        String command = query.getData();
        switch (command) {
            case "login" -> {
                userService.saveTempUser(new LoginUser(user));
                messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                        "Введите ваш логин:", null);
            }
            case "register" -> {
                userService.saveTempUser(new RegisterUser(user));
                messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                        "Введите ваш логин:", null);
            }
            default -> throw new RuntimeException("Unknown command");
        }
    }

    private void handleAuthorization(Update update, LoginUser loginUser) {
        if (loginUser.getUsername() == null) {
            setUsername(loginUser, update);
        } else if (loginUser.getPassword() == null) {
            setPassword(loginUser, update);
        }
    }

    private void handleRegistration(Update update, RegisterUser registerUser) {
        if (registerUser.getRepeatPassword() == null) {
            setRepeatPassword(registerUser, update);
        } else if (registerUser.getPhone() == null) {
            setPhone(registerUser, update);
//        } else if (registerUser.getFirstName() == null) {
//            setFirstName(registerUser, update);
//        } else if (registerUser.getLastName() == null) {
//            setLastName(registerUser, update);
        }
    }


    //todo ask abaut fiirst name and last name (leave or change)
    private void setFirstName(RegisterUser registerUser, Update update) {

    }

    private void setLastName(RegisterUser registerUser, Update update) {

    }

    private static InlineKeyboardMarkup getFirstButtons() {
        InlineKeyboardButton registerButton =
                InlineKeyboardButton.builder()
                                    .text("Зарегистрироваться")
                                    .callbackData("register")
                                    .build();
        InlineKeyboardButton loginButton =
                InlineKeyboardButton.builder()
                                    .text("Войти")
                                    .callbackData("login")
                                    .build();
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(registerButton, loginButton))
                                   .build();
    }

    private void setUsername(LoginUser user, Update update) {
        user.setUsername(update.getMessage().getText());
        userService.saveTempUser(user);
        messageService.sendText(user.getTelegramId(), "Введите пароль:");
    }

    private void setPassword(LoginUser user, Update update) {
        user.setPassword(update.getMessage().getText());
        messageService.deleteMessage(user.getTelegramId(), update.getMessage().getMessageId());
        if (user instanceof RegisterUser registerUser) {
            messageService.sendText(user.getTelegramId(),"Пароль принят. Повторите пароль:");
            userService.saveTempUser(registerUser);
        } else {
            boolean isAuthenticated = userService.authenticateUser(user);
            if (isAuthenticated) {
                InlineKeyboardMarkup mainMenu =
                        new ClientVisitHandler(messageService).getMainMenu();
                messageService.editMenu(user.getTelegramId(), update.getMessage().getMessageId() - 1,
                        "Здравствуй, %s".formatted(user.getFirstName()), mainMenu);
            } else {
                messageService.sendMenu(user.getTelegramId(), "Неправильный пароль или логин. Попробуйте еще раз",
                        getFirstButtons());
                userService.deleteTempUser(user);
            }
        }
    }

    private void setRepeatPassword(RegisterUser user, Update update) {
        String text = update.getMessage().getText();
        messageService.deleteMessage(user.getTelegramId(), update.getMessage().getMessageId());
        if (user.getPassword().equals(text)) {
            messageService.sendText(user.getTelegramId(), "Пароль принят.");
            user.setRepeatPassword(text);
            userService.saveTempUser(user);
            messageService.sendText(user.getTelegramId(), "Введите телефон:");
        } else {
            messageService.sendText(user.getTelegramId(), "Пароли не совпадают. Повторите попытку:");
            user.setPassword(null);
            userService.saveTempUser(user);
        }
    }

    private void setPhone(RegisterUser user, Update update) {
        user.setPhone(update.getMessage().getText());
        Optional<User> optionalUser = userService.registerUser(user);
        if (optionalUser.isPresent()) {
            messageService.sendMenu(user.getTelegramId(), "Поменять имя/фамилию?", new ClientVisitHandler(messageService).getMainMenu());
        } else {
            messageService.sendMenu(user.getTelegramId(), "Ошибка должна прийти предыдущим сообщением",
                    getFirstButtons());
            userService.deleteTempUser(user);
        }
    }
}
