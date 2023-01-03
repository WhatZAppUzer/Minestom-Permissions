package dev.whatsappuser.minestom.permissions.commands;

import dev.whatsappuser.minestom.permissions.commands.subcommands.GroupSubCommand;
import dev.whatsappuser.minestom.permissions.commands.subcommands.GuiSubCommand;
import dev.whatsappuser.minestom.permissions.commands.subcommands.PlayerSubCommand;
import dev.whatsappuser.minestom.permissions.commands.subcommands.ReloadSubCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

/**
 * development by TimoH created on 18:59:23 | 01.01.2023
 */

public class PermissionCommand extends Command {

    public PermissionCommand() {
        super("mperms");

        setCondition((sender, commandString) -> {
            if(sender instanceof Player player)
                return player.hasPermission("mperms.command");
            return true;
        });

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("/mperms user [name] info"));
            sender.sendMessage(Component.text("/mperms user [name] perm add | remove [permission]"));
            sender.sendMessage(Component.text("/mperms user [name] group set | remove [groupName]"));
            sender.sendMessage(Component.text("/mperms group [name] info"));
            sender.sendMessage(Component.text("/mperms group [name] perm add | remove [permission]"));
            sender.sendMessage(Component.text("/mperms group [name] create [default(true:false)]"));
            sender.sendMessage(Component.text("/mperms group [name] delete"));
            sender.sendMessage(Component.text("/mperms group [name] setPriority [priority(int)]"));
            sender.sendMessage(Component.text("/mperms group [name] setPrefix [prefix]"));
            sender.sendMessage(Component.text("/mperms group [name] setDisplay [display]"));
            sender.sendMessage(Component.text("/mperms group [name] setSuffix [suffix]"));
            sender.sendMessage(Component.text("/mperms reload"));
        });

        addSubcommand(new PlayerSubCommand());
        addSubcommand(new GroupSubCommand());
        addSubcommand(new ReloadSubCommand());
        addSubcommand(new GuiSubCommand());

    }
}
