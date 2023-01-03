package dev.whatsappuser.minestom.permissions.inventory.gui;

import dev.whatsappuser.minestom.permissions.inventory.ClickableItem;
import dev.whatsappuser.minestom.permissions.inventory.SingletonInventory;
import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

/**
 * development by TimoH created on 19:17:56 | 03.01.2023
 */

public class PermissionGUI extends SingletonInventory {

    private static final ClickableItem PLACEHOLDER = new ClickableItem(ItemStack.of(Material.GRAY_STAINED_GLASS_PANE).withDisplayName(Component.empty()));

    public PermissionGUI() {
        super(InventoryType.CHEST_1_ROW, "§8» §cPermissions");

        update();
    }

    public void update() {
        fill(1, new ClickableItem(ItemStack.of(Material.DIAMOND).withDisplayName(Component.text("§8» §cView all Groups"))).getStack()
                , player -> new ViewGroupsGUI().open(player));
    }
}
