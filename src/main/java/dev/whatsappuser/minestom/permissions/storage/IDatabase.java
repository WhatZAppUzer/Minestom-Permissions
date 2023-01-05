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

    PermissionGroup getGroup(String name);

    PermissionUser loadPlayer(UUID uuid);

    void loadPlayers();

    PermissionUser loadPlayer(String name);

    void createPlayer(UUID uuid);

    void createPlayer(String name);

    void createGroup(PermissionGroup group);

    void deleteGroup(String group);

    Set<PermissionGroup> getAllGroups();

    Set<PermissionGroup> getAllLoadedGroups();

    void reloadGroup(PermissionGroup group);

    void savePlayer(PermissionUser user);

    void savePlayers();

    void saveGroup(PermissionGroup group);

    void saveGroups();

    PermissionGroup loadGroup(String name);

    PermissionGroup loadGroup(int id);

    boolean isDefaultGroupExists();

    void loadGroups();

    Set<PermissionUser> getCachedUsers();
}
