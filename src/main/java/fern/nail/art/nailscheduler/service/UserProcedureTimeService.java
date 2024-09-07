package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.dto.user.UpdateProcedureTimesDto;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.model.UserProcedureTime;
import java.util.Set;

public interface UserProcedureTimeService {
    Set<UserProcedureTime> getDefault(User user);

    void setToUser(UpdateProcedureTimesDto requestDto, User user);

    UserProcedureTime get(ProcedureType procedure, User user);
}
