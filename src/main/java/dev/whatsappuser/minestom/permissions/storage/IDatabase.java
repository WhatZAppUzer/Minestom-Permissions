package dev.whatsappuser.minestom.permissions.storage;

import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;

import java.util.Set;
import java.util.UUID;

/**
 * development by TimoH created on 18:47:10 | 01.01.2023
 */

public interface IDatabase {

    PermissionUser getPlayer(UUID uuid);

    PermissionGroup getGroup(String name);

    PermissionUser loadPlayer(UUID uuid);

    void loadPlayers();

    void createPlayer(UUID uuid);

    void createGroup(PermissionGroup group);

    void deleteGroup(String group);

    Set<PermissionGroup> getAllGroups();

    Set<PermissionGroup> getAllLoadedGroups();

    void savePlayer(PermissionUser user);

    void savePlayers();

    void saveGroup(PermissionGroup group);

    void saveGroups();

    PermissionGroup loadGroup(String name);

    boolean isDefaultGroupExists();

    void loadGroups();

    Set<PermissionUser> getCachedUsers();
}
