package me.bkrmt.bkshop;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.command.CommandModule;
import me.bkrmt.bkcore.command.HelpCmd;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.commands.DelShopCmd;
import me.bkrmt.bkshop.commands.SetShop;
import me.bkrmt.bkshop.commands.ShopCmd;
import me.bkrmt.bkshop.commands.ShopsCmd;
import me.bkrmt.bkshop.menus.MainMenu;
import me.bkrmt.bkshop.menus.ShopsMenu;
import me.bkrmt.teleport.TeleportCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BkShop extends BkPlugin {
    public static BkPlugin plugin;
    public static BukkitTask reloadDelay;
    public static int frameType;
    private static ShopsMenu shopsMenu;
    private static MainMenu mainMenu;

    @Override
    public void onEnable() {
        plugin = this;
        start(true);
        setRunning(true);
        getCommandMapper().addCommand(new CommandModule(new HelpCmd(plugin, "bkshop", ""), (a, b, c, d) -> Collections.singletonList("")))
                .addCommand(new CommandModule(new ShopsCmd(plugin, "shops", "bkshop.shops"), (a, b, c, d) -> Collections.singletonList("")))
                .addCommand(new CommandModule(new DelShopCmd(plugin, "delshop", "bkshop.delshop"), (a, b, c, d) -> Collections.singletonList("")))
                .addCommand(new CommandModule(new ShopCmd(plugin, "shop", "bkshop.shop"), (sender, b, c, args) ->
                {
                    List<String> completions = new ArrayList<>();
                    if (sender.hasPermission("bkshop.shop")) {
                        if (args.length == 1) {
                            String partialCommand = args[0];
                            List<String> lojas = Arrays.asList(getLojas());
                            StringUtil.copyPartialMatches(partialCommand, lojas, completions);
                        }
                    }
                    Collections.sort(completions);

                    return completions;
                }))
                .addCommand(new CommandModule(new SetShop(plugin, "setshop", "bkshop.setshop"), (sender, b, c, args) ->
                {
                    List<String> completions = new ArrayList<>();
                    if (sender.hasPermission("bkshop.setshop")) {
                        String shop = plugin.getLangFile().get("commands.setshop.subcommands.shop");
                        String color = plugin.getLangFile().get("commands.setshop.subcommands.color");
                        String message = plugin.getLangFile().get("commands.setshop.subcommands.message");
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
        getServer().getPluginManager().registerEvents(new ButtonFunctions(), this);
        frameType = getConfig().getInt("frame");
        shopsMenu = new ShopsMenu();
        mainMenu = new MainMenu();
        try {
            TeleportCore.playersInCooldown.get("Core-Started");
        } catch (NullPointerException ignored) {
            new TeleportCore(this);
            TeleportCore.playersInCooldown.put("Core-Started", true);
        }
    }

    public void updateShop() {
        new BukkitRunnable() {

            @Override
            public void run() {
                getShopsMenu().reloadMenu(false);
            }
        }.runTaskTimerAsynchronously(this, 0, getConfig().getInt("shop-update-delay") * 20);
    }

    public static void loadMenuLojas() {
        shopsMenu = new ShopsMenu();
    }

    public static void loadMainMenu() {
        mainMenu = new MainMenu();
    }

    public static ItemStack[] getFrameItems() {
        ItemStack[] items = new ItemStack[3];
        switch (BkShop.frameType) {
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
                items[1] = new ItemStack(plugin.getHandler().getItemManager().getIronBars());
                break;
            case 4:
                items[0] = new ItemStack(Material.GOLD_INGOT);
                items[1] = new ItemStack(plugin.getHandler().getItemManager().getRails());
                break;
        }
        items[2] = new ItemStack(plugin.getHandler().getItemManager().getWhitePane());
        ItemMeta meta = items[0].getItemMeta();
        meta.setDisplayName(" ");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        items[0].setItemMeta(meta);
        items[1].setItemMeta(meta);
        items[2].setItemMeta(meta);
        return items;

    }

    public static void closeShop(Player player, String shopOwner) {
        player.closeInventory();
        player.sendMessage(plugin.getLangFile().get("info.shop-closed"));
        Configuration config = plugin.getConfig("shops", shopOwner.toLowerCase());
        config.set("shop.open", false);
        config.save(false);
        BkShop.getShopsMenu().reloadMenu();
    }

    public static void openShop(Player player, String shopOwner) {
        Configuration config = plugin.getConfig("shops", shopOwner.toLowerCase());
        config.set("shop.open", true);
        config.save(false);
        player.closeInventory();
        player.sendMessage(plugin.getLangFile().get("info.shop-open"));
        BkShop.getShopsMenu().reloadMenu();
    }

    public static String[] getLojas() {
        File lojasFolder = new File(plugin.getDataFolder().getPath() + File.separator + "shops");
        if (!lojasFolder.exists()) lojasFolder.mkdir();
        int filesLenght = lojasFolder.listFiles().length;
        String[] lojas;
        if (filesLenght == 0) {
            lojas = new String[]{"-"};
        } else {
            File[] lojasLista = lojasFolder.listFiles();
            lojas = new String[filesLenght];
            for (int c = 0; c < filesLenght; c++) {
                lojas[c] = plugin.getConfig("shops", lojasLista[c].getName().toLowerCase()).getString("shop.player");
            }
        }
        return lojas;
    }

    public static ShopsMenu getShopsMenu() {
        return shopsMenu;
    }

    public static MainMenu getMainMenu() {
        return mainMenu;
    }

}
