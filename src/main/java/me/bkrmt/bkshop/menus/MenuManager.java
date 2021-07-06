package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.PagedItem;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.Shop;
import me.bkrmt.opengui.ItemBuilder;
import me.bkrmt.opengui.Page;
import me.bkrmt.opengui.Rows;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;

import static me.bkrmt.bkshop.BkShop.getInstance;
import static me.bkrmt.bkshop.BkShop.plugin;

public class MenuManager {

    public void openMainMenu(Player player) {
        new MainMenu(player).openMenu();
    }

    public void openShopsMenu(Player player, int page) {
        ArrayDeque<PagedItem> shops = new ArrayDeque<>();
        List<Shop> shopList = getInstance().getShopsManager().getShops();
        shopList.forEach(Shop::reloadDisplayItem);
        Collections.addAll(shops, shopList.toArray(new Shop[0]));

        PagedList pagedList = new PagedList(getInstance(), null, "shops-list", shops)
                .setGuiRows(Rows.FIVE)
                .setListRows(3)
                .setStartingSlot(11)
                .setListRowSize(5)
                .setButtonSlots(18, 26)
                .setMenuTitle(BkShop.getInstance().getLangFile().get(null, "info.list-title"))
                .buildMenu();
        for (Page tempPage : pagedList.getPages()) {
            buildPageFrame(tempPage);
        }

        if (pagedList.getPages().size() > 0)
            pagedList.getPages().get(0).setItemOnXY(1, 3,
                new ItemBuilder(plugin.getHandler().getItemManager().getRedWool())
                    .setName(plugin.getLangFile().get(player, "gui-buttons.previous-page.name"))
                    .setUnchangedName(plugin.getLangFile().get(player, "gui-buttons.previous-page.name"))
                    .setLore(plugin.getLangFile().getStringList("gui-buttons.return.description"))
                    .hideTags()
                    .update(),
                player.getName().toLowerCase() + "-shops-list-back-button",
                event -> BkShop.getInstance().getMenuManager().openMainMenu(player));

        if (page - 1 < pagedList.getPages().size()) {
            pagedList.getPages().get(page).openGui(player);
        } else {
            player.sendMessage(BkShop.getInstance().getLangFile().get(player, "error.invalid-page").replace("{page}", String.valueOf(page)));
        }
    }

    public void buildPageFrame(Page tempPage) {
        ItemStack[] items = BkShop.getInstance().getFrameItems();
        for (int c = 0; c < 45; c++) {
            if (c == 0 || c == 8 || c == 36 || c == 44)
                tempPage.pageSetItem(c, new ItemBuilder(items[0]), "shops-frame-slot-" + c + "-page-" + tempPage.getPageNumber(), event -> {});
            if (c == 1 || c == 7 || c == 9 || c == 17 || c == 37 || c == 43 || c == 27 || c == 35)
                tempPage.pageSetItem(c, new ItemBuilder(items[1]), "shops-frame-slot-" + c + "-page-" + tempPage.getPageNumber(), event -> {});
        }
    }
}

