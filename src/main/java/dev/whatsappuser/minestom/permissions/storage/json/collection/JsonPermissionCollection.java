package dev.whatsappuser.minestom.permissions.storage.json.collection;

import com.google.gson.reflect.TypeToken;
import dev.whatsappuser.minestom.lib.configuration.JsonConfiguration;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import dev.whatsappuser.minestom.permissions.storage.json.JsonService;
import net.minestom.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * development by TimoH created on 00:32:41 | 07.01.2023
 */

public class JsonPermissionCollection extends JsonService implements IDatabase {

    private final File groupFile;
    private final JsonConfiguration groupDocument;
    private JsonConfiguration userDocument;

    public JsonPermissionCollection() {
        this.groupFile = getPermissionsFile();
        this.groupDocument = getGroupsConfig();
        File file = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/Users");
        if (! file.isDirectory()) {
            file.mkdirs();
        }
    }

    @Override
    public PermissionUser getPlayer(UUID uuid) {
        for (PermissionUser permissionUser : PermissionBootstrap.PERMISSION_USERS) {
            if (permissionUser.getUniqueId().equals(uuid))
                return permissionUser;
        }
        return null;
    }

    @Override
    public PermissionGroup getGroup(String name) {
        for (PermissionGroup permissionGroup : PermissionBootstrap.PERMISSION_GROUPS) {
            if (permissionGroup.getName().equalsIgnoreCase(name))
                return permissionGroup;
        }
        return null;
    }

    @Override
    public PermissionUser loadPlayer(UUID uuid) {
        File file = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/Users/", uuid.toString() + ".json");
        if (! file.exists())
            createPlayer(uuid);

        this.userDocument = JsonConfiguration.loadDocument(file);
        var name = this.userDocument.getString("name");
        var group = this.userDocument.getString("group");
        Set<String> permissions = this.userDocument.getObject("permissions", new TypeToken<Set<String>>() {
        }.getType());
        PermissionUser user = new PermissionUser(uuid, name, getGroup(group), permissions);
        PermissionBootstrap.PERMISSION_USERS.add(user);
        this.userDocument = null;
        return user;
    }

    @Override
    public void loadPlayers() {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> loadPlayer(player.getUuid()));
    }

    @Override
    public void createPlayer(UUID uuid) {
        File file = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/Users/", uuid.toString() + ".json");
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.userDocument = JsonConfiguration.loadDocument(file);
        PermissionUser user = new PermissionUser(uuid, MinecraftServer.getConnectionManager().getPlayer(uuid).getUsername(), PermissionPool.DEFAULT, new HashSet<>());
        this.userDocument.append("name", user.getName()).append("group", user.getGroup().getName()).append("permissions", user.getPermissions());
        this.userDocument.save(file);
    }

    @Override
    public void createGroup(PermissionGroup group) {
        if (this.groupDocument.contains(group.getName()))
            return;

        JsonConfiguration config = new JsonConfiguration(group.getName())
                .append("prefix", group.getPrefix())
                .append("display", group.getDisplay())
                .append("suffix", group.getSuffix())
                .append("colorcode", group.getColorCode())
                .append("chatformat", group.getChatFormat())
                .append("permissions", group.getPermissions())
                .append("id", group.getId())
                .append("priority", group.getPriority())
                .append("default", group.isDefault());
        this.groupDocument.append(group.getName(), config);
        this.groupDocument.save(this.groupFile);
    }

    @Override
    public void deleteGroup(String group) {
        if (! this.groupDocument.contains(group))
            return;
        this.groupDocument.remove(group);
    }

    @Override
    public Set<PermissionGroup> getAllGroups() {
        Set<PermissionGroup> groups = new HashSet<>();
        for (String key : this.groupDocument.keys()) {
            JsonConfiguration config = this.groupDocument.getDocument(key);
            groups.add(new PermissionGroup(config.getString("name"), config.getString("prefix"), config.getString("display")
                    , config.getString("suffix"), config.getString("colorcode"), config.getString("chatformat")
                    , config.getObject("permissions", new TypeToken<List<String>>() {
            }.getType()), config.getInt("id"), config.getInt("priority")
                    , config.getBoolean("default")));
        }
        return groups;
    }

    @Override
    public Set<PermissionGroup> getAllLoadedGroups() {
        return PermissionBootstrap.PERMISSION_GROUPS;
    }

    @Override
    public void savePlayer(PermissionUser user) {
        File file = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/Users/", user.getUniqueId().toString() + ".json");
        if (! file.exists())
            createPlayer(user.getUniqueId());

        this.userDocument = JsonConfiguration.loadDocument(file);
        this.userDocument.append("name", user.getName())
                .append("group", user.getGroup().getName())
                .append("permissions", user.getPermissions());
        this.userDocument.save(file);
        this.userDocument = null;
    }

    @Override
    public void savePlayers() {
        getCachedUsers().forEach(this::savePlayer);
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        if (! this.groupDocument.contains(group.getName()))
            createGroup(group);

        var document = this.groupDocument.getDocument(group.getName());
        document.append("prefix", group.getPrefix())
                .append("display", group.getDisplay())
                .append("suffix", group.getSuffix())
                .append("colorcode", group.getColorCode())
                .append("chatformat", group.getChatFormat())
                .append("permissions", group.getPermissions())
                .append("id", group.getId())
                .append("priority", group.getPriority())
                .append("default", group.isDefault());
        this.groupDocument.append(group.getName(), document);
        this.groupDocument.save(this.groupFile);
    }

    @Override
    public void saveGroups() {
        getAllLoadedGroups().forEach(this::saveGroup);
    }

    @Override
    public PermissionGroup loadGroup(String name) {
        if (! this.groupDocument.contains(name))
            return null;

        JsonConfiguration config = this.groupDocument.getDocument(name);
        PermissionGroup group = new PermissionGroup(config.getString("name"), config.getString("prefix"), config.getString("display")
                , config.getString("suffix"), config.getString("colorcode"), config.getString("chatformat")
                , config.getObject("permissions", new TypeToken<List<String>>() {
        }.getType()), config.getInt("id"), config.getInt("priority")
                , config.getBoolean("default"));
        PermissionBootstrap.PERMISSION_GROUPS.add(group);
        return group;
    }

    @Override
    public boolean isDefaultGroupExists() {
        for (PermissionGroup allLoadedGroup : this.getAllLoadedGroups()) {
            return allLoadedGroup.isDefault();
        }
        return false;
    }

    @Override
    public void loadGroups() {
        getAllGroups().forEach(group -> loadGroup(group.getName()));
    }

    @Override
    public Set<PermissionUser> getCachedUsers() {
        return PermissionBootstrap.PERMISSION_USERS;
    }
}
