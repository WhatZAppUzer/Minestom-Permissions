package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.lib.command.Command;
import dev.whatsappuser.minestom.lib.permissions.PermissionProvider;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import org.jetbrains.annotations.NotNull;

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

        /*addSyntax(this::executeInfo, players, Literal("info"));

        addSyntax(this::executePermAdd, players, Literal("perm"), action, permission);

        addSyntax(this::executeGroup, players, Literal("group"), groupAction, groupName);*/
    }

    /*private void executeGroup(@NotNull CommandSender sender, @NotNull CommandContext context) {
        final String action = context.get("action");
        final EntityFinder target = context.get("player");
        final String group = context.get("groupName");
        final Player player = target.findFirstPlayer(sender);

        if(player == null) {
            sender.sendMessage(Component.text("The user was unable to be found", NamedTextColor.RED));
            return;
        }

        switch (action) {
            case "set" -> {
                if(!PermissionBootstrap.getBootstrap().getPermissionPool().isGroupRegistered(group)) {
                    sender.sendMessage(Component.text("the group " + group + " doesn't exist.", NamedTextColor.RED));
                    return;
                }
                final PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getPermissionPool().getGroup(group);
                if (PermissionBootstrap.getBootstrap().getPermissionPool().isPlayerInGroup(player, permissionGroup.getName())) {
                    sender.sendMessage(Component.text(player.getUsername() + " is already in the group " + permissionGroup.getName(), NamedTextColor.RED));
                    return;
                }
                PermissionBootstrap.getBootstrap().getPermissionPool().setPlayerInGroup(player, permissionGroup.getName());
                PermissionBootstrap.getBootstrap().getPermissionPool().updatePlayer(player);
                sender.sendMessage(Component.text(player.getUsername() + " now is in the group " + permissionGroup.getName()));
                player.sendMessage(Component.text("Your group was changed.", NamedTextColor.YELLOW));
            }
            case "remove" -> {
                if(!PermissionBootstrap.getBootstrap().getPermissionPool().isGroupRegistered(group)) {
                    sender.sendMessage(Component.text("the group " + group + " doesn't exist.", NamedTextColor.RED));
                    return;
                }
                final PermissionGroup permissionGroup = PermissionBootstrap.getBootstrap().getPermissionPool().getGroup(group);
                if (! PermissionBootstrap.getBootstrap().getPermissionPool().isPlayerInGroup(player, permissionGroup.getName())) {
                    sender.sendMessage(Component.text(player.getUsername() + " isn't in the group " + permissionGroup.getName(), NamedTextColor.RED));
                    return;
                }
                PermissionBootstrap.getBootstrap().getPermissionPool().setPlayerInGroup(player, PermissionPool.DEFAULT.getName());
                PermissionBootstrap.getBootstrap().getPermissionPool().updatePlayer(player);
                sender.sendMessage(Component.text(player.getUsername() + " is no longer in group " + permissionGroup.getName()));
                player.sendMessage(Component.text("Your group was changed.", NamedTextColor.YELLOW));
            }
        }
    }

    private void executePermAdd(CommandSender sender, CommandContext context) {
        final String action = context.get("action");
        final EntityFinder target = context.get("player");
        final String permission = context.get("permission");

        final Player player = target.findFirstPlayer(sender);

        if(player == null) {
            sender.sendMessage(Component.text("The user was unable to be found", NamedTextColor.RED));
            return;
        }

        if(action.equalsIgnoreCase("add")) {
            if (player.hasPermission(permission)) {
                sender.sendMessage(Component.text(player.getUsername() + " already has the permission " + permission, NamedTextColor.RED));
                return;
            }
            PermissionBootstrap.getBootstrap().getPermissionPool().addPermission(player, permission);
            sender.sendMessage(Component.text(player.getUsername() + " has now the permission " + permission, NamedTextColor.GREEN));
        } else if(action.equalsIgnoreCase("remove")) {
            if(!player.hasPermission(permission)) {
                sender.sendMessage(Component.text(player.getUsername() + " doesn't have the permission " + permission, NamedTextColor.RED));
                return;
            }
            PermissionBootstrap.getBootstrap().getPermissionPool().removePermission(player, permission);
            sender.sendMessage(Component.text("Removed the permission " + permission + " from " + player.getUsername(), NamedTextColor.GREEN));
        }
    }

    private void executeInfo(CommandSender sender, CommandContext context) {
        final EntityFinder target = context.get("player");
        Player targetPlayer = target.findFirstPlayer(sender);

        if(targetPlayer == null) {
            sender.sendMessage(Component.text("The user was unable to be found", NamedTextColor.RED));
            return;
        }

        PermissionUser user = PermissionBootstrap.getBootstrap().getPermissionPool().getPlayer(targetPlayer.getUuid());
        sender.sendMessage(Component.text("§eUserInformation's from " + user.getGroup().getPrefix() + targetPlayer.getUsername() + " §8» "));
        sender.sendMessage(Component.text("§ePermissionGroup §8» " + user.getGroup().getPrefix()));
        if(user.getPermissions() != null) {
            sender.sendMessage(Component.text("§ePermissions §8» §c" + String.join("§7, §c", user.getPermissions())));
        } else {
            sender.sendMessage(Component.text("§ePermissions §8» §c'-'"));
        }
    }*/
}
