package dev.quantumassembly;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

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
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    private static void panel(GuiGraphics graphics, int x, int y, int w, int h) {
        graphics.fill(x, y, x + w, y + h, -15397091);
        graphics.fill(x + 2, y + 2, x + w - 2, y + h - 2, -12963760);
        graphics.fill(x + 4, y + 4, x + w - 4, y + h - 4, -13950152);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        panel(graphics, x, y, this.imageWidth, this.imageHeight);
        // the upgrade column on the left
        panel(graphics, x - 30, y + 12, 30, KitBlockEntity.UPGRADE_SLOTS * 18 + 16);
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.kit_upgrades"), x - 28, y + 3, 0xFFB9AAFF, true);
        for (Slot slot : this.menu.slots) {
            int sx = x + slot.x - 1;
            int sy = y + slot.y - 1;
            graphics.fill(sx, sy, sx + 18, sy + 18, -16054256);
            graphics.fill(sx + 1, sy + 1, sx + 18, sy + 18, -11911322);
            graphics.fill(sx + 1, sy + 1, sx + 17, sy + 17, -14804951);
        }
    }
}
