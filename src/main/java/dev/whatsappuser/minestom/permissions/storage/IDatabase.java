package dev.whatsappuser.minestom.permissions.storage;

import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;

import java.util.Set;
import java.util.UUID;

/**
 * development by TimoH created on 18:47:10 | 01.01.2023
 */

public interface IDatabase {

    void loadDatabase();

    void unloadDatabase();

    PermissionUser getPlayer(UUID uuid);

    PermissionUser getPlayer(String name);

    PermissionGroup getGroup(String name);

    PermissionGroup getGroup(int id);

    PermissionUser loadPlayer(UUID uuid);

    PermissionUser loadPlayer(String name);

    void createPlayer(UUID uuid);

    void createPlayer(String name);

    void createGroup(PermissionGroup group);

    void deleteGroup(String group);

    Set<PermissionGroup> getAllGroups();

    Set<PermissionGroup> getAllLoadedGroups();

    void savePlayer(PermissionUser user);

    void saveGroup(PermissionGroup group);

    PermissionGroup loadGroup(String name);

    PermissionGroup loadGroup(int id);

    boolean isDefaultGroupExists();

    void loadGroups();

    Set<PermissionUser> getCachedUsers();
}
