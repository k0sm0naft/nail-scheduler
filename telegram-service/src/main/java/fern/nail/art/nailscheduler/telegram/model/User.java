package fern.nail.art.nailscheduler.telegram.model;

import fern.nail.art.nailscheduler.common.annotation.Name;
import fern.nail.art.nailscheduler.common.annotation.Phone;
import java.io.Serializable;
import java.util.Locale;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public sealed class User implements Serializable permits AuthUser {
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
    private Integer menuId;
    private GlobalState globalState;
    private LocalState localState;

    public enum Role {
        CLIENT,
        MASTER,
        UNKNOWN
    }
}
