package fern.nail.art.nailscheduler.telegram.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Command {
    MAIN("toMain"),
    LOGIN("login"),
    REGISTER("register"),
    REGISTRATION("registration"),
    SAVE_USERNAME("saveUsername"),
    CHANGE_USERNAME( "changeUsername"),
    CHANGE_FIRST_NAME("changeFirstName"),
    CHANGE_LAST_NAME("changeLastName");

    private final String command;

    public static Command fromString(String text) {
        for (Command c : Command.values()) {
            if (c.command.equals(text)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Can't find command %s".formatted(text));
    }
}
