package fern.nail.art.nailscheduler.telegram.model;

import fern.nail.art.nailscheduler.common.annotation.Name;
import fern.nail.art.nailscheduler.common.annotation.Phone;
import java.io.Serializable;
import java.util.Locale;
import java.util.Stack;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public sealed class User implements Serializable permits LoginUser {
    private Long telegramId;
    private Long userId;
    @Name
    private String firstName;
    @Name
    private String lastName;
    @Phone
    private String phone;
    private Locale locale;
    private Role role;

    public enum Role {
        CLIENT,
        MASTER,
        UNKNOWN
    }
}
