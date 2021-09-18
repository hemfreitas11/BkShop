package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.PagedItem;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.xlibs.XMaterial;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.api.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListSet;

import static me.bkrmt.bkshop.BkShop.getInstance;
import static me.bkrmt.bkshop.BkShop.plugin;

public class MenuManager implements me.bkrmt.bkshop.api.MenuManager {
    @Override
    public void openShopsMenu(Player player, int page) {
        ArrayDeque<PagedItem> shops = new ArrayDeque<>();
        ConcurrentSkipListSet<Shop> shopList = getInstance().getShopsManager().getShops();
        shopList.forEach(Shop::reloadDisplayItem);
        Collections.addAll(shops, shopList.toArray(new Shop[0]));

        PagedList pagedList = new PagedList(getInstance(), player, "shops-list", shops)
                .setGuiRows(Rows.FIVE)
                .setListRows(3)
                .setStartingSlot(11)
                .setListRowSize(5)
                .setButtonSlots(18, 26)
                .setGuiTitle(BkShop.getInstance().getLangFile().get(null, "info.list-title"))
                .buildMenu();
        for (Page tempPage : pagedList.getPages()) {
            buildPageFrame(tempPage);
        }

        if (pagedList.getPages().size() > 0)
            pagedList.getPages().get(0).setItemOnXY(1, 3,
                new ItemBuilder(XMaterial.RED_WOOL)
                    .setName(plugin.getLangFile().get(player, "gui-buttons.previous-page.name"))
                    .setLore(plugin.getLangFile().getStringList("gui-buttons.return.description"))
                    .hideTags(),
                player.getName().toLowerCase() + "-shops-list-back-button",
                event -> {
                    MenuSound.BACK.play(event.getWhoClicked());
                    new MainMenu(player).openMenu();
                });

        if (page - 1 < pagedList.getPages().size()) {
            pagedList.getPages().get(page).openGui(player);
        } else {
            player.sendMessage(BkShop.getInstance().getLangFile().get(player, "error.invalid-page").replace("{page}", String.valueOf(page)));
        }
    }

    @Override
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

