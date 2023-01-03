package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.inventory.gui.PermissionGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

/**
 * development by TimoH created on 19:06:19 | 03.01.2023
 */

public class GuiSubCommand extends Command {

    public GuiSubCommand() {
        super("gui");

        setCondition((sender, commandString) -> {
            if (!checkPermissions(sender, "mperms.command")) {
                sender.sendMessage(Component.text("You are not authorized to use this command.", NamedTextColor.RED));
                return false;
            }
            return true;
        });

        addSyntax((sender, context) -> {
            Player player = (Player) sender;
            new PermissionGUI().open(player);
        });
    }

    public final boolean checkPermissions(CommandSender sender, String permission) {
        if(sender instanceof Player player)
            return player.hasPermission(permission);
        return true;
    }
}
