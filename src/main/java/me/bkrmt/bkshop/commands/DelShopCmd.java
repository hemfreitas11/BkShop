package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.events.PlayerDelShopEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class DelShopCmd extends Executor {

    public DelShopCmd(BkPlugin plugin, String langKey, String permission) {
        super(plugin, langKey, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage(getPlugin().getLangFile().get("error.no-permission"));
        } else {
            if (args.length == 0) {
                deleteShop(getPlugin(), sender, sender.getName());
            } else {
                sendUsage(sender);
            }
        }
        return true;
    }

    public static void deleteShop(BkPlugin plugin, CommandSender sender, String shopOwner) {
        File shopFile = plugin.getFile("shops", shopOwner.toLowerCase() + ".yml");
        if (!shopFile.exists()) {
            sender.sendMessage(plugin.getLangFile().get("error.no-shop"));
        } else {
            PlayerDelShopEvent delShopEvent = new PlayerDelShopEvent((Player) sender, YamlConfiguration.loadConfiguration(shopFile));
            plugin.getServer().getPluginManager().callEvent(delShopEvent);
            if (!delShopEvent.isCancelled()) {
                shopFile.delete();
                BkShop.getShopsMenu().reloadMenu();
                sender.sendMessage(plugin.getLangFile().get("info.shop-deleted"));
            }
        }
    }
}
