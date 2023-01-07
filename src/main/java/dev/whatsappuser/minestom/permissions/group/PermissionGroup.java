package dev.whatsappuser.minestom.permissions.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * development by TimoH created on 18:42:42 | 01.01.2023
 */

@Getter
@Setter
public class PermissionGroup {

    private String name, prefix, display, suffix, colorCode, chatFormat;
    private List<String> permissions;
    private int id, priority;
    private boolean isDefault;

    public PermissionGroup(String name, String prefix, String display, String suffix, String colorCode, String chatFormat, List<String> permissions, int id, int priority, boolean isDefault) {
        this.name = name;
        this.prefix = prefix;
        this.display = display;
        this.suffix = suffix;
        this.colorCode = colorCode;
        this.chatFormat = chatFormat;
        this.permissions = permissions;
        this.id = id;
        this.priority = priority;
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
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
