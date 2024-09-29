package fern.nail.art.nailscheduler.telegram.handler.impl;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import fern.nail.art.nailscheduler.telegram.model.LoginUser;
import fern.nail.art.nailscheduler.telegram.model.RegisterUser;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Service
@RequiredArgsConstructor
public class FirstVisitHandler implements UpdateHandler {
    private final ValidationUtil validationUtil;
    private final MessageService messageService;
    private final UserService userService;

    @Override
    public void handleUpdate(Update update, User user) {
        user = userService.getTempUser(user);

        if (update.getCallbackQuery() != null) {
            handleQuery(update, user);
            return;
        }

        if (user instanceof RegisterUser registerUser && update.getMessage() != null) {
            handleRegistration(update, registerUser);
            return;
        }

        if (user instanceof LoginUser loginUser && update.getMessage() != null) {
            handleAuthorization(update, loginUser);
            return;
        }

        messageService.deleteMessage(user.getTelegramId(), update.getMessage().getMessageId());
        messageService.sendMenu(user.getTelegramId(), "Вы у нас впервые?" + System.lineSeparator()
                + "Выберите действие:", getFirstButtons());
    }

    private void handleQuery(Update update, User user) {
        CallbackQuery query = update.getCallbackQuery();
        switch (query.getData()) {
            case "login" -> {
                userService.saveTempUser(new LoginUser(user));
                messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                        "Введите логин:", getToBeginningButton());
            }
            case "register" -> {
                RegisterUser registerUser = new RegisterUser(user);
                userService.saveTempUser(registerUser);
                String telegramUsername = AbilityUtils.getUser(update).getUserName();
                registerUser.setUsername(telegramUsername);
                Optional<String> violations =
                        validationUtil.findViolationsOf(registerUser, user.getLocale());
                if (violations.isPresent()) {
                    messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                            "Введите логин:", getToBeginningButton());
                } else {
                    messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                            "Использовать для логина \"%s\"".formatted(telegramUsername),
                            getSaveTelegramUsernameButtons());
                }
            }
            case "saveUsername" -> {
                RegisterUser registerUser = (RegisterUser) user;
                registerUser.setUsername(AbilityUtils.getUser(update).getUserName());
                userService.saveTempUser(user);
                messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                        "Логин: %s%sВведите пароль:".formatted(registerUser.getUsername(), System.lineSeparator()), getToBeginningButton()); }
            case "changeUsername" -> messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                    "Введите логин:", getToBeginningButton());
            case "changeFirstName" -> {
                user.setFirstName(null);
                userService.saveTempUser(user);
                messageService.editTextMessage(user.getTelegramId(),
                        query.getMessage().getMessageId(), "Введите имя:");
            }
            case "changeLastName" -> {
                user.setLastName(null);
                userService.saveTempUser(user);
                messageService.editTextMessage(user.getTelegramId(),
                        query.getMessage().getMessageId(), "Введите фамилию:");
            }
            case "registration" -> tryRegistration(user, update);
            case "toStart" -> {
                userService.deleteTempUser(user);
                messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(),
                        "Сделайте выбор:", getFirstButtons());
            }
            default -> messageService.editMenu(user.getTelegramId(), query.getMessage().getMessageId(), "Неизвестная команда. Попробуйте снова:", getFirstButtons());
        }
    }

    private void handleAuthorization(Update update, LoginUser user) {
        if (user.getUsername() == null) {
            setUsername(user, update);
        } else {
            setPassword(user, update);
        }
    }

    private void handleRegistration(Update update, RegisterUser user) {
        if (user.getUsername() == null) {
            setUsername(user, update);
        } else if (user.getPassword() == null) {
            setPassword(user, update);
        } else if (user.getRepeatPassword() == null) {
            setRepeatPassword(user, update);
        } else if (user.getPhone() == null) {
            setPhone(user, update);
        } else if (user.getFirstName() == null) {
            setFirstName(user, update);
        } else {
            setLastName(user, update);
        }
    }

    private static InlineKeyboardMarkup getToBeginningButton() {
        InlineKeyboardButton toBeginningButton =
                InlineKeyboardButton.builder()
                                    .text("Вернуться к началу")
                                    .callbackData("toStart")
                                    .build();
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(toBeginningButton))
                                   .build();
    }

    private InlineKeyboardMarkup getSaveTelegramUsernameButtons() {
        InlineKeyboardButton saveUsername =
                InlineKeyboardButton.builder()
                                    .text("Использовать")
                                    .callbackData("saveUsername")
                                    .build();
        InlineKeyboardButton changeUsername =
                InlineKeyboardButton.builder()
                                    .text("Изменить")
                                    .callbackData("changeUsername")
                                    .build();
        InlineKeyboardButton toBeginningButton =
                InlineKeyboardButton.builder()
                                    .text("Вернуться к началу")
                                    .callbackData("toStart")
                                    .build();
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(saveUsername, changeUsername))
                                   .keyboardRow(new InlineKeyboardRow(toBeginningButton))
                                   .build();
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

    private static InlineKeyboardMarkup getChangeNameButtons(User user) {
        InlineKeyboardButton registerButton =
                InlineKeyboardButton.builder()
                                    .text("Изменить имя" + System.lineSeparator() + '('
                                            + user.getFirstName() + ')')
                                    .callbackData("changeFirstName")
                                    .build();
        InlineKeyboardButton loginButton =
                InlineKeyboardButton.builder()
                                    .text("Изменить фамилию" + System.lineSeparator() + '('
                                            + user.getFirstName() + ')')
                                    .callbackData("changeLastName")
                                    .build();
        InlineKeyboardButton save =
                InlineKeyboardButton.builder()
                                    .text("Сохранить")
                                    .callbackData("registration")
                                    .build();
        InlineKeyboardButton toBeginningButton =
                InlineKeyboardButton.builder()
                                    .text("Вернуться к началу")
                                    .callbackData("toStart")
                                    .build();
        return InlineKeyboardMarkup.builder()
                                   .keyboardRow(new InlineKeyboardRow(registerButton, loginButton))
                                   .keyboardRow(new InlineKeyboardRow(save, toBeginningButton))
                                   .build();
    }

    private void setUsername(LoginUser user, Update update) {
        user.setUsername(update.getMessage().getText());
        Optional<String> violations = validationUtil.findViolationsOf(user, user.getLocale());
        if (violations.isPresent()) {
            messageService.sendMenu(user.getTelegramId(),
                    violations.get() + System.lineSeparator() + "Повторите попытку:",
                    getToBeginningButton());
        } else {
            userService.saveTempUser(user);
            messageService.sendMenu(user.getTelegramId(), "Введите пароль:",
                    getToBeginningButton());
        }
    }

    private void setPassword(LoginUser user, Update update) {
        user.setPassword(update.getMessage().getText());
        messageService.deleteMessage(user.getTelegramId(), update.getMessage().getMessageId());
        Optional<String> violations = validationUtil.findViolationsOf(user, user.getLocale());
        if (violations.isPresent()) {
            messageService.sendMenu(user.getTelegramId(),
                    violations.get() + System.lineSeparator() + "Повторите попытку:",
                    getToBeginningButton());
        } else {
            if (user instanceof RegisterUser registerUser) {
                messageService.sendMenu(user.getTelegramId(), "Пароль принят. Повторите пароль:",
                        getToBeginningButton());
                userService.saveTempUser(registerUser);
            } else {
                boolean isAuthenticated = userService.authenticateUser(user);
                if (isAuthenticated) {
                    InlineKeyboardMarkup mainMenu =
                            new ClientVisitHandler(messageService).getMainMenu();
                    messageService.editMenu(user.getTelegramId(),
                            update.getMessage().getMessageId() - 1,
                            "Здравствуй, %s".formatted(user.getFirstName()), mainMenu);
                } else {
                    messageService.editMenu(user.getTelegramId(), update.getMessage().getMessageId() - 1,
                            "Неправильный пароль или логин. Попробуйте еще раз:",
                            getFirstButtons());
                }
                userService.deleteTempUser(user);
            }
        }
    }

    private void setRepeatPassword(RegisterUser user, Update update) {
        String text = update.getMessage().getText();
        messageService.deleteMessage(user.getTelegramId(), update.getMessage().getMessageId());
        Optional<String> violations = validationUtil.findViolationsOf(user, user.getLocale());
        if (violations.isPresent()) {
            messageService.sendMenu(user.getTelegramId(),
                    violations.get() + System.lineSeparator() + "Повторите попытку:",
                    getToBeginningButton());
        } else {
            user.setRepeatPassword(text);
            userService.saveTempUser(user);
            messageService.editMenu(user.getTelegramId(), update.getMessage().getMessageId() - 1,
                    "Пароль принят." + System.lineSeparator() + "Введите телефон:",
                    getToBeginningButton());
        }
    }

    private void setPhone(RegisterUser user, Update update) {
        user.setPhone(update.getMessage().getText());
        Optional<String> violations = validationUtil.findViolationsOf(user, user.getLocale());
        if (violations.isPresent()) {
            messageService.sendMenu(user.getTelegramId(),
                    violations.get() + System.lineSeparator() + "Повторите попытку:",
                    getToBeginningButton());
        } else {
            userService.saveTempUser(user);
            messageService.sendMenu(user.getTelegramId(),
                    "Поменять имя/фамилию?", getChangeNameButtons(user));
        }
    }

    private void setFirstName(User user, Update update) {
        user.setFirstName(update.getMessage().getText());
        Optional<String> violations = validationUtil.findViolationsOf(user, user.getLocale());
        if (violations.isPresent()) {
            messageService.sendMenu(user.getTelegramId(),
                    violations.get() + System.lineSeparator() + "Повторите попытку:",
                    getChangeNameButtons(user));
        } else {
            messageService.sendMenu(user.getTelegramId(),
                    "Поменять имя/фамилию?", getChangeNameButtons(user));
            userService.saveTempUser(user);
        }
    }

    private void setLastName(User user, Update update) {
        user.setLastName(update.getMessage().getText());
        Optional<String> violations = validationUtil.findViolationsOf(user, user.getLocale());
        if (violations.isPresent()) {
            messageService.sendMenu(user.getTelegramId(),
                    violations.get() + System.lineSeparator() + "Повторите попытку:",
                    getChangeNameButtons(user));
        } else {
            messageService.sendMenu(user.getTelegramId(),
                    "Поменять имя/фамилию?", getChangeNameButtons(user));
            userService.saveTempUser(user);
        }
    }

    private void tryRegistration(User user, Update update) {
        Optional<User> optionalUser = userService.registerUser((RegisterUser) user);
        if (optionalUser.isPresent()) {
            InlineKeyboardMarkup mainMenu =
                    new ClientVisitHandler(messageService).getMainMenu();
            messageService.sendMenu(user.getTelegramId(),
                    "Здравствуй, %s".formatted(user.getFirstName()), mainMenu);
        } else {
            messageService.sendMenu(user.getTelegramId(),
                    "Произошла ошибка. Попробовать еще?", getFirstButtons());
        }
        userService.deleteTempUser(user);
    }
}
