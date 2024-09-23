package fern.nail.art.nailscheduler.telegram.menu;

import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class MasterMenuManager extends ClientMenuManager {
    @Override
    public InlineKeyboardMarkup createMainMenu(User.Role role) {
        return null;
    }

    @Override
    public InlineKeyboardMarkup createSubMenu(User.Role role, String context) {
        return null;
    }
}
