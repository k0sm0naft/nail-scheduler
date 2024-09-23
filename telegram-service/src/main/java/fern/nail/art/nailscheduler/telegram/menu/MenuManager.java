package fern.nail.art.nailscheduler.telegram.menu;

import fern.nail.art.nailscheduler.telegram.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MenuManager {
    InlineKeyboardMarkup createMainMenu(User.Role role);

    InlineKeyboardMarkup createSubMenu(User.Role role, String context);
}
