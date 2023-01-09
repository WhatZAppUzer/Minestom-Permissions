package dev.whatsappuser.minestom.permissions.config;

import dev.whatsappuser.minestom.lib.configuration.JsonConfiguration;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.utilities.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

/**
 * development by TimoH created on 15:49:31 | 01.01.2023
 */

@Getter
public class PermissionsConfig {

    private MongoDB mongoDB;
    private boolean useStorage;


    public void loadConfig() {
        try {
            if (! FileUtil.doesFileExist(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions"))
                FileUtil.createDirectory(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions");

            if (! FileUtil.doesFileExist(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/config.json")) {
                PermissionBootstrap.getBootstrap().getLogger().info("Config not found, creating one now..");
                FileUtil.createFile(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/config.json");
                PermissionBootstrap.getBootstrap().getLogger().info("Config is now created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonConfiguration document;
        File file = FileUtil.getFile(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/config.json");
        try {
            document = JsonConfiguration.loadDocument(file);
            PermissionBootstrap.getBootstrap().getLogger().info("Config has been loading.");
        } catch (Exception e) {
            PermissionBootstrap.getBootstrap().getLogger().error("An error occurred while loading this configuration: " + e.getMessage());
            if(e.getCause() != null)
                e.getCause().printStackTrace();

            System.exit(1);
            return;
        }

        if(!document.contains("mongodb")) {
            document.append("useStorage", false);
            document.append("mongodb", new MongoDB("localhost", "root", "rootPassword", "permissions", "admin", 27017, false, true));
            document.save(file);
        } else {
            this.useStorage = document.getBoolean("useStorage");
            this.mongoDB = document.getObject("mongodb", MongoDB.class);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class MongoDB {
        private String host, username, password, database, authenticationDatabase;
        private int port;
        private boolean useSSL, authentication;
    }
}
