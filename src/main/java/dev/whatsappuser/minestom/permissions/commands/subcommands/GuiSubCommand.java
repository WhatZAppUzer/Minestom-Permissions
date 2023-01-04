package dev.whatsappuser.minestom.permissions.commands.subcommands;

import dev.whatsappuser.minestom.lib.command.Command;
import dev.whatsappuser.minestom.lib.permissions.PermissionProvider;
import dev.whatsappuser.minestom.permissions.inventory.gui.PermissionGUI;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * development by TimoH created on 19:06:19 | 03.01.2023
 */

public class GuiSubCommand extends Command {

    public GuiSubCommand(@NotNull PermissionProvider provider) {
        super(provider, "gui");

        setPermission("mpermission.command.gui");
        addSyntax((sender, context) -> {
            Player player = (Player) sender;
            new PermissionGUI().open(player);
        });
    }
}
