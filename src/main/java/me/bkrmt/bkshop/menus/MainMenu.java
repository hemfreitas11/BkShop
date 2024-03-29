package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.gui.GUI;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.xlibs.XMaterial;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.api.Shop;
import me.bkrmt.bkshop.api.ShopState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMenu implements me.bkrmt.bkshop.api.MainMenu {
    private final Page page;
    private final BkShop plugin;
    private final Player player;
    private int i = 1;

    public MainMenu(Player player) {
        this.player = player;
        this.plugin = BkShop.getInstance();
        page = new Page(BkShop.getInstance(), BkShop.getInstance().getAnimatorManager(), new GUI(
                BkShop.getInstance().getLangFile().get(player, "info.main-menu-title"), Rows.SIX
        ), 1);
        setStaticButtons();
    }

    @Override
    public Page getPage() {
        return page;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
    private void setStaticButtons() {
        setOpenButtons(XMaterial.RED_STAINED_GLASS_PANE.parseItem(), "gui-buttons.close.name", "gui-buttons.close.description", "-main-menu-close-shop", false, 30);
        setOpenButtons(XMaterial.GREEN_STAINED_GLASS_PANE.parseItem(), "gui-buttons.open.name", "gui-buttons.open.description", "-main-menu-open-shop", true, 32);

        ItemBuilder shopsMenu = new ItemBuilder(XMaterial.EMERALD)
                .setName(plugin.getLangFile().get(player, "gui-buttons.shops.name"))
                .setLore(plugin.getLangFile().getStringList(player, "info.shops-desc"))
                .hideTags();

        getPage().pageSetItem(22, shopsMenu, player.getName().toLowerCase() + "-main-menu-shops-list", event -> {
            if (plugin.getShopsManager().getShops().size() > 0) {
                MenuSound.CLICK.play(event.getWhoClicked());
                plugin.getMenuManager().openShopsMenu(player, 0);
            } else {
                MenuSound.ERROR.play(event.getWhoClicked());
                page.displayItemMessage(22, 1.5, ChatColor.RED, plugin.getLangFile().get(player, "error.no-created-shop"), null);
            }
        });
    }

    private void setOpenButtons(ItemStack greenPane, String s, String s2, String s3, boolean b, int slot) {
        ItemBuilder openBuilder = new ItemBuilder(greenPane)
                .setName(plugin.getLangFile().get(player, s))
                .setLore(plugin.getLangFile().getStringList(player, s2))
                .hideTags();

        getPage().pageSetItem(slot, openBuilder, player.getName().toLowerCase() + s3, event -> {
            Shop shop = plugin.getShopsManager().getShop(player.getUniqueId());
            if (shop != null) {
                if (event.getWhoClicked().hasPermission("bkshop.setshop")) {
                    if (b)
                        MenuSound.SUCCESS.play(event.getWhoClicked());
                    else
                        MenuSound.WARN.play(event.getWhoClicked());
                    shop.setShopState(b ? ShopState.OPEN : ShopState.CLOSED);
                    me.bkrmt.bkshop.api.MainMenu mainMenu = new MainMenu((Player) event.getWhoClicked());
                    mainMenu.openMenu();
                    mainMenu.getPage().displayItemMessage(event.getSlot(), 1.5, b ? ChatColor.GREEN : ChatColor.YELLOW, BkShop.getInstance().getLangFile().get(shop.getOwner(), "info.shop-" + (b ? "open" : "closed")), null);
                } else {
                    MenuSound.ERROR.play(event.getWhoClicked());
                    page.displayItemMessage(slot, 1.5, ChatColor.RED, plugin.getLangFile().get(player, "error.no-permission"), null);
                }
            } else {
                MenuSound.ERROR.play(event.getWhoClicked());
                page.displayItemMessage(slot, 1.5, ChatColor.RED, plugin.getLangFile().get(player, "error.create-shop-first"), null);
            }
        });
    }

    @Override
    public void openMenu() {
        Shop shop = plugin.getShopsManager().getShop(player.getUniqueId());

        setMainHead(shop, true);

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
                new ItemBuilder(XMaterial.PAPER)
                        .setName(plugin.getLangFile().get(player, "gui-buttons.info.name"))
                        .setLore(infoLore)
                        .hideTags(),
                player.getName().toLowerCase() + "-main-menu-info",
                event -> {
                    if (shop == null) {
                        MenuSound.ERROR.play(event.getWhoClicked());
                        page.displayItemMessage(24, 1.5, ChatColor.RED, plugin.getLangFile().get(player, "error.create-shop-first"), null);
                    }
                }
        );

        String visitColor = "a";
        if (shop != null && shop.isPublicData()) visitColor = "e";

        page.pageSetItem(40,
                new ItemBuilder(XMaterial.OAK_SIGN)
                        .setName(Utils.translateColor(plugin.getLangFile().get(player, "gui-buttons.visits.name", false).replace("{color}", visitColor)))
                        .setLore(visitLore)
                        .hideTags(),
                player.getName().toLowerCase() + "-main-menu-info",
                event -> {
                    if (shop != null) {
                        shop.setPublicData(!shop.isPublicData());
                        if (shop.isPublicData())
                            MenuSound.SUCCESS.play(event.getWhoClicked());
                        else
                            MenuSound.WARN.play(event.getWhoClicked());
                        me.bkrmt.bkshop.api.MainMenu mainMenu = new MainMenu((Player) event.getWhoClicked());
                        mainMenu.openMenu();
                        mainMenu.getPage().displayItemMessage(40, 1.5, shop.isPublicData() ? ChatColor.GREEN : ChatColor.YELLOW, BkShop.getInstance().getLangFile().get((Player) event.getWhoClicked(), "info.info-visit-" + (shop.isPublicData() ? "private" : "public") + "-message"), null);
                    } else {
                        MenuSound.ERROR.play(event.getWhoClicked());
                        page.displayItemMessage(40, 1.5, ChatColor.RED, plugin.getLangFile().get(player, "error.create-shop-first"), null);
                    }
                }
        );
        buildFrame(page, player.getName() + "-main-menu-shops");
        page.openGui(player);
    }

    private void setMainHead(Shop shop, boolean b) {
        String color = "7";
        if (shop != null) {
            color = shop.getColor();
        }

        String headName = b ? ChatColor.COLOR_CHAR + color + ChatColor.COLOR_CHAR + "l" + player.getName() :
            BkShop.getInstance().getLangFile().getLanguage().equals("pt_BR") ? "§6§lFeito por Bkr__" : "§6§lMade by Bkr__";

        getPage().pageSetHead(20, player, headName, b ? new ArrayList<>() : Collections.singletonList("§eBkShop v"+BkShop.getInstance().getDescription().getVersion()), player.getName().toLowerCase() + "-main-menu-head", event -> {
            MenuSound.SPECIAL.play(event.getWhoClicked());
            if (i == 6) {
                i = 0;
                setMainHead(shop, false);
                return;
            } else if (i == 0) {
                setMainHead(shop, true);
            }
            i++;
        });

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
