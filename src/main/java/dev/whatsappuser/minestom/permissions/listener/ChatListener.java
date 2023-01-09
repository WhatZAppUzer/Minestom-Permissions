package dev.whatsappuser.minestom.permissions.listener;

import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import net.kyori.adventure.text.Component;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * development by TimoH created on 16:46:34 | 03.01.2023
 */

public class ChatListener implements EventListener<PlayerChatEvent> {
    @Override
    public @NotNull Class<PlayerChatEvent> eventType() {
        return PlayerChatEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerChatEvent event) {
        PermissionUser user = PermissionBootstrap.getBootstrap().getPermissionPool().getUser(event.getPlayer().getUuid());
        var permissionGroup = user.getGroup();
        event.setChatFormat(playerChatEvent -> Component.text(permissionGroup.getChatFormat() + event.getPlayer().getUsername() + " §8» §r" + playerChatEvent.getMessage()));
        return Result.SUCCESS;
    }
}
