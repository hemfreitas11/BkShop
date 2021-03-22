package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.menu.Menu;
import me.bkrmt.bkshop.BkShop;
import org.bukkit.inventory.ItemStack;

public abstract class ShopMenu extends Menu {
    public ShopMenu(BkPlugin plugin, String titleKey) {
        super(plugin, titleKey);
        buildMenu();
    }

    public void buildMenu() {
        buildFrame();
    }

    public void buildFrame() {
        ItemStack[] items = BkShop.getFrameItems();
        for (int c = 0; c < 54; c++) {
            if (c == 0 || c == 8 || c == 45 || c == 53)
                getMenu().setItem(c, items[0]);

            if (c == 1 || c == 4 || c == 7 || c == 9 || c == 17 || c == 36 || c == 46 || c == 52 || c == 49 || c == 44)
                getMenu().setItem(c, items[1]);

            if (c == 2 || c == 3 || c == 5 || c == 6 || c == 47 || c == 48 || c == 50 || c == 51 || c == 10 || c == 16 || c == 37 || c == 43 || c == 18 || c == 26 || c == 27 || c == 35)
                getMenu().setItem(c, items[2]);

        }
    }
}
