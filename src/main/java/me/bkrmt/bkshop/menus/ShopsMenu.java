package me.bkrmt.bkshop.menus;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.config.Configuration;
import me.bkrmt.bkshop.BkShop;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

import static me.bkrmt.bkshop.BkShop.plugin;

public class ShopsMenu {
    private Inventory[] menuLoja;
    private String[] lojasCriadas;
    private static int paginas;

    public ShopsMenu() {
        loadMenu();
    }

    public void reloadMenu() {
        reloadMenu(true);
    }

    public void reloadMenu(boolean delayed) {
        if (delayed) {
            if (BkShop.reloadDelay != null) BkShop.reloadDelay.cancel();
            BukkitTask reloadDelay = new BukkitRunnable() {
                @Override
                public void run() {
                    loadMenu();
                    BkShop.reloadDelay = null;
                    cancel();
                }
            }.runTaskLater(plugin, 20 * 2);
            BkShop.reloadDelay = reloadDelay;
        } else loadMenu();
    }

    private void loadMenu() {
        getLojasCriadas();
        menuLoja = new Inventory[paginas];
        String formatPaginas;
        if (paginas > 99) formatPaginas = "99+";
        else formatPaginas = String.valueOf(paginas);
        for (int c = 0; c < paginas; c++) {
            String title = plugin.getLangFile().get("info.list-title").replace("{current-page}", String.valueOf(c + 1)).replace("{total-pages}", formatPaginas);
            StringBuilder builder = new StringBuilder();
            builder.append(title);
            if (paginas >= 10 && paginas <= 100 && builder.charAt(0) == ' ' && builder.charAt(1) == ' ' && builder.charAt(2) == ' ')
                builder.delete(0, 2);
            else if (paginas > 100 && builder.charAt(0) == ' ' && builder.charAt(1) == ' ' && builder.charAt(2) == ' ' && builder.charAt(3) == ' ')
                builder.delete(0, 3);
            title = builder.toString();
            menuLoja[c] = plugin.getServer().createInventory(null, 45, title);
            setMenuItems(c);
        }
    }

    public boolean isEmpty() {
        try {
            boolean value = true;
            if (!lojasCriadas[0].equals("-")) value = false;
            return value;
        } catch (NullPointerException ignored) {
            return true;
        }
    }

    public static int getPaginas() {
        return paginas;
    }

    public void displayMenu(Player player, int pagina) {
        try {
            player.openInventory(menuLoja[pagina]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            player.sendMessage(Utils.translateColor(plugin.getLangFile().get("error.invalid-page").replace("{page}", String.valueOf(pagina + 1))));
        }
    }

    private void getLojasCriadas() {
        lojasCriadas = BkShop.getLojas();
        paginas = (int) Math.ceil((double) lojasCriadas.length / 25);
    }

    private int getArrayIndexStart(int pagina) {
        int temp = pagina * 25;
        if (pagina > 0) temp++;
        return temp;
    }

    private void setMenuItems(int pagina) {
        if (!isEmpty()) {
            int arrayIndex = pagina * 25;
            ItemStack buttonVoltar = Utils.createItem(plugin, plugin.getLangFile().get("info.return-name"), null, plugin.getLangFile().get("info.return-desc"));

            ItemStack buttonProximo = Utils.createItem(plugin, plugin.getLangFile().get("info.next-name"), null, plugin.getLangFile().get("info.next-desc"));

            menuLoja[pagina].setItem(18, buttonVoltar);

            if (pagina != paginas - 1) menuLoja[pagina].setItem(26, buttonProximo);

            for (int c = 0; c < 45; c++) {
                if (c == 0 || c == 8 || c == 36 || c == 44) {
                    menuLoja[pagina].setItem(c, BkShop.getFrameItems()[0]);
                }
                if (c == 1 || c == 7 || c == 9 || c == 17 || c == 37 || c == 43 || c == 27 || c == 35) {
                    menuLoja[pagina].setItem(c, BkShop.getFrameItems()[1]);
                }
            }

            for (int c = 0; c < 45 / 9; c++) {
                int start = 9 * c;
                int end = start + 8;
                for (int i = start; i <= end; i++) {
                    if (i == start || i == start + 1 || i == end - 1 || i == end) continue;
                    String customColor = "7";
                    Configuration configFile = plugin.getConfig("shops", lojasCriadas[arrayIndex].toLowerCase() + ".yml");
                    if (configFile.getString("shop.color") != null) customColor = configFile.getString("shop.color");

                    ItemStack menuItem;
                    ArrayList<String> headLore = new ArrayList<>();
                    String status = configFile.getBoolean("shop.open") ? plugin.getLangFile().get("info.info-open") : plugin.getLangFile().get("info.info-closed");
                    headLore.add(plugin.getLangFile().get("info.info-status").replace("{status}", status));
                    if (configFile.getBoolean("shop.public-visits")) {
                        headLore.add(plugin.getLangFile().get("info.info-visits").replace("{visits}", Utils.translateColor("&" + customColor +
                                ChatColor.stripColor(configFile.getString("shop.visits")))));
                        headLore.add(plugin.getLangFile().get("info.info-last-visit").replace("{player}", Utils.translateColor("&" + customColor +
                                ChatColor.stripColor(configFile.getString("shop.last-visitor")))));
                    }
                    String shopCmd = plugin.getLangFile().get("commands.shop.command");
                    headLore.add(Utils.translateColor("&7&o/" + shopCmd + " &" + customColor + "&o" + lojasCriadas[arrayIndex]));

                    if (configFile.getString("shop.message") != null)
                        headLore.add(ChatColor.translateAlternateColorCodes
                                ('&', "&" + customColor + configFile.getString("shop.message")));
                    menuItem = Utils.createItem(plugin, Utils.translateColor("&" + customColor + "&l" + lojasCriadas[arrayIndex]),
                            plugin.getHandler().getItemManager().getHead().getType(), headLore);
                    menuLoja[pagina].setItem(i, menuItem);
                    arrayIndex++;
                    if (arrayIndex == lojasCriadas.length) return;
                }
            }
        }
    }
}
