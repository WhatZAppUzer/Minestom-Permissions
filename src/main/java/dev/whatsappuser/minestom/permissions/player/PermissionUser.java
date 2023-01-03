package dev.whatsappuser.minestom.permissions.player;

import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

/**
 * development by TimoH created on 18:42:26 | 01.01.2023
 */

@Getter
@Setter
public class PermissionUser {

    private UUID uniqueId;
    private String name;
    private PermissionGroup group;
    private Set<String> permissions;

    public PermissionUser(UUID uniqueId, String name, PermissionGroup group, Set<String> permissions) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.group = group;
        this.permissions = permissions;
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    public boolean hasPermissionFromGroup(String permission) {
        return this.group.hasPermission(permission);
    }

    public void addPermission(String permission) {
        if(hasPermission(permission))
            return;
        this.permissions.add(permission);
    }

    public void removePermission(String permission) {
        if(!hasPermission(permission))
            return;
        this.permissions.remove(permission);
    }
}
