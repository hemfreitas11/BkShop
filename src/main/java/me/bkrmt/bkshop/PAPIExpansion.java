package me.bkrmt.bkshop;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class PAPIExpansion extends PlaceholderExpansion {

    private final BkShop bkShop;

    public PAPIExpansion(BkShop bkShop) {
        this.bkShop = bkShop;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return bkShop.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return bkShop.getName();
    }

    @Override
    public String getVersion() {
        return bkShop.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        String returnValue = null;
        if (player != null) {
            Shop shop = bkShop.getShopsManager().getShop(player.getUniqueId());
            if (shop != null && identifier != null) {
                if (identifier.equalsIgnoreCase("visits")) {
                    returnValue = String.valueOf(shop.getVisits());
                } else if (identifier.equalsIgnoreCase("open")) {
                    returnValue = (shop.getShopState().equals(ShopState.OPEN) ? "Open" : "Closed");
                } else if (identifier.equalsIgnoreCase("public")) {
                    returnValue = (shop.isPublicData() ? "Public" : "Private");
                } else if (identifier.equalsIgnoreCase("last-visitor")) {
                    returnValue = String.valueOf(shop.getLastVisitor());
                }
            } else {
                returnValue = "No shop";
            }
        }
        return returnValue;
    }
}
