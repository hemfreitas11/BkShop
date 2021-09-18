package me.bkrmt.bkshop;


import me.bkrmt.bkcore.BkPlugin;

public enum InternalMessages {
    LOADING_SHOPS("§7[§1BkShop§7] §bLoading shops, please wait...",
    "§7[§1BkLoja§7] §bCarregando lojas, por favor aguarde..."),
    OWN_PLUGINS_FOUND("§7[§1BkShop§7] §bOne or more of my plugins found, enabling support...",
    "§7[§1BkLoja§7] §bUm ou mais dos meus plugins encontrados, habilitando suporte..."),
    PLUGIN_STARTING("§7[§1BkShop§7] §bPlugin starting...",
    "§7[§1BkLoja§7] §bPlugin iniciando..."),
    PLUGIN_STARTED("§7[§1BkShop§7] §bPlugin started!",
    "§7[§1BkLoja§7] §bPlugin iniciado!"),
    INVALID_SHOP("§7[§4BkShop§7] §cThe shop {0} is invalid and could not be loaded!",
    "§7[§4BkLoja§7] §cA loja {0} esta invalida e nao pode ser carregada!"),
    INVALID_LOCATION("§c[§4BkShop§c] §4WARNING! §cThe location for the shop \"{0}\" is invalid and could not be loaded!",
    "§c[§4BkLoja§c] §4AVISO! §cO local da loja \"{0}\" esta invalido e nao foi possivel carrega-lo!"),
    PLACEHOLDER_FOUND("§7[§1BkShop§7] §bPlaceholderAPI found, enabling support...",
    "§7[§1BkLoja§7] §bPlaceholderAPI encontrado, habilitando suporte...");

    private final String[] message;

    InternalMessages(String enMessage, String brMessage) {
        message = new String[2];
        this.message[0] = enMessage;
        this.message[1] = brMessage;
    }

    public String getMessage(BkPlugin plugin) {
        if (plugin.getLangFile().getLanguage().equalsIgnoreCase("pt_br"))  return message[1];
        else return message[0];
    }

}
