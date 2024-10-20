package fern.nail.art.nailscheduler.telegram.processor.impl.workday;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_MAIN;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.SPECIFIC;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.TEMPLATES;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.CHOSE_OPTION;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
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
public class WorkdayMenuUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.WORKDAY
                && user.getLocalState() == null
                && update.hasCallbackQuery();
    }

    @Override
    public void process(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        switch (ButtonType.valueOf(data)) {
            case WORKDAYS, BACK_TO_WORKDAYS -> sendWorkdayMenu(user);
            case TEMPLATES -> switchProcessor(update, user, LocalState.TEMPLATES);
            case SPECIFIC -> switchProcessor(update, user, LocalState.SPECIFICS);
            case BACK_TO_MAIN -> switchProcessorToIdle(update, user);
            default -> throw new IllegalArgumentException("Unknown callback data: " + data);
        }
    }

    private void sendWorkdayMenu(User user) {
        user.setGlobalState(GlobalState.WORKDAY);
        Locale locale = user.getLocale();
        String text = localizationService.localize(CHOSE_OPTION, locale);
        List<ButtonType> buttonTypes = List.of(TEMPLATES, SPECIFIC, BACK_TO_MAIN);
        InlineKeyboardMarkup markup = markupFactory.create(buttonTypes, locale);

        messageService.editMenu(user,user.getMenuId(), text, markup);
        userService.saveUser(user);
    }

    private void switchProcessor(Update update, User user, LocalState localState) {
        user.setLocalState(localState);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }

    private void switchProcessorToIdle(Update update, User user) {
        user.setGlobalState(GlobalState.IDLE);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
