package dev.whatsappuser.minestom.permissions;

import dev.whatsappuser.minestom.lib.BootExtension;
import dev.whatsappuser.minestom.permissions.commands.PermissionCommand;
import dev.whatsappuser.minestom.permissions.config.MessageConfig;
import dev.whatsappuser.minestom.permissions.config.PermissionsConfig;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.listener.ChatListener;
import dev.whatsappuser.minestom.permissions.listener.PlayerDisconnectListener;
import dev.whatsappuser.minestom.permissions.listener.PlayerSpawnListener;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import dev.whatsappuser.minestom.permissions.storage.IDatabaseService;
import dev.whatsappuser.minestom.permissions.storage.json.JsonService;
import dev.whatsappuser.minestom.permissions.storage.mongodb.MongoDBService;
import lombok.Getter;
import net.minestom.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * development by TimoH created on 15:46:46 | 01.01.2023
 */

@Getter
public class PermissionBootstrap extends BootExtension {

    private static PermissionBootstrap BOOTSTRAP;
    private PermissionsConfig config;
    private MessageConfig messageConfig;
    private IDatabaseService service;
    private PermissionPool permissionPool;
    public final static Set<PermissionGroup> PERMISSION_GROUPS = new HashSet<>();
    public final static Set<PermissionUser> PERMISSION_USERS = new HashSet<>();

    @Override
    public void initialize() {
        BOOTSTRAP = this;

        getLogger().info("Enabling " + getOrigin().getName() + "...");

        this.config = new PermissionsConfig();
        this.config.loadConfig();

        this.messageConfig = new MessageConfig();
        this.messageConfig.loadConfig();

        loadDatabase();

        this.service.getDatabase().loadGroups();
        if (! this.service.getDatabase().isDefaultGroupExists()) {
            getLogger().warn("default group does not exists, creating a new default group.");
            if (this.service.getDatabase().getGroup("default") != null) {
                this.service.getDatabase().getGroup("default").setDefault(true);
                getLogger().info("the group 'default' was changed to default group");
            } else {
                PermissionGroup group = new PermissionGroup("default", "§7Spieler §8| §7", "§7S §8| §7", ""
                        , "§7", "§7Spieler §8| §7", new ArrayList<>(), this.service.getDatabase().getAllLoadedGroups().size() + 1, 10, true);
                this.service.getDatabase().createGroup(group);
                this.service.getDatabase().getAllLoadedGroups().add(group);
                getLogger().info("the default group was created.");
            }
        }


        getLogger().info("Loaded Groups » " + (this.service.getDatabase()).getAllLoadedGroups().size());

        MinecraftServer.getCommandManager().register(new PermissionCommand(provider()));

        MinecraftServer.getGlobalEventHandler().addListener(new PlayerSpawnListener(this.service.getDatabase()));
        MinecraftServer.getGlobalEventHandler().addListener(new PlayerDisconnectListener());
        MinecraftServer.getGlobalEventHandler().addListener(new ChatListener());

        getLogger().info("Extension: " + getOrigin().getName() + " Enabled.");
    }

    @Override
    public void terminate() {
        if (this.service.isDatabaseLoaded()) {
            this.service.unloadDatabase();
        }
        getLogger().info("Extension: " + getOrigin().getName() + " Disabled.");
    }

    public void loadDatabase() {
        if (this.config.isUseStorage()) {
            final PermissionsConfig.MongoDB mongodb = this.config.getMongoDB();
            this.service = new MongoDBService(mongodb.getHost(), mongodb.getDatabase(), mongodb.getUsername(), mongodb.getPassword(), mongodb.getAuthenticationDatabase(), mongodb.getPort(), mongodb.isAuthentication(), mongodb.isUseSSL());
            this.service.loadDatabase();
        } else {
            this.service = new JsonService();
            this.service.loadDatabase();
        }

        getLogger().info("Database SET to: " + (this.config.isUseStorage() ? "External" : "Local"));
    }

    public static PermissionBootstrap getBootstrap() {
        return BOOTSTRAP;
    }
}
