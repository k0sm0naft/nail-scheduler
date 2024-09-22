package fern.nail.art.nailscheduler.telegram.model;

public class User {
    private Long id;
    private Long chatId;
    private Role role;

    public enum Role {
        CLIENT,
        MASTER,
        UNKNOWN
    }
}
