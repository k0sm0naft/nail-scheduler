package fern.nail.art.nailscheduler.telegram.handler.impl;

import static java.lang.System.lineSeparator;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import fern.nail.art.nailscheduler.telegram.model.LoginUser;
import fern.nail.art.nailscheduler.telegram.model.RegisterUser;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.utils.Command;
import fern.nail.art.nailscheduler.telegram.utils.ValidationUtil;
import fern.nail.art.nailscheduler.telegram.utils.menu.AuthorizationMenuUtil;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
@RequiredArgsConstructor
public class FirstVisitHandler implements UpdateHandler {
    private static final String CHOSE_OPTION = "message.chose.option";
    private static final String USE_FOR_LOGIN = "message.use.for.login";
    private static final String ENTER_LOGIN = "message.enter.login";
    private static final String ENTER_PASSWORD = "message.enter.password";
    private static final String ENTER_PHONE = "message.enter.phone";
    private static final String ENTER_FIRST_NAME = "message.enter.first.name";
    private static final String ENTER_LAST_NAME = "message.enter.last.name";
    private static final String PASSWORD_ACCEPTED = "message.password.accepted";
    private static final String REPEAT_PASSWORD = "message.repeat.password";
    private static final String REPEAT = "message.repeat";
    private static final String CHANGE_NAMES = "message.change.names";
    private static final String WRONG_CREDENTIALS = "message.wrong.credentials";
    private static final String UNKNOWN_COMMAND = "message.unknown.command";
    private static final String MISTAKE_OCCURS = "message.mistake.occurs";
    private static final String HELLO = "message.hello";
    private static final String VALUE_MISSING = "---";
    private static final String START_COMMAND = "/start";
    private final AuthorizationMenuUtil menu;
    private final ValidationUtil validationUtil;
    private final MessageService messageService;
    private final UserService userService;
    private final LocalizationService localizationService;

    @Override
    public void handleUpdate(Update update, User user) {
        CallbackQuery query = update.getCallbackQuery();
        Message message = update.getMessage();
        user = userService.getTempUser(user);

        if (query != null) {
            handleQuery(query, user);
            return;
        }

        if (message != null) {
            if (message.getText() != null && message.getText().startsWith(START_COMMAND)) {
                handleStart(user, message);
            } else {
                handleMessage(message, user);
            }
            messageService.deleteMessage(user, message.getMessageId());
        }
    }

    private void handleMessage(Message message, User user) {
        if (user instanceof RegisterUser registerUser && registerUser.getPassword() != null) {
            handleRegistration(message, registerUser);
            return;
        }

        if (user instanceof LoginUser loginUser) {
            handleAuthorization(message, loginUser);
        }
    }

    private void handleStart(User user, MaybeInaccessibleMessage message) {
        String text = getStartText(user);
        InlineKeyboardMarkup authMenu = menu.registration(user.getLocale());
        messageService.sendMenu(user, text, authMenu);
    }

    private String getStartText(User user) {
        return localizationService.localize(
                localizationService.localize(CHOSE_OPTION, user.getLocale()), user.getLocale());
    }

    private void handleQuery(CallbackQuery query, User user) {
        String text;
        Integer messageId = query.getMessage().getMessageId();
        Locale locale = user.getLocale();
        user.setMenuId(messageId);

        switch (Command.fromString(query.getData())) {
            case MAIN -> {
                messageService.editMenu(user, messageId, getStartText(user),
                        menu.registration(user.getLocale()));
            }

            case LOGIN -> {
                user = new LoginUser(user);
                editMessageOnEnterLogin(user);
            }

            case REGISTER -> {
                user = new RegisterUser(user);
                handleRegister(query, (RegisterUser) user);
            }

            case SAVE_USERNAME -> handleSaveUsername((RegisterUser) user, query);

            case CHANGE_USERNAME -> {
                ((RegisterUser) user).setUsername(null);
                editMessageOnEnterLogin(user);
            }

            case CHANGE_FIRST_NAME -> {
                user.setFirstName(null);
                text = localizationService.localize(ENTER_FIRST_NAME, locale);
                messageService.editTextMessage(user, messageId, text);
            }

            case CHANGE_LAST_NAME -> {
                user.setLastName(null);
                text = localizationService.localize(ENTER_LAST_NAME, locale);
                messageService.editTextMessage(user, messageId, text);
            }

            case REGISTRATION -> tryRegistration(user);

            default -> {
                text = localizationService.localize(UNKNOWN_COMMAND, locale)
                        + localizationService.localize(REPEAT, locale);
                messageService.editMenu(user, messageId, text, menu.registration(locale));
            }
        }

        userService.saveTempUser(user);
    }

    private void handleAuthorization(Message message, LoginUser user) {
        if (user.getUsername() == null) {
            setUsername(user, message);
        } else if (user.getPassword() == null) {
            setPassword(user, message);
        }
    }

    private void handleRegistration(Message message, RegisterUser user) {
        if (user.getRepeatPassword() == null) {
            setRepeatPassword(user, message);
        } else if (user.getPhone() == null) {
            setPhone(user, message);
        } else if (user.getFirstName() == null) {
            setFirstName(user, message);
        } else if (user.getLastName() == null) {
            setLastName(user, message);
        }
    }

    private void handleRegister(CallbackQuery query, RegisterUser user) {
        String username = query.getFrom().getUserName();
        Locale locale = user.getLocale();

        user.setUsername(username);
        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            editMessageOnEnterLogin(user);
        } else {
            messageService.editMenu(user,
                    user.getMenuId(), localizationService.localize(USE_FOR_LOGIN, locale)
                                                  .formatted(username), menu.saveUsername(locale));
        }
    }

    private void handleSaveUsername(RegisterUser user, CallbackQuery query) {
        Integer messageId = query.getMessage().getMessageId();
        Locale locale = user.getLocale();

        String text = localizationService.localize(ENTER_PASSWORD, locale);
        messageService.editMenu(user, messageId, text, menu.beckToMainButton(locale));
    }

    private void setUsername(LoginUser user, Message message) {
        String text;
        Locale locale = user.getLocale();

        user.setUsername(message.getText());

        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            text = violations.get() + lineSeparator()
                    + localizationService.localize(REPEAT, locale);
            messageService.editMenu(user, user.getMenuId(), text, menu.beckToMainButton(locale));
        } else {
            text = localizationService.localize(ENTER_PASSWORD, locale);
            messageService.editMenu(user, user.getMenuId(), text, menu.beckToMainButton(locale));
            userService.saveTempUser(user);
        }
    }

    private void setPassword(LoginUser user, Message message) {
        String text;
        Locale locale = user.getLocale();
        Integer messageId = user.getMenuId();

        user.setPassword(message.getText());

        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            text = violations.get() + lineSeparator()
                    + localizationService.localize(REPEAT, locale);
            messageService.editMenu(user, messageId, text, menu.beckToMainButton(locale));
        } else {
            if (user instanceof RegisterUser registerUser) {
                text = localizationService.localize(PASSWORD_ACCEPTED, locale)
                        + localizationService.localize(REPEAT_PASSWORD, locale);
                messageService.editMenu(user, messageId, text, menu.beckToMainButton(locale));
                userService.saveTempUser(registerUser);
            } else {
                boolean isAuthenticated = userService.authenticateUser(user);
                if (isAuthenticated) {
                    text = localizationService.localize(HELLO, locale)
                                              .formatted(user.getFirstName());
                    messageService.editMenu(user, messageId, text,
                            menu.beckToMainButton(locale));
                } else {
                    text = localizationService.localize(WRONG_CREDENTIALS, locale)
                            + localizationService.localize(REPEAT, locale);
                    messageService.editMenu(user, messageId, text,
                            menu.registration(locale));
                }
                userService.deleteTempUser(user);
            }
        }
    }

    private void setRepeatPassword(RegisterUser user, Message message) {
        String text;
        Locale locale = user.getLocale();
        Integer messageId = user.getMenuId();
        user.setRepeatPassword(message.getText());

        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            user.setPassword(null);
            user.setRepeatPassword(null);
            text = violations.get() + lineSeparator()
                     + localizationService.localize(REPEAT, locale);
            messageService.editMenu(user, messageId, text, menu.beckToMainButton(locale));
        } else {
            text = localizationService.localize(PASSWORD_ACCEPTED, locale)
                    + lineSeparator() + localizationService.localize(ENTER_PHONE, locale);
            messageService.editMenu(user, messageId, text, menu.beckToMainButton(locale));
        }
        userService.saveTempUser(user);
    }

    private void setPhone(RegisterUser user, Message message) {
        user.setPhone(message.getText());
        handleLastStage(user, menu.beckToMainButton(user.getLocale()));
    }

    private void setFirstName(User user, Message message) {
        user.setFirstName(message.getText());
        handleLastStage(user, menu.changeName(user.getLocale()));
    }

    private void setLastName(User user, Message message) {
        user.setLastName(message.getText());
        handleLastStage(user, menu.changeName(user.getLocale()));
    }

    private void handleLastStage(User user, InlineKeyboardMarkup failMenu) {
        String text;
        Locale locale = user.getLocale();

        Optional<String> violations = validationUtil.findViolationsOf(user, locale);
        if (violations.isPresent()) {
            text = violations.get() + lineSeparator()
                    + localizationService.localize(REPEAT, locale);
            messageService.editMenu(user, user.getMenuId(), text, failMenu);
        } else {
            userService.saveTempUser(user);
            String lastName = user.getLastName() == null ? VALUE_MISSING : user.getLastName();
            text = localizationService.localize(CHANGE_NAMES, locale)
                                      .formatted(lineSeparator(), user.getFirstName(), lastName);
            messageService.editMenu(user, user.getMenuId(), text, menu.changeName(locale));
        }
    }

    private void tryRegistration(User user) {
        String text;
        Locale locale = user.getLocale();

        Optional<User> optionalUser = userService.registerUser((RegisterUser) user);
        if (optionalUser.isPresent()) {
            text = localizationService.localize(HELLO, locale).formatted(user.getFirstName());
            messageService.editMenu(user, user.getMenuId(), text, menu.beckToMainButton(locale));
        } else {
            text = localizationService.localize(MISTAKE_OCCURS, locale)
                    + localizationService.localize(REPEAT, locale);
            messageService.editMenu(user, user.getMenuId(), text, menu.registration(locale));
        }
        userService.deleteTempUser(user);
    }

    private void editMessageOnEnterLogin(User user) {
        Locale locale = user.getLocale();
        messageService.editMenu(user, user.getMenuId(),
                localizationService.localize(ENTER_LOGIN, locale), menu.beckToMainButton(locale));
    }
}
