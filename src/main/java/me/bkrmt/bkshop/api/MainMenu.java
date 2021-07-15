package me.bkrmt.bkshop.api;

import me.bkrmt.opengui.page.Page;
import org.bukkit.entity.Player;

public interface MainMenu {
    Page getPage();

    Player getPlayer();

    void openMenu();
}
