package fern.nail.art.nailscheduler.telegram.processor.impl.workday;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_WORKDAYS;
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
public class SpecificWorkdayUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.WORKDAY
                && user.getLocalState() == LocalState.SPECIFICS;
    }

    @Override
    public void process(Update update, User user) {
        if (update.hasCallbackQuery()) {

            String data = update.getCallbackQuery().getData();
            switch (ButtonType.valueOf(data)) {
                case SHOW_ALL -> user.setLocalState(LocalState.SHOW_TEMPLATES);
                case SET -> user.setLocalState(LocalState.SET_DEFAULT);
                case GET_BY_PERIOD -> user.setLocalState(LocalState.GET_BY_PERIOD);
                case CHANGE_BY_DATE -> user.setLocalState(LocalState.CHANGE_BY_DATE);
                case CLEAR_BY_DATE -> user.setLocalState(LocalState.CLEAR_BY_DATE);
                case BACK_TO_WORKDAYS -> user.setLocalState(null);
                default -> throw new IllegalArgumentException("Unknown callback data: " + data);
            }
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));

        } else {
            sendWorkdayMenu(user);
        }
    }

    private void sendWorkdayMenu(User user) {
        Locale locale = user.getLocale();
        String text = localizationService.localize(CHOSE_OPTION, locale);
        List<ButtonType> buttonTypes = List.of(TEMPLATES, SPECIFIC, BACK_TO_WORKDAYS);
        InlineKeyboardMarkup markup = markupFactory.create(buttonTypes, locale);

        Integer menuId = messageService.sendMenuAndGetId(user, text, markup);
        user.setMenuId(menuId);
        userService.saveUser(user);
    }
}
