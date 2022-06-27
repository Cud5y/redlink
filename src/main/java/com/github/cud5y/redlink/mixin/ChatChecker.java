package com.github.cud5y.redlink.mixin;

import com.github.cud5y.redlink.DiscordWebhook;
import com.github.cud5y.redlink.Redlink;
import com.github.cud5y.redlink.config.ModConfigs;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

@Mixin(ChatHudListener.class)
public class ChatChecker {
    boolean found = false;
    int messageInt = 1;
    //Checks the webhook URL from the config file
    DiscordWebhook webhook = new DiscordWebhook(ModConfigs.WEBHOOK);
    DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
    //Injects into the onChatMessage method so it runs when a chat is received and then checks if the message type is SYSTEM
    @Inject(at = @At("HEAD"),method = "onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V")
    private void check(MessageType type, Text message, UUID sender, CallbackInfo info){
        if(type.toString() == "SYSTEM") {
            //Changes the message from Text to a String and then splits it into a array where there are commas
            String messageText = message.toString();
            String[] messageArray = messageText.split(",");
            messageArray[0] = messageArray[0].replace("TextComponent{text=", "");
            messageArray[3] = messageArray[3].replace(" siblings=[TextComponent{text=", "");
            //If messageArray 3 contains Shop info it creates a embed using the webhook and sets found to true
            if (messageArray[3].contains("Shop Information")) {
                webhook.addEmbed(embed);
                found = true;
                saveMessage(messageArray);
            }
            //If found is true then this will run due to there being 3 messages that contain all the shop info message 1 contains the owner,stock and item message two buy and message three sell
            else if (found) {
                messageArray[0] = messageArray[0].replace("'","");
                saveMessage(messageArray);
            }
        }
    }

    private void saveMessage(String[] message){
        try {
            //if message int is 3 it sends the last message and then resets all values and sends the webhook
            if(messageInt == 3){
                send(message);
                messageInt = 1;
                found = false;
                webhook.execute();
            }
            else {
                send(message);
                messageInt ++;
            }

        } catch (IOException e) {
            Redlink.LOGGER.info(String.valueOf(e));
            messageInt = 1;
            found = false;
        }
    }
    public void send(String[] message){
        //webhook username set with avatar and title it then checks the messageInt if it is a specific value it will add specific values of the message.
            webhook.setAvatarUrl("https://cdn.discordapp.com/icons/981966128039460884/19bca2031f71631086ae7c8bdb2f102d.png");
            webhook.setUsername("Redlink Stonks");
            embed.setTitle("Shop Information:");
            embed.setColor(Color.RED);
        switch (messageInt) {
            case 1 -> {
                message[49] = message[49].replace(" siblings=[TextComponent{text='", "");
                message[49] = message[49].replace("\n'", "");
                message[95] = message[95].replace(" siblings=[TextComponent{text='", "");
                message[95] = message[95].replace("\n'", "");
                message[141] = message[141].replace(" siblings=[TextComponent{text='", "");
                message[141] = message[141].replace("'", "");
                embed.addField("Owner: " + message[49], "|", false);
                embed.addField("Stock: " + message[95], "|", false);
                embed.addField("Item: " + message[141], "|", false);
            }
            case 2 -> {
                message[26] = message[26].replace(" siblings=[TextComponent{text='", "");
                message[26] = message[26].replace("'", "");
                message[72] = message[72].replace(" siblings=[TextComponent{text='", "");
                message[72] = message[72].replace("'", "");
                if (message[73].contains("siblings")) {
                    embed.addField("Buy: " + message[26] + " for " + message[72], "|", false);
                } else {
                    embed.addField("Buy: " + message[26] + " for " + message[72] + message[73], "|", false);
                }
            }
            case 3 -> {
                message[26] = message[26].replace(" siblings=[TextComponent{text='", "");
                message[26] = message[26].replace("'", "");
                message[72] = message[72].replace(" siblings=[TextComponent{text='", "");
                message[72] = message[72].replace("'", "");
                if (message[3].contains("Sell")) {
                    if (message[73].contains("siblings")) {
                        embed.addField("Sell: " + message[26] + " for " + message[72], "|", false);
                    } else {
                        embed.addField("Sell: " + message[26] + " for " + message[72] + message[73], "|", false);
                    }
                }
            }
        }
    }
}
