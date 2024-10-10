package fern.nail.art.nailscheduler.telegram.event;

import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.Update;

public record RequestedUpdateRouteEvent(Update update, User user) {
}
