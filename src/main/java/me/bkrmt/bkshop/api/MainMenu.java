package me.bkrmt.bkshop.api;

import me.bkrmt.bkcore.bkgui.page.Page;
import org.bukkit.entity.Player;

public interface MainMenu {
    Page getPage();

    Player getPlayer();

    void openMenu();
}
