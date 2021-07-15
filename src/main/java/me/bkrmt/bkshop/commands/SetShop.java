package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.api.Shop;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetShop extends Executor {
    public SetShop(BkPlugin plugin, String langKey, String permission) {
        super(plugin, langKey, permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.no-permission"));
        } else {
            if (args.length == 0) {
                sendUsage(sender);
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase(getPlugin().getLangFile().get(((Player) sender), "commands." + getLangKey() + ".subcommands.shop"))) BkShop.getInstance().getShopsManager().setShop(sender);
                else if (args[0].equals(getPlugin().getLangFile().get(((Player) sender), "commands." + getLangKey() + ".subcommands.color")))
                    sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.no-color"));
                else sendUsage(sender);
            } else {
                Player player = (Player) sender;
                Shop shop = BkShop.getInstance().getShopsManager().getShop(player.getUniqueId());
                if (shop == null) {
                    sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.create-shop-first"));
                } else {
                    if (args[0].equals(getPlugin().getLangFile().get(((Player) sender), "commands." + getLangKey() + ".subcommands.color"))) {
                        if (args.length == 2) {
                            if (Utils.isValidColor(args[1])) {
                                shop.setColor(args[1]);
                                sender.sendMessage(Utils.translateColor(getPlugin().getLangFile().get(((Player) sender), "info.color-set", false).replace("{color}", args[1])));
                            } else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('§',
                                        getPlugin().getLangFile().get(((Player) sender), "error.invalid-color.line1").replaceAll("&", "§").replace("{color}", args[1])));
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('§',
                                        getPlugin().getLangFile().get(((Player) sender), "error.invalid-color.line2").replaceAll("&", "§").replace("{color-simbol}", "&")));
                            }
                        }
                    } else if (args[0].equals(getPlugin().getLangFile().get(((Player) sender), "commands." + getLangKey() + ".subcommands.message"))) {
                        StringBuilder builder = new StringBuilder();
                        for (int c = 1; c < args.length; c++) {
                            builder.append(args[c]);
                            if (c < args.length - 1) builder.append(" ");
                        }
                        String description = builder.toString();

                        if (description.length() > getPlugin().getConfigManager().getConfig().getInt("max-message-length"))
                            sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.large-message"));
                        else {
                            char[] messageChar = description.toCharArray();
                            boolean invalid = false;
                            for (char c : messageChar) {
                                if (!(Character.isAlphabetic(c)) && !(Character.isDigit(c))) {
                                    if (c == '&' || c == ' ') continue;
                                    invalid = true;
                                    break;
                                }
                            }
                            if (invalid) sender.sendMessage(getPlugin().getLangFile().get(((Player) sender), "error.invalid-message"));
                            else {
                                shop.setDescription(description);
                                sender.sendMessage(Utils.translateColor(getPlugin().getLangFile().get(((Player) sender), "info.message-set", false).replace("{message}", description)));
                            }
                        }
                    } else {
                        sendUsage(sender);
                    }
                }
            }
        }

        return true;
    }

}