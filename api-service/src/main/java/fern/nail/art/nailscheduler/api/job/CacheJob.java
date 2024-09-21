package fern.nail.art.nailscheduler.api.job;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheJob {

    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(cacheNames = {"slotsCache", "workdayCache"}, allEntries = true)
    public void cleanCache() {
    }
}
