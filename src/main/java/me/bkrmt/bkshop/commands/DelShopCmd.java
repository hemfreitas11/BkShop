package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkshop.BkShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelShopCmd extends Executor {

    public DelShopCmd(BkPlugin plugin, String langKey, String permission) {
        super(plugin, langKey, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.no-permission"));
        } else {
            if (args.length == 0) {
                BkShop.getInstance().getShopsManager().deleteShop(sender, ((Player) sender).getUniqueId());
            } else {
                sendUsage(sender);
            }
        }
        return true;
    }
}
