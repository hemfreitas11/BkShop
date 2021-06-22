package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.menus.ShopOptionsMenu;
import me.bkrmt.teleport.Teleport;
import me.bkrmt.teleport.TeleportType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCmd extends Executor {

    public ShopCmd(BkPlugin plugin, String langKey, String permission) {
        super(plugin, langKey, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage(getPlugin().getLangFile().get("error.no-permission"));
        } else {
            if (args.length == 1) {
                String fileName = args[0].toLowerCase() + ".yml";
                if (!getPlugin().getFile("shops", fileName.toLowerCase()).exists()) {
                    sender.sendMessage(getPlugin().getLangFile().get("error.unkown-shop").replace("{player}", args[0]));
                } else {
                    if (sender.hasPermission("bkshop.admin")) {
                        new ShopOptionsMenu("info.shop-options-title", ((Player) sender), args[0].toLowerCase()).openMenu();
                    } else {
                        goToShop(getPlugin(), sender, args[0].toLowerCase());
                    }
                }
            } else if (args.length == 0) {
                BkShop.getMainMenu().openMenu(sender);
            } else {
                sendUsage(sender);
            }
        }
        return true;
    }

    public static void goToShop(BkPlugin plugin, CommandSender sender, String shopOwner) {
        Configuration config = plugin.getConfigManager().getConfig("shops", shopOwner.toLowerCase() + ".yml");
        if (config.getBoolean("shop.open") || sender.hasPermission("bkshop.admin")) {
            config.set("shop.visits", config.getInt("shop.visits") + 1);
            config.set("shop.last-visitor", sender.getName());
            if (config.getBoolean("shop.public-visits")) BkShop.getShopsMenu().reloadMenu();
            new Teleport(plugin, sender, shopOwner.toLowerCase(), TeleportType.Loja);
        } else sender.sendMessage(plugin.getLangFile().get("error.closed-shop"));
    }
}