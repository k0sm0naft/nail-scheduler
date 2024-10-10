package fern.nail.art.nailscheduler.telegram.utils;

import fern.nail.art.nailscheduler.telegram.event.RequestedUpdateRouteEvent;
import fern.nail.art.nailscheduler.telegram.exception.ProcessorNotFoundException;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UpdateRouter {
    private final List<UpdateProcessor> updateProcessors;

    @EventListener
    public void route(RequestedUpdateRouteEvent event) {
        Update update = event.update();
        User user = event.user();

        updateProcessors.stream()
                        .filter(processor -> processor.canProcess(update, user))
                        .findFirst()
                        .orElseThrow(() ->
                                new ProcessorNotFoundException(
                                        "Can't find processor for user: " + user))
                        .process(update, user);
    }
}
