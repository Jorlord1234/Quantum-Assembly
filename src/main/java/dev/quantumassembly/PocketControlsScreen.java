package dev.quantumassembly;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** The Pocket Controls screen: a title bar, one card per upgrade (with level pips), and the machine buttons. */
public class PocketControlsScreen extends AbstractContainerScreen<PocketControlsMenu> {
    private static final int W = 232;
    private static final int H = 206;
    private static final int CARD_H = 56;
    private static final int SPACE_Y = 26;
    private static final int GUEST_Y = 88;
    private static final int KINETIC_Y = 150;

    private static final int COL_FRAME = 0xFF150F1D;
    private static final int COL_EDGE = 0xFF3A3050;
    private static final int COL_PANEL = 0xFF2B2338;
    private static final int COL_CARD = 0xFF1E1829;
    private static final int COL_CARD_EDGE = 0xFF4A3F66;
    private static final int COL_ACCENT = 0xFFB18CF0;
    private static final int COL_PIP_OFF = 0xFF0B0810;

    private Button spaceButton;
    private Button guestButton;

    public PocketControlsScreen(PocketControlsMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = W;
        this.imageHeight = H;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        spaceButton = addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.btn_upgrade"),
                b -> press(PocketControlsMenu.BUTTON_UPGRADE)).bounds(x + W - 74, y + SPACE_Y + 28, 62, 20).build());
        guestButton = addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.btn_upgrade"),
                b -> press(PocketControlsMenu.BUTTON_GUESTS)).bounds(x + W - 74, y + GUEST_Y + 28, 62, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.link"),
                b -> press(PocketControlsMenu.BUTTON_LINK)).bounds(x + 10, y + H - 28, 104, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.disassemble"),
                b -> press(PocketControlsMenu.BUTTON_DISASSEMBLE)).bounds(x + W - 114, y + H - 28, 104, 20).build());
    }

    private void press(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    private int starglassInInventory() {
        int have = 0;
        if (this.minecraft != null && this.minecraft.player != null) {
            for (ItemStack stack : this.minecraft.player.getInventory().items) {
                if (stack.is(ModItems.STARGLASS.get())) {
                    have += stack.getCount();
                }
            }
        }
        return have;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        spaceButton.active = menu.nextCost() != 0;
        guestButton.active = menu.guestNextCost() != 0;
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void card(GuiGraphics g, int x, int y, int h) {
        g.fill(x, y, x + W - 20, y + h, COL_CARD_EDGE);
        g.fill(x + 1, y + 1, x + W - 21, y + h - 1, COL_CARD);
        g.fill(x + 1, y + 1, x + 3, y + h - 1, COL_ACCENT);
    }

    private void pips(GuiGraphics g, int x, int y, int total, int filled) {
        for (int i = 0; i < total; i++) {
            int px = x + i * 14;
            g.fill(px, y, px + 11, y + 7, COL_PIP_OFF);
            g.fill(px + 1, y + 1, px + 10, y + 6, i < filled ? COL_ACCENT : 0xFF2A2238);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x, y, x + W, y + H, COL_FRAME);
        graphics.fill(x + 2, y + 2, x + W - 2, y + H - 2, COL_EDGE);
        graphics.fill(x + 4, y + 4, x + W - 4, y + H - 4, COL_PANEL);
        // title bar
        graphics.fill(x + 4, y + 4, x + W - 4, y + 20, COL_FRAME);
        graphics.fill(x + 4, y + 20, x + W - 4, y + 22, COL_ACCENT);

        int cx = x + 10;
        ItemStack starglass = new ItemStack(ModItems.STARGLASS.get());

        // card 1: pocket space
        int cy = y + SPACE_Y;
        card(graphics, cx, cy, CARD_H);
        graphics.renderItem(starglass, cx + 8, cy + 8);
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.space_title"), cx + 30, cy + 7, 0xFFFFFFFF, false);
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.space_sub", menu.level(), menu.size()), cx + 30, cy + 19, 0xFFE3C8EA, false);
        pips(graphics, cx + 30, cy + 32, PocketManager.RADIUS.length - 1, menu.level());
        graphics.drawString(this.font, menu.nextCost() == 0
                ? Component.translatable("gui.quantum_assembly.maxed")
                : Component.translatable("gui.quantum_assembly.cost", menu.nextCost(), starglassInInventory()), cx + 30, cy + 43, 0xFF7AFCFF, false);

        // card 2: guests
        cy = y + GUEST_Y;
        card(graphics, cx, cy, CARD_H);
        graphics.renderItem(new ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD), cx + 8, cy + 8);
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.guests_title"), cx + 30, cy + 7, 0xFFFFFFFF, false);
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.guests_sub", menu.guestMax()), cx + 30, cy + 19, 0xFFE3C8EA, false);
        pips(graphics, cx + 30, cy + 32, PocketManager.GUEST_MAX.length - 1, menu.guestLevel());
        graphics.drawString(this.font, menu.guestNextCost() == 0
                ? Component.translatable("gui.quantum_assembly.maxed")
                : Component.translatable("gui.quantum_assembly.cost", menu.guestNextCost(), starglassInInventory()), cx + 30, cy + 43, 0xFF7AFCFF, false);

        // card 3: kinetic link (not built yet, shown locked)
        cy = y + KINETIC_Y;
        card(graphics, cx, cy, 40);
        graphics.fill(cx + 1, cy + 1, cx + 3, cy + 39, 0xFF6B6478);
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.kinetic_title"), cx + 30, cy + 9, 0xFF9A93A8, false);
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.kinetic_sub"), cx + 30, cy + 22, 0xFF6B6478, false);
        graphics.renderItem(new ItemStack(net.minecraft.world.item.Items.BARRIER), cx + 8, cy + 12);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 10, 8, 0xFFFFFFFF, false);
    }
}
