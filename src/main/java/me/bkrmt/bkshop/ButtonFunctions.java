package me.bkrmt.bkshop;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.menus.ShopOptionsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.bkrmt.bkshop.BkShop.plugin;

public class ButtonFunctions implements Listener {
    @EventHandler
    public void onButtonClick(InventoryClickEvent event) {
        String inventoryName = ChatColor.stripColor(event.getView().getTitle()).trim();

        String lojaString = event.getWhoClicked().getName().toLowerCase() + ".yml";
        if (Utils.cleanName(inventoryName).equalsIgnoreCase(Utils.cleanName(ChatColor.stripColor(plugin.getLangFile().get("info.list-title")).trim()))) {
            event.setCancelled(true);
            if (event.getSlotType().equals(InventoryType.SlotType.CONTAINER)) {
                ItemStack button = event.getCurrentItem();
                if (button != null && !button.getType().equals(Material.AIR)) {
                    int paginaAtual = getPage(inventoryName);
                    if (button.getItemMeta().getDisplayName() == null) return;
                    if (ChatColor.stripColor(button.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(plugin.getLangFile().get("info.return-name")).trim())) {
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), plugin.getHandler().getSoundManager().getClick(), 0.5f, 0.1f);
                        if (paginaAtual == 0) BkShop.getMainMenu().openMenu(event.getWhoClicked());
                        else BkShop.getShopsMenu().displayMenu(((Player) event.getWhoClicked()), paginaAtual - 1);
                    } else if (ChatColor.stripColor(button.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(plugin.getLangFile().get("info.next-name")).trim())) {
                        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), plugin.getHandler().getSoundManager().getClick(), 0.5f, 1f);
                        BkShop.getShopsMenu().displayMenu(((Player) event.getWhoClicked()), paginaAtual + 1);
                    } else if (!button.getItemMeta().getDisplayName().equalsIgnoreCase(" ")) {
                        if (event.getWhoClicked().hasPermission("bkshop.admin")) {
                            if (new File(plugin.getDataFolder() + File.separator + "shops", ChatColor.stripColor(button.getItemMeta().getDisplayName()).toLowerCase() + ".yml").exists()) {
                                new ShopOptionsMenu("info.shop-options-title", ((Player) event.getWhoClicked()), ChatColor.stripColor(button.getItemMeta().getDisplayName())).openMenu();
                            }
                        } else {
                            event.getWhoClicked().closeInventory();
                            String shopCmd = plugin.getLangFile().get("commands.shop.command") + " ";
                            String lojaName = ChatColor.stripColor(button.getItemMeta().getDisplayName());
                            ((Player) event.getWhoClicked()).performCommand(shopCmd + lojaName);
                        }
                    }
                }
            }
        } else if (Utils.cleanName(inventoryName).equalsIgnoreCase(ChatColor.stripColor(plugin.getLangFile().get("info.main-menu-title")).trim())) {
            event.setCancelled(true);
            if (event.getSlotType().equals(InventoryType.SlotType.CONTAINER)) {
                ItemStack button = event.getCurrentItem();
                if (button != null && !button.getType().equals(Material.AIR)) {
                    if (button.getType().equals(Material.EMERALD)) {
                        event.getWhoClicked().closeInventory();
                        if (!BkShop.getShopsMenu().isEmpty()) {
                            BkShop.getShopsMenu().displayMenu(((Player) event.getWhoClicked()), 0);
                        } else {
                            event.getWhoClicked().sendMessage(plugin.getLangFile().get("error.no-created-shop"));
                        }
                    } else if (button.getItemMeta().getDisplayName() == null) return;
                    else if (button.getType().equals(plugin.getHandler().getItemManager().getSign())) {
                        if (plugin.getFile("shops", lojaString.toLowerCase()).exists()) {
                            Configuration config = plugin.getConfig("shops", lojaString.toLowerCase());
                            config.set("shop.public-visits", !config.getBoolean("shop.public-visits"));
                            config.save(false);
                            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), plugin.getHandler().getSoundManager().getClick(), 0.5f, 1f);
                            if (!config.getBoolean("shop.public-visits")) {
                                event.getWhoClicked().sendMessage(plugin.getLangFile().get("info.info-visit-public-message"));
                            } else {
                                event.getWhoClicked().sendMessage(plugin.getLangFile().get("info.info-visit-private-message"));
                            }
                            event.getWhoClicked().closeInventory();
                            BkShop.getShopsMenu().reloadMenu();
                        } else {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage(plugin.getLangFile().get("error.create-shop-first"));
                        }
                    } else if (ChatColor.stripColor(button.getItemMeta().getDisplayName()).contains(ChatColor.stripColor(plugin.getLangFile().get("info.close-name")))) {
                        if (plugin.getFile("shops", lojaString.toLowerCase()).exists()) {
                            BkShop.closeShop(((Player) event.getWhoClicked()), lojaString.toLowerCase());
                        } else {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage(plugin.getLangFile().get("error.create-shop-first"));
                        }
                    } else if (ChatColor.stripColor(button.getItemMeta().getDisplayName()).contains(ChatColor.stripColor(plugin.getLangFile().get("info.open-name")))) {
                        if (plugin.getFile("shops", lojaString.toLowerCase()).exists()) {
                            BkShop.openShop(((Player) event.getWhoClicked()), lojaString.toLowerCase());
                        } else {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage(plugin.getLangFile().get("error.create-shop-first"));
                        }
                    }

                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), plugin.getHandler().getSoundManager().getClick(), 0.5f, 1f);
                }
            }
        }
    }

    private String cleanName(String name) {
        String temp = name.replace("0", "");
        temp = temp.replace("1", "");
        temp = temp.replace("2", "");
        temp = temp.replace("3", "");
        temp = temp.replace("4", "");
        temp = temp.replace("5", "");
        temp = temp.replace("6", "");
        temp = temp.replace("7", "");
        temp = temp.replace("8", "");
        temp = temp.replace("9", "");
        temp = temp.replace("{", "");
        temp = temp.replace("}", "");
        temp = temp.replace("current-page", "");
        temp = temp.replace("total-pages", "");
        temp = temp.replace("/", "");
        temp = temp.replace("\\", "");
        temp = temp.replace("+", "");
        temp = temp.trim();
        return temp;
    }

    private int getPage(String inventoryName) {
        int page = 0;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(inventoryName);
        while (m.find()) {
            page = Integer.parseInt(m.group()) - 1;
            break;
        }
        return page;
    }

}
