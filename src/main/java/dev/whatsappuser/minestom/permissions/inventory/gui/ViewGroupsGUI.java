package dev.whatsappuser.minestom.permissions.inventory.gui;

import dev.whatsappuser.minestom.permissions.PermissionBootstrap;
import dev.whatsappuser.minestom.permissions.group.PermissionGroup;
import dev.whatsappuser.minestom.permissions.inventory.ClickableItem;
import dev.whatsappuser.minestom.permissions.inventory.SingletonInventory;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * development by TimoH created on 19:23:55 | 03.01.2023
 */

public class ViewGroupsGUI extends SingletonInventory {

    private final Map<ItemStack, PermissionGroup> viewableGroups = new ConcurrentHashMap<>();

    public ViewGroupsGUI() {
        super(InventoryType.CHEST_3_ROW, "§8» §cAll Groups");

        for (int i = 0; i < PermissionBootstrap.getBootstrap().getDatabase().getAllLoadedGroups().size(); i++) {
            PermissionGroup group = PermissionBootstrap.getBootstrap().getDatabase().getAllLoadedGroups().stream().toList().get(i);
            fill(i + 1, new ClickableItem(
                    ItemStack.builder(Material.ANCIENT_DEBRIS).displayName(Component.text(group.getColorCode() + group.getName()))
                            .lore(Component.text("§7Click for options"),
                                    Component.text(""), Component.text("§7Id §8» §c" + group.getId()),
                                    Component.text("§7Default §8» " + (group.isDefault() ? "§atrue" : "§cfalse")),
                                    Component.text("§7Prefix §8» " + group.getPrefix()),
                                    Component.text("§7Display §8» " + group.getDisplay()),
                                    Component.text("§7ChatFormat §8» " + group.getChatFormat()),
                                    Component.text("§7Priority §8» §c" + group.getPriority())).build()));
        }
    }
}
