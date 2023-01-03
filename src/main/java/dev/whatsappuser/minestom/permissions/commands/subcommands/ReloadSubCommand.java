package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

/**
 * development by TimoH created on 18:32:32 | 03.01.2023
 */

public class ReloadSubCommand extends Command {
    public ReloadSubCommand() {
        super("reload");

        setCondition((sender, commandString) -> {
            if (!checkPermissions(sender, "mperms.command")) {
                sender.sendMessage(Component.text("You are not authorized to use this command.", NamedTextColor.RED));
                return false;
            }
            return true;
        });

        addSyntax((sender, context) -> {
            PermissionBootstrap.getBootstrap().getPermissionPool().reload();
            sender.sendMessage(Component.text("Successfully reloaded all permissions.", NamedTextColor.GREEN));
        });
    }

    public final boolean checkPermissions(CommandSender sender, String permission) {
        if(sender instanceof Player player)
            return player.hasPermission(permission);
        return true;
    }
}
