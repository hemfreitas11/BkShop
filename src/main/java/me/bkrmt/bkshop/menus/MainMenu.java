package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static me.bkrmt.bkshop.BkShop.plugin;

public class MainMenu extends ShopMenu {
    Inventory menu;

    public MainMenu() {
        super(plugin, "info.main-menu-title");
    }

    @Override
    public void buildMenu() {
        buildFrame();
        setStaticButtons();
    }

    public void setStaticButtons() {
        ItemStack fecharloja = plugin.getHandler().getItemManager().getRedPane();
        ItemMeta metaFechar = fecharloja.getItemMeta();
        metaFechar.setDisplayName(plugin.getLangFile().get("info.close-name"));
        metaFechar.setLore(Collections.singletonList(plugin.getLangFile().get("info.close-desc")));
        if (plugin.getNmsVer().number > 7) metaFechar.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        fecharloja.setItemMeta(metaFechar);
        getMenu().setItem(30, fecharloja);

        ItemStack abrirloja = plugin.getHandler().getItemManager().getGreenPane();
        ItemMeta metaAbrir = abrirloja.getItemMeta();
        metaAbrir.setDisplayName(plugin.getLangFile().get("info.open-name"));
        metaAbrir.setLore(Collections.singletonList(plugin.getLangFile().get("info.open-desc")));
        if (plugin.getNmsVer().number > 7) metaAbrir.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        abrirloja.setItemMeta(metaAbrir);
        getMenu().setItem(32, abrirloja);

        getMenu().setItem(22, Utils.createItem(plugin, plugin.getLangFile().get("info.shops-name"), Material.EMERALD, plugin.getLangFile().get("info.shops-desc")));
    }

    @Override
    public void openMenu(CommandSender sender) {
        String lojaString = sender.getName().toLowerCase() + ".yml";
        File loja = plugin.getFile("shops", lojaString.toLowerCase());

        String cor = "&7";
        if (loja.exists()) {
            FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(loja);
            cor = tempConfig.getString("shop.color") != null ? "&" + tempConfig.getString("shop.color") : "&7";
        }

        String headName = cor + "&l" + sender.getName();

        getMenu().setItem(20, Utils.createItem(plugin, Utils.translateColor(headName), plugin.getHandler().getItemManager().getHead().getType()));

        ArrayList<String> visitLore = new ArrayList<>();
        ArrayList<String> infoLore = new ArrayList<>();
        Configuration lojaFile;
        if (loja.exists()) {
            lojaFile = plugin.getConfig("shops", lojaString.toLowerCase());
            infoLore.add(plugin.getLangFile().get("info.info-status").replace("{status}",
                    lojaFile.getBoolean("shop.open") ? plugin.getLangFile().get("info.info-open") : plugin.getLangFile().get("info.info-closed")));

            infoLore.add(plugin.getLangFile().get("info.info-visit-status").replace("{info-visibility}",
                    lojaFile.getBoolean("shop.public-visits") ? plugin.getLangFile().get("info.info-visit-public") : plugin.getLangFile().get("info.info-visit-private")));

            infoLore.add(plugin.getLangFile().get("info.info-visits").replace("{visits}", String.valueOf(lojaFile.getInt("shop.visits"))));

            infoLore.add(plugin.getLangFile().get("info.info-last-visit").replace("{player}", lojaFile.getString("shop.last-visitor")));

            if (lojaFile.getString("shop.message") != null)
                infoLore.add(plugin.getLangFile().get("info.info-desc").replace("{message}", Utils.translateColor(lojaFile.getString("shop.message"))));

            visitLore.add(lojaFile.getBoolean("shop.public-visits") ?
                    Utils.translateColor(plugin.getLangFile().get("info.info-disable-visists", false).replace("{color}", lojaFile.getBoolean("shop.public-visits") ? "e" : "a")) :
                    Utils.translateColor(plugin.getLangFile().get("info.info-enable-visists", false).replace("{color}", lojaFile.getBoolean("shop.public-visits") ? "e" : "a")));

        } else {
            infoLore.add(ChatColor.GRAY + ChatColor.stripColor(plugin.getLangFile().get("error.no-shop")));
            visitLore.add(ChatColor.GRAY + ChatColor.stripColor(plugin.getLangFile().get("error.no-shop")));
        }

        getMenu().setItem(24, Utils.createItem(plugin, plugin.getLangFile().get("info.info-name"), Material.PAPER, infoLore));

        String visitColor = "a";
        if (loja.exists()) {
            lojaFile = plugin.getConfig("shops", lojaString.toLowerCase());
            if (lojaFile.getBoolean("shop.public-visits")) visitColor = "e";
        }
        getMenu().setItem(40, Utils.createItem(plugin, Utils.translateColor(plugin.getLangFile().get("info.visits-name", false).replace("{color}", visitColor)), plugin.getHandler().getItemManager().getSign(), visitLore));
        ((Player) sender).openInventory(getMenu());
    }
}
