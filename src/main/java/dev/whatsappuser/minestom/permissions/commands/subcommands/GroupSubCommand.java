package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.lib.command.Command;
import dev.whatsappuser.minestom.lib.permissions.PermissionProvider;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.config.MessageConfig;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;
import static net.minestom.server.command.builder.arguments.ArgumentType.Word;

/**
 * development by TimoH created on 14:13:48 | 03.01.2023
 */

public class GroupSubCommand extends Command {

    public GroupSubCommand(@NotNull PermissionProvider provider) {
        super(provider, "group");


        setPermission("mpermission.command.group");
        ArgumentWord option = Word("option").from("perm");
        ArgumentWord action = Word("action").from("add", "remove");
        ArgumentWord group = Word("groupName");
        ArgumentWord defaults = Word("default");

        ArgumentWord permission = Word("permission");
        ArgumentWord priority = Word("priority");

        addSyntax(this::executeInfo, group, Literal("info"));
        addSyntax(this::executePerm, group, option, action, permission);
        addSyntax(this::executeCreate, group, Literal("create"), defaults);
        addSyntax(this::executeDelete, group, Literal("delete"));
        addSyntax(this::executeSetPriority, group, Literal("setpriority"), priority);
    }

    //<editor-fold desc="executeSetPriority">
    private void executeSetPriority(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final int priority = Integer.parseInt(context.get("priority"));
        final String groupName = context.get("groupName");

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isRegistered(groupName)) {
            sender.sendMessage(MessageConfig.GROUP_IS_NOT_EXISTS.replace("%group%", groupName));
            return;
        }

        PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getService().getDatabase().getGroup(groupName);
        try {
            permissionGroup.setPriority(priority);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageConfig.ONLY_NUMBER_ALLOWED);
            return;
        }
        PermissionBootstrap.getBootstrap().getPermissionPool().update(permissionGroup);
        sender.sendMessage(MessageConfig.GROUP_PRIORITY_CHANGED.replace("%group%", permissionGroup.getColorCode() + permissionGroup.getName()).replace("%priority%", String.valueOf(priority)));
    }
    //</editor-fold>

    //<editor-fold desc="executeDelete">
    private void executeDelete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String groupName = context.get("groupName");

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isRegistered(groupName)) {
            sender.sendMessage(MessageConfig.GROUP_IS_NOT_EXISTS.replace("%group%", groupName));
            return;
        }

        PermissionBootstrap.getBootstrap().getService().getDatabase().deleteGroup(groupName);
        sender.sendMessage(MessageConfig.GROUP_SUCCESSFULLY_DELETED.replace("%group%", groupName));
    }
    //</editor-fold>

    //<editor-fold desc="executeCreate">
    private void executeCreate(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final boolean defaults = Boolean.parseBoolean(context.get("default"));
        final String groupName = context.get("groupName");

        if (PermissionBootstrap.getBootstrap().getPermissionPool().isRegistered(groupName)) {
            sender.sendMessage(MessageConfig.GROUP_IS_ALREADY_IN_USE.replace("%group%", groupName));
            return;
        }

        PermissionGroup group = new PermissionGroup(groupName, "", "", "", "", "", new ArrayList<>()
                , PermissionBootstrap.getBootstrap().getService().getDatabase().getAllLoadedGroups().size() + 1, 1, defaults);
        PermissionBootstrap.getBootstrap().getService().getDatabase().createGroup(group);
        sender.sendMessage(MessageConfig.GROUP_SUCCESSFULLY_CREATED.replace("%group%", groupName));

        if (defaults) {
            var aDefault = PermissionPool.DEFAULT;
            aDefault.setDefault(false);
            PermissionBootstrap.getBootstrap().getService().getDatabase().saveGroup(aDefault);
            PermissionPool.DEFAULT = group;
        }
        PermissionBootstrap.getBootstrap().getPermissionPool().update(group);
    }
    //</editor-fold>

    //<editor-fold desc="executePerm">
    private void executePerm(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String action = context.get("action");
        final String permission = context.get("permission");
        final String groupName = context.get("groupName");

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isRegistered(groupName)) {
            sender.sendMessage(MessageConfig.GROUP_IS_NOT_EXISTS.replace("%group%", groupName));
            return;
        }

        PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getService().getDatabase().getGroup(groupName);
        if (action.equalsIgnoreCase("add")) {
            if (permissionGroup.hasPermission(permission)) {
                sender.sendMessage(MessageConfig.GROUP_HAS_ALREADY_PERMISSION.replace("%group%", permissionGroup.getColorCode() + permissionGroup.getName()).replace("%permission%", permission));
                return;
            }
            permissionGroup.addPermission(permission);
            PermissionBootstrap.getBootstrap().getPermissionPool().update(permissionGroup);
            sender.sendMessage(MessageConfig.GROUP_SUCCESSFULLY_PERMISSION_ADD.replace("%permission%", permission).replace("%group%", permissionGroup.getColorCode() + permissionGroup.getName()));
        } else if (action.equalsIgnoreCase("remove")) {
            if (! permissionGroup.hasPermission(permission)) {
                sender.sendMessage(MessageConfig.GROUP_HAS_NO_PERMISSION.replace("%group%", permissionGroup.getColorCode() + permissionGroup.getName()).replace("%permission%", permission));
                return;
            }
            permissionGroup.removePermission(permission);
            PermissionBootstrap.getBootstrap().getPermissionPool().update(permissionGroup);
            sender.sendMessage(MessageConfig.GROUP_SUCCESSFULLY_PERMISSION_REMOVE.replace("%permission%", permission).replace("%group%", permissionGroup.getColorCode() + permissionGroup.getName()));
        }

    }
    //</editor-fold>

    //<editor-fold desc="executeInfo">
    private void executeInfo(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String groupName = context.get("groupName");

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isRegistered(groupName)) {
            sender.sendMessage(MessageConfig.GROUP_IS_NOT_EXISTS.replace("%group%", groupName));
            return;
        }

        sender.sendMessage("");
        PermissionGroup group = PermissionBootstrap.getBootstrap().getService().getDatabase().getGroup(groupName);
        sender.sendMessage(MessageConfig.GROUP_INFORMATION.replace("%group%", group.getColorCode() + group.getName()));
        for (String line : MessageConfig.GROUP_OPTION_INFORMATION) {
            line = line.replace("%group_prefix%", group.getPrefix())
                    .replace("%group_display%", group.getDisplay())
                    .replace("%group_suffix%", group.getSuffix())
                    .replace("%group_chatFormat%", group.getChatFormat())
                    .replace("%group_id%", String.valueOf(group.getId()))
                    .replace("%group_priority%", String.valueOf(group.getPriority()))
                    .replace("%group_isdefault%", String.valueOf(group.isDefault()))
                    .replace("%permissions%", String.join("§7, §c", group.getPermissions()));
            sender.sendMessage(line);
        }
    }
    //</editor-fold>
}
