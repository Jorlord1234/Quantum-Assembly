package dev.quantumassembly;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** A backpack-style screen: ritual slots as tabs on the left, a big storage grid, your inventory below. */
public class KitScreen extends AbstractContainerScreen<KitMenu> {
    private static final int[] TAB_COLORS = {0xFF41FFDE, 0xFFB18CF0, 0xFFF7D349};

    public KitScreen(KitMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 210;
        this.imageHeight = 232;
        this.titleLabelX = 40;
        this.titleLabelY = 7;
        this.inventoryLabelX = 40;
        this.inventoryLabelY = 136;
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
        // main panel
        graphics.fill(x + 34, y, x + imageWidth, y + imageHeight, 0xFF150F1D);
        graphics.fill(x + 36, y + 2, x + imageWidth - 2, y + imageHeight - 2, 0xFF3A3050);
        graphics.fill(x + 38, y + 4, x + imageWidth - 4, y + imageHeight - 4, 0xFF2B2338);
        // tabs for the 3 ritual slots
        for (int i = 0; i < 3; i++) {
            int ty = y + 18 + i * 28;
            graphics.fill(x + 2, ty, x + 38, ty + 28, 0xFF150F1D);
            graphics.fill(x + 4, ty + 2, x + 38, ty + 26, 0xFF3A3050);
            graphics.fill(x + 6, ty + 4, x + 36, ty + 24, 0xFF2B2338);
            graphics.fill(x + 4, ty + 2, x + 6, ty + 26, TAB_COLORS[i]);
        }
        // a frame around every slot
        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            graphics.fill(sx, sy, sx + 18, sy + 18, 0xFF0B0810);
            graphics.fill(sx + 1, sy + 1, sx + 18, sy + 18, 0xFF4A3F66);
            graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF1E1829);
        }
    }
}
