package me.bkrmt.bkshop;

import me.bkrmt.bkcore.HeadDisplay;
import me.bkrmt.bkcore.PagedList;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.gui.GUI;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.api.ShopState;
import me.bkrmt.teleport.Teleport;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Shop implements me.bkrmt.bkshop.api.Shop {
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
        this.lastVisitor = shopConfig.get("shop.last-visitor") == null ? "N\\A" : shopConfig.getString("shop.last-visitor");
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

//        this.displayItem = new ItemBuilder(BkShop.getInstance().getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19"));
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
//        this.displayItem = new ItemBuilder(BkShop.getInstance().getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19"));
        if (!shopConfig.getFile().exists()) shopConfig.saveToFile();
    }

    @Override
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

    @Override
    public OfflinePlayer getOwner() {
        return owner;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getVisits() {
        return visits;
    }

    @Override
    public ShopState getShopState() {
        return shopState;
    }

    @Override
    public String getLastVisitor() {
        return lastVisitor;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isPublicData() {
        return publicData;
    }

    @Override
    public Shop setPublicData(boolean publicData) {
        this.publicData = publicData;
        saveValue("public-visits", publicData);
        return this;
    }

    @Override
    public Shop setShopState(ShopState state) {
        this.shopState = state;
        saveValue("open", state.equals(ShopState.OPEN));
        return this;
    }

    @Override
    public Shop setLastVisitor(String lastVisitor) {
        boolean isEmpty = this.lastVisitor == null;
        this.lastVisitor = lastVisitor;
        if (isEmpty) {
            getConfig().set("shop.last-visitor", lastVisitor);
            getConfig().saveToFile();
        }
        return this;
    }

    @Override
    public Shop setLocation(Location location) {
        this.location = location;
        getConfig().setLocation("shop", location);
        getConfig().saveToFile();
        return this;
    }

    @Override
    public Shop incrementVisits() {
        visits += 1;
        return this;
    }

    @Override
    public Shop setVisits(int visits) {
        this.visits = visits;
        return this;
    }

    @Override
    public Shop setColor(String color) {
        this.color = color;
        saveValue("color", color);
        return this;
    }

    @Override
    public Shop setDescription(String description) {
        this.description = description;
        saveValue("message", description);
        return this;
    }

    @Override
    public Configuration getConfig() {
        return BkShop.getInstance().getConfigManager().getConfig("shops", (owner == null ? ownerName : owner.getUniqueId()) + ".yml");
    }

    @Override
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
        new Teleport(BkShop.getInstance(), player, BkShop.getInstance().getConfigManager().getConfig().getBoolean("teleport-countdown.cancel-on-move"))
                .setLocation(getOwnerName(), getLocation())
                .setTitle(Utils.translateColor("&" + getColor() + BkShop.getInstance().getLangFile().get(owner, "info.warped.title").replace("{player}", getOwnerName() == null ? "N/A" : getOwnerName())))
                .setSubtitle(Utils.translateColor("&" + getColor() + (description != null && !description.isEmpty() ? description : "")))
                .setDuration(Utils.intFromPermission(player, 5, "bkshop.countdown", new String[]{"bkshop.countdown.0"}))
                .setIsCancellable(true)
                .startTeleport();
    }

    @Override
    public String getDisplayName(PagedList list, Page currentPage) {
        return displayName;
    }

    @Override
    public int getSlot() {
        return -1;
    }

    @Override
    public int getPage() {
        return -1;
    }

    @Override
    public void setPage(int slot) {}

    @Override
    public void setSlot(int slot) {}

    @Override
    public List<String> getLore(PagedList list, Page currentPage) {
        return lore;
    }

    @Override
    public Object getDisplayItem(PagedList list, Page currentPage) {
        return new HeadDisplay(owner == null ? Bukkit.getOfflinePlayer("Steve") : owner, displayName, lore);
    }

    @Override
    public ElementResponse getElementResponse(PagedList list, Page currentPage) {
        return event -> {
            MenuSound.CLICK.play(event.getWhoClicked());
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

    @Override
    public void openDeleteMenu(Player player, int previousPage) {
        BkShop plugin = BkShop.getInstance();

        Page menu = new Page(plugin, plugin.getAnimatorManager(), new GUI(plugin.getLangFile().get(owner, "info.delete-confirm-title"), Rows.FIVE), 1);

        ItemBuilder info = new ItemBuilder(plugin.getHandler().getItemManager().getSign())
                .setName(plugin.getLangFile().get(player, "gui-buttons.delete-sign.name"))
                .setLore(plugin.getLangFile().getStringList(player, "info.delete-sign-lore"));

        ItemBuilder confirm = new ItemBuilder(Material.EMERALD_BLOCK)
                .setName(plugin.getLangFile().get(player, "gui-buttons.delete-confirm.name"));

        ItemBuilder cancel = new ItemBuilder(Material.REDSTONE_BLOCK)
                .setName(plugin.getLangFile().get(player, "gui-buttons.delete-decline.name"));

        menu.setItemOnXY(5, 2, info, "delete-menu-info-" + player.getName().toLowerCase() + "-info", event -> {
        });

        menu.setItemOnXY(7, 3, confirm, "delete-menu-confirm-" + player.getName().toLowerCase() + "-confirm", event -> {
            MenuSound.SUCCESS.play(event.getWhoClicked());
            event.getWhoClicked().closeInventory();
            if (owner == null) {
                plugin.getShopsManager().deleteShop(event.getWhoClicked(), ownerName);
            } else {
                plugin.getShopsManager().deleteShop(event.getWhoClicked(), owner.getUniqueId());
            }
        });

        menu.setItemOnXY(3, 3, cancel, "delete-menu-cancel-" + player.getName().toLowerCase() + "-cancel", event -> {
            MenuSound.BACK.play(event.getWhoClicked());
            openOptionsMenu((Player) event.getWhoClicked(), previousPage);
        });

        BkShop.getInstance().getMenuManager().buildPageFrame(menu);

        menu.openGui(player);
    }

    @Override
    public void openOptionsMenu(Player shopUser, int previousPage) {
        BkShop plugin = BkShop.getInstance();
        Page page = new Page(BkShop.getInstance(), BkShop.getInstance().getAnimatorManager(), new GUI(
                BkShop.getInstance().getLangFile().get(shopUser, "info.shop-options-title"), Rows.FIVE
        ), 1);

        String customColor = "7";
        if (getColor() != null) customColor = getColor();

        ItemBuilder teleportBuilder = new ItemBuilder(plugin.getHandler().getItemManager().getPearl())
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-teleport.name"))
                .hideTags();

        page.setItemOnXY(5, 2, teleportBuilder, shopUser.getName().toLowerCase() + "-shop-options-teleport", event -> {
            event.getWhoClicked().closeInventory();
            teleportToShop(shopUser);
        });

        ItemBuilder closeBuilder = new ItemBuilder(plugin.getHandler().getItemManager().getRedPane())
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-close.name"))
                .hideTags();

        page.setItemOnXY(4, 3, closeBuilder, shopUser.getName().toLowerCase() + "-shop-options-close", event -> {
            if (event.getWhoClicked().hasPermission("bkshop.setshop")) {
                MenuSound.WARN.play(event.getWhoClicked());
                setShopState(ShopState.CLOSED);
                page.displayItemMessage(event.getSlot(), 1.5, ChatColor.YELLOW, BkShop.getInstance().getLangFile().get(shopUser, "info.shop-closed"), null);
            } else {
                MenuSound.ERROR.play(event.getWhoClicked());
                page.displayItemMessage(event.getSlot(), 1.5, ChatColor.RED, plugin.getLangFile().get((OfflinePlayer) event.getWhoClicked(), "error.no-permission"), null);
            }
        });

        ItemBuilder openBuilder = new ItemBuilder(plugin.getHandler().getItemManager().getGreenPane())
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-open.name"))
                .hideTags();

        page.setItemOnXY(6, 3, openBuilder, shopUser.getName().toLowerCase() + "-shop-options-open", event -> {
            if (event.getWhoClicked().hasPermission("bkshop.setshop")) {
                MenuSound.SUCCESS.play(event.getWhoClicked());
                setShopState(ShopState.OPEN);
                page.displayItemMessage(event.getSlot(), 1.5, ChatColor.GREEN, BkShop.getInstance().getLangFile().get(shopUser, "info.shop-open"), null);
            } else {
                MenuSound.ERROR.play(event.getWhoClicked());
                page.displayItemMessage(event.getSlot(), 1.5, ChatColor.RED, plugin.getLangFile().get((OfflinePlayer) event.getWhoClicked(), "error.no-permission"), null);
            }
        });

        ItemBuilder deleteBuilder = new ItemBuilder(Material.TNT)
                .setName(plugin.getLangFile().get(owner, "gui-buttons.admin-delete.name"))
                .hideTags();

        page.setItemOnXY(5, 4, deleteBuilder, shopUser.getName().toLowerCase() + "-shop-options-open", event -> {
            MenuSound.CLICK.play(event.getWhoClicked());
            openDeleteMenu(shopUser, previousPage);
        });

        BkShop.getInstance().getMenuManager().buildPageFrame(page);

        page.setItemOnXY(1, 3,
                new ItemBuilder(plugin.getHandler().getItemManager().getRedWool())
                        .setName(plugin.getLangFile().get(owner, "gui-buttons.previous-page.name"))
                        .setLore(plugin.getLangFile().getStringList("gui-buttons.return.description")),
                shopUser.getName().toLowerCase() + "-shops-list-back-button",
                event -> {
                    MenuSound.BACK.play(event.getWhoClicked());
                    BkShop.getInstance().getMenuManager().openShopsMenu(shopUser, previousPage - 1);
                });

        page.openGui(shopUser);
    }

    @Override
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

    @Override
    public int compareTo(me.bkrmt.bkshop.api.Shop shop) {
        if (shop.getOwnerName() == null) return 1;
        if (shop.getOwnerName().equals(getOwnerName())) return 0;
        else return 1;
    }

    private void saveValue(String key, Object value) {
        getConfig().set("shop" + (key.isEmpty() ? "" : "." + key), value);
        getConfig().saveToFile();
    }
}
