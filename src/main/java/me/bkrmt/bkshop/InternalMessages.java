package me.bkrmt.bkshop;


import me.bkrmt.bkcore.BkPlugin;

public enum InternalMessages {
    LOADING_SHOPS("§7[§1BkShop§7] §bLoading shops...",
    "§7[§1BkLoja§7] §bCarregando lojas..."),
    OWN_PLUGINS_FOUND("§7[§1BkShop§7] §bOne or more of my plugins found, enabling support...",
    "§7[§1BkLoja§7] §bUm ou mais dos meus plugins encontrados, habilitando suporte..."),
    PLUGIN_STARTING("§7[§1BkShop§7] §bPlugin starting...",
    "§7[§1BkLoja§7] §bPlugin iniciando..."),
    PLUGIN_STARTED("§7[§1BkShop§7] §bPlugin started!",
    "§7[§1BkLoja§7] §bPlugin iniciado!");

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
