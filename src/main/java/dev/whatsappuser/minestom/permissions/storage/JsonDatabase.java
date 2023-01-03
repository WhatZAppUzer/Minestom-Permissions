package dev.whatsappuser.minestom.permissions.storage;

import com.google.gson.reflect.TypeToken;
import dev.whatsappuser.minestom.lib.configuration.JsonConfiguration;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.utilities.FileUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * development by TimoH created on 18:50:31 | 01.01.2023
 */

public class JsonDatabase implements IDatabase {

    private File groupFile;
    private JsonConfiguration userDocument;
    private JsonConfiguration groupDocument;

    private final Set<PermissionUser> permissionUsers;
    private final Set<PermissionGroup> permissionGroups;

    public JsonDatabase() {
        this.permissionUsers = new HashSet<>();
        this.permissionGroups = new HashSet<>();
    }

    @Override
    public void loadDatabase() {
        try {
            if (! FileUtil.doesFileExist(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/"))
                FileUtil.createDirectory(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/");

            if (! FileUtil.doesFileExist(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/Users/"))
                FileUtil.createDirectory(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/Users/");

            if (! FileUtil.doesFileExist(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/permissions.json"))
                FileUtil.createFile(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/permissions.json");

            this.groupFile = FileUtil.getFile(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/permissions.json");
            this.groupDocument = JsonConfiguration.loadDocument(groupFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unloadDatabase() {

    }

    @Override
    public PermissionUser getPlayer(UUID uuid) {
        for (PermissionUser permissionUser : this.permissionUsers) {
            if (permissionUser.getUniqueId().equals(uuid))
                return permissionUser;
        }
        return null;
    }

    @Override
    public PermissionUser getPlayer(String name) {
        for (PermissionUser permissionUser : this.permissionUsers) {
            if (permissionUser.getName().equalsIgnoreCase(name))
                return permissionUser;
        }
        return null;
    }

    @Override
    public PermissionGroup getGroup(String name) {
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            if (permissionGroup.getName().equalsIgnoreCase(name))
                return permissionGroup;
        }
        return null;
    }

    @Override
    public PermissionGroup getGroup(int id) {
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            if (permissionGroup.getId() == id)
                return permissionGroup;
        }
        return null;
    }

    @Override
    public PermissionUser loadPlayer(UUID uuid) {
        if (! FileUtil.doesFileExist(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/Users/" + uuid.toString() + ".json"))
            createPlayer(uuid);

        this.userDocument = JsonConfiguration.loadDocument(FileUtil.getFile(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/Users/" + uuid.toString() + ".json"));

        var name = this.userDocument.getString("name");
        var groupName = this.userDocument.getString("groupName");
        Set<String> permissions = this.userDocument.getObject("permissions", new TypeToken<Set<String>>() {
        }.getType());
        PermissionUser permissionPlayer = new PermissionUser(uuid, name, getGroup(groupName), permissions);
        if(this.permissionUsers.contains(permissionPlayer))
            this.permissionUsers.remove(permissionPlayer);
        this.permissionUsers.add(permissionPlayer);
        return permissionPlayer;
    }

    @Override
    public void loadPlayers() {
        for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers())
            loadPlayer(onlinePlayer.getUuid());
    }

    @Override
    public PermissionUser loadPlayer(String name) {
        throw new UnsupportedOperationException("this method is not supported, use: loadPlayer(UUID)");
    }

    @Override
    public void createPlayer(UUID uuid) {
        File file = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/Users/" + uuid.toString() + ".json");
        if (! file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.userDocument = JsonConfiguration.loadDocument(file);
        PermissionUser user = new PermissionUser(uuid, MinecraftServer.getConnectionManager().getPlayer(uuid).getUsername(), PermissionPool.DEFAULT, new HashSet<>());
        this.userDocument.append("name", user.getName()).append("groupName", user.getGroup().getName()).append("permissions", user.getPermissions());
        this.userDocument.save(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/Users/" + uuid.toString() + ".json");
    }

    @Override
    public void createPlayer(String name) {
        throw new UnsupportedOperationException("this method is not supported, use: loadPlayer(UUID)");
    }

    @Override
    public void createGroup(PermissionGroup group) {
        if (this.groupDocument.contains(group.getName()))
            return;

        var groupDoc = new JsonConfiguration(group.getName());
        groupDoc.append("prefix", group.getPrefix())
                .append("display", group.getDisplay())
                .append("suffix", group.getSuffix())
                .append("colorCode", group.getColorCode())
                .append("chatFormat", group.getChatFormat())
                .append("permissions", group.getPermissions())
                .append("priority", group.getPriority())
                .append("id", group.getId())
                .append("default", group.isDefault());
        this.groupDocument.append(group.getName(), groupDoc);
        this.groupDocument.save(this.groupFile);
    }

    @Override
    public void deleteGroup(String group) {
        if(!this.groupDocument.contains(group))
            return;

        getAllLoadedGroups().remove(getGroup(group));
        this.groupDocument.remove(group).save(this.groupFile);
    }

    @Override
    public Set<PermissionGroup> getAllGroups() {
        Set<PermissionGroup> set = new HashSet<>();
        for (String key : this.groupDocument.keys()) {
            JsonConfiguration document = this.groupDocument.getDocument(key);
            PermissionGroup group = new PermissionGroup(document.getString("name"), document.getString("prefix"), document.getString("display"), document.getString("suffix")
                    , document.getString("colorCode"), document.getString("chatFormat")
                    , document.getObject("permissions", new TypeToken<Set<String>>() {
            }.getType()), document.getInt("id"), document.getInt("priority"), document.getBoolean("default"));
            set.add(group);
        }
        return set;
    }

    @Override
    public Set<PermissionGroup> getAllLoadedGroups() {
        return this.permissionGroups;
    }

    @Override
    public void savePlayer(PermissionUser user) {
        File file = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/Permissions/Users/", user.getUniqueId().toString() + ".json");
        if (this.userDocument == null)
            this.userDocument = JsonConfiguration.loadDocument(file);

        var name = user.getName();
        var groupName = user.getGroup().getName();
        var permissions = user.getPermissions();
        this.userDocument.append("name", name);
        this.userDocument.append("groupName", groupName);
        this.userDocument.append("permissions", permissions);
        this.userDocument.save(file);
    }

    @Override
    public void savePlayers() {
        for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers())
            savePlayer(getPlayer(onlinePlayer.getUuid()));
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        var name = group.getName();
        var isDefault = group.isDefault();
        var prefix = group.getPrefix();
        var display = group.getDisplay();
        var suffix = group.getSuffix();
        var colorCode = group.getColorCode();
        var chatFormat = group.getChatFormat();
        var id = group.getId();
        var priority = group.getPriority();
        var permissions = group.getPermissions();

        var groupDoc = this.groupDocument.getDocument(name) != null ? this.groupDocument.getDocument(name) : new JsonConfiguration(name);

        groupDoc.append("prefix", prefix).append("display", display).append("suffix", suffix)
                .append("colorCode", colorCode).append("chatFormat", chatFormat).append("permissions", permissions).append("id", id).append("priority", priority).append("default", isDefault);
        if(name == null) return;
        this.groupDocument.append(name, groupDoc);
        this.groupDocument.save(this.groupFile);
    }

    @Override
    public void saveGroups() {
        for (PermissionGroup allLoadedGroup : this.getAllLoadedGroups()) {
            saveGroup(allLoadedGroup);
        }
    }

    @Override
    public PermissionGroup loadGroup(String name) {
        var document = this.groupDocument.getDocument(name);
        var group = new PermissionGroup(name, document.getString("prefix"), document.getString("display"), document.getString("suffix")
                , document.getString("colorCode"), document.getString("chatFormat")
                , document.getObject("permissions", new TypeToken<Set<String>>() {
        }.getType()), document.getInt("id"), document.getInt("priority"), document.getBoolean("default"));
        getAllLoadedGroups().add(group);
        return group;
    }

    @Override
    @Deprecated
    public PermissionGroup loadGroup(int id) {
        throw new UnsupportedOperationException("this method is not supported use: loadGroup(name)");
    }

    @Override
    public boolean isDefaultGroupExists() {
        for (PermissionGroup allGroup : getAllGroups()) {
            return allGroup.isDefault();
        }
        return false;
    }

    @Override
    public void loadGroups() {
        for (String key : this.groupDocument.keys()) {
            var group = loadGroup(key);
            if(this.permissionGroups.contains(group))
                this.permissionGroups.remove(group);
            this.permissionGroups.add(group);
        }
    }

    @Override
    public Set<PermissionUser> getCachedUsers() {
        return this.permissionUsers;
    }
}
