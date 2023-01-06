package dev.whatsappuser.minestom.permissions.storage.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

/**
 * development by TimoH created on 17:43:31 | 04.01.2023
 */

public class MongoConnection implements IDatabase {

    private MongoClient client;
    private MongoDatabase database;

    private final String host, collection, username, password, authdb;
    private final int port;
    private final boolean authentication, useSSL;

    private Set<PermissionUser> permissionUsers;
    private Set<PermissionGroup> permissionGroups;

    public MongoConnection(String host, String collection, String username, String password, String authdb, int port, boolean authentication, boolean useSSL) {
        this.host = host;
        this.collection = collection;
        this.username = username;
        this.password = password;
        this.authdb = authdb;
        this.port = port;
        this.authentication = authentication;
        this.useSSL = useSSL;
    }

    @Override
    public void loadDatabase() {
        MongoCredential credential = MongoCredential.createCredential(this.username, this.authdb, this.password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(this.useSSL).build();
        ServerAddress address = new ServerAddress(this.host, this.port);
        if (this.authentication) {
            this.client = new MongoClient(address, Collections.singletonList(credential), options);
        } else {
            this.client = new MongoClient(address, options);
        }
        this.database = this.client.getDatabase(this.collection);
        this.database.createCollection("mpermissions_users");
        this.database.createCollection("mpermissions_groups");
        this.client.startSession();
        this.permissionUsers = new HashSet<>();
        this.permissionGroups = new HashSet<>();
    }

    @Override
    public void unloadDatabase() {
        if (this.client != null)
            this.client.close();
    }

    @Override
    public PermissionUser getPlayer(UUID uuid) {
        for (PermissionUser permissionUser : this.permissionUsers)
            if (permissionUser.getUniqueId().equals(uuid))
                return permissionUser;
        return null;
    }

    @Override
    public PermissionGroup getGroup(String name) {
        for (PermissionGroup permissionGroup : this.permissionGroups)
            if (permissionGroup.getName().equalsIgnoreCase(name))
                return permissionGroup;
        return null;
    }

    @Override
    public PermissionUser loadPlayer(UUID uuid) {
        if (getPlayer(uuid) != null)
            this.permissionUsers.remove(getPlayer(uuid));

        var document = this.getUserCollection().find(createUserFilter(uuid)).first();
        if (document == null)
            return null;

        Set<String> permissions = new HashSet<>(document.getList("permissions", String.class));
        PermissionUser player = new PermissionUser(uuid, document.getString("name"), getGroup(document.getString("group")), permissions);
        this.permissionUsers.add(player);
        return player;
    }

    @Override
    public void loadPlayers() {
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            loadPlayer(player.getUuid());
        }
    }

    @Override
    public void createPlayer(UUID uuid) {
        var documentUser = getUserCollection().find(createUserFilter(uuid)).first();
        if (documentUser != null)
            return;

        PermissionUser user = new PermissionUser(uuid, Objects.requireNonNull(MinecraftServer.getConnectionManager().getPlayer(uuid)).getUsername(), PermissionPool.DEFAULT, new HashSet<>());
        Document document = new Document("uuid", uuid.toString())
                .append("group", user.getGroup().getName())
                .append("permissions", user.getPermissions());
        getUserCollection().insertOne(document);
    }

    @Override
    public void createGroup(PermissionGroup group) {
        var documentGroup = getGroupCollection().find(createGroupFilter(group.getName())).first();
        if (documentGroup != null)
            return;

        Document document = new Document("name", group.getName())
                .append("prefix", group.getPrefix())
                .append("display", group.getDisplay())
                .append("suffix", group.getSuffix())
                .append("colorCode", group.getColorCode())
                .append("chatFormat", group.getChatFormat())
                .append("permissions", group.getPermissions())
                .append("priority", group.getPriority())
                .append("id", group.getId())
                .append("default", group.isDefault());

        getGroupCollection().insertOne(document);
    }

    @Override
    public void deleteGroup(String group) {
        var document = getGroupCollection().find(createGroupFilter(group)).first();
        if (document == null)
            return;

        getGroupCollection().deleteOne(document);
    }

    @Override
    public Set<PermissionGroup> getAllGroups() {
        Set<PermissionGroup> groups = new HashSet<>();
        for (Document document : getGroupCollection().listIndexes()) {
            groups.add(new PermissionGroup(document.getString("name"), document.getString("display"),
                    document.getString("prefix"), document.getString("suffix"),
                    document.getString("colorCode"), document.getString("chatFormat"),
                    new HashSet<>(document.getList("permissions", String.class)),
                    document.getInteger("priority"), document.getInteger("id"), document.getBoolean("default")));
        }
        return groups;
    }

    @Override
    public Set<PermissionGroup> getAllLoadedGroups() {
        return this.permissionGroups;
    }

    @Override
    public void reloadGroup(PermissionGroup group) {
        if (! this.permissionGroups.contains(group))
            return;

        this.permissionGroups.remove(group);
        saveGroup(group);
        this.permissionGroups.add(group);
    }

    @Override
    public void savePlayer(PermissionUser user) {
        var document = getUserCollection().find(createUserFilter(user.getUniqueId())).first();
        if (document == null)
            return;

        document.append("name", user.getName())
                .append("group", user.getGroup().getName())
                .append("permissions", user.getGroup().getPermissions());

        getUserCollection().updateOne(createUserFilter(user.getUniqueId()), new BasicDBObject("$set", document));
    }

    @Override
    public void savePlayers() {
        getCachedUsers().forEach(this::savePlayer);
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        var document = getGroupCollection().find(createGroupFilter(group.getName())).first();
        if (document == null)
            return;

        document.append("prefix", group.getPrefix())
                .append("display", group.getDisplay())
                .append("suffix", group.getSuffix())
                .append("colorCode", group.getColorCode())
                .append("chatFormat", group.getChatFormat())
                .append("permissions", group.getPermissions())
                .append("id", group.getId())
                .append("priority", group.getPriority())
                .append("default", group.isDefault());

        getGroupCollection().updateOne(createGroupFilter(group.getName()), new BasicDBObject("$set", document));
    }

    @Override
    public void saveGroups() {
        getAllLoadedGroups().forEach(this::saveGroup);
    }

    @Override
    public PermissionGroup loadGroup(String name) {
        var document = getGroupCollection().find(createGroupFilter(name)).first();
        if (document == null) return null;

        PermissionGroup group = new PermissionGroup(name, document.getString("prefix"), document.getString("display")
                , document.getString("suffix"), document.getString("colorCode"), document.getString("chatFormat")
                , new HashSet<>(document.getList("permissions", String.class)), document.getInteger("id"), document.getInteger("priority")
                , document.getBoolean("default"));
        this.permissionGroups.add(group);
        return group;
    }

    @Override
    public boolean isDefaultGroupExists() {
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            return permissionGroup.isDefault();
        }
        return false;
    }

    @Override
    public void loadGroups() {
        for (Document document : getGroupCollection().listIndexes()) {
            loadGroup(document.getString("name"));
        }
    }

    @Override
    public Set<PermissionUser> getCachedUsers() {
        return this.permissionUsers;
    }

    public MongoCollection<Document> getGroupCollection() {
        return this.database.getCollection("mpermission_groups");
    }

    public MongoCollection<Document> getUserCollection() {
        return this.database.getCollection("mpermission_users");
    }

    public Bson createUserFilter(UUID uuid) {
        return Filters.eq("uuid", uuid.toString());
    }

    public Bson createGroupFilter(String name) {
        return Filters.eq("name", name);
    }
}
