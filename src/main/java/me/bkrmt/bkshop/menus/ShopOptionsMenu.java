package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkcore.menu.MenuListener;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.commands.ShopCmd;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.bkrmt.bkshop.BkShop.plugin;

public class ShopOptionsMenu extends ShopMenu {
    private final Player shopUser;
    private final String shopOwner;

    public ShopOptionsMenu(String titleKey, Player shopUser, String shopOwner) {
        super(plugin, titleKey);
        this.shopOwner = shopOwner;
        this.shopUser = shopUser;
        if (!plugin.getFile("shops", shopOwner.toLowerCase() + ".yml").exists()) {
            shopUser.closeInventory();
            return;
        }
        setButtons();

        setListener(new MenuListener(plugin, titleKey) {
            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (isValidClick(event)) {
                    ItemStack button = event.getCurrentItem();
                    if (button.getType().equals(plugin.getHandler().getItemManager().getPearl())) {
                        cancel();
                        event.getWhoClicked().closeInventory();
                        ShopCmd.goToShop(plugin, event.getWhoClicked(), ChatColor.stripColor(button.getItemMeta().getDisplayName()).toLowerCase());
                    } else if (button.getType().equals(Material.TNT)) {
                        cancel();
                        new DeleteMenu("info.delete-confirm-title", ChatColor.stripColor(
                                ChatColor.stripColor(Utils.getOnCoordinate(event.getInventory(), 3, 5).getItemMeta().getDisplayName()).toLowerCase()))
                                .openMenu(event.getWhoClicked());
                    } else if (ChatColor.stripColor(Utils.translateColor(button.getItemMeta().getDisplayName())).equals(ChatColor.stripColor(Utils.translateColor(plugin.getLangFile().get("info.admin-close-shop"))))) {
                        cancel();
                        BkShop.closeShop(((Player) event.getWhoClicked()),
                                ChatColor.stripColor(Utils.getOnCoordinate(event.getInventory(), 3, 5).getItemMeta().getDisplayName()).toLowerCase() + ".yml");
                    } else if (ChatColor.stripColor(Utils.translateColor(button.getItemMeta().getDisplayName())).equals(ChatColor.stripColor(Utils.translateColor(plugin.getLangFile().get("info.admin-open-shop"))))) {
                        cancel();
                        BkShop.openShop(((Player) event.getWhoClicked()),
                                ChatColor.stripColor(Utils.getOnCoordinate(event.getInventory(), 3, 5).getItemMeta().getDisplayName()).toLowerCase() + ".yml");
                    }
                }
            }
        });

        plugin.getServer().getPluginManager().registerEvents(getListener(), plugin);
    }

    private void setButtons() {
        String customColor = "7";
        Configuration configFile = plugin.getConfigManager().getConfig("shops", shopOwner.toLowerCase() + ".yml");
        if (configFile.getString("shop.color") != null) customColor = configFile.getString("shop.color");

        ItemStack goToShop = Utils.createItem(plugin, Utils.translateColor("&" + customColor + "&l" + configFile.getString("shop.player")),
                plugin.getHandler().getItemManager().getPearl(),
                Utils.translateColor("&" + customColor + plugin.getLangFile().get("info.admin-go-to-shop")));
        setOnCoordinate(goToShop, 3, 5);

        ItemStack closeShop = plugin.getHandler().getItemManager().getRedPane();
        ItemMeta itemMeta = closeShop.getItemMeta();
        itemMeta.setDisplayName(Utils.translateColor(plugin.getLangFile().get("info.admin-close-shop")));
        closeShop.setItemMeta(itemMeta);
        setOnCoordinate(closeShop, 4, 4);

        ItemStack openShop = plugin.getHandler().getItemManager().getGreenPane();
        itemMeta = openShop.getItemMeta();
        itemMeta.setDisplayName(Utils.translateColor(plugin.getLangFile().get("info.admin-open-shop")));
        openShop.setItemMeta(itemMeta);
        setOnCoordinate(openShop, 4, 6);

        ItemStack deleteShop = Utils.createItem(plugin, Utils.translateColor(plugin.getLangFile().get("info.admin-delete-shop")),
                Material.TNT,
                new ArrayList<>());
        setOnCoordinate(deleteShop, 5, 5);
    }

    public void openMenu() {
        shopUser.openInventory(getMenu());
    }
}
