package dev.whatsappuser.minestom.permissions.group;

import java.util.ArrayList;

/**
 * development by TimoH created on 20:30:15 | 07.01.2023
 */

public class DefaultPermissionGroup extends PermissionGroup {

    public DefaultPermissionGroup() {
        super("default", "§7", "§7", "", "§7", "§7", new ArrayList<>(), 0, 10, true);
    }
}
