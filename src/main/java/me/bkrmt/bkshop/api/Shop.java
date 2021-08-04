package me.bkrmt.bkshop.api;

import me.bkrmt.bkcore.PagedItem;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.opengui.event.ElementResponse;
import me.bkrmt.opengui.page.Page;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public interface Shop extends PagedItem, Comparable<Shop> {
    void reloadDisplayItem();

    OfflinePlayer getOwner();

    String getColor();

    String getOwnerName();

    String getDescription();

    int getVisits();

    ShopState getShopState();

    String getLastVisitor();

    Location getLocation();

    boolean isPublicData();

    Shop setPublicData(boolean publicData);

    Shop setShopState(ShopState state);

    Shop setLastVisitor(String lastVisitor);

    Shop setLocation(Location location);

    Shop incrementVisits();

    Shop setVisits(int visits);

    Shop setColor(String color);

    Shop setDescription(String description);

    Configuration getConfig();

    void teleportToShop(Player player);

    String getDisplayName(PagedList list, Page currentPage);

    List<String> getLore(PagedList list, Page currentPage);

    Object getDisplayItem(PagedList list, Page currentPage);

    ElementResponse getElementResponse(PagedList list, Page currentPage);

    void openDeleteMenu(Player player, int previousPage);

    void openOptionsMenu(Player shopUser, int previousPage);

    void saveProperties();

    int compareTo(Shop shop);
}
