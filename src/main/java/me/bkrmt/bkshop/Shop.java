package me.bkrmt.bkshop;

import me.bkrmt.bkcore.PagedItem;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.opengui.GUI;
import me.bkrmt.opengui.ItemBuilder;
import me.bkrmt.opengui.Page;
import me.bkrmt.opengui.Rows;
import me.bkrmt.opengui.event.ElementResponse;
import me.bkrmt.teleport.Teleport;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Shop implements PagedItem, Comparable<Shop> {
    private final OfflinePlayer owner;
    private String color;
    private final String ownerName;
    private String description;
    private List<String> lore;
    private int visits;
    private String displayName;
    private String lastVisitor;
    private Location location;
    private boolean publicData;
    private ShopState shopState;
    private final ItemBuilder displayItem;

    public Shop(OfflinePlayer owner) {
        this.owner = owner;
        Configuration shopConfig = getConfig();

        if (shopConfig.get("shop.player-name") == null) {
            this.ownerName = owner.getName();
            getConfig().set("shop.player-name", ownerName);
            getConfig().saveToFile();
        } else {
            ownerName = shopConfig.getString("shop.player-name");
        }

        this.color = shopConfig.get("shop.color") == null ? "7" : shopConfig.getString("shop.color");
        this.displayName = Utils.translateColor(BkShop.getInstance().getLangFile().get(owner, "info.shop-head-name")
                .replace("{shop-color}", color)
                .replace("{player}", ownerName));

        this.description = shopConfig.get("shop.message") == null ? null : shopConfig.getString("shop.message");
        this.visits = shopConfig.getInt("shop.visits");
        this.publicData = shopConfig.getBoolean("shop.public-visits");
        this.lastVisitor = shopConfig.getString("shop.last-visitor");
        this.shopState = shopConfig.getBoolean("shop.open") ? ShopState.OPEN : ShopState.CLOSED;
        if (shopConfig.get("shop.world") == null) {
            if (owner.isOnline()) {
                shopConfig.setLocation("shop", owner.getPlayer().getLocation());
                this.location = owner.getPlayer().getLocation();
            }
        } else {
            this.location = shopConfig.getLocation("shop");
        }

        reloadDisplayItem();

        this.displayItem = new ItemBuilder(BkShop.getInstance().createHead(owner.getUniqueId(), displayName, lore));
        displayItem.setUnchangedName(displayName);
        if (!shopConfig.getFile().exists()) shopConfig.saveToFile();
    }

    public Shop(String ownerName) {
        this.owner = null;
        this.ownerName = ownerName;

        Configuration shopConfig = getConfig();

        this.color = shopConfig.get("shop.color") == null ? "7" : shopConfig.getString("shop.color");
        this.displayName = Utils.translateColor(BkShop.getInstance().getLangFile().get(null, "info.shop-head-name")
                .replace("{shop-color}", color)
                .replace("{player}", ownerName));

        this.description = shopConfig.get("shop.message") == null ? null : shopConfig.getString("shop.message");
        this.visits = shopConfig.getInt("shop.visits");
        this.publicData = shopConfig.getBoolean("shop.public-visits");
        this.lastVisitor = shopConfig.getString("shop.last-visitor");
        this.shopState = shopConfig.getBoolean("shop.open") ? ShopState.OPEN : ShopState.CLOSED;
        this.location = shopConfig.getLocation("shop");

        reloadDisplayItem();

        this.displayItem = new ItemBuilder(BkShop.getInstance().createHead(Bukkit.getOfflinePlayer(ownerName).getUniqueId(), displayName, lore));
        displayItem.setUnchangedName(displayName);
        if (!shopConfig.getFile().exists()) shopConfig.saveToFile();
    }

    public void reloadDisplayItem() {
        this.displayName = Utils.translateColor(BkShop.getInstance().getLangFile().get(owner, "info.shop-head-name")
                .replace("{shop-color}", color)
                .replace("{player}", ownerName));

        this.lore = new ArrayList<>();
        String status = shopState.equals(ShopState.OPEN) ? BkShop.getInstance().getLangFile().get(owner, "info.info-open") : BkShop.getInstance().getLangFile().get(owner, "info.info-closed");
        lore.add(BkShop.getInstance().getLangFile().get(owner, "info.info-status").replace("{status}", status));
        if (publicData) {
            lore.add(BkShop.getInstance().getLangFile().get(owner, "info.info-visits").replace("{visits}", Utils.translateColor("&" + color +
                    ChatColor.stripColor(String.valueOf(visits)))));
            lore.add(BkShop.getInstance().getLangFile().get(owner, "info.info-last-visit").replace("{player}", Utils.translateColor("&" + color +
                    ChatColor.stripColor(lastVisitor))));
        }
        String shopCmd = BkShop.getInstance().getLangFile().get(owner, "commands.shop.command");
        lore.add(Utils.translateColor(BkShop.getInstance().getLangFile().get(owner, "info.shop-head-command").replace("{shop-color}", color).replace("{player}", ownerName).replace("{command}", shopCmd)));
        if (description != null) lore.add(Utils.translateColor("&" + color + description));
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getColor() {
        return color;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getDescription() {
        return description;
    }

    public int getVisits() {
        return visits;
    }

    public ShopState getShopState() {
        return shopState;
    }

    public String getLastVisitor() {
        return lastVisitor;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isPublicData() {
        return publicData;
    }

    public Shop setPublicData(Player sender, boolean publicData) {
        this.publicData = publicData;
        saveValue("public-visits", publicData);
        if (sender != null)
            sender.sendMessage(BkShop.getInstance().getLangFile().get(owner, "info.info-visit-" + (publicData ? "private" : "public") + "-message"));
        return this;
    }

    public Shop setShopState(Player sender, ShopState state) {
        this.shopState = state;
        saveValue("open", state.equals(ShopState.OPEN));
        if (sender != null) {
//            sender.closeInventory();
            sender.sendMessage(BkShop.getInstance().getLangFile().get(owner, "info.shop-" + (state.equals(ShopState.OPEN) ? "open" : "closed")));
        }
        return this;
    }

    public Shop setLastVisitor(String lastVisitor) {
        this.lastVisitor = lastVisitor;
        return this;
    }

    public Shop setLocation(Location location) {
        this.location = location;
        getConfig().setLocation("shop", location);
        getConfig().saveToFile();
        return this;
    }

    public Shop incrementVisits() {
        visits += 1;
        return this;
    }

    public Shop setVisits(int visits) {
        this.visits = visits;
        return this;
    }

    public Shop setColor(String color) {
        this.color = color;
        saveValue("color", color);
        return this;
    }

    public Shop setDescription(String description) {
        this.description = description;
        saveValue("message", description);
        return this;
    }

    public Configuration getConfig() {
        return BkShop.getInstance().getConfigManager().getConfig("shops", (owner == null ? ownerName : owner.getUniqueId()) + ".yml");
    }

    public void teleportToShop(Player player) {
        if (player.getName().equalsIgnoreCase(ownerName)) {
            beginTeleport(player);
            return;
        }

        if (shopState.equals(ShopState.OPEN) || player.hasPermission("bkshop.admin")) {
            incrementVisits();
            setLastVisitor(player.getName());
            beginTeleport(player);
        } else player.sendMessage(BkShop.getInstance().getLangFile().get(owner, "error.closed-shop"));
    }

    private void beginTeleport(Player player) {
        new Teleport(BkShop.getInstance(), player, BkShop.getInstance().getConfigManager().getConfig().getBoolean("cancel-on-move"))
                .setLocation(getOwnerName(), getLocation())
                .setTitle(Utils.translateColor("&" + getColor() + BkShop.getInstance().getLangFile().get(owner, "info.warped.title").replace("{player}", player.getName())))
                .setSubtitle(Utils.translateColor("&" + getColor() + (description != null && !description.isEmpty() ? description : "")))
                .setDuration(Utils.intFromPermission(player, "bkshop.countdown", new String[]{"bkshop.countdown.0"}))
                .setIsCancellable(true)
                .startTeleport();
    }

    @Override
    public String getDisplayName(PagedList list, Page currentPage) {
        return displayName;
    }

    @Override
    public List<String> getLore(PagedList list, Page currentPage) {
        return lore;
    }

    @Override
    public ItemStack getDisplayItem(PagedList list, Page currentPage) {
        return displayItem.getItem();
    }

    @Override
    public ElementResponse getElementResponse(PagedList list, Page currentPage) {
        return event -> {
            if (event.getWhoClicked().hasPermission("bkshop.admin") || event.getWhoClicked().getName().equalsIgnoreCase(ownerName)) {
                if (getConfig().getFile().exists()) {
                    openOptionsMenu((Player) event.getWhoClicked(), currentPage.getPageNumber());
                }
            } else {
                event.getWhoClicked().closeInventory();
                String shopCmd = BkShop.getInstance().getLangFile().get(owner, "commands.shop.command") + " ";
                ((Player) event.getWhoClicked()).performCommand(shopCmd + ownerName.toLowerCase());
            }
        };
    }

    public void openDeleteMenu(Player player, int previousPage) {
        BkShop plugin = BkShop.getInstance();

        Page menu = new Page(plugin, plugin.getAnimatorManager(), new GUI(plugin.getLangFile().get(owner, "info.delete-confirm-title"), Rows.FIVE), 1);

        ItemBuilder info = new ItemBuilder(plugin.getHandler().getItemManager().getSign())
                .setName(plugin.getLangFile().get(player, "gui-buttons.delete-sign.name"))
                .setUnchangedName(plugin.getLangFile().get(player, "gui-buttons.delete-sign.name"))
                .setLore(plugin.getLangFile().getStringList(player, "info.delete-sign-lore"))
                .update();

        ItemBuilder confirm = new ItemBuilder(Material.EMERALD_BLOCK)
                .setName(plugin.getLangFile().get(player, "gui-buttons.delete-confirm.name"))
                .setUnchangedName(plugin.getLangFile().get(player, "gui-buttons.delete-confirm.name"))
                .update();

        ItemBuilder cancel = new ItemBuilder(Material.REDSTONE_BLOCK)
                .setName(plugin.getLangFile().get(player, "gui-buttons.delete-decline.name"))
                .setUnchangedName(plugin.getLangFile().get(player, "gui-buttons.delete-decline.name"))
                .update();

        menu.setItemOnXY(5, 2, info, "delete-menu-info-" + player.getName().toLowerCase() + "-info", event -> {
        });

        menu.setItemOnXY(7, 3, confirm, "delete-menu-confirm-" + player.getName().toLowerCase() + "-confirm", event -> {
            event.getWhoClicked().closeInventory();
            if (owner == null) {
                plugin.getShopsManager().deleteShop(event.getWhoClicked(), ownerName);
            } else {
                plugin.getShopsManager().deleteShop(event.getWhoClicked(), owner.getUniqueId());
            }
        });

        menu.setItemOnXY(3, 3, cancel, "delete-menu-cancel-" + player.getName().toLowerCase() + "-cancel", event -> openOptionsMenu((Player) event.getWhoClicked(), previousPage));

        BkShop.getInstance().getMenuManager().buildPageFrame(menu);

        menu.openGui(player);
    }

    public void openOptionsMenu(Player shopUser, int previousPage) {
        BkShop plugin = BkShop.getInstance();
        Page page = new Page(BkShop.getInstance(), BkShop.getInstance().getAnimatorManager(), new GUI(
                BkShop.getInstance().getLangFile().get(shopUser, "info.shop-options-title"), Rows.FIVE
        ), 1);

        String customColor = "7";
        if (getColor() != null) customColor = getColor();

        ItemBuilder teleportBuilder = new ItemBuilder(plugin.getHandler().getItemManager().getPearl())
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-teleport.name"))
                .setUnchangedName(plugin.getLangFile().get(owner, "gui-buttons.admin-teleport.name"))
                .hideTags()
                .update();

        page.setItemOnXY(5, 2, teleportBuilder, shopUser.getName().toLowerCase() + "-shop-options-teleport", event -> {
            event.getWhoClicked().closeInventory();
            teleportToShop(shopUser);
        });

        ItemBuilder closeBuilder = new ItemBuilder(plugin.getHandler().getItemManager().getRedPane())
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-close.name"))
                .setUnchangedName(plugin.getLangFile().get(owner, "gui-buttons.admin-close.name"))
                .hideTags()
                .update();

        page.setItemOnXY(4, 3, closeBuilder, shopUser.getName().toLowerCase() + "-shop-options-close", event -> setShopState(shopUser, ShopState.CLOSED));

        ItemBuilder openBuilder = new ItemBuilder(plugin.getHandler().getItemManager().getGreenPane())
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-open.name"))
                .setUnchangedName(plugin.getLangFile().get(owner, "gui-buttons.admin-open.name"))
                .hideTags()
                .update();

        page.setItemOnXY(6, 3, openBuilder, shopUser.getName().toLowerCase() + "-shop-options-open", event -> setShopState(shopUser, ShopState.OPEN));

        ItemBuilder deleteBuilder = new ItemBuilder(Material.TNT)
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-delete.name"))
                .setUnchangedName(plugin.getLangFile().get(owner, "gui-buttons.admin-delete.name"))
                .hideTags()
                .update();

        page.setItemOnXY(5, 4, deleteBuilder, shopUser.getName().toLowerCase() + "-shop-options-open", event -> openDeleteMenu(shopUser, previousPage));

        BkShop.getInstance().getMenuManager().buildPageFrame(page);

        page.setItemOnXY(1, 3,
            new ItemBuilder(plugin.getHandler().getItemManager().getRedWool())
                .setName(plugin.getLangFile().get(owner, "gui-buttons.previous-page.name"))
                .setUnchangedName(plugin.getLangFile().get(owner, "gui-buttons.previous-page.name"))
                .setLore(plugin.getLangFile().getStringList("gui-buttons.return.description"))
                .hideTags()
                .update(),
            shopUser.getName().toLowerCase() + "-shops-list-back-button",
            event -> BkShop.getInstance().getMenuManager().openShopsMenu(shopUser, previousPage - 1));

        page.openGui(shopUser);
    }

    public void saveProperties() {
        getConfig().setLocation("shop", location);
        if (color != null) getConfig().set("shop.color", color);
        if (description != null) getConfig().set("shop.message", description);
        if (getConfig().get("shop.player-name") == null) getConfig().set("shop.player-name", ownerName); 
        getConfig().set("shop.visits", visits);
        getConfig().set("shop.last-visitor", lastVisitor);
        getConfig().set("shop.public-visits", publicData);
        getConfig().set("shop.open", shopState.equals(ShopState.OPEN));
        getConfig().saveToFile();
    }

    private void saveValue(String key, Object value) {
        getConfig().set("shop" + (key.isEmpty() ? "" : "." + key), value);
        getConfig().saveToFile();
    }

    @Override
    public int compareTo(Shop shop) {
        if (shop.getOwnerName().equals(getOwnerName())) return 0;
        else return 1;
    }
}
