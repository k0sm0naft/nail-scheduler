package fern.nail.art.nailscheduler.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonType implements Localizable {
    // AUTH
    LOGIN("button.login"),
    REGISTRATION("button.register"),

    // COMMON
    MAIN("button.to.main"),
    CONFIRM("button.confirm"),
    BACK_TO_MAIN("button.go.back"),

    // SETTINGS
    SETTINGS("button.settings"),
    CHANGE_USERNAME("button.change"),
    CHANGE_FIRST_NAME("button.change.first.name"),
    CHANGE_LAST_NAME("button.change.last.name"),

    // MASTER
    USERS("button.users"),
    SLOTS("button.slots"),
    APPOINTMENTS("button.appointments"),
    WORKDAYS("button.workday"),

    // WORKDAY
    TEMPLATES("button.templates"),
    SHOW_ALL("button.show.all"),
    SET("button.set"),
    GET_BY_PERIOD("button.get.by.period"),
    CHANGE_BY_DATE("button.change.by.date"),
    CLEAR_BY_DATE("button.clear.by.date"),
    SPECIFIC("button.specific"),
    BACK_TO_WORKDAYS("button.go.back"),
    BACK_TO_TEMPLATES("button.go.back");

    private final String localizationKey;
}
