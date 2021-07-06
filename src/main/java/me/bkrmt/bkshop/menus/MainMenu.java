package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.Shop;
import me.bkrmt.bkshop.ShopState;
import me.bkrmt.opengui.GUI;
import me.bkrmt.opengui.ItemBuilder;
import me.bkrmt.opengui.Page;
import me.bkrmt.opengui.Rows;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    private final Page page;
    private final BkShop plugin;
    private final Player player;

    protected MainMenu(Player player) {
        this.player = player;
        this.plugin = BkShop.getInstance();
        page = new Page(BkShop.getInstance(), BkShop.getInstance().getAnimatorManager(), new GUI(
                BkShop.getInstance().getLangFile().get(player, "info.main-menu-title"), Rows.SIX
        ), 1);
        setStaticButtons();
    }

    protected Page getPage() {
        return page;
    }

    protected Player getPlayer() {
        return player;
    }

    private void setStaticButtons() {
        setOpenButtons(plugin.getHandler().getItemManager().getRedPane(), "gui-buttons.close.name", "gui-buttons.close.description", "-main-menu-close-shop", false, 30);
        setOpenButtons(plugin.getHandler().getItemManager().getGreenPane(), "gui-buttons.open.name", "gui-buttons.open.description", "-main-menu-open-shop", true, 32);

        ItemBuilder shopsMenu = new ItemBuilder(Material.EMERALD)
                .setName(plugin.getLangFile().get(player, "gui-buttons.shops.name"))
                .setUnchangedName(plugin.getLangFile().get(player, "gui-buttons.shops.name"))
                .setLore(plugin.getLangFile().getStringList(player, "info.shops-desc"))
                .hideTags()
                .update();

        getPage().pageSetItem(22, shopsMenu, player.getName().toLowerCase() + "-main-menu-shops-list", event -> {
            if (plugin.getShopsManager().getShops().size() > 0) {
                plugin.getMenuManager().openShopsMenu(player, 0);
            } else {
                event.getWhoClicked().sendMessage(plugin.getLangFile().get(player, "error.no-created-shop"));
            }
        });
    }

    private void setOpenButtons(ItemStack greenPane, String s, String s2, String s3, boolean b, int slot) {
        ItemBuilder openBuilder = new ItemBuilder(greenPane)
                .setName(plugin.getLangFile().get(player, s))
                .setUnchangedName(plugin.getLangFile().get(player, s))
                .setLore(plugin.getLangFile().getStringList(player, s2))
                .hideTags()
                .update();

        getPage().pageSetItem(slot, openBuilder, player.getName().toLowerCase() + s3, event -> {
            Shop shop = plugin.getShopsManager().getShop(player.getUniqueId());
            if (shop != null) {
                shop.setShopState(((Player) event.getWhoClicked()), b ? ShopState.OPEN : ShopState.CLOSED);
                BkShop.getInstance().getMenuManager().openMainMenu((Player) event.getWhoClicked());
            } else {
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage(plugin.getLangFile().get(player, "error.create-shop-first"));
            }
        });
    }

    protected void openMenu() {
        Shop shop = plugin.getShopsManager().getShop(player.getUniqueId());

        String color = "7";
        if (shop != null) {
            color = shop.getColor();
        }

        String headName = "§" + color + "§l" + player.getName();
        ItemBuilder headBuilder = new ItemBuilder(plugin.createHead(player.getUniqueId(), headName, new ArrayList<>()))
                .setUnchangedName(headName);

        getPage().pageSetItem(20, headBuilder, player.getName().toLowerCase() + "-main-menu-head", event -> {});

        List<String> visitLore = new ArrayList<>();
        List<String> infoLore = new ArrayList<>();
        if (shop != null) {
            infoLore.add(plugin.getLangFile().get(player, "info.info-status").replace("{status}",
                    shop.getShopState().equals(ShopState.OPEN) ? plugin.getLangFile().get(player, "info.info-open") : plugin.getLangFile().get(player, "info.info-closed")));
            infoLore.add(plugin.getLangFile().get(player, "info.info-visit-status").replace("{info-visibility}",
                    shop.isPublicData() ? plugin.getLangFile().get(player, "info.info-visit-public") : plugin.getLangFile().get(player, "info.info-visit-private")));
            infoLore.add(plugin.getLangFile().get(player, "info.info-visits").replace("{visits}", String.valueOf(shop.getVisits())));
            infoLore.add(plugin.getLangFile().get(player, "info.info-last-visit").replace("{player}", shop.getLastVisitor()));

            if (shop.getDescription() != null)
                infoLore.add(plugin.getLangFile().get(player, "info.info-desc").replace("{message}", Utils.translateColor(shop.getDescription())));

            visitLore.add(shop.isPublicData() ?
                    Utils.translateColor(plugin.getLangFile().get(player, "info.info-disable-visists", false).replace("{color}", shop.isPublicData() ? "e" : "a")) :
                    Utils.translateColor(plugin.getLangFile().get(player, "info.info-enable-visists", false).replace("{color}", shop.isPublicData() ? "e" : "a")));
        } else {
            infoLore.add(ChatColor.GRAY + ChatColor.stripColor(plugin.getLangFile().get(player, "error.no-shop")));
            visitLore.add(ChatColor.GRAY + ChatColor.stripColor(plugin.getLangFile().get(player, "error.no-shop")));
        }
        
        page.pageSetItem(24, 
            new ItemBuilder(Material.PAPER)
                .setName(plugin.getLangFile().get(player, "gui-buttons.info.name"))
                .setUnchangedName(plugin.getLangFile().get(player, "gui-buttons.info.name"))
                .setLore(infoLore)
                .hideTags()
                .update(),
            player.getName().toLowerCase() + "-main-menu-info",
            event -> {}
        );

        String visitColor = "a";
        if (shop != null && shop.isPublicData()) visitColor = "e";
        
        page.pageSetItem(40, 
            new ItemBuilder(plugin.getHandler().getItemManager().getSign())
                .setName(Utils.translateColor(plugin.getLangFile().get(player, "gui-buttons.visits.name", false).replace("{color}", visitColor)))
                .setUnchangedName(Utils.translateColor(plugin.getLangFile().get(player, "gui-buttons.visits.name", false).replace("{color}", visitColor)))
                .setLore(visitLore)
                .hideTags()
                .update(),
            player.getName().toLowerCase() + "-main-menu-info",
            event -> {
                if (shop != null) {
                    shop.setPublicData((Player) event.getWhoClicked(), !shop.isPublicData());
                    BkShop.getInstance().getMenuManager().openMainMenu((Player) event.getWhoClicked());
                }
            }
        );
        buildFrame(page, player.getName() + "-main-menu-shops");
        page.openGui(player);
    }

    private void buildFrame(Page page, String identifier) {
        ItemStack[] items = BkShop.getInstance().getFrameItems();
        for (int c = 0; c < 54; c++) {
            if (c == 0 || c == 8 || c == 45 || c == 53)
                page.pageSetItem(c, new ItemBuilder(items[0]), identifier + "-slot-" + c + "-page-" + page.getPageNumber(), event -> {
                });

            if (c == 1 || c == 4 || c == 7 || c == 9 || c == 17 || c == 36 || c == 46 || c == 52 || c == 49 || c == 44)
                page.pageSetItem(c, new ItemBuilder(items[1]), identifier + "-slot-" + c + "-page-" + page.getPageNumber(), event -> {
                });

            if (c == 2 || c == 3 || c == 5 || c == 6 || c == 47 || c == 48 || c == 50 || c == 51 || c == 10 || c == 16 || c == 37 || c == 43 || c == 18 || c == 26 || c == 27 || c == 35)
                page.pageSetItem(c, new ItemBuilder(items[2]), identifier + "-slot-" + c + "-page-" + page.getPageNumber(), event -> {
                });
        }
    }
}
