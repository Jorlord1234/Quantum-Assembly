package dev.quantumassembly;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class PocketControlsScreen extends AbstractContainerScreen<PocketControlsMenu> {
    public PocketControlsScreen(PocketControlsMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 200;
        this.imageHeight = 130;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos + 20;
        int y = this.topPos + 62;
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.upgrade"), b -> press(PocketControlsMenu.BUTTON_UPGRADE))
                .bounds(x, y, 160, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.link"), b -> press(PocketControlsMenu.BUTTON_LINK))
                .bounds(x, y + 24, 160, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.disassemble"), b -> press(PocketControlsMenu.BUTTON_DISASSEMBLE))
                .bounds(x, y + 48, 160, 20).build());
    }

    private void press(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    private int starglassInInventory() {
        int have = 0;
        for (ItemStack stack : this.minecraft.player.getInventory().items) {
            if (stack.is(ModItems.STARGLASS.get())) {
                have += stack.getCount();
            }
        }
        return have;
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
        Component level = Component.translatable("gui.quantum_assembly.level", menu.level(), menu.size());
        graphics.drawString(this.font, level, x + 12, y + 22, 0xFFE3C8EA, false);
        Component next = menu.nextCost() == 0
                ? Component.translatable("gui.quantum_assembly.max")
                : Component.translatable("gui.quantum_assembly.next", menu.nextCost(), starglassInInventory());
        graphics.drawString(this.font, next, x + 12, y + 36, 0xFF7AFCFF, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 12, 8, 0xFFFFFFFF, false);
    }
}
