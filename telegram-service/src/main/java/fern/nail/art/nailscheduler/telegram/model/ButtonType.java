package fern.nail.art.nailscheduler.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonType {
    MAIN("button.to.main"),
    LOGIN("button.login"),
    REGISTRATION("button.register"),
    CHANGE_USERNAME("button.change"),
    CHANGE_FIRST_NAME("button.change.first.name"),
    CHANGE_LAST_NAME("button.change.last.name"),
    CONFIRM("button.confirm");

    private final String localizationKey;
}
