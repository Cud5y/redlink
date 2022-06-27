package com.github.cud5y.redlink.config;

import com.mojang.datafixers.util.Pair;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String WEBHOOK;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(  "Redlink config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("key.redlink.WebhookLink", "https://discord.com/api/webhooks/..."), "Discord Webhook goes here");
    }

    private static void assignConfigs() {
        WEBHOOK = CONFIG.getOrDefault("key.redlink.WebhookLink", "https://discord.com/api/webhooks/...");
        System.out.println("All " + configs.getConfigsList().size() + "configs have been set properly");
    }
}