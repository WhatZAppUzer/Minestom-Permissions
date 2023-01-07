package dev.whatsappuser.minestom.permissions.storage.json;

import dev.whatsappuser.minestom.lib.configuration.JsonConfiguration;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import dev.whatsappuser.minestom.permissions.storage.IDatabaseService;
import dev.whatsappuser.minestom.permissions.storage.json.collection.JsonPermissionCollection;
import lombok.Getter;
import net.minestom.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

/**
 * development by TimoH created on 00:27:24 | 07.01.2023
 */

@Getter
public class JsonService implements IDatabaseService {

    private File permissionsFile;
    private JsonConfiguration groupsConfig;
    private JsonPermissionCollection collection;

    @Override
    public void loadDatabase() {
        this.permissionsFile = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions", "permissions.json");
        if (! this.permissionsFile.exists()) {
            try {
                this.permissionsFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.groupsConfig = JsonConfiguration.loadDocument(this.permissionsFile);
        this.collection = new JsonPermissionCollection();
    }

    @Override
    public void unloadDatabase() {
        this.groupsConfig = null;
    }

    @Override
    public boolean isDatabaseLoaded() {
        return true;
    }

    @Override
    public IDatabase getDatabase() {
        return this.collection;
    }
}
