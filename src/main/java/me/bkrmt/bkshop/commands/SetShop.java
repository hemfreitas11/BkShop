package me.bkrmt.bkshop.commands;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.command.Executor;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.BkShop;
import me.bkrmt.bkshop.events.PlayerSetShopEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
            sender.sendMessage(getPlugin().getLangFile().get("error.no-permission"));
        } else {
            if (args.length == 0) {
                sendUsage(sender);
            } else if (args.length == 1) {
                if (args[0].equals("shop") || args[0].equals("loja")) setarLoja(sender);
                else if (args[0].equals(getPlugin().getLangFile().get("commands." + getLangKey() + ".subcommands.color")))
                    sender.sendMessage(getPlugin().getLangFile().get("error.no-color"));
                else sendUsage(sender);
            } else {
                String fileName = sender.getName().toLowerCase() + ".yml";
                if (!getPlugin().getFile("shops", fileName).exists()) {
                    sender.sendMessage(getPlugin().getLangFile().get("error.create-shop-first"));
                } else {
                    if (args[0].equals(getPlugin().getLangFile().get("commands." + getLangKey() + ".subcommands.color"))) {
                        if (args.length == 2) {
                            if (Utils.isValidColor(args[1])) {
                                Configuration config = getPlugin().getConfig("shops", fileName);
                                config.set("shop.color", args[1]);
                                config.save(false);
                                BkShop.getShopsMenu().reloadMenu();
                                sender.sendMessage(Utils.translateColor(getPlugin().getLangFile().get("info.color-set", false).replace("{color}", args[1])));
                            } else {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('§',
                                        getPlugin().getLangFile().get("error.invalid-color.line1").replaceAll("&", "§").replace("{color}", args[1])));
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('§',
                                        getPlugin().getLangFile().get("error.invalid-color.line2").replaceAll("&", "§").replace("{color-simbol}", "&")));
                            }
                        }
                    } else if (args[0].equals(getPlugin().getLangFile().get("commands." + getLangKey() + ".subcommands.message"))) {
                        StringBuilder builder = new StringBuilder();
                        for (int c = 1; c < args.length; c++) {
                            builder.append(args[c]);
                            if (c < args.length - 1) builder.append(" ");
                        }
                        String mensagem = builder.toString();

                        if (mensagem.length() > getPlugin().getConfig().getInt("max-message-length"))
                            sender.sendMessage(getPlugin().getLangFile().get("error.large-message"));
                        else {
                            char[] messageChar = mensagem.toCharArray();
                            boolean invalid = false;
                            for (char c : messageChar) {
                                if (!(Character.isAlphabetic(c)) && !(Character.isDigit(c))) {
                                    if (c == '&' || c == ' ') continue;
                                    invalid = true;
                                    break;
                                }
                            }
                            if (invalid) sender.sendMessage(getPlugin().getLangFile().get("error.invalid-message"));
                            else {
                                Configuration config = getPlugin().getConfig("shops", fileName);
                                config.set("shop.message", mensagem);
                                config.save(false);
                                sender.sendMessage(Utils.translateColor(getPlugin().getLangFile().get("info.message-set", false).replace("{message}", mensagem)));
                                BkShop.getShopsMenu().reloadMenu();
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

    private void setarLoja(CommandSender sender) {
        Player player = (Player) sender;
        PlayerSetShopEvent setShopEvent = new PlayerSetShopEvent(player);
        getPlugin().getServer().getPluginManager().callEvent(setShopEvent);
        if (!setShopEvent.isCancelled()) {
            setLojaValues(sender);
            getPlugin().sendTitle((Player) sender, 5, 40, 10, getPlugin().getLangFile().get("info.shop-set"), "");
            BkShop.getShopsMenu().reloadMenu();
        }
    }

    private void setLojaValues(CommandSender sender) {
        Player player = (Player) sender;
        Location location = player.getLocation();
        Configuration config = getPlugin().getConfig("shops", player.getName().toLowerCase() + ".yml");
        config.set("shop.player", player.getName());
        config.setLocation("shop", location);
        config.set("shop.visits", 0);
        config.set("shop.last-visitor", sender.getName());
        config.set("shop.public-visits", false);
        config.set("shop.open", true);
        config.set("config-file-version", 1);
        config.save(false);
    }

}