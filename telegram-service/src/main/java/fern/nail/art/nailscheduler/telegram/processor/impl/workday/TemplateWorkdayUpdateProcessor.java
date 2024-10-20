package fern.nail.art.nailscheduler.telegram.processor.impl.workday;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_WORKDAYS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.SET;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.TEMPLATES;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.CHOSE_OPTION;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.service.WorkdayService;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class TemplateWorkdayUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final WorkdayService workdayService;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.WORKDAY
                && user.getLocalState() == LocalState.TEMPLATES
                && update.hasCallbackQuery();
    }

    @Override
    public void process(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        switch (ButtonType.valueOf(data)) {
            case TEMPLATES, BACK_TO_TEMPLATES -> sendTemplateMenu(user);
            case SET -> switchProcessor(update, user, LocalState.SET_TEMPLATES);
            case BACK_TO_WORKDAYS -> switchProcessor(update, user, null);
            default -> throw new IllegalArgumentException("Unknown callback data: " + data);
        }
    }

    private void sendTemplateMenu(User user) {
        Locale locale = user.getLocale();
        String templatesTable = getTemplatesTable(locale);
        String text = localizationService
                .localize(List.of(TEMPLATES, templatesTable, CHOSE_OPTION), locale);
        List<ButtonType> buttonTypes = List.of(SET, BACK_TO_WORKDAYS);
        InlineKeyboardMarkup markup = markupFactory.create(buttonTypes, locale);

        messageService.editMenu(user, user.getMenuId(), text, markup);
        userService.saveUser(user);
    }

    private String getTemplatesTable(Locale locale) {
        Set<WorkdayTemplate> templates = workdayService.getTemplates();

        return templates.stream()
                        .sorted(Comparator.comparing(WorkdayTemplate::getDayOfWeek))
                        .map(template -> template.getFormated(locale))
                        .collect(Collectors.joining(System.lineSeparator()));
    }

    private void switchProcessor(Update update, User user, LocalState localState) {
        user.setLocalState(localState);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
