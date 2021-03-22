package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.menu.MenuListener;
import me.bkrmt.bkshop.commands.DelShopCmd;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.bkrmt.bkshop.BkShop.plugin;

public class DeleteMenu extends ShopMenu {

    private final String shopOwner;

    public DeleteMenu(String titleKey, String shopOwner) {
        super(plugin, titleKey);
        this.shopOwner = shopOwner;

        setOnCoordinate(Utils.createItem(plugin,
                plugin.getLangFile().get("info.delete-sign"),
                plugin.getHandler().getItemManager().getSign(),
                plugin.getLangFile().get("info.delete-sign-lore")), 3, 5);

        setOnCoordinate(Utils.createItem(plugin,
                plugin.getLangFile().get("info.delete-confirm"),
                Material.EMERALD_BLOCK,
                new ArrayList<>()), 4, 4);

        setOnCoordinate(Utils.createItem(plugin,
                plugin.getLangFile().get("info.delete-decline"),
                Material.REDSTONE_BLOCK,
                new ArrayList<>()), 4, 6);

        setListener(new MenuListener(plugin, titleKey) {
            @EventHandler
            public void onClick(InventoryClickEvent event) {
                if (isValidClick(event)) {
                    ItemStack button = event.getCurrentItem();
                    if (button.getType().equals(Material.EMERALD_BLOCK)) {
                        cancel();
                        event.getWhoClicked().closeInventory();
                        DelShopCmd.deleteShop(plugin, event.getWhoClicked(), ChatColor.stripColor(shopOwner).toLowerCase());
                    } else if (button.getType().equals(Material.REDSTONE_BLOCK)) {
                        cancel();
                        event.getWhoClicked().closeInventory();
                    }
                }
            }
        });
        plugin.getServer().getPluginManager().registerEvents(getListener(), plugin);
    }
}
