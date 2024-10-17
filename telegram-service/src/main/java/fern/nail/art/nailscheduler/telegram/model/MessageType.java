package fern.nail.art.nailscheduler.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType implements Localizable {
    // AUTH
    USE_FOR_LOGIN("message.use.for.login"),
    ENTER_LOGIN("message.enter.login"),
    ENTER_PASSWORD("message.enter.password"),
    REPEAT_PASSWORD("message.repeat.password"),
    PASSWORD_ACCEPTED("message.password.accepted"),
    WRONG_CREDENTIALS("message.wrong.credentials"),

    // COMMON
    HELLO("message.hello"),
    REPEAT("message.repeat"),
    CHOSE_OPTION("message.chose.option"),
    UPDATED("message.updated"),

    // SETTINGS
    CHANGE_NAMES("message.change.names"),
    ENTER_FIRST_NAME("message.enter.first.name"),
    ENTER_LAST_NAME("message.enter.last.name"),
    ENTER_PHONE("message.enter.phone"),

    // WORKDAY
    CURRENT("message.current"),
    DEFAULT("message.date"),
    DATE("message.date"),
    ENTER_TIME_AND_DAYS("message.enter.time.and.days"),
    INCORRECT_INPUT("message.incorrect.input"),
    TEMPLATE_INPUT_FORMAT("message.enter.template.format"),
    ALL_MATCHING_DEFAULTS("message.all.matching.defaults"),
    ENTER_WORKDAY("message.enter.workday"),
    WORKDAY_INPUT_FORMAT("message.enter.workday.format");

    private final String localizationKey;
}
