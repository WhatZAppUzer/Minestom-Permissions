package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.lib.command.Command;
import dev.whatsappuser.minestom.lib.permissions.PermissionProvider;
import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.config.MessageConfig;
import org.jetbrains.annotations.NotNull;

/**
 * development by TimoH created on 11:58:09 | 09.01.2023
 */

public class ReloadSubCommand extends Command {

    public ReloadSubCommand(@NotNull PermissionProvider provider) {
        super(provider, "reload", "rl");

        setPermission("mpermissions.command.reload");
        addSyntax((sender, context) -> {
            PermissionBootstrap.getBootstrap().getPermissionPool().reloadGroups();
            sender.sendMessage(MessageConfig.RELOAD_SUCCESSFULLY);
        });
    }
}
