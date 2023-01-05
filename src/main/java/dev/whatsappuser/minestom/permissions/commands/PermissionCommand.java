package dev.whatsappuser.minestom.permissions.commands;

import dev.whatsappuser.minestom.lib.command.Command;
import dev.whatsappuser.minestom.lib.permissions.PermissionProvider;
import dev.whatsappuser.minestom.permissions.commands.subcommands.GroupSubCommand;
import dev.whatsappuser.minestom.permissions.commands.subcommands.GuiSubCommand;
import dev.whatsappuser.minestom.permissions.commands.subcommands.PlayerSubCommand;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * development by TimoH created on 18:59:23 | 01.01.2023
 */

public class PermissionCommand extends Command {

    public PermissionCommand(@NotNull PermissionProvider provider) {
        super(provider, "mperms", "permissions");

        setPermission("mpermission.command");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("/mperms user [name] info"));
            sender.sendMessage(Component.text("/mperms user [name] perm add | remove [permission]"));
            sender.sendMessage(Component.text("/mperms user [name] group set | remove [groupName]"));
            sender.sendMessage(Component.text("/mperms group [name] info"));
            sender.sendMessage(Component.text("/mperms group [name] perm add | remove [permission]"));
            sender.sendMessage(Component.text("/mperms group [name] create [default(true:false)]"));
            sender.sendMessage(Component.text("/mperms group [name] delete"));
            sender.sendMessage(Component.text("/mperms group [name] setPriority [priority(int)]"));
        });

        addSubcommand(new GuiSubCommand(provider));
        addSubcommand(new PlayerSubCommand(provider));
        addSubcommand(new GroupSubCommand(provider));
    }
}
