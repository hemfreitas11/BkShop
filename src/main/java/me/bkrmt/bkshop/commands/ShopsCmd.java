package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkshop.BkShop;
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
            sender.sendMessage(getPlugin().getLangFile().get("error.no-permission"));
        } else {
            if (!BkShop.getShopsMenu().isEmpty()) {
                if (args.length == 0) {
                    BkShop.getShopsMenu().displayMenu(((Player) sender), 0);
                } else {
                    int pagina = Integer.parseInt(args[0]) <= 0 ? 1 : Integer.parseInt(args[0]);
                    BkShop.getShopsMenu().displayMenu(((Player) sender), pagina - 1);
                }
            } else {
                sender.sendMessage(plugin.getLangFile().get("error.no-created-shop"));
            }
        }
        return true;
    }
}
