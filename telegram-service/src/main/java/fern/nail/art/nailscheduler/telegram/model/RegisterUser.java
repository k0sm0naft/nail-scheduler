package fern.nail.art.nailscheduler.telegram.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class RegisterUser extends LoginUser {
    private String repeatPassword;

    public RegisterUser(User user) {
        super(user);
    }
}
