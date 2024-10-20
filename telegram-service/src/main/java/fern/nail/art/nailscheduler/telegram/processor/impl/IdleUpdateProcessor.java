package fern.nail.art.nailscheduler.telegram.processor.impl;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.APPOINTMENTS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.LOGIN;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.REGISTRATION;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.SETTINGS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.SLOTS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.USERS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.WORKDAYS;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.CHOSE_OPTION;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.HELLO;

import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class IdleUpdateProcessor implements UpdateProcessor {
    private final ApplicationEventPublisher eventPublisher;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final MessageService messageService;
    private final UserService userService;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.IDLE
                && user.getLocalState() == null;
    }

    @Override
    public void process(Update update, User user) {
        switch (user.getRole()) {
            case CLIENT -> sendClientMenu(user);
            case MASTER -> sendMasterMenu(user);
            case UNKNOWN -> sendAuthMenu(user);
            default -> throw new IllegalStateException("Unexpected role: " + user.getRole());
        }
    }

    private void sendClientMenu(User user) {
        user.setGlobalState(GlobalState.CLIENT_MENU);
        //todo put buttons
        sendMenu(user, null);
    }

    private void sendMasterMenu(User user) {
        user.setGlobalState(GlobalState.MASTER_MENU);
        Locale locale = user.getLocale();
        List<ButtonType> buttons = List.of(USERS, SLOTS, APPOINTMENTS, WORKDAYS, SETTINGS);
        InlineKeyboardMarkup markup = markupFactory.create(buttons, locale);

        sendMenu(user, markup);
    }

    private void sendAuthMenu(User user) {
        user.setGlobalState(GlobalState.AUTHENTICATION);
        Locale locale = user.getLocale();
        InlineKeyboardMarkup markup = markupFactory.create(List.of(REGISTRATION, LOGIN), locale);

        sendMenu(user, markup);
    }

    private void sendMenu(User user, InlineKeyboardMarkup markup) {
        String text = localizationService.localize(List.of(HELLO, CHOSE_OPTION), user.getLocale())
                                         .formatted(user.getFirstName());

        if (user.getMenuId() != null) {
            messageService.deleteMessage(user, user.getMenuId());
        }

        Integer menuId = messageService.sendMenuAndGetId(user, text, markup);
        user.setMenuId(menuId);
        userService.saveUser(user);
    }
}
