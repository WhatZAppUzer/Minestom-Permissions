package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.lib.command.Command;
import dev.whatsappuser.minestom.lib.permissions.PermissionProvider;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.config.MessageConfig;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * development by TimoH created on 11:46:22 | 09.01.2023
 */

public class GroupsSubCommand extends Command {

    public GroupsSubCommand(@NotNull PermissionProvider provider) {
        super(provider, "groups");

        setPermission("mpermissions.command.groups");

        addSyntax((sender, context) -> {
            sender.sendMessage(MessageConfig.GROUP_LIST_GROUPS);
            List<String> groupNames = new ArrayList<>(PermissionBootstrap.PERMISSION_GROUPS.stream().map(PermissionGroup::getName).toList());
            sender.sendMessage(String.join(", ", groupNames));
        });
    }
}
