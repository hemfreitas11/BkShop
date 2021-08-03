package me.bkrmt.bkshop;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.CommandModule;
import me.bkrmt.bkcore.command.HelpCmd;
import me.bkrmt.bkcore.command.ReloadCmd;
import me.bkrmt.bkcore.textanimator.AnimatorManager;
import me.bkrmt.bkshop.api.MenuManager;
import me.bkrmt.bkshop.api.ShopsManager;
import me.bkrmt.bkshop.commands.DelShopCmd;
import me.bkrmt.bkshop.commands.SetShop;
import me.bkrmt.bkshop.commands.ShopCmd;
import me.bkrmt.bkshop.commands.ShopsCmd;
import me.bkrmt.opengui.OpenGUI;
import me.bkrmt.teleport.TeleportCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BkShop extends BkPlugin {
    public static BkShop plugin;
    private int frameType;
    private AnimatorManager animatorManager;
    private MenuManager menuManager;
    private ShopsManager shopsManager;

    @Override
    public void onEnable() {
        plugin = this;
        OpenGUI.INSTANCE.register(this);
        animatorManager = new AnimatorManager(this);
        start(true);
        setRunning(true);

        sendConsoleMessage("§b__________ __     §1________.__");
        sendConsoleMessage("§b\\______   |  | __§1/   _____|  |__   ____ ______");
        sendConsoleMessage("§b |    |  _|  |/ /§1\\_____  \\|  |  \\ /  _ \\____  \\");
        sendConsoleMessage("§b |    |   |    < §1/        |   |  (  |_| |  |_| |");
        sendConsoleMessage("§b |______  |__|_ §1/_______  |___|  /\\____/|   __/");
        sendConsoleMessage("§b        \\/     §b\\/       §1\\/     \\/       |__|");
        sendConsoleMessage("");
        sendConsoleMessage("      §b© BkPlugins | discord.io/bkplugins");
        sendConsoleMessage("");
        sendConsoleMessage(InternalMessages.PLUGIN_STARTING.getMessage(this));
        
        File shopsFolder = getFile("shops", "");
        if (!shopsFolder.exists()) {
            if (!shopsFolder.mkdir()) {
                try {
                    throw new IOException("The plugin was not started because the folder \"shops\" could not be created.");
                } catch (IOException e) {
                    e.printStackTrace();
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                    return;
                }
            }
        }

        getCommandMapper()
                .addCommand(new CommandModule(new HelpCmd(plugin, "bkshop", ""), (a, b, c, d) -> Collections.singletonList("")))
                .addCommand(new CommandModule(new ReloadCmd(plugin, "bkshopreload", "bkshop.reload"), (a, b, c, d) -> Collections.singletonList("")))
                .addCommand(new CommandModule(new ShopsCmd(plugin, "shops", "bkshop.shops"), (a, b, c, d) -> Collections.singletonList("")))
                .addCommand(new CommandModule(new DelShopCmd(plugin, "delshop", "bkshop.delshop"), (a, b, c, d) -> Collections.singletonList("")))
                .addCommand(new CommandModule(new ShopCmd(plugin, "shop", "bkshop.shop"), (sender, b, c, args) -> {
                    List<String> completions = new ArrayList<>();
                    if (sender.hasPermission("bkshop.shop")) {
                        if (args.length == 1) {
                            String partialCommand = args[0];
                            List<String> lojas = new ArrayList<>();
                            getShopsManager().getShops().forEach(shop -> lojas.add(shop.getOwnerName()));
                            StringUtil.copyPartialMatches(partialCommand, lojas, completions);
                        }
                    }
                    Collections.sort(completions);

                    return completions;
                }))
                .addCommand(new CommandModule(new SetShop(plugin, "setshop", "bkshop.setshop"), (sender, b, c, args) -> {
                    List<String> completions = new ArrayList<>();
                    if (sender.hasPermission("bkshop.setshop")) {
                        String shop = plugin.getLangFile().get(null, "commands.setshop.subcommands.shop");
                        String color = plugin.getLangFile().get(null, "commands.setshop.subcommands.color");
                        String message = plugin.getLangFile().get(null, "commands.setshop.subcommands.message");
                        List<String> subCommands = new ArrayList<>(Arrays.asList(shop, color, message));

                        if (args.length == 1) {
                            String partialCommand = args[0];
                            StringUtil.copyPartialMatches(partialCommand, subCommands, completions);
                        }

                        if (args.length == 2 && args[0].equalsIgnoreCase(color)) {
                            String partialColor = args[1];
                            List<String> colors = Arrays.asList("a", "b", "c", "d", "e", "f", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0");
                            StringUtil.copyPartialMatches(partialColor, colors, completions);
                        }

                        Collections.sort(completions);
                    }
                    return completions;
                }))
                .registerAll();

        frameType = getConfigManager().getConfig().getInt("frame");
        sendConsoleMessage(InternalMessages.LOADING_SHOPS.getMessage(this));
        getConfigManager().loadAllConfigs();
        shopsManager = new me.bkrmt.bkshop.ShopsManager();
        menuManager = new me.bkrmt.bkshop.menus.MenuManager();

        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            sendConsoleMessage(InternalMessages.PLACEHOLDER_FOUND.getMessage(this));
            new PAPIExpansion(this).register();
        }

        if (TeleportCore.INSTANCE.getPlayersInCooldown().get("Core-Started") == null)
            TeleportCore.INSTANCE.start(this);

        sendConsoleMessage(InternalMessages.PLUGIN_STARTED.getMessage(this));
    }

    public AnimatorManager getAnimatorManager() {
        return animatorManager;
    }

    public ItemStack[] getFrameItems() {
        ItemStack[] items = new ItemStack[3];
        switch (getFrameType()) {
            case 0:
                items[0] = new ItemStack(Material.AIR);
                items[1] = new ItemStack(Material.AIR);
                items[2] = new ItemStack(Material.AIR);
                return items;
            case 1:
                items[0] = new ItemStack(Material.DIAMOND);
                items[1] = new ItemStack(Material.LADDER);
                break;
            case 2:
                items[0] = new ItemStack(Material.EMERALD);
                items[1] = new ItemStack(Material.VINE);
                break;
            case 3:
                items[0] = new ItemStack(Material.IRON_INGOT);
                items[1] = new ItemStack(BkShop.getInstance().getHandler().getItemManager().getIronBars());
                break;
            case 4:
                items[0] = new ItemStack(Material.GOLD_INGOT);
                items[1] = new ItemStack(BkShop.getInstance().getHandler().getItemManager().getRails());
                break;
        }
        items[2] = new ItemStack(BkShop.getInstance().getHandler().getItemManager().getWhitePane());
        ItemMeta meta = items[0].getItemMeta();
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        items[0].setItemMeta(meta);
        items[1].setItemMeta(meta);
        items[2].setItemMeta(meta);
        return items;
    }

    public ShopsManager getShopsManager() {
        return shopsManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public int getFrameType() {
        return frameType;
    }

    @Override
    public void onDisable() {
        getShopsManager().saveShops();
        getConfigManager().saveConfigs();
    }

    public static BkShop getInstance() {
        return plugin;
    }

}
