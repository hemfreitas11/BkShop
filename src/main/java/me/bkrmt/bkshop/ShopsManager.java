package me.bkrmt.bkshop;

import io.netty.util.internal.ConcurrentSet;
import me.bkrmt.bkcore.config.ConfigType;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.api.Shop;
import me.bkrmt.bkshop.api.ShopState;
import me.bkrmt.bkshop.api.events.PlayerDelShopEvent;
import me.bkrmt.bkshop.api.events.PlayerSetShopEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopsManager implements me.bkrmt.bkshop.api.ShopsManager {
    private final List<Shop> shops;
    private final ConcurrentSet<File> toBeConverted;

    public ShopsManager() {
        toBeConverted = new ConcurrentSet<>();
        shops = new ArrayList<>();

        boolean closeShop = BkShop.getInstance().getConfigManager().getConfig().getBoolean("permission-check.close-shop");
        boolean deleteShop = !closeShop && BkShop.getInstance().getConfigManager().getConfig().getBoolean("permission-check.delete-shop");

        File[] shopFiles = BkShop.getInstance().getFile("shops", "").listFiles();
        if (shopFiles.length > 0) {
            for (File shopFile : shopFiles) {
                if (verifyShop(shopFile)) {
                    String uuidString = shopFile.getName().replace(".yml", "");
                    if (!uuidString.isEmpty()) {
                        try {
                            UUID uuid = UUID.fromString(uuidString);
                            shopAdd(new me.bkrmt.bkshop.Shop(Bukkit.getOfflinePlayer(uuid)));
                        } catch (Exception ignored) {
                            convertToUUID(shopFile, uuidString);
                        }
                    }
                }
            }
        }

        for (Shop shop : shops) {
            OfflinePlayer offlinePlayer = shop.getOwner();
            if (offlinePlayer != null && offlinePlayer.getPlayer() != null)
                checkOwnerPermission(offlinePlayer.getPlayer(), shop, closeShop, deleteShop);
        }

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                final boolean closeShop = BkShop.getInstance().getConfigManager().getConfig().getBoolean("permission-check.close-shop");
                final boolean deleteShop = !closeShop && BkShop.getInstance().getConfigManager().getConfig().getBoolean("permission-check.delete-shop");
                Player player = event.getPlayer();
                if (toBeConverted.size() > 0) {
                    for (File shopFile : toBeConverted) {
                        String cleanName = shopFile.getName().replace(".yml", "");
                        if (player.getName().equalsIgnoreCase(cleanName)) {
                            convertToUUID(shopFile, cleanName);
                        }
                    }
                }

                Shop shop = getShop(player.getUniqueId());
                checkOwnerPermission(player, shop, closeShop, deleteShop);
            }
        }, BkShop.getInstance());
    }

    private void checkOwnerPermission(Player player, Shop shop, boolean closeShop, boolean deleteShop) {
        if (shop != null) {
            if (!player.hasPermission("bkshop.setshop")) {
                BkShop bkShop = BkShop.getInstance();
                if (closeShop) {
                    shop.setShopState(ShopState.CLOSED);
                    player.sendMessage(bkShop.getLangFile().get("info.permission-check.shop-closed"));
                } else if (deleteShop) {
                    delete(null, shop);
                    player.sendMessage(bkShop.getLangFile().get("info.permission-check.shop-deleted"));
                }
            }
        }
    }

    private boolean verifyShop(File shopFile) {
        boolean returnValue = false;
        try {
            FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
            boolean hasChanged = false;

            if (shopConfig.get("shop.world") == null ||
                    shopConfig.get("shop.x") == null ||
                    shopConfig.get("shop.y") == null ||
                    shopConfig.get("shop.z") == null) {
                throw new MalformedParametersException("Invalid Shop");
            }

            if (shopConfig.get("shop.player-name") == null) {
                shopConfig.set("shop.player-name", "N/A");
                hasChanged = true;
            }

            if (shopConfig.get("shop.last-visitor") == null) {
                shopConfig.set("shop.last-visitor", "N/A");
                hasChanged = true;
            }

            if (shopConfig.get("shop.pitch") == null) {
                shopConfig.set("shop.pitch", 0);
                hasChanged = true;
            }

            if (shopConfig.get("shop.yaw") == null) {
                shopConfig.set("shop.yaw", 0);
                hasChanged = true;
            }

            if (hasChanged) shopConfig.save(shopFile);
            returnValue = true;
        } catch (Exception ignored) {
            BkShop.getInstance().sendConsoleMessage(InternalMessages.INVALID_SHOP.getMessage(BkShop.getInstance()).replace("{0}", shopFile.getName()));
        }
        return returnValue;
    }

    private void convertToUUID(File shopFile, String name) {
        Player player = Bukkit.getPlayer(name);

        if (player != null && player.isOnline()) {
            String oldName = shopFile.getName();
            if (shopFile.renameTo(BkShop.getInstance().getFile("shops", player.getUniqueId() + ".yml"))) {
                if (toBeConverted.size() > 0) toBeConverted.removeIf(file -> file.getName().equalsIgnoreCase(oldName));
                if (shops.size() > 0) shops.removeIf(shop -> shop.getOwnerName().equalsIgnoreCase(player.getName()));
                shopAdd(new me.bkrmt.bkshop.Shop(player));
            }
        } else {
            toBeConverted.add(shopFile);
            shopAdd(new me.bkrmt.bkshop.Shop(name));
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
                        .setPublicData(false)
                        .setShopState(ShopState.OPEN);
            }
            BkShop.getInstance().sendTitle((Player) sender, 5, 40, 10, BkShop.getInstance().getLangFile().get(((Player) sender), "info.shop-set"), "");
        }
    }

    private void shopAdd(Shop shop) {
        if (!shops.contains(shop)) shops.add(shop);
    }

    @Override
    public List<Shop> getShops() {
        return shops;
    }

    @Override
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

    @Override
    public void saveShops() {
        shops.forEach(Shop::saveProperties);
        for (Shop shop : shops) {
            shop.saveProperties();
        }
    }

    @Override
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

    @Override
    public Shop createShop(UUID uuid) {
        File shopFile = BkShop.getInstance().getFile("shops", uuid + ".yml");
        if (!shopFile.exists()) {
            Configuration newShopConfig = new Configuration(BkShop.getInstance(), shopFile, ConfigType.PLAYER_DATA);
            BkShop.getInstance().getConfigManager().addConfig(newShopConfig);
            Shop newShop = new me.bkrmt.bkshop.Shop(Bukkit.getOfflinePlayer(uuid));
            shops.add(newShop);
            return newShop;
        } else return BkShop.getInstance().getShopsManager().getShop(uuid);
    }

    @Override
    public void deleteShop(CommandSender sender, UUID ownerUuid) {
        delete(sender, getShop(ownerUuid));
    }

    @Override
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
