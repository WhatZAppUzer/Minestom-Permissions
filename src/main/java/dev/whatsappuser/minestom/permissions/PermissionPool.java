package dev.whatsappuser.minestom.permissions;

import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;

import java.util.UUID;

/**
 * development by TimoH created on 18:59:04 | 01.01.2023
 */

public class PermissionPool {

    public static PermissionGroup DEFAULT;

    public PermissionPool(PermissionGroup group) {
        DEFAULT = group;
    }

    public PermissionGroup getGroup(String name) {
        return PermissionBootstrap.getBootstrap().getService().getDatabase().getGroup(name);
    }

    public PermissionUser getUser(UUID uuid) {
        return PermissionBootstrap.getBootstrap().getService().getDatabase().getPlayer(uuid);
    }

    public void addPermission(String permission, PermissionGroup group) {
        group.addPermission(permission);
    }

    public void removePermission(String permission, PermissionGroup group) {
        group.removePermission(permission);
    }

    public void addPermission(String permission, PermissionUser user) {
        user.addPermission(permission);
    }

    public void removePermission(String permission, PermissionUser user) {
        user.removePermission(permission);
    }

    public void update(PermissionGroup group) {
        PermissionBootstrap.getBootstrap().getService().getDatabase().saveGroup(group);
        PermissionBootstrap.PERMISSION_GROUPS.remove(group);
        PermissionBootstrap.getBootstrap().getService().getDatabase().loadGroup(group.getName());
    }

    public void update(PermissionUser user) {
        var group = user.getGroup();
        Player player = MinecraftServer.getConnectionManager().getPlayer(user.getUniqueId());
        for (Permission allPermission : player.getAllPermissions()) {
            player.removePermission(allPermission.getPermissionName());
        }

        for (String permission : group.getPermissions()) {
            player.getAllPermissions().add(new Permission(permission));
        }
        player.refreshCommands();
    }

}
