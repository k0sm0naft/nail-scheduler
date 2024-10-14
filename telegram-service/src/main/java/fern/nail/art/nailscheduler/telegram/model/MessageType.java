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

    // SETTINGS,
    CHANGE_NAMES("message.change.names"),
    ENTER_FIRST_NAME("message.enter.first.name"),
    ENTER_LAST_NAME("message.enter.last.name"),
    ENTER_PHONE("message.enter.phone");

    private final String localizationKey;
}
