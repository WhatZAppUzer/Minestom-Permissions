package dev.whatsappuser.minestom.permissions.storage;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.config.PermissionsConfig;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

/**
 * development by TimoH created on 18:30:24 | 01.01.2023
 */

@Getter
public class DatabaseConnection implements IDatabase {

    private MongoClient client;
    private MongoDatabase clientDatabase;
    private final String host, database, username, password, authenticationDatabase;
    private final int port;
    private final boolean useSSL, authentication;

    private final Set<PermissionUser> permissionUsers;
    private final Set<PermissionGroup> permissionGroups;

    public DatabaseConnection(PermissionsConfig config) {
        PermissionBootstrap.getBootstrap().getLogger().info("Setting up DatabaseConnection...");
        this.host = config.getMongoDB().getHost();
        this.database = config.getMongoDB().getDatabase();
        this.username = config.getMongoDB().getUsername();
        this.password = config.getMongoDB().getPassword();
        this.authenticationDatabase = config.getMongoDB().getAuthenticationDatabase();

        this.port = config.getMongoDB().getPort();

        this.useSSL = config.getMongoDB().isUseSSL();
        this.authentication = config.getMongoDB().isAuthentication();
        PermissionBootstrap.getBootstrap().getLogger().info("DatabaseConnection is now ready to connect.");
        this.permissionUsers = new HashSet<>();
        this.permissionGroups = new HashSet<>();
    }

    @Override
    public void loadDatabase() {
        MongoCredential credential = MongoCredential.createCredential(this.username, this.authenticationDatabase, this.password.toCharArray());
        ServerAddress address = new ServerAddress(this.host, this.port);
        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(this.useSSL).build();
        if (this.authentication) {
            this.client = new MongoClient(address, Collections.singletonList(credential), options);
        } else {
            this.client = new MongoClient(address, options);
        }
        this.clientDatabase = this.client.getDatabase(this.database);
        clientDatabase.createCollection("permissions");
        this.client.startSession();
        PermissionBootstrap.getBootstrap().getLogger().info("DatabaseConnection is successfully connected to Database-Server.");
    }

    @Override
    public void unloadDatabase() {
        this.client.close();
    }

    @Override
    public PermissionUser getPlayer(UUID uuid) {
        for (PermissionUser permissionUser : this.permissionUsers) {
            if (permissionUser.getUniqueId().equals(uuid)) {
                return permissionUser;
            }
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
    public PermissionGroup getGroup(int id) {
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            if (permissionGroup.getId() == id)
                return permissionGroup;
        }

        return PermissionPool.DEFAULT;
    }

    @Override
    public PermissionGroup getGroup(String name) {
        for (PermissionGroup permissionGroup : this.permissionGroups) {
            if (permissionGroup.getName().equalsIgnoreCase(name))
                return permissionGroup;
        }
        return PermissionPool.DEFAULT;
    }

    @Override
    public PermissionUser loadPlayer(UUID uuid) {
        Document document = getUsers().find(createUserFilter(uuid)).first();

        if (document == null) createPlayer(uuid);

        var name = document.getString("name");
        var groupName = document.getString("groupName");
        PermissionGroup group = getGroup(groupName);

        return new PermissionUser(uuid, name, group, new HashSet<>(document.getList("permissions", String.class)));
    }

    @Override
    public void loadPlayers() {
        //TODO: loadPlayers impl
    }

    @Override
    public PermissionUser loadPlayer(String name) {
        Document document = getUsers().find(createUserFilter(name)).first();

        if (document == null) createPlayer(name);

        UUID uniqueId = UUID.fromString(document.getString("uuid"));
        var groupName = document.getString("groupName");
        PermissionGroup group = getGroup(groupName);

        return new PermissionUser(uniqueId, name, group, new HashSet<>(document.getList("permissions", String.class)));
    }

    @Override
    public void createPlayer(String name) {
        Document document = new Document("name", name);
        document.append("uuid", "unknown");
        document.append("groupName", "default");
        Set<String> set = new HashSet<>();
        set.add("default");
        document.append("permissions", set);
        getUsers().insertOne(document);
    }

    @Override
    public void createGroup(PermissionGroup group) {
        if (getGroups().find(createGroupFilter(group.getName())).first() != null) {
            return;
        }
        Document document = new Document("name", group.getName())
                .append("prefix", group.getPrefix())
                .append("display", group.getDisplay())
                .append("suffix", group.getSuffix())
                .append("colorCode", group.getColorCode())
                .append("chatFormat", group.getChatFormat())
                .append("id", group.getId())
                .append("priority", group.getPriority())
                .append("default", group.isDefault())
                .append("permissions", group.getPermissions());
        getGroups().insertOne(document);
    }

    @Override
    public void deleteGroup(String group) {
        var document = getGroups().find(createGroupFilter(group)).first();

        if (document == null) return;
        getGroups().deleteOne(document);
    }

    @Override
    public void createPlayer(UUID uuid) {
        Document document = new Document("uuid", uuid.toString());
        document.append("name", "unknown");
        document.append("groupName", "default");
        Set<String> set = new HashSet<>();
        set.add("default");
        document.append("permissions", set);
        getUsers().insertOne(document);
    }

    @Override
    public Set<PermissionGroup> getAllGroups() {
        Set<PermissionGroup> groups = new HashSet<>();
        for (Document document : this.getGroups().listIndexes()) {
            PermissionGroup group = new PermissionGroup(document.getString("name"), document.getString("prefix"), document.getString("display"), document.getString("suffix"),
                    document.getString("colorCode"), document.getString("chatFormat")
                    , new HashSet<>(document.getList("permissions", String.class)), document.getInteger("id"), document.getInteger("priority"), document.getBoolean("default"));
            groups.add(group);
        }
        return groups;
    }

    @Override
    public Set<PermissionGroup> getAllLoadedGroups() {
        return this.permissionGroups;
    }

    @Override
    public void savePlayer(PermissionUser user) {
        Document document = getUsers().find(createUserFilter(user.getUniqueId())).first();


        document.append("uuid", user.getUniqueId().toString()).append("name", user.getName()).append("groupName", user.getGroup().getName()).append("permissions", user.getPermissions());
        if (document == null)
            getUsers().insertOne(document);
        else
            getUsers().updateOne(new BasicDBObject("$set", new BasicDBObject("uuid", user.getUniqueId().toString())), document);

    }

    @Override
    public void savePlayers() {
        //TODO: savePlayers impl
    }

    @Override
    public void saveGroup(PermissionGroup group) {
        Document document = getGroups().find(createGroupFilter(group.getName())).first();

        document.append("name", group.getName()).append("prefix", group.getPrefix()).append("display", group.getDisplay()).append("suffix", group.getSuffix())
                .append("permissions", group.getPermissions()).append("id", group.getId()).append("priority", group.getPriority()).append("default", group.isDefault());
        if (document == null)
            getGroups().insertOne(document);
        else
            getGroups().updateOne(new BasicDBObject("$set", new BasicDBObject("name", group.getName())), document);

    }

    @Override
    public void saveGroups() {
        //TODO: saveGroups impl
    }

    @Override
    public PermissionGroup loadGroup(String name) {
        Document document = getGroups().find(createGroupFilter(name)).first();

        if (document == null) {
            return null;
        }

        var prefix = document.getString("prefix");
        var suffix = document.getString("suffix");
        var display = document.getString("display");
        List<String> permissions = document.getList("permissions", String.class);
        var id = document.getInteger("id");
        var priority = document.getInteger("priority");
        var isDefault = document.getBoolean("default");
        var colorCode = document.getString("colorCode");
        var chatFormat = document.getString("chatFormat");

        return new PermissionGroup(name, prefix, display, suffix, colorCode, chatFormat, new HashSet<>(permissions), id, priority, isDefault);
    }

    @Override
    public PermissionGroup loadGroup(int id) {
        Document document = getGroups().find(createGroupFilter(id)).first();

        if (document == null) {
            return null;
        }

        var name = document.getString("name");
        var prefix = document.getString("prefix");
        var suffix = document.getString("suffix");
        var display = document.getString("display");
        List<String> permissions = document.getList("permissions", String.class);
        var priority = document.getInteger("priority");
        var isDefault = document.getBoolean("default");
        var colorCode = document.getString("colorCode");
        var chatFormat = document.getString("chatFormat");

        return new PermissionGroup(name, prefix, display, suffix, colorCode, chatFormat, new HashSet<>(permissions), id, priority, isDefault);
    }

    @Override
    public boolean isDefaultGroupExists() {
        for (Document document : getGroups().listIndexes())
            return document.getBoolean("default");
        return false;
    }

    @Override
    public void loadGroups() {
        for (Document document : getGroups().listIndexes()) {
            loadGroup(document.getString("name"));
        }
    }

    @Override
    public Set<PermissionUser> getCachedUsers() {
        return this.permissionUsers;
    }

    public MongoCollection<Document> getUsers() {
        return this.clientDatabase.getCollection("permissions_users");
    }

    public MongoCollection<Document> getGroups() {
        return this.clientDatabase.getCollection("permissions_groups");
    }

    public Bson createUserFilter(UUID uuid) {
        return getUsers().find(new Document("uuid", uuid.toString())).first();
    }

    public Bson createUserFilter(String name) {
        return getUsers().find(new Document("name", name)).first();
    }

    public Bson createGroupFilter(String name) {
        return getUsers().find(new Document("name", name)).first();
    }

    public Bson createGroupFilter(int id) {
        return getUsers().find(new Document("id", id)).first();
    }

}
