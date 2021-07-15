package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.api.Shop;
import me.bkrmt.bkshop.menus.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
            sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.no-permission"));
        } else {
            if (args.length == 1) {
                OfflinePlayer shopOwner = Bukkit.getOfflinePlayer(args[0]);
                if (shopOwner != null) {
                    Shop shop = BkShop.getInstance().getShopsManager().getShop(shopOwner.getName());
                    if (shop != null) {
                        if (sender.hasPermission("bkshop.admin")) {
                            shop.openOptionsMenu((Player) sender, 1);
                        } else {
                            shop.teleportToShop((Player) sender);
                        }
                    } else {
                        sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.unkown-shop").replace("{player}", args[0]));
                    }
                } else {
                    sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.unkown-shop").replace("{player}", args[0]));
                }
            } else if (args.length == 0) {
                new MainMenu((Player) sender).openMenu();
            } else {
                sendUsage(sender);
            }
        }
        return true;
    }
}