package fern.nail.art.nailscheduler.telegram.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public sealed class LoginUser extends User permits RegisterUser {
    private String username;
    private String password;

    public LoginUser(User user) {
        super(user.getTelegramId(),
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getLocale(),
                user.getRole(),
                user.getMessageIds());
    }
}
