package fern.nail.art.nailscheduler.telegram.processor.impl.command;

import fern.nail.art.nailscheduler.telegram.model.Command;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class InfoCommandUpdateProcessor extends AbstractCommandUpdateProcessor {
    private final MessageService messageService;

    @Override
    protected Command command() {
        return Command.INFO;
    }

    @Override
    public void process(Update update, User user) {
        clearPreviousMenu(user, messageService);
        messageService.sendText(user, "Here will be some info");
    }
}
