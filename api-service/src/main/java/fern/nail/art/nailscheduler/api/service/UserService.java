package fern.nail.art.nailscheduler.api.service;

import fern.nail.art.nailscheduler.api.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.api.dto.user.UserUpdateRequestDto;
import fern.nail.art.nailscheduler.api.model.User;
import java.util.Set;

public interface UserService {
    User register(User user);

    boolean isMaster(User user);

    User getInfo(Long userId);

    User update(Long userId, UserUpdateRequestDto updateRequestDto);

    void changePassword(Long userId, String newPassword);

    User updateProcedureTimes(Long id, Set<ProcedureTimeDto> procedureTimes);

    void setTelegramId(Long userId, String telegramId);

    User getByTelegramId(String telegramId);
}
