package me.bkrmt.bkshop.api;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public interface ShopsManager {
    void setShop(CommandSender sender);

    List<Shop> getShops();

    Shop getShop(UUID ownerUuid);

    void saveShops();

    Shop getShop(String ownerName);

    Shop createShop(UUID uuid);

    void deleteShop(CommandSender sender, UUID ownerUuid);

    void deleteShop(CommandSender sender, String name);
}
