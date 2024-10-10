package fern.nail.art.nailscheduler.telegram.processor.impl.command;

import fern.nail.art.nailscheduler.telegram.model.Command;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PriceCommandUpdateProcessor extends AbstractCommandUpdateProcessor {
    private final MessageService messageService;
    private final UserService userService;

    @Override
    protected Command command() {
        return Command.PRICE;
    }

    @Override
    public void process(Update update, User user) {
        clearPreviousMenu(user, messageService);
        Integer messageId = messageService.sendTextAndGetId(user, "Here will be price list");
        saveMenuId(user, messageId, userService);
    }
}
