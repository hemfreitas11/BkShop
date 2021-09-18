package me.bkrmt.bkshop;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.command.CommandModule;
import me.bkrmt.bkcore.command.MainCommand;
import me.bkrmt.bkcore.textanimator.AnimatorManager;
import me.bkrmt.bkcore.xlibs.XMaterial;
import me.bkrmt.bkshop.api.MenuManager;
import me.bkrmt.bkshop.api.ShopsManager;
import me.bkrmt.bkshop.commands.DelShopCmd;
import me.bkrmt.bkshop.commands.SetShop;
import me.bkrmt.bkshop.commands.ShopCmd;
import me.bkrmt.bkshop.commands.ShopsCmd;
import me.bkrmt.teleport.TeleportCore;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import java.io.File;
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
        BkGUI.INSTANCE.register(this);
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
                    throw new RuntimeException("The plugin was not started because the folder \"shops\" could not be created.");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                    return;
                }
            }
        }

        getCommandMapper()
                .addCommand(new CommandModule(new MainCommand(plugin, "bkshop.admin", (plugin1, player, configuration) -> {
                    player.sendMessage(getLangFile().get("commands.bkcommand.usage"));
                }), (a, b, c, d) -> null))
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
                items[0] = XMaterial.AIR.parseItem();
                items[1] = XMaterial.AIR.parseItem();
                items[2] = XMaterial.AIR.parseItem();
                return items;
            case 1:
                items[0] = XMaterial.DIAMOND.parseItem();
                items[1] = XMaterial.LADDER.parseItem();
                break;
            case 2:
                items[0] = XMaterial.EMERALD.parseItem();
                items[1] = XMaterial.VINE.parseItem();
                break;
            case 3:
                items[0] = XMaterial.IRON_INGOT.parseItem();
                items[1] = XMaterial.IRON_BARS.parseItem();
                break;
            case 4:
                items[0] = XMaterial.GOLD_INGOT.parseItem();
                items[1] = XMaterial.RAIL.parseItem();
                break;
        }
        items[2] = XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
        if (items[0] != null && items[1] != null && items[2] != null) {
            ItemMeta meta = items[0].getItemMeta();
            meta.setDisplayName(" ");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            items[0].setItemMeta(meta);
            items[1].setItemMeta(meta);
            items[2].setItemMeta(meta);
        }
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
        getHeadManager().saveNewHeads();
    }

    public static BkShop getInstance() {
        return plugin;
    }

}
