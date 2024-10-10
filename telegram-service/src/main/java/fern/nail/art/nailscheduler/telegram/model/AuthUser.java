package fern.nail.art.nailscheduler.telegram.model;

import fern.nail.art.nailscheduler.common.annotation.Password;
import fern.nail.art.nailscheduler.common.annotation.PasswordMatchValidator;
import fern.nail.art.nailscheduler.common.annotation.Username;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatchValidator(field = "password", fieldMatch = "repeatPassword")
public final class AuthUser extends User {
    @Username
    private String username;
    @Password
    private String password;
    @Password
    private String repeatPassword;

    public AuthUser(User user) {
        super(user.getTelegramId(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getLocale(),
                user.getRole(),
                user.getMenuId(),
                user.getGlobalState(),
                user.getLocalState());
    }
}
