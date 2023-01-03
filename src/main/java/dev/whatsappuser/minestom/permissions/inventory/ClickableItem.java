package dev.whatsappuser.minestom.permissions.inventory;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

/**
 * development by TimoH created on 19:14:30 | 03.01.2023
 */

public final class ClickableItem {
    private final ItemStack stack;

    private ClickAction playerClick;

    public ClickableItem(ItemStack stack, ClickAction playerClick) {
        this.playerClick = null;
        this.stack = stack;
        this.playerClick = playerClick;
    }

    public ClickableItem(ItemStack stack) {
        this.playerClick = null;
        this.stack = stack;
    }

    public ItemStack getStack() {
        return this.stack;
    }

    public ClickAction getPlayerClick() {
        return this.playerClick;
    }

    public void click(Player player) {
        if (this.playerClick != null)
            this.playerClick.click(player);
    }
}