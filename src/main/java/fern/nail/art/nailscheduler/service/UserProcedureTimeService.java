package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.model.UserProcedureTime;
import java.util.Set;

public interface UserProcedureTimeService {
    Set<UserProcedureTime> getDefault(User user);

    void setToUser(Set<ProcedureTimeDto> procedureTimes, User user);

    UserProcedureTime get(ProcedureType procedure, Long userId);
}
