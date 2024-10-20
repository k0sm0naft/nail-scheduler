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
    SHOW("button.show"),
    MONTH("button.month"),
    WEEK("button.week"),
    DAY("button.day"),
    NEXT("button.next"),
    PREVIOUS("button.previous"),
    ADD("button.add"),
    CHANGE("button.change"),

    // SETTINGS
    SETTINGS("button.settings"),
    CHANGE_FIRST_NAME("button.change.first.name"),
    CHANGE_LAST_NAME("button.change.last.name"),

    // MASTER
    USERS("button.users"),
    SLOTS("button.slots"),
    APPOINTMENTS("button.appointments"),
    WORKDAYS("button.workday"),

    // WORKDAY
    TEMPLATES("button.templates"),
    SET("button.set"),
    GET_BY_PERIOD("button.get.by.period"),
    SET_DEFAULT("button.set.default"),
    SPECIFIC("button.specific"),
    BACK_TO_WORKDAYS("button.go.back"),
    BACK_TO_TEMPLATES("button.go.back"),
    BACK_TO_SPECIFIC("button.go.back"),
    BACK_TO_SHOW("button.go.back"),
    BACK_TO_MONTH("button.go.back");

    private final String localizationKey;
}
