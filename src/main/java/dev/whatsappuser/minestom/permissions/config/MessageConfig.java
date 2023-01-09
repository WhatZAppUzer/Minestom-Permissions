package dev.whatsappuser.minestom.permissions.config;

import com.google.gson.reflect.TypeToken;
import dev.whatsappuser.minestom.lib.configuration.JsonConfiguration;
import net.minestom.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * development by TimoH created on 23:26:00 | 03.01.2023
 */

public class MessageConfig {

    private File file;
    private JsonConfiguration document;

    public static String RELOAD_SUCCESSFULLY;
    public static String HAS_NO_PERMISSION;
    public static String ONLY_NUMBER_ALLOWED;

    /**
     * group's messages
     */
    public static String GROUP_IS_NOT_EXISTS;
    public static String GROUP_SUCCESSFULLY_DELETED;
    public static String GROUP_SUCCESSFULLY_CREATED;
    public static String GROUP_SUCCESSFULLY_PERMISSION_ADD;
    public static String GROUP_SUCCESSFULLY_PERMISSION_REMOVE;
    public static String GROUP_IS_ALREADY_IN_USE;
    public static String GROUP_HAS_ALREADY_PERMISSION;
    public static String GROUP_HAS_NO_PERMISSION;
    public static String GROUP_PRIORITY_CHANGED;
    public static String GROUP_PREFIX_CHANGED;
    public static String GROUP_DISPLAY_CHANGED;
    public static String GROUP_SUFFIX_CHANGED;
    public static String GROUP_INFORMATION;
    public static List<String> GROUP_OPTION_INFORMATION;
    public static String GROUP_LIST_GROUPS;

    /**
     * user's messages
     */

    public static String USER_NOT_FOUND;
    public static String USER_ALREADY_IN_GROUP;
    public static String USER_CHANGED_GROUP;
    public static String USER_NOT_IN_GROUP;
    public static String USER_NO_LONGER_IN_GROUP;
    public static String USER_HAS_ALREADY_PERMISSION;
    public static String USER_SUCCESSFULLY_PERMISSION_ADD;
    public static String USER_SUCCESSFULLY_PERMISSION_REMOVED;
    public static String USER_HAS_NO_PERMISSION;
    public static String USER_INFORMATION_HEADER;
    public static List<String> USER_INFORMATION_LIST;

    public MessageConfig() {
        load();

        registerDefaults();
    }

    //<editor-fold desc="load">
    private void load() {
        this.file = new File(MinecraftServer.getExtensionManager().getExtensionFolder() + "/MinePermissions/messages.json");
        if (! this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.document = JsonConfiguration.loadDocument(this.file);
    }
    //</editor-fold>

    //<editor-fold desc="save">
    private void save() {
        this.document.save(this.file);
    }
    //</editor-fold>

    //<editor-fold desc="loadConfig">
    public void loadConfig() {
        var messages = getDocument("messages");
        HAS_NO_PERMISSION = messages.getString("missing-permissions");
        RELOAD_SUCCESSFULLY = messages.getString("reload-successfully");
        ONLY_NUMBER_ALLOWED = messages.getString("only-number");

        var group = messages.getDocument("group");

        GROUP_IS_NOT_EXISTS = group.getString("not-exists");
        GROUP_SUCCESSFULLY_DELETED = group.getString("successfully-deleted");
        GROUP_SUCCESSFULLY_CREATED = group.getString("successfully-created");
        GROUP_SUCCESSFULLY_PERMISSION_ADD = group.getString("successfully-permission-add");
        GROUP_SUCCESSFULLY_PERMISSION_REMOVE = group.getString("successfully-permission-remove");
        GROUP_IS_ALREADY_IN_USE = group.getString("already-in-use");
        GROUP_HAS_ALREADY_PERMISSION = group.getString("has-already-permission");
        GROUP_HAS_NO_PERMISSION = group.getString("has-no-permission");
        GROUP_PRIORITY_CHANGED = group.getString("priority-changed");
        GROUP_PREFIX_CHANGED = group.getString("prefix-changed");
        GROUP_DISPLAY_CHANGED = group.getString("display-changed");
        GROUP_SUFFIX_CHANGED = group.getString("suffix-changed");
        GROUP_INFORMATION = group.getString("information");
        GROUP_OPTION_INFORMATION = group.getObject("option-information", new TypeToken<List<String>>() {
        }.getType());
        GROUP_LIST_GROUPS = group.getString("list_groups");

        var user = messages.getDocument("user");
        USER_NOT_FOUND = user.getString("not-found");
        USER_ALREADY_IN_GROUP = user.getString("already-in-group");
        USER_CHANGED_GROUP = user.getString("changed-group");
        USER_NOT_IN_GROUP = user.getString("is-not-in-group");
        USER_NO_LONGER_IN_GROUP = user.getString("no-longer-in-group");
        USER_HAS_ALREADY_PERMISSION = user.getString("has-already-permission");
        USER_HAS_NO_PERMISSION = user.getString("have-not-permission");
        USER_SUCCESSFULLY_PERMISSION_ADD = user.getString("successfully-permission-add");
        USER_SUCCESSFULLY_PERMISSION_REMOVED = user.getString("successfully-permission-remove");
        USER_INFORMATION_HEADER = user.getString("information-header");
        USER_INFORMATION_LIST = user.getObject("information-list", new TypeToken<List<String>>() {
        }.getType());
    }
    //</editor-fold>

    //<editor-fold desc="registerDefaults">
    private void registerDefaults() {
        if (! isFirstStart()) {
            JsonConfiguration messages = new JsonConfiguration("messages");
            messages.append("missing-permissions", "§cYou are not authorized to use this command.");
            messages.append("reload-successfully", "§aSuccessfully reloaded all permissions.");
            messages.append("only-number", "§cYou must enter a number");

            JsonConfiguration group = new JsonConfiguration("group");
            group.append("not-exists", "§cthe group %group% doesn't exist.");
            group.append("successfully-deleted", "§aSuccessfully %group% deleted.");
            group.append("successfully-created", "§aYou have created %group%.");
            group.append("successfully-permission-add", "§aAdded the permission %permission% to %group%");
            group.append("successfully-permission-remove", "§aRemoved the permission %permission% from %group%");
            group.append("already-in-use", "§cThis group %group% is already in use");
            group.append("has-already-permission", "§cThe group %group% §chas already the permission: %permission%");
            group.append("has-no-permission", "The group %group% §cdoesn't have the permission: %permission%");
            group.append("priority-changed", "§aPriority from group %group% §awas changed to §e%priority%");
            group.append("prefix-changed", "§aPrefix from group %group% §awas changed to %prefix%");
            group.append("display-changed", "§aDisplay from group %group% §awas changed to %display%");
            group.append("suffix-changed", "§aSuffix from group %group% §awas changed to %suffix%");
            group.append("information", "§7PermissionGroup@%group%");

            List<String> options = new ArrayList<>();
            options.add("§7Prefix §8» %group_prefix%");
            options.add("§7Display §8» %group_display%");
            options.add("§7Suffix §8» %group_suffix%");
            options.add("§7ChatFormat §8» %group_chatFormat%");
            options.add("§7Id §8» %group_id%");
            options.add("§7Priority §8» %group_priority%");
            options.add("§7Default §8» %group_isdefault%");
            options.add("§7Permissions §8» §c%permissions%");
            group.append("option-information", options);
            group.append("list_groups", "List of all PermissionGroups » ");


            var user = new JsonConfiguration("user");
            user.append("not-found", "§cThe user was unable to be found");
            user.append("already-in-group", "§c%player% is already in group %group%");
            user.append("changed-group", "%player% is now in group %group%");
            user.append("is-not-in-group", "§c%player% isn't in the group %group%");
            user.append("no-longer-in-group", "%player% is no longer in group %group%");
            user.append("has-already-permission", "§c%player% has already the permission %permission%");
            user.append("successfully-permission-add", "§a%player% has now the permission %permission%");
            user.append("successfully-permission-remove", "§aremoved the permission %permission% from %player%");
            user.append("have-not-permission", "§c%player% doesn't have the permission %permission%");
            user.append("information-header", "§ePermissionUser@%player% §8»");

            List<String> userOptions = new ArrayList<>();
            userOptions.add("§eGroup §8» %group%");
            userOptions.add("§ePermissions §8» §c%permissions%");

            user.append("information-list", userOptions);

            messages.append("group", group);
            messages.append("user", user);
            this.document.append("messages", messages);
            save();
        }
    }
    //</editor-fold>

    //<editor-fold desc="isFirstStart">
    private boolean isFirstStart() {
        return this.document.contains("messages");
    }
    //</editor-fold>

    //<editor-fold desc="getDocument">
    private JsonConfiguration getDocument(String key) {
        return this.document.getDocument(key);
    }
    //</editor-fold>
}
