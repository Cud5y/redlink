package com.github.cud5y.redlink;

import com.github.cud5y.redlink.config.ModConfigs;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Redlink implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("redlink");

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();

		LOGGER.info("Redlink Stonks is loaded");
	}
}
