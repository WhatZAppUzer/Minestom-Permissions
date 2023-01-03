package dev.whatsappuser.minestom.permissions.listener;

import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.permission.Permission;
import org.jetbrains.annotations.NotNull;

/**
 * development by TimoH created on 20:10:29 | 02.01.2023
 */

public class PlayerSpawnListener implements EventListener<PlayerSpawnEvent> {

    private final IDatabase database;

    public PlayerSpawnListener(IDatabase database) {
        this.database = database;
    }

    @Override
    public @NotNull Class<PlayerSpawnEvent> eventType() {
        return PlayerSpawnEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerSpawnEvent event) {

        final var player = event.getPlayer();
        var permissionUser = this.database.loadPlayer(player.getUuid());
        for (String permission : permissionUser.getPermissions()) {
            player.getAllPermissions().add(new Permission(permission));
        }
        for (String permission : permissionUser.getGroup().getPermissions()) {
            player.getAllPermissions().add(new Permission(permission));
        }
        player.refreshCommands();
        player.setDisplayName(Component.text(permissionUser.getGroup().getDisplay() + player.getUsername()));
        return Result.SUCCESS;
    }
}
