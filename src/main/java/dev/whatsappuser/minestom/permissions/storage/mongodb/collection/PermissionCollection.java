package dev.whatsappuser.minestom.permissions.storage.mongodb.collection;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import dev.whatsappuser.minestom.permissions.storage.mongodb.MongoDBService;
import dev.whatsappuser.minestom.permissions.utilities.MongoUtils;
import net.minestom.server.MinecraftServer;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * development by TimoH created on 21:51:30 | 06.01.2023
 */

public class PermissionCollection implements IDatabase {

    private final MongoDBService service;


    public PermissionCollection(MongoDBService service) {
        this.service = service;
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
        var document = getUserCollection().find(MongoUtils.createUserFilter(uuid)).first();
        if (document == null)
            return null;

        PermissionUser user = new PermissionUser(uuid, document.getString("name"), getGroup(document.getString("group")), new HashSet<>(document.getList("permissions", String.class)));
        PermissionBootstrap.PERMISSION_USERS.add(user);
        return user;
    }

    @Override
    public void loadPlayers() {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> loadPlayer(player.getUuid()));
    }

    @Override
    public void createPlayer(UUID uuid) {
        var document = getUserCollection().find(MongoUtils.createUserFilter(uuid)).first();
        if (document != null)
            return;

        document = new Document("uuid", uuid.toString())
                .append("name", MinecraftServer.getConnectionManager().getPlayer(uuid).getUsername())
                .append("group", PermissionPool.DEFAULT)
                .append("permissions", new HashSet<>());
        getUserCollection().insertOne(document);
    }

    @Override
    public void createGroup(PermissionGroup group) {
        var document = getGroupCollection().find(MongoUtils.createGroupFilter(group.getName())).first();
        if (document != null)
            return;

        document = new Document("name", group.getName())
                .append("prefix", group.getPrefix())
                .append("display", group.getDisplay())
                .append("suffix", group.getSuffix())
                .append("colorcode", group.getColorCode())
                .append("chatformat", group.getChatFormat())
                .append("permissions", group.getPermissions())
                .append("idGroup", group.getId())
                .append("priority", group.getPriority())
                .append("default", group.isDefault());
        getGroupCollection().insertOne(document);
    }

    @Override
    public void deleteGroup(String group) {
        var document = getGroupCollection().find(MongoUtils.createGroupFilter(group)).first();
        if (document == null)
            return;
        getGroupCollection().deleteOne(document);
    }

    @Override
    public Set<PermissionGroup> getAllGroups() {
        Set<PermissionGroup> groups = new HashSet<>();
        for (Document document : getGroupCollection().find()) {
            var name = document.getString("name");
            var prefix = document.getString("prefix");
            var display = document.getString("display");
            var suffix = document.getString("suffix");
            var colorcode = document.getString("colorcode");
            var chatformat = document.getString("chatformat");
            var permissions = document.getList("permissions", String.class);
            var idGroup = document.getInteger("idGroup");
            var priority = document.getInteger("priority");
            var aDefault = document.getBoolean("default");
            groups.add(new PermissionGroup(name, prefix, display, suffix, colorcode, chatformat, permissions, idGroup, priority, aDefault));
        }
        return groups;
    }

    @Override
    public Set<PermissionGroup> getAllLoadedGroups() {
        return PermissionBootstrap.PERMISSION_GROUPS;
    }

    @Override
    public void savePlayer(PermissionUser user) {
        var document = getUserCollection().find(MongoUtils.createUserFilter(user.getUniqueId())).first();
        if (document == null)
            return;

        getUserCollection().updateOne(MongoUtils.createUserFilter(user.getUniqueId()), new BasicDBObject("$set", document));
    }

    @Override
    public void savePlayers() {
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> savePlayer(getPlayer(player.getUuid())));
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        var document = getGroupCollection().find(MongoUtils.createGroupFilter(group.getName())).first();
        if (document == null)
            return;

        getGroupCollection().updateOne(MongoUtils.createGroupFilter(group.getName()), new BasicDBObject("$set", document));
    }

    @Override
    public void saveGroups() {
        getAllLoadedGroups().forEach(this::saveGroup);
    }

    @Override
    public PermissionGroup loadGroup(String name) {
        var document = getGroupCollection().find(MongoUtils.createGroupFilter(name)).first();
        if (document == null)
            return null;

        PermissionGroup group = new PermissionGroup(name, document.getString("prefix"), document.getString("display")
                , document.getString("suffix"), document.getString("colorcode"), document.getString("chatformat")
                , document.getList("permissions", String.class), document.getInteger("idGroup")
                , document.getInteger("priority"), document.getBoolean("default"));
        PermissionBootstrap.PERMISSION_GROUPS.add(group);
        return group;
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
        getAllGroups().forEach(group -> loadGroup(group.getName()));
    }

    @Override
    public Set<PermissionUser> getCachedUsers() {
        return PermissionBootstrap.PERMISSION_USERS;
    }

    public MongoCollection<Document> getUserCollection() {
        return this.service.getCollection("permission_users");
    }

    public MongoCollection<Document> getGroupCollection() {
        return this.service.getCollection("permission_groups");
    }
}
