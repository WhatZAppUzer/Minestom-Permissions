package dev.whatsappuser.minestom.permissions.commands;

import dev.whatsappuser.minestom.permissions.commands.subcommands.GroupSubCommand;
import dev.whatsappuser.minestom.permissions.commands.subcommands.GuiSubCommand;
import dev.whatsappuser.minestom.permissions.commands.subcommands.PlayerSubCommand;
import dev.whatsappuser.minestom.permissions.config.MessageConfig;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

/**
 * development by TimoH created on 18:59:23 | 01.01.2023
 */

public class PermissionCommand extends Command {

    public PermissionCommand() {
        super("mperms");

        setCondition((sender, commandString) -> {
            if(!checkPermissions(sender, "mperms.command")) {
                sender.sendMessage(MessageConfig.HAS_NO_PERMISSION);
                return false;
            }
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
        });

        addSubcommand(new PlayerSubCommand());
        addSubcommand(new GroupSubCommand());
        addSubcommand(new GuiSubCommand());

    }

    public final boolean checkPermissions(CommandSender sender, String permission) {
        if(sender instanceof Player player)
            return player.hasPermission(permission);
        return true;
    }
}
