package dev.whatsappuser.minestom.permissions.inventory;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * development by TimoH created on 19:14:37 | 03.01.2023
 */

@Getter
public class SingletonInventory {

    private final Inventory inventory;

    private final Map<Integer, ClickableItem> inventoryItems = new ConcurrentHashMap<>();

    public SingletonInventory(InventoryType inventoryType, String title) {
        this.inventory = new Inventory(inventoryType, title);

        this.inventory.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
            inventoryConditionResult.setCancel(true);
            clickIfPresent(slot, player);
        });
    }

    public SingletonInventory fill(int slot, ItemStack stack, ClickAction clickAction) {
        this.inventoryItems.put(slot, new ClickableItem(stack, clickAction));
        this.inventory.setItemStack(slot, stack);
        return this;
    }

    public SingletonInventory fill(int slot, ClickableItem clickAction) {
        this.inventoryItems.put(slot, clickAction);
        this.inventory.setItemStack(slot, clickAction.getStack());
        return this;
    }

    public void clear(int slot) {
        this.inventory.setItemStack(slot, ItemStack.AIR);
        this.inventoryItems.remove(slot);
    }

    public void clear() {
        this.inventory.clear();
        this.inventoryItems.clear();
    }

    public int size() {
        return this.inventory.getSize();
    }

    private void clickIfPresent(int slot, Player player) {
        if (this.inventoryItems.containsKey(slot))
            ((ClickableItem)this.inventoryItems.get(slot)).click(player);
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    public void updateDisplayName(String title) {
        this.inventory.setTitle((Component) Component.text(title));
    }
}
