package fern.nail.art.nailscheduler.api.service;

import fern.nail.art.nailscheduler.api.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.api.model.ProcedureType;
import fern.nail.art.nailscheduler.api.model.User;
import fern.nail.art.nailscheduler.api.model.UserProcedureTime;
import java.util.Set;

public interface UserProcedureTimeService {
    Set<UserProcedureTime> getDefault(User user);

    void setToUser(Set<ProcedureTimeDto> procedureTimes, User user);

    UserProcedureTime get(ProcedureType procedure, Long userId);
}
