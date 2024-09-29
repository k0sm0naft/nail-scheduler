package fern.nail.art.nailscheduler.telegram.model;

import fern.nail.art.nailscheduler.common.annotation.Password;
import fern.nail.art.nailscheduler.common.annotation.PasswordMatchValidator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatchValidator(field = "password", fieldMatch = "repeatPassword")
public final class RegisterUser extends LoginUser {
    @Password
    private String repeatPassword;

    public RegisterUser(User user) {
        super(user);
    }
}
