package me.bkrmt.bkshop.api;

import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public interface ShopsManager {
    void setShop(CommandSender sender);

    ConcurrentSkipListSet<Shop> getShops();

    Shop getShop(UUID ownerUuid);

    void saveShops();

    Shop getShop(String ownerName);

    Shop createShop(UUID uuid);

    void deleteShop(CommandSender sender, UUID ownerUuid);

    void deleteShop(CommandSender sender, String name);
}
