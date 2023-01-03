package dev.whatsappuser.minestom.permissions;

import dev.whatsappuser.minestom.permissions.commands.PermissionCommand;
import dev.whatsappuser.minestom.permissions.config.PermissionsConfig;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.listener.ChatListener;
import dev.whatsappuser.minestom.permissions.listener.PlayerDisconnectListener;
import dev.whatsappuser.minestom.permissions.listener.PlayerSpawnListener;
import dev.whatsappuser.minestom.permissions.storage.DatabaseConnection;
import dev.whatsappuser.minestom.permissions.storage.IDatabase;
import dev.whatsappuser.minestom.permissions.storage.JsonDatabase;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;

import java.util.HashSet;

/**
 * development by TimoH created on 15:46:46 | 01.01.2023
 */

@Getter
public class PermissionBootstrap extends Extension {

    private static PermissionBootstrap BOOTSTRAP;
    private PermissionsConfig config;
    private IDatabase database;
    private PermissionPool permissionPool;

    @Override
    public void initialize() {
        BOOTSTRAP = this;

        getLogger().info("Enabling " + getOrigin().getName() + "...");

        this.config = new PermissionsConfig();
        this.config.loadConfig();

        loadDatabase();

        if(!this.database.isDefaultGroupExists()) {
            this.database.createGroup(new PermissionGroup("default", "§7Spieler §8| §7", "§7S §8| §7", "", "§7", "§7Spieler §8| §7", new HashSet<>(), 0, 10, true));
        }

        this.database.loadGroups();

        getLogger().info(this.database.getAllLoadedGroups().size() + " Groups loaded.");
        for (PermissionGroup allGroup : this.database.getAllLoadedGroups()) {
            if(allGroup.isDefault()) {
                this.permissionPool = new PermissionPool(allGroup);
                getLogger().info("The Default Group is '" + allGroup.getName() + "'");
            }
        }

        MinecraftServer.getCommandManager().register(new PermissionCommand());

        MinecraftServer.getGlobalEventHandler().addListener(new PlayerSpawnListener(this.database));
        MinecraftServer.getGlobalEventHandler().addListener(new PlayerDisconnectListener());
        MinecraftServer.getGlobalEventHandler().addListener(new ChatListener());

        getLogger().info("Extension: " + getOrigin().getName() + " Enabled.");
    }

    @Override
    public void terminate() {
        for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            this.database.savePlayer(this.permissionPool.getPlayer(onlinePlayer.getUuid()));
        }
        for (PermissionGroup allLoadedGroup : this.database.getAllLoadedGroups()) {
            this.database.saveGroup(allLoadedGroup);
        }
        this.database.unloadDatabase();
        getLogger().info("Extension: " + getOrigin().getName() + " Disabled.");
    }

    public void loadDatabase() {
        if(this.config.isUseStorage()) {
            getLogger().info("Database is set to external 'mongodb'");
            this.database = new DatabaseConnection(this.config);
            this.database.loadDatabase();
        } else {
            getLogger().info("Database is set to local");
            this.database = new JsonDatabase();
            this.database.loadDatabase();
        }
    }

    public static PermissionBootstrap getBootstrap() {
        return BOOTSTRAP;
    }
}
