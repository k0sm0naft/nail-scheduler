package fern.nail.art.nailscheduler.telegram.processor.impl.workday;

import static fern.nail.art.nailscheduler.telegram.model.ButtonType.ADD;
import static fern.nail.art.nailscheduler.telegram.model.ButtonType.BACK_TO_SPECIFIC;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.ENTER_WORKDAY;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.INCORRECT_INPUT;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.REPEAT;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.UPDATED;
import static fern.nail.art.nailscheduler.telegram.model.MessageType.WORKDAY_INPUT_FORMAT;
import static fern.nail.art.nailscheduler.telegram.model.PatternAndRegex.DATE;
import static fern.nail.art.nailscheduler.telegram.model.PatternAndRegex.SPACE;
import static fern.nail.art.nailscheduler.telegram.model.PatternAndRegex.TIME;
import static fern.nail.art.nailscheduler.telegram.model.PatternAndRegex.getPatternOf;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.model.ButtonType;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.LocalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.model.Workday;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.LocalizationService;
import fern.nail.art.nailscheduler.telegram.service.MarkupFactory;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import fern.nail.art.nailscheduler.telegram.service.WorkdayService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class AddSpecificWorkdayUpdateProcessor implements UpdateProcessor {
    private final MessageService messageService;
    private final LocalizationService localizationService;
    private final MarkupFactory markupFactory;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final WorkdayService workdayService;

    @Override
    public boolean canProcess(Update update, User user) {
        return user.getGlobalState() == GlobalState.WORKDAY
                && user.getLocalState() == LocalState.ADD_SPECIFIC_WORKDAY;
    }

    @Override
    public void process(Update update, User user) {
        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            switch (ButtonType.valueOf(data)) {
                case ADD -> sendAddSpecificWorkdayMenu(user);
                case BACK_TO_SPECIFIC -> switchProcessorToSpecific(update, user);
                default -> throw new IllegalArgumentException("Unknown callback data: " + data);
            }
        } else {
            handleMessage(update, user);
        }
    }

    private void sendAddSpecificWorkdayMenu(User user) {
        Locale locale = user.getLocale();
        String text =
                localizationService.localize(List.of(ENTER_WORKDAY, WORKDAY_INPUT_FORMAT), locale);
        InlineKeyboardMarkup markup = markupFactory.create(List.of(BACK_TO_SPECIFIC), locale);

        messageService.editMenu(user, user.getMenuId(), text, markup);
        userService.saveUser(user);
    }

    private void handleMessage(Update update, User user) {
        String input = update.getMessage().getText().strip();
        if (Pattern.matches(getPatternOf(List.of(DATE, SPACE, TIME, SPACE, TIME)), input)) {
            Workday workday = parseToWorkday(input);
            workdayService.setWorkday(workday);
            handleSuccessUpdate(user, workday);
        } else {
            handleIncorrectInput(List.of(INCORRECT_INPUT, WORKDAY_INPUT_FORMAT, REPEAT), user);
        }
    }

    private Workday parseToWorkday(String input) {
        String[] elements = input.split(SPACE.getRegex());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE.getRegex());
        LocalDate day = LocalDate.parse(elements[0], formatter);
        LocalTime startTime = LocalTime.parse(elements[1]);
        LocalTime endTime = LocalTime.parse(elements[2]);
        return new Workday(day, startTime, endTime);
    }

    private void handleSuccessUpdate(User user, Workday workday) {
        Locale locale = user.getLocale();

        String text = localizationService
                .localize(List.of(UPDATED, workday.getFormated()), locale);
        InlineKeyboardMarkup markup = markupFactory.create(List.of(ADD, BACK_TO_SPECIFIC), locale);

        messageService.editMenu(user, user.getMenuId(), text, markup);
        user.setLocalState(LocalState.SPECIFICS);
        userService.saveUser(user);
    }

    private void handleIncorrectInput(List<Object> messageTypes, User user) {
        Locale locale = user.getLocale();
        String text = localizationService.localize(messageTypes, locale);
        InlineKeyboardMarkup markup = markupFactory.create(List.of(BACK_TO_SPECIFIC), locale);
        messageService.editMenu(user, user.getMenuId(), text, markup);
    }

    private void switchProcessorToSpecific(Update update, User user) {
        user.setLocalState(LocalState.SPECIFICS);
        eventPublisher.publishEvent(new RequestedUpdateRouteEvent(update, user));
    }
}
