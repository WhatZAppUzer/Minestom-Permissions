package dev.whatsappuser.minestom.permissions.storage.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import dev.whatsappuser.minestom.permissions.storage.IDatabaseService;
import dev.whatsappuser.minestom.permissions.storage.mongodb.collection.PermissionCollection;
import org.bson.Document;

import java.util.Collections;

/**
 * development by TimoH created on 21:43:22 | 06.01.2023
 */

public class MongoDBService implements IDatabaseService {

    private MongoClient client;
    private MongoDatabase database;

    private final String host, data, username, password, authDB;
    private final int port;
    private final boolean useAuthentication, useSSL;
    private PermissionCollection collection;

    public MongoDBService(String host, String data, String username, String password, String authDB, int port, boolean useAuthentication, boolean useSSL) {
        this.host = host;
        this.data = data;
        this.username = username;
        this.password = password;
        this.authDB = authDB;
        this.port = port;
        this.useAuthentication = useAuthentication;
        this.useSSL = useSSL;
    }

    @Override
    public void loadDatabase() {
        MongoCredential credential = MongoCredential.createCredential(this.username, this.authDB, this.password.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder().sslEnabled(this.useSSL).build();
        ServerAddress address = new ServerAddress(this.host, this.port);
        if (this.useAuthentication) {
            this.client = new MongoClient(address, Collections.singletonList(credential), options);
        } else {
            this.client = new MongoClient(address, options);
        }
        this.database = this.client.getDatabase(this.data);
        this.client.startSession();
        if (getCollection("permission_groups") == null)
            this.database.createCollection("permission_groups");

        if (getCollection("permission_users") == null)
            this.database.createCollection("permission_users");

        this.collection = new PermissionCollection(this);
    }

    @Override
    public void unloadDatabase() {
        if (isDatabaseLoaded())
            this.client.close();
    }

    @Override
    public boolean isDatabaseLoaded() {
        return this.client != null;
    }

    @Override
    public IDatabase getDatabase() {
        return this.collection;
    }

    public MongoCollection<Document> getCollection(String name) {
        return this.database.getCollection(name, Document.class);
    }
}
