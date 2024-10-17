package fern.nail.art.nailscheduler.telegram.processor.impl.workday;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_TEMPLATES;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.ENTER_TIME_AND_DAYS;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.TEMPLATE_INPUT_FORMAT;

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
public class SetTemplateWorkdayUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.WORKDAY
                && user.getLocalState() == LocalState.SET_TEMPLATES
                && update.hasCallbackQuery();
    }

    @Override
    public void process(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        switch (ButtonType.valueOf(data)) {
            case SET -> sendEditRequest(user);
            case BACK_TO_TEMPLATES -> switchProcessorToTemplates(update, user);
            default -> throw new IllegalArgumentException("Unknown callback data: " + data);
        }
    }

    private void sendEditRequest(User user) {
        Locale locale = user.getLocale();
        String text = localizationService
                .localize(List.of(ENTER_TIME_AND_DAYS, TEMPLATE_INPUT_FORMAT), locale);
        InlineKeyboardMarkup markup = markupFactory.create(List.of(BACK_TO_TEMPLATES), locale);

        user.setLocalState(LocalState.AWAITING_EDIT_TEMPLATE);
        messageService.editMenu(user, user.getMenuId(), text, markup);
        userService.saveUser(user);
    }

    private void switchProcessorToTemplates(Update update, User user) {
        user.setLocalState(LocalState.TEMPLATES);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
