package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.dto.user.ProcedureTimeDto;
import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.model.ProcedureType;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.model.UserProcedureTime;
import fern.nail.art.nailscheduler.repository.UserProcedureTimeRepository;
import fern.nail.art.nailscheduler.service.UserProcedureTimeService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProcedureTimeServiceImpl implements UserProcedureTimeService {
    private final UserProcedureTimeRepository procedureTimeRepository;
    @Value("${duration.avg.manicure}")
    private Integer defaultManicureTime;
    @Value("${duration.avg.pedicure}")
    private Integer defaultPedicureTime;

    @Override
    @CacheEvict(value = "userProcedureTimeCache", key = "#user.id + '*'", allEntries = true)
    public Set<UserProcedureTime> getDefault(User user) {
        UserProcedureTime manicureTime =
                new UserProcedureTime(user, ProcedureType.MANICURE, defaultManicureTime);
        UserProcedureTime pedicureTime =
                new UserProcedureTime(user, ProcedureType.PEDICURE, defaultPedicureTime);

        int defaultComplexTime = defaultManicureTime + defaultPedicureTime;
        UserProcedureTime complexTime =
                new UserProcedureTime(user, ProcedureType.COMPLEX, defaultComplexTime);
        return Set.of(manicureTime, pedicureTime, complexTime);
    }

    @Override
    @CacheEvict(value = "userProcedureTimeCache", key = "#user.id + '*'", allEntries = true)
    public void setToUser(Set<ProcedureTimeDto> procedureTimes, User user) {
        for (ProcedureTimeDto ptd : procedureTimes) {

            user.getProcedureTimes().stream()
                .filter(upt -> upt.getId().getProcedure().equals(ptd.procedure()))
                .findFirst()
                .ifPresent(upt -> upt.setDuration(ptd.duration()));
        }
    }

    @Override
    @Cacheable(value = "userProcedureTimeCache", key = "#userId + '_' + #procedure")
    public UserProcedureTime get(ProcedureType procedure, Long userId) {
        UserProcedureTime.Id id = new UserProcedureTime.Id(userId, procedure);
        return procedureTimeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(UserProcedureTime.class, id));
    }
}
