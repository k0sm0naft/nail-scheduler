package fern.nail.art.nailscheduler.telegram.processor.impl.command;

import fern.nail.art.nailscheduler.telegram.model.Command;
import fern.nail.art.nailscheduler.telegram.model.GlobalState;
import fern.nail.art.nailscheduler.telegram.model.User;
import fern.nail.art.nailscheduler.telegram.processor.UpdateProcessor;
import fern.nail.art.nailscheduler.telegram.service.MessageService;
import fern.nail.art.nailscheduler.telegram.service.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public abstract class AbstractCommandUpdateProcessor implements UpdateProcessor {
    @Override
    public boolean canProcess(Update update, User user) {
        Message message = update.getMessage();
        return user.getGlobalState() == GlobalState.COMMAND
                && message.getText().equals(command().get());
    }

    protected abstract Command command();

    protected void clearPreviousMenu(User user, MessageService messageService) {
        if (user.getMenuId() != null) {
            messageService.deleteMessage(user, user.getMenuId());
            user.setMenuId(null);
        }
    }

    protected void saveMenuId(User user, Integer menuId, UserService userService) {
        user.setMenuId(menuId);
        userService.saveUser(user);
    }
}
