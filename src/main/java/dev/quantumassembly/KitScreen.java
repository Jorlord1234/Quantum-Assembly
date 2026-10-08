package dev.quantumassembly;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** A backpack-style screen: a big storage grid, your inventory below. */
public class KitScreen extends AbstractContainerScreen<KitMenu> {
    public KitScreen(KitMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 128;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF150F1D);
        graphics.fill(x + 2, y + 2, x + imageWidth - 2, y + imageHeight - 2, 0xFF3A3050);
        graphics.fill(x + 4, y + 4, x + imageWidth - 4, y + imageHeight - 4, 0xFF2B2338);
        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            graphics.fill(sx, sy, sx + 18, sy + 18, 0xFF0B0810);
            graphics.fill(sx + 1, sy + 1, sx + 18, sy + 18, 0xFF4A3F66);
            graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF1E1829);
        }
    }
}
