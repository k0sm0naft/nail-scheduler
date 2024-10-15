package fern.nail.art.nailscheduler.telegram.processor.impl.workday;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_TEMPLATES;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_WORKDAYS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.SET;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.CHOSE_OPTION;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.INCORRECT_INPUT;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.MISTAKE_OCCURS;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.REPEAT;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.REPEAT_LATER;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.TEMPLATE_INPUT_FORMAT;

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
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class AwaitingEditTemplateWorkdayUpdateProcessor implements UpdateProcessor {
    private static final int FIRST_DAY_INDEX = 2;
    private static final String HOURS_REGEX = "([01]\\d|2[0-3])";
    private static final String MINUTES_REGEX = "([0-5]\\d)";
    private static final String TIME_REGEX = HOURS_REGEX + ':' + MINUTES_REGEX;
    private static final String SPACE_REGEX = "\\s";
    private static final String DAYS_OF_WEEK_REGEX = "([1-7](\\s[1-7])*)";
    private static final String TEMPLATE_INPUT_REGEX =
            '^' + TIME_REGEX + SPACE_REGEX + TIME_REGEX + SPACE_REGEX + DAYS_OF_WEEK_REGEX + '$';
    private static final String SPACE = " ";

    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final WorkdayService workdayService;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.WORKDAY
                && user.getLocalState() == LocalState.AWAITING_EDIT_TEMPLATE;
    }

    @Override
    public void process(Update update, User user) {
        if (update.hasCallbackQuery()) {
            handleCallback(update, user);
        } else {
            handleMessage(update, user);
        }
    }

    private void handleCallback(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        if (ButtonType.valueOf(data) == BACK_TO_TEMPLATES) {
            user.setLocalState(LocalState.TEMPLATES);
            eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
        } else {
            throw new IllegalArgumentException("Unknown callback data: " + data);
        }
    }

    private void handleMessage(Update update, User user) {
        String input = update.getMessage().getText().strip();
        if (Pattern.matches(TEMPLATE_INPUT_REGEX, input)) {
            Set<WorkdayTemplate> templates = setTemplates(input);
            if (templates.isEmpty()) {
                handleIncorrectInput(List.of(MISTAKE_OCCURS, REPEAT_LATER), user);
            } else {
                handleSuccessUpdate(user, templates);
            }
        } else {
            handleIncorrectInput(List.of(INCORRECT_INPUT, TEMPLATE_INPUT_FORMAT, REPEAT), user);
        }
    }

    private Set<WorkdayTemplate> setTemplates(String input) {
        String[] elements = input.split(SPACE);
        LocalTime startTime = LocalTime.parse(elements[0]);
        LocalTime endTime = LocalTime.parse(elements[1]);
        String[] days = Arrays.copyOfRange(elements, FIRST_DAY_INDEX, elements.length);
        Set<DayOfWeek> daysOfWeek = Arrays.stream(days)
                                          .mapToInt(Integer::parseInt)
                                          .mapToObj(DayOfWeek::of)
                                          .collect(Collectors.toSet());
        return workdayService.setTemplates(startTime, endTime, daysOfWeek);
    }

    private void handleSuccessUpdate(User user, Set<WorkdayTemplate> templates) {
        Locale locale = user.getLocale();
        String templatesTable = templates.stream()
                                         .sorted()
                                         .map(template -> template.getFormated(locale))
                                         .collect(Collectors.joining(System.lineSeparator()));

        String text = localizationService.localize(List.of(templatesTable, CHOSE_OPTION), locale);
        List<ButtonType> buttonTypes = List.of(SET, BACK_TO_WORKDAYS);
        InlineKeyboardMarkup markup = markupFactory.create(buttonTypes, locale);

        messageService.editMenu(user, user.getMenuId(), text, markup);
        user.setLocalState(LocalState.TEMPLATES);
        userService.saveUser(user);
    }

    private void handleIncorrectInput(List<Object> messageTypes, User user) {
        String text = localizationService.localize(messageTypes, user.getLocale());
        InlineKeyboardMarkup markup =
                markupFactory.create(List.of(BACK_TO_TEMPLATES), user.getLocale());
        messageService.editMenu(user, user.getMenuId(), text, markup);
    }
}
