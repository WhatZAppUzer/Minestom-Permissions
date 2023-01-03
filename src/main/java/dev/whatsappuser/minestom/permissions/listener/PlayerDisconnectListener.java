package dev.whatsappuser.minestom.permissions.listener;

import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;

/**
 * development by TimoH created on 16:20:44 | 03.01.2023
 */

public class PlayerDisconnectListener implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
        var player = event.getPlayer();
        PermissionUser permissionUser = PermissionBootstrap.getBootstrap().getPermissionPool().getPlayer(player.getUuid());
        PermissionBootstrap.getBootstrap().getDatabase().savePlayer(permissionUser);
        return Result.SUCCESS;
    }
}
