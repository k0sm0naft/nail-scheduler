package fern.nail.art.nailscheduler.telegram.model;

public enum GlobalState {
    // COMMON
    IDLE,
    COMMAND,
    SETTINGS,
    SLOT,
    APPOINTMENT,

    // AUTH
    AUTHENTICATION,
    REGISTRATION,
    LOGIN,

    // CLIENT
    CLIENT_MENU,

    // MASTER
    MASTER_MENU,
    WORKDAY,
    USER
}
