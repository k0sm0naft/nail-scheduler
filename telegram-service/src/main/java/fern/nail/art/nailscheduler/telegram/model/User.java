package fern.nail.art.nailscheduler.telegram.model;

import java.io.Serializable;
import java.util.Locale;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User implements Serializable {
    private Long telegramId;
    private Long chatId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private Locale locale;
    private Role role;

    public enum Role {
        CLIENT,
        MASTER,
        UNKNOWN
    }
}
