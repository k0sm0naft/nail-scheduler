package fern.nail.art.nailscheduler.telegram.model;

import lombok.Getter;

@Getter
public enum Command {
    START("Get start menu"),
    INFO("About services"),
    BOT_INFO("About bot abilities"),
    PRICE("Price list"),
    CONTACTS("Contacts of master");

    private final String description;

    Command(String description) {
        this.description = description;
    }

    public String get() {
        return '/' + this.name().toLowerCase();
    }
}
