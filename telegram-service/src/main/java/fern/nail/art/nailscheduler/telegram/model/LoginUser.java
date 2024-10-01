package fern.nail.art.nailscheduler.telegram.model;

import fern.nail.art.nailscheduler.common.annotation.Password;
import fern.nail.art.nailscheduler.common.annotation.Username;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public sealed class LoginUser extends User permits RegisterUser {
    @Username
    private String username;
    @Password
    private String password;

    public LoginUser(User user) {
        super(user.getTelegramId(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getLocale(),
                user.getRole(),
                user.getMenuId());
    }
}
