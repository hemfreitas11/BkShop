package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkshop.BkShop;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.bkrmt.bkshop.BkShop.plugin;

public class ShopsCmd extends Executor {

    public ShopsCmd(BkPlugin plugin, String langKey, String permission) {
        super(plugin, langKey, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!hasPermission(sender)) {
            sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.no-permission"));
        } else {
            if (BkShop.getInstance().getShopsManager().getShops().size() > 0) {
                if (args.length == 0) {
                    BkShop.getInstance().getMenuManager().openShopsMenu(((Player) sender), 0);
                } else {
                    int page = 1;
                    if (StringUtils.isNumeric(args[0])) {
                        page = Integer.parseInt(args[0]) <= 0 ? 1 : Integer.parseInt(args[0]);
                    }
                    BkShop.getInstance().getMenuManager().openShopsMenu(((Player) sender), page-1);
                }
            } else {
                sender.sendMessage(plugin.getLangFile().get(((Player) sender), "error.no-created-shop"));
            }
        }
        return true;
    }
}
