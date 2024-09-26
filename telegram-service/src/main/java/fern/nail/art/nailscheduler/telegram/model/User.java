package fern.nail.art.nailscheduler.telegram.model;

import java.io.Serializable;
import java.util.Locale;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder()
@EqualsAndHashCode
public sealed class User implements Serializable permits LoginUser {
    private Long telegramId;
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
