package me.bkrmt.bkshop.api;

import me.bkrmt.opengui.page.Page;
import org.bukkit.entity.Player;

public interface MenuManager {
    void openShopsMenu(Player player, int page);

    void buildPageFrame(Page tempPage);
}
