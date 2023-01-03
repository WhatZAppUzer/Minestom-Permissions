package dev.whatsappuser.minestom.permissions;

import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;

import java.util.Objects;
import java.util.UUID;

/**
 * development by TimoH created on 18:59:04 | 01.01.2023
 */

public class PermissionPool {

    public static PermissionGroup DEFAULT;

    public PermissionPool(PermissionGroup defaultGroup) {
        DEFAULT = defaultGroup;
    }

    public PermissionUser getPlayer(UUID uuid) {
        return PermissionBootstrap.getBootstrap().getDatabase().getPlayer(uuid);
    }

    public PermissionUser getPlayer(String name) {
        return PermissionBootstrap.getBootstrap().getDatabase().getPlayer(name);
    }

    public PermissionGroup getGroup(String name) {
        return PermissionBootstrap.getBootstrap().getDatabase().getGroup(name);
    }

    public PermissionGroup getGroup(int id) {
        return PermissionBootstrap.getBootstrap().getDatabase().getGroup(id);
    }

    public boolean isGroupRegistered(String group) {
        return this.getGroup(group) != null;
    }

    public boolean isPlayerInGroup(Player player, String group) {
        var user = getPlayer(player.getUuid());
        var permissionGroup = getGroup(group);

        return user.getGroup().getName().equalsIgnoreCase(permissionGroup.getName());
    }

    public void setPlayerInGroup(Player player, String group) {
        var permissionUser = getPlayer(player.getUuid());
        var permissionGroup = getGroup(group);
        permissionUser.setGroup(permissionGroup);
    }

    public void updatePlayer(Player player) {
        var permissionUser = getPlayer(player.getUuid());
        PermissionBootstrap.getBootstrap().getDatabase().getCachedUsers().remove(permissionUser);
        PermissionBootstrap.getBootstrap().getDatabase().getCachedUsers().add(permissionUser);
        player.getAllPermissions().clear();
        for (String permission : permissionUser.getGroup().getPermissions()) {
            player.addPermission(new Permission(permission));
        }
        for (String permission : permissionUser.getPermissions()) {
            player.addPermission(new Permission(permission));
        }
        player.refreshCommands();
        player.setDisplayName(Component.text(permissionUser.getGroup().getDisplay() + player.getUsername()));
    }

    public void updateGroup(PermissionGroup group) {
        PermissionBootstrap.getBootstrap().getDatabase().getAllLoadedGroups().remove(group);
        PermissionBootstrap.getBootstrap().getDatabase().getAllLoadedGroups().add(group);
        for (PermissionUser cachedUser : PermissionBootstrap.getBootstrap().getDatabase().getCachedUsers()) {
            if(cachedUser.getGroup().equals(group)) {
                Player player = MinecraftServer.getConnectionManager().getPlayer(cachedUser.getUniqueId());
                assert player != null;
                updatePlayer(player);
            }
        }
    }

    public void reload() {
        var database = PermissionBootstrap.getBootstrap().getDatabase();
        for (PermissionGroup allLoadedGroup : database.getAllLoadedGroups()) {
            updateGroup(allLoadedGroup);
            database.saveGroup(allLoadedGroup);
            database.loadGroups();
        }
        for (PermissionUser cachedUser : database.getCachedUsers()) {
            updatePlayer(Objects.requireNonNull(MinecraftServer.getConnectionManager().getPlayer(cachedUser.getUniqueId())));
            database.savePlayer(cachedUser);
            database.loadPlayer(cachedUser.getUniqueId());
        }
    }

    public void createGroup(PermissionGroup group) {
        PermissionBootstrap.getBootstrap().getDatabase().saveGroup(group);
        PermissionBootstrap.getBootstrap().getDatabase().loadGroup(group.getName());
    }

    public boolean addPermission(String permission, PermissionGroup group) {
        if(group.hasPermission(permission))
            return false;
        group.getPermissions().add(permission);
        return true;
    }

    public void addPermission(Player player, String permission) {
        PermissionUser user = getPlayer(player.getUuid());
        user.addPermission(permission);
        player.addPermission(new Permission(permission));
        player.refreshCommands();
    }

    public void removePermission(Player player, String permission) {
        PermissionUser user = getPlayer(player.getUuid());
        user.removePermission(permission);
        player.removePermission(permission);
        player.refreshCommands();
    }
}
