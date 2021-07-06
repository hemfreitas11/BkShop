package me.bkrmt.bkshop;

import io.netty.util.internal.ConcurrentSet;
import me.bkrmt.bkcore.config.ConfigType;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.events.PlayerDelShopEvent;
import me.bkrmt.bkshop.events.PlayerSetShopEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopsManager {
    private final List<Shop> shops;
    private final ConcurrentSet<File> toBeConverted;

    public ShopsManager() {
        toBeConverted = new ConcurrentSet<>();
        shops = new ArrayList<>();
        File[] shopFiles = BkShop.getInstance().getFile("shops", "").listFiles();
        if (shopFiles.length > 0) {
            for (File shopFile : shopFiles) {
                String uuidString = shopFile.getName().replace(".yml", "");
                if (!uuidString.isEmpty()) {
                    try {
                        UUID uuid = UUID.fromString(uuidString);
                        shopAdd(new Shop(Bukkit.getOfflinePlayer(uuid)));
                    } catch (Exception ignored) {
                        convertToUUID(shopFile, uuidString);
                    }
                }
            }
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                if (toBeConverted.size() > 0) {
                    Player player = event.getPlayer();
                    for (File shopFile : toBeConverted) {
                        String cleanName = shopFile.getName().replace(".yml", "");
                        if (player.getName().equalsIgnoreCase(cleanName)) {
                            convertToUUID(shopFile, cleanName);
                        }
                    }
                }
            }
        }, BkShop.getInstance());
    }

    private void convertToUUID(File shopFile, String name) {
        Player player = Bukkit.getPlayer(name);

        if (player != null && player.isOnline()) {
            String oldName = shopFile.getName();
            if (shopFile.renameTo(BkShop.getInstance().getFile("shops", player.getUniqueId() + ".yml"))) {
                if (toBeConverted.size() > 0) toBeConverted.removeIf(file -> file.getName().equalsIgnoreCase(oldName));
                if (shops.size() > 0) shops.removeIf(shop -> shop.getOwnerName().equalsIgnoreCase(player.getName()));
                shopAdd(new Shop(player));
            }
        } else {
            toBeConverted.add(shopFile);
            shopAdd(new Shop(name));
        }
    }

    public void setShop(CommandSender sender) {
        Player player = (Player) sender;
        PlayerSetShopEvent setShopEvent = new PlayerSetShopEvent(player);
        BkShop.getInstance().getServer().getPluginManager().callEvent(setShopEvent);
        if (!setShopEvent.isCancelled()) {
            if (BkShop.getInstance().getFile("shops", ((Player) sender).getUniqueId() + ".yml").exists()) {
                BkShop.getInstance().getShopsManager().getShop(((Player) sender).getUniqueId()).setLocation(((Player) sender).getLocation());
            } else {
                BkShop.getInstance().getShopsManager().createShop(player.getUniqueId())
                    .setLastVisitor(player.getName())
                    .setVisits(1)
                    .setPublicData(null, false)
                    .setShopState(null, ShopState.OPEN);
            }
            BkShop.getInstance().sendTitle((Player) sender, 5, 40, 10, BkShop.getInstance().getLangFile().get(((Player) sender), "info.shop-set"), "");
        }
    }

    private void shopAdd(Shop shop) {
        if (!shops.contains(shop)) shops.add(shop);
    }

    public List<Shop> getShops() {
        return shops;
    }

    public Shop getShop(UUID ownerUuid) {
        Shop returnValue = null;
        for (Shop shop : shops) {
            if (shop.getOwner() != null && shop.getOwner().getUniqueId().equals(ownerUuid)) {
                returnValue = shop;
                break;
            }
        }
        return returnValue;
    }

    public void saveShops() {
        for (Shop shop : shops) {
            shop.saveProperties();
        }
    }

    public Shop getShop(String ownerName) {
        Shop returnValue = null;
        for (Shop shop : shops) {
            if (shop.getOwnerName().equalsIgnoreCase(ownerName)) {
                returnValue = shop;
                break;
            }
        }
        return returnValue;
    }

    public Shop createShop(UUID uuid) {
        File shopFile = BkShop.getInstance().getFile("shops", uuid + ".yml");
        if (!shopFile.exists()) {
            Configuration newShopConfig = new Configuration(BkShop.getInstance(), shopFile, ConfigType.Player_Data);
            BkShop.getInstance().getConfigManager().addConfig(newShopConfig);
            Shop newShop = new Shop(Bukkit.getOfflinePlayer(uuid));
            shops.add(newShop);
            return newShop;
        } else return BkShop.getInstance().getShopsManager().getShop(uuid);
    }

    public void deleteShop(CommandSender sender, UUID ownerUuid) {
        delete(sender, getShop(ownerUuid));
    }

    public void deleteShop(CommandSender sender, String name) {
        delete(sender, getShop(name));
    }

    private void delete(CommandSender sender, Shop shop) {
        BkShop plugin = BkShop.getInstance();
        if (shop == null || !shop.getConfig().getFile().exists()) {
            if (sender != null)
                sender.sendMessage(plugin.getLangFile().get(((Player) sender), "error.no-shop"));
        } else {
            PlayerDelShopEvent delShopEvent = new PlayerDelShopEvent((Player) sender, shop);
            plugin.getServer().getPluginManager().callEvent(delShopEvent);
            if (!delShopEvent.isCancelled()) {
                if (shop.getConfig().getFile().delete()) {
                    plugin.getConfigManager().removeConfig("shops", shop.getOwnerName().toLowerCase() + ".yml");
                    shops.remove(shop);
                    if (sender != null)
                        sender.sendMessage(plugin.getLangFile().get(((Player) sender), "info.shop-deleted"));
                }
            }
        }
    }
}
