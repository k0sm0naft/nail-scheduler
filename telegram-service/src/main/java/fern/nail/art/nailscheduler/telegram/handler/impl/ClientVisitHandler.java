package fern.nail.art.nailscheduler.telegram.handler.impl;

import fern.nail.art.nailscheduler.telegram.handler.UpdateHandler;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.utils.AuthorizationMenuUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class ClientVisitHandler implements UpdateHandler {
    private final MessageService messageService;
    private final AuthorizationMenuUtil menu;

    @Override
    public void handleUpdate(Update update, User user) {
        messageService.sendText(user,"Hello, %s!".formatted(user.getFirstName()));
    }
}
