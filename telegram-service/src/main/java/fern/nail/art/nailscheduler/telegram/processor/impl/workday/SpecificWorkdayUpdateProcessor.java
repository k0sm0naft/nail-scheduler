package fern.nail.art.nailscheduler.telegram.processor.impl.workday;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.ADD;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_WORKDAYS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.NEXT;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.PREVIOUS;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.SET_DEFAULT;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.SPECIFIC;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.TEMPLATES;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.ALL_MATCHING_DEFAULTS;
import static fern.nail.art.nailscheduler.telegram.model.PatternAndRegex.DATE;
import static fern.nail.art.nailscheduler.telegram.model.PatternAndRegex.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.Localizable;
import fern.nail.art.nailscheduler.telegram.model.PeriodType;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.model.Workday;
import fern.nail.art.nailscheduler.telegram.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.service.WorkdayService;
import fern.nail.art.nailscheduler.telegram.utils.OffsetUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class SpecificWorkdayUpdateProcessor implements UpdateProcessor {
    private static final String MONTH_PATTERN = "LLLL yyyy";
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final WorkdayService workdayService;
    private final OffsetUtil offsetUtil;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.WORKDAY
                && user.getLocalState() == LocalState.SPECIFICS
                && update.hasCallbackQuery();
    }

    @Override
    public void process(Update update, User user) {
        String data = update.getCallbackQuery().getData();
        if (data.matches(ISO_DATE.getPattern())) {
            sendSpecificWorkdayMenu(user, LocalDate.parse(data));
        } else if (data.startsWith(SET_DEFAULT.name())) {
            LocalDate date = LocalDate.parse(data.replace(SET_DEFAULT.name(), ""));
            workdayService.setToDefault(date);
            sendSpecificWorkdayMenu(user, date);
        } else {
            switch (ButtonType.valueOf(data)) {
                case SPECIFIC, BACK_TO_SPECIFIC -> sendSpecificWorkdayMenu(user, LocalDate.now());
                case ADD -> switchProcessor(update, user, LocalState.ADD_SPECIFIC_WORKDAY);
                case BACK_TO_WORKDAYS -> switchProcessor(update, user, null);
                default -> throw new IllegalArgumentException("Unknown callback data: " + data);
            }
        }
    }

    private void sendSpecificWorkdayMenu(User user, LocalDate date) {
        Locale locale = user.getLocale();
        DateTimeFormatter monthNameFormatter = DateTimeFormatter.ofPattern(MONTH_PATTERN, locale);
        String currentMonthName = date.format(monthNameFormatter).toUpperCase();
        Set<WorkdayTemplate> templates = workdayService.getTemplates();
        List<Workday> specific = getDifferentFromDefaultWorkdays(templates, date);

        Localizable messageType = specific.isEmpty() ? ALL_MATCHING_DEFAULTS : SPECIFIC;

        String text = buildText(currentMonthName, messageType, locale, specific, templates);

        List<ButtonType> buttonTypes = List.of(PREVIOUS, NEXT, ADD, BACK_TO_WORKDAYS);
        InlineKeyboardMarkup markup = markupFactory.create(buttonTypes, locale);

        setCallbacksToNextAndPreviousButtons(markup, date);

        addSpecificToDefaultButtons(markup, specific, locale);

        messageService.editMenu(user, user.getMenuId(), text, markup);
        userService.saveUser(user);
    }

    @NotNull
    private String buildText(String currentMonthName, Localizable messageType, Locale locale,
            List<Workday> specific, Set<WorkdayTemplate> templates) {
        return new StringBuilder()
                .append(currentMonthName)
                .append(System.lineSeparator())
                .append(localizationService.localize(messageType, locale))
                .append(System.lineSeparator())
                .append(specific.stream()
                                .map(Workday::getFormated)
                                .collect(Collectors.joining(System.lineSeparator())))
                .append(System.lineSeparator())
                .append(System.lineSeparator())
                .append(getTemplateTable(templates, locale))
                .toString();
    }

    private void addSpecificToDefaultButtons(
            InlineKeyboardMarkup markup, List<Workday> workdays, Locale locale
    ) {
        Function<Workday, String> getButtonCallback = w -> SET_DEFAULT.name() + w.getDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE.getRegex());
        Function<Workday, String> getButtonName = w ->
                localizationService.localize(SET_DEFAULT, locale)
                                   .formatted(w.getDate().format(dateFormatter));

        markupFactory.addCustomButtons(getButtonName, getButtonCallback, workdays, markup);
    }


    private List<Workday> getDifferentFromDefaultWorkdays(Set<WorkdayTemplate> templates,
            LocalDate date) {
        Map<DayOfWeek, WorkdayTemplate> mapTemplates =
                templates.stream().collect(Collectors.toMap(WorkdayTemplate::getDayOfWeek, t -> t));
        int offset = offsetUtil.getMonthOffsetToDate(date);
        return workdayService.getWorkdays(PeriodType.MONTH, offset).stream()
                             .filter(workday -> isNotDefault(workday, mapTemplates))
                             .sorted(Comparator.comparing(Workday::getDate))
                             .toList();
    }

    private String getTemplateTable(Set<WorkdayTemplate> templates, Locale locale) {
        return localizationService.localize(TEMPLATES, locale) + System.lineSeparator()
                + templates.stream()
                           .sorted(Comparator.comparing(WorkdayTemplate::getDayOfWeek))
                           .map(template -> template.getFormated(locale))
                           .collect(Collectors.joining(System.lineSeparator()));
    }

    private static boolean isNotDefault(Workday workday,
            Map<DayOfWeek, WorkdayTemplate> templates) {
        WorkdayTemplate template = templates.get(workday.getDate().getDayOfWeek());
        return template != null && !(template.getStartTime().equals(workday.getStartTime())
                                             && template.getEndTime().equals(workday.getEndTime()));
    }

    private void setCallbacksToNextAndPreviousButtons(InlineKeyboardMarkup markup, LocalDate date) {
        int monthOffset = 1;
        String nextMonthDate = date.plusMonths(monthOffset).format(ISO_LOCAL_DATE);
        markupFactory.setCallbackToButton(NEXT, nextMonthDate, markup);

        String prevMonthDate = date.minusMonths(monthOffset).format(ISO_LOCAL_DATE);
        markupFactory.setCallbackToButton(PREVIOUS, prevMonthDate, markup);
    }

    private void switchProcessor(Update update, User user, LocalState localState) {
        user.setLocalState(localState);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
