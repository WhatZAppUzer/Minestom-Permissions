package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.config.MessageConfig;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;
import static net.minestom.server.command.builder.arguments.ArgumentType.Word;

/**
 * development by TimoH created on 14:13:48 | 03.01.2023
 */

public class GroupSubCommand extends Command {

    public GroupSubCommand() {
        super("group");

        ArgumentWord option = Word("option").from("perm");
        ArgumentWord action = Word("action").from("add", "remove");
        ArgumentWord group = Word("groupName");
        ArgumentWord defaults = Word("default");

        ArgumentWord permission = Word("permission");
        ArgumentWord priority = Word("priority");
        ArgumentWord prefix = Word("prefix");
        ArgumentWord display = Word("display");
        ArgumentWord suffix = Word("suffix");

        addSyntax(this::executeInfo, group, Literal("info"));
        addSyntax(this::executePerm, group, option, action, permission);
        addSyntax(this::executeCreate, group, Literal("create"), defaults);
        addSyntax(this::executeDelete, group, Literal("delete"));
        addSyntax(this::executeSetPriority, group, Literal("setPriority"), priority);
    }

    private void executeSetPriority(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final int priority = Integer.parseInt(context.get("priority"));
        final String groupName = context.get("groupName");

        if (! checkPermissions(sender, "mperms.command")) {
            sender.sendMessage(Component.text("You are not authorized to use this command.", NamedTextColor.RED));
            return;
        }

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isGroupRegistered(groupName)) {
            sender.sendMessage(Component.text("the group " + groupName + " doesn't exist.", NamedTextColor.RED));
            return;
        }
        PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getPermissionPool().getGroup(groupName);
        try {
            permissionGroup.setPriority(priority);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("the priority allowed only numbers", NamedTextColor.RED));
            return;
        }
        PermissionBootstrap.getBootstrap().getPermissionPool().updateGroup(permissionGroup);
        sender.sendMessage(Component.text("Priority from group " + permissionGroup.getColorCode() + permissionGroup.getName() + " §awas changed to §e" +priority, NamedTextColor.GREEN));
    }

    private void executeDelete(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String groupName = context.get("groupName");

        if (! checkPermissions(sender, "mperms.command")) {
            sender.sendMessage(Component.text("You are not authorized to use this command.", NamedTextColor.RED));
            return;
        }

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isGroupRegistered(groupName)) {
            sender.sendMessage(Component.text("the group " + groupName + " doesn't exist.", NamedTextColor.RED));
            return;
        }

        PermissionBootstrap.getBootstrap().getDatabase().deleteGroup(groupName);
        sender.sendMessage(Component.text("Successfully " + groupName + " deleted.", NamedTextColor.GREEN));
    }

    private void executeCreate(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final boolean defaults = Boolean.parseBoolean(context.get("default"));
        final String groupName = context.get("groupName");

        if (! checkPermissions(sender, "mperms.command")) {
            sender.sendMessage(Component.text("You are not authorized to use this command.", NamedTextColor.RED));
            return;
        }

        if (PermissionBootstrap.getBootstrap().getPermissionPool().isGroupRegistered(groupName)) {
            sender.sendMessage(Component.text("This group " + groupName + " is already in use", NamedTextColor.RED));
            return;
        }
        PermissionGroup group = new PermissionGroup(groupName, "", "", "", "", "", new HashSet<>()
                , PermissionBootstrap.getBootstrap().getDatabase().getAllLoadedGroups().size() + 1, 1, defaults);
        PermissionBootstrap.getBootstrap().getPermissionPool().createGroup(group);
        sender.sendMessage(Component.text("You have created " + groupName + ".", NamedTextColor.GREEN));

        if (defaults) {
            var aDefault = PermissionPool.DEFAULT;
            aDefault.setDefault(false);
            PermissionBootstrap.getBootstrap().getDatabase().saveGroup(aDefault);
            PermissionPool.DEFAULT = group;
        }

    }

    private void executePerm(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String action = context.get("action");
        final String permission = context.get("permission");
        final String groupName = context.get("groupName");

        if (! checkPermissions(sender, "mperms.command")) {
            sender.sendMessage(Component.text("You are not authorized to use this command.", NamedTextColor.RED));
            return;
        }

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isGroupRegistered(groupName)) {
            sender.sendMessage(Component.text("the group " + groupName + " doesn't exist.", NamedTextColor.RED));
            return;
        }

        PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getPermissionPool().getGroup(groupName);
        if (action.equalsIgnoreCase("add")) {
            if (permissionGroup.hasPermission(permission)) {
                sender.sendMessage(Component.text("The group " + permissionGroup.getColorCode() + permissionGroup.getName() + " §chas already the permission: " + permission, NamedTextColor.RED));
                return;
            }
            permissionGroup.addPermission(permission);
            PermissionBootstrap.getBootstrap().getPermissionPool().updateGroup(permissionGroup);
            sender.sendMessage(Component.text("Added the permission " + permission + " to " + permissionGroup.getColorCode() + permissionGroup.getName()));
        } else if (action.equalsIgnoreCase("remove")) {
            if (! permissionGroup.hasPermission(permission)) {
                sender.sendMessage(Component.text("The group " + permissionGroup.getColorCode() + permissionGroup.getName() + " §cdoesn't have the permission: " + permission, NamedTextColor.RED));
                return;
            }
            permissionGroup.removePermission(permission);
            PermissionBootstrap.getBootstrap().getPermissionPool().updateGroup(permissionGroup);
            sender.sendMessage(Component.text("Removed the permission " + permission + " from " + permissionGroup.getColorCode() + permissionGroup.getName()));
        }

    }

    private void executeInfo(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String groupName = context.get("groupName");

        if (! checkPermissions(sender, "mperms.command")) {
            sender.sendMessage(MessageConfig.HAS_NO_PERMISSION);
            return;
        }

        if (! PermissionBootstrap.getBootstrap().getPermissionPool().isGroupRegistered(groupName)) {
            sender.sendMessage(MessageConfig.GROUP_IS_NOT_EXISTS.replace("%group%", groupName));
            return;
        }

        sender.sendMessage("");
        PermissionGroup group = PermissionBootstrap.getBootstrap().getPermissionPool().getGroup(groupName);
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
        /*sender.sendMessage(Component.text("§7Prefix §8» §c" + group.getPrefix()));
        sender.sendMessage(Component.text("§7Display §8» §c" + group.getDisplay()));
        sender.sendMessage(Component.text("§7Suffix §8» §c" + (group.getSuffix().isEmpty() ? group.getSuffix() : "none")));
        sender.sendMessage(Component.text("§7ChatFormat §8» " + group.getChatFormat()));
        sender.sendMessage(Component.text("§7Id §8» §c" + group.getId()));
        sender.sendMessage(Component.text("§7Priority §8» §c" + group.getPriority()));
        sender.sendMessage(Component.text("§7Default §8» " + (group.isDefault() ? "§atrue" : "§cfalse")));
        if (group.getPermissions().isEmpty()) {
            sender.sendMessage(Component.text("§7Permissions §8» §cnone"));
        } else {
            sender.sendMessage(Component.text("§7Permissions §8» §c" + String.join("§7, §c", group.getPermissions())));
        }*/
    }

    public final boolean checkPermissions(CommandSender sender, String permission) {
        if (sender instanceof Player player)
            return player.hasPermission(permission);
        return true;
    }
}
