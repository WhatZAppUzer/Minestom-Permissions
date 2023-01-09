package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.lib.command.Command;
import dev.whatsappuser.minestom.lib.permissions.PermissionProvider;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.PermissionPool;
import dev.whatsappuser.minestom.permissions.config.MessageConfig;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.player.PermissionUser;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;

import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;
import static net.minestom.server.command.builder.arguments.ArgumentType.Word;

/**
 * development by TimoH created on 20:22:29 | 02.01.2023
 */

public class PlayerSubCommand extends Command {
    public PlayerSubCommand(@NotNull PermissionProvider provider) {
        super(provider, "user");

        setPermission("mpermission.command.user");

        ArgumentEntity players = ArgumentType.Entity("player").onlyPlayers(true).singleEntity(true);
        ArgumentWord action = Word("action").from("add", "remove");
        ArgumentWord groupAction = Word("action").from("remove", "set");

        ArgumentWord groupName = Word("groupName");

        ArgumentWord permission = Word("permission");

        addSyntax(this::executeInfo, players, Literal("info"));

        addSyntax(this::executePermAdd, players, Literal("perm"), action, permission);

        addSyntax(this::executeGroup, players, Literal("group"), groupAction, groupName);
    }

    private void executeGroup(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String action = context.get("action");
        final EntityFinder target = context.get("player");
        final String group = context.get("groupName");
        final Player player = target.findFirstPlayer(sender);

        if (player == null) {
            sender.sendMessage(MessageConfig.USER_NOT_FOUND);
            return;
        }
        PermissionUser user = PermissionBootstrap.getBootstrap().getPermissionPool().getUser(player.getUuid());
        switch (action) {
            case "set" -> {
                if (! PermissionBootstrap.getBootstrap().getPermissionPool().isRegistered(group)) {
                    sender.sendMessage(MessageConfig.GROUP_IS_NOT_EXISTS.replace("%group%", group));
                    return;
                }
                final PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getPermissionPool().getGroup(group);
                if (PermissionBootstrap.getBootstrap().getPermissionPool().isPlayerInGroup(user, permissionGroup.getName())) {
                    sender.sendMessage(MessageConfig.USER_ALREADY_IN_GROUP.replace("%player%", player.getUsername()).replace("%group%", permissionGroup.getName()));
                    return;
                }
                PermissionBootstrap.getBootstrap().getPermissionPool().setPlayerInGroup(user, permissionGroup.getName());
                sender.sendMessage(MessageConfig.USER_CHANGED_GROUP.replace("%player%", player.getUsername()).replace("%group%", permissionGroup.getName()));
            }
            case "remove" -> {
                if (! PermissionBootstrap.getBootstrap().getPermissionPool().isRegistered(group)) {
                    sender.sendMessage(MessageConfig.GROUP_IS_NOT_EXISTS.replace("%group%", group));
                    return;
                }
                final PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getPermissionPool().getGroup(group);
                if (! PermissionBootstrap.getBootstrap().getPermissionPool().isPlayerInGroup(user, permissionGroup.getName())) {
                    sender.sendMessage(MessageConfig.USER_NOT_IN_GROUP.replace("%player%", player.getUsername()).replace("%group%", permissionGroup.getName()));
                    return;
                }
                PermissionBootstrap.getBootstrap().getPermissionPool().setPlayerInGroup(user, PermissionPool.DEFAULT.getName());
                sender.sendMessage(MessageConfig.USER_NO_LONGER_IN_GROUP.replace("%player%", player.getUsername()).replace("%group%", permissionGroup.getName()));
            }
        }
    }

    private void executePermAdd(CommandSender sender, CommandContext context) {
        final String action = context.get("action");
        final EntityFinder target = context.get("player");
        final String permission = context.get("permission");

        final Player player = target.findFirstPlayer(sender);

        if (player == null) {
            sender.sendMessage(MessageConfig.USER_NOT_FOUND);
            return;
        }

        PermissionUser user = PermissionBootstrap.getBootstrap().getPermissionPool().getUser(player.getUuid());

        if (action.equalsIgnoreCase("add")) {
            if (user.hasPermission(permission)) {
                sender.sendMessage(MessageConfig.USER_HAS_ALREADY_PERMISSION.replace("%player%", player.getUsername()).replace("%permission%", permission));
                return;
            }
            PermissionBootstrap.getBootstrap().getPermissionPool().addPermission(permission, user);
            sender.sendMessage(MessageConfig.USER_SUCCESSFULLY_PERMISSION_ADD.replace("%permission%", permission).replace("%player%", player.getUsername()));
        } else if (action.equalsIgnoreCase("remove")) {
            if (! user.hasPermission(permission)) {
                sender.sendMessage(MessageConfig.USER_HAS_NO_PERMISSION.replace("%player%", player.getUsername()).replace("%permission%", permission));
                return;
            }
            PermissionBootstrap.getBootstrap().getPermissionPool().removePermission(permission, user);
            sender.sendMessage(MessageConfig.USER_SUCCESSFULLY_PERMISSION_REMOVED.replace("%permission%", permission).replace("%player%", player.getUsername()));
        }
    }

    private void executeInfo(CommandSender sender, CommandContext context) {
        final EntityFinder target = context.get("player");
        Player targetPlayer = target.findFirstPlayer(sender);

        if (targetPlayer == null) {
            sender.sendMessage(MessageConfig.USER_NOT_FOUND);
            return;
        }

        PermissionUser user = PermissionBootstrap.getBootstrap().getPermissionPool().getUser(targetPlayer.getUuid());
        sender.sendMessage(MessageConfig.USER_INFORMATION_HEADER.replace("%player%", user.getGroup().getColorCode() + targetPlayer.getUsername()));
        for (String line : MessageConfig.USER_INFORMATION_LIST) {
            line = line.replace("%group%", user.getGroup().getColorCode() + user.getGroup().getName())
                    .replace("%permissions%", (user.getPermissions().isEmpty() ? "§c'-'" : String.join("§8, §c", user.getPermissions())));
            sender.sendMessage(line);
        }
    }
}
