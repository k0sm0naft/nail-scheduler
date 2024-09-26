package fern.nail.art.nailscheduler.telegram.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CallbackQueryData {
    MAIN("toMain"),
    LOGIN("login"),
    REGISTRATION("registration"),
    SAVE_USERNAME("saveUsername"),
    CHANGE_USERNAME("changeUsername"),
    CHANGE_FIRST_NAME("changeFirstName"),
    CHANGE_LAST_NAME("changeLastName"),
    SAVE_FULL_NAME("saveFullName");

    private final String command;

    public static CallbackQueryData fromString(String text) {
        for (CallbackQueryData c : CallbackQueryData.values()) {
            if (c.command.equals(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Can't find command %s".formatted(text));
    }
}
