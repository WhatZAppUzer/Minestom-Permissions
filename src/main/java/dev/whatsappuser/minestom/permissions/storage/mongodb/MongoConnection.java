package dev.whatsappuser.minestom.permissions.storage.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    public PermissionUser loadPlayer(String name) {
        return null;
    }

    @Override
    public void createPlayer(UUID uuid) {

    }

    @Override
    public void createPlayer(String name) {

    }

    @Override
    public void createGroup(PermissionGroup group) {

    }

    @Override
    public void deleteGroup(String group) {

    }

    @Override
    public Set<PermissionGroup> getAllGroups() {
        return null;
    }

    @Override
    public Set<PermissionGroup> getAllLoadedGroups() {
        return null;
    }

    @Override
    public void reloadGroup(PermissionGroup group) {

    }

    @Override
    public void savePlayer(PermissionUser user) {

    }

    @Override
    public void savePlayers() {

    }

    @Override
    public void saveGroup(PermissionGroup group) {

    }

    @Override
    public void saveGroups() {

    }

    @Override
    public PermissionGroup loadGroup(String name) {
        return null;
    }

    @Override
    public PermissionGroup loadGroup(int id) {
        return null;
    }

    @Override
    public boolean isDefaultGroupExists() {
        return false;
    }

    @Override
    public void loadGroups() {

    }

    @Override
    public Set<PermissionUser> getCachedUsers() {
        return null;
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
