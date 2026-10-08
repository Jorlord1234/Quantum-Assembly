package dev.quantumassembly;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The Pocket Controls screen: a title bar, one compact card per upgrade (with level pips), and the machine buttons. */
public class PocketControlsScreen extends AbstractContainerScreen<PocketControlsMenu> {
    private static final int W = 232;
    private static final int H = 224;
    private static final int CARD_H = 38;
    private static final int FIRST_CARD_Y = 28;
    private static final int CARD_PITCH = 40;

    private static final int COL_FRAME = 0xFF150F1D;
    private static final int COL_EDGE = 0xFF3A3050;
    private static final int COL_PANEL = 0xFF2B2338;
    private static final int COL_CARD = 0xFF1E1829;
    private static final int COL_CARD_EDGE = 0xFF4A3F66;
    private static final int COL_ACCENT = 0xFFB18CF0;
    private static final int COL_PIP_OFF = 0xFF0B0810;

    private Button spaceButton;
    private Button guestButton;
    private Button restButton;
    private Button vaultUpgradeButton;
    private Button vaultOpenButton;

    public PocketControlsScreen(PocketControlsMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = W;
        this.imageHeight = H;
    }

    private static int cardY(int index) {
        return FIRST_CARD_Y + index * CARD_PITCH;
    }

    private Button smallButton(int x, int y, int w, String key, int id) {
        return addRenderableWidget(Button.builder(Component.translatable(key), b -> press(id)).bounds(x, y, w, 16).build());
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;
        int bx = x + W - 76;
        spaceButton = smallButton(bx, y + cardY(0) + 11, 60, "gui.quantum_assembly.btn_upgrade", PocketControlsMenu.BUTTON_UPGRADE);
        guestButton = smallButton(bx, y + cardY(1) + 11, 60, "gui.quantum_assembly.btn_upgrade", PocketControlsMenu.BUTTON_GUESTS);
        restButton = smallButton(bx, y + cardY(2) + 11, 60, "gui.quantum_assembly.btn_upgrade", PocketControlsMenu.BUTTON_REST);
        vaultUpgradeButton = smallButton(bx, y + cardY(3) + 3, 60, "gui.quantum_assembly.btn_upgrade", PocketControlsMenu.BUTTON_VAULT_UPGRADE);
        vaultOpenButton = smallButton(bx, y + cardY(3) + 20, 60, "gui.quantum_assembly.btn_open", PocketControlsMenu.BUTTON_VAULT_OPEN);
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.link"),
                b -> press(PocketControlsMenu.BUTTON_LINK)).bounds(x + 10, y + H - 26, 104, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.disassemble"),
                b -> press(PocketControlsMenu.BUTTON_DISASSEMBLE)).bounds(x + W - 114, y + H - 26, 104, 20).build());
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
        restButton.active = menu.restNextCost() != 0;
        vaultUpgradeButton.active = menu.vaultNextCost() != 0;
        vaultOpenButton.active = menu.vaultLevel() > 0;
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private void card(GuiGraphics g, int x, int y) {
        g.fill(x, y, x + W - 20, y + CARD_H, COL_CARD_EDGE);
        g.fill(x + 1, y + 1, x + W - 21, y + CARD_H - 1, COL_CARD);
        g.fill(x + 1, y + 1, x + 3, y + CARD_H - 1, COL_ACCENT);
    }

    /** One upgrade card: icon, title, one line of text, level pips and the cost of the next level. */
    private void upgradeCard(GuiGraphics g, int x, int y, ItemStack icon, Component title, Component sub,
                             int pipCount, int pipsFilled, int nextCost) {
        card(g, x, y);
        g.renderItem(icon, x + 8, y + 11);
        g.drawString(this.font, title, x + 28, y + 5, 0xFFFFFFFF, false);
        g.drawString(this.font, sub, x + 28, y + 16, 0xFFE3C8EA, false);
        for (int i = 0; i < pipCount; i++) {
            int px = x + 28 + i * 10;
            g.fill(px, y + 28, px + 8, y + 33, COL_PIP_OFF);
            g.fill(px + 1, y + 29, px + 7, y + 32, i < pipsFilled ? COL_ACCENT : 0xFF2A2238);
        }
        Component cost = nextCost == 0
                ? Component.translatable("gui.quantum_assembly.maxed")
                : Component.translatable("gui.quantum_assembly.cost_short", nextCost);
        g.drawString(this.font, cost, x + 28 + pipCount * 10 + 6, y + 27, 0xFF7AFCFF, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.fill(x, y, x + W, y + H, COL_FRAME);
        graphics.fill(x + 2, y + 2, x + W - 2, y + H - 2, COL_EDGE);
        graphics.fill(x + 4, y + 4, x + W - 4, y + H - 4, COL_PANEL);
        // title bar, with how much Starglass you carry on the right
        graphics.fill(x + 4, y + 4, x + W - 4, y + 20, COL_FRAME);
        graphics.fill(x + 4, y + 20, x + W - 4, y + 22, COL_ACCENT);
        Component have = Component.translatable("gui.quantum_assembly.have", starglassInInventory());
        graphics.drawString(this.font, have, x + W - 10 - this.font.width(have), y + 8, 0xFF7AFCFF, false);

        int cx = x + 10;
        upgradeCard(graphics, cx, y + cardY(0), new ItemStack(ModItems.STARGLASS.get()),
                Component.translatable("gui.quantum_assembly.space_title"),
                Component.translatable("gui.quantum_assembly.space_sub", menu.level(), menu.size()),
                PocketManager.RADIUS.length - 1, menu.level(), menu.nextCost());
        upgradeCard(graphics, cx, y + cardY(1), new ItemStack(Items.PLAYER_HEAD),
                Component.translatable("gui.quantum_assembly.guests_title"),
                Component.translatable("gui.quantum_assembly.guests_sub", menu.guestMax()),
                PocketManager.GUEST_MAX.length - 1, menu.guestLevel(), menu.guestNextCost());
        upgradeCard(graphics, cx, y + cardY(2), new ItemStack(Items.GOLDEN_APPLE),
                Component.translatable("gui.quantum_assembly.rest_title"),
                Component.translatable("gui.quantum_assembly.rest_sub_" + Math.min(3, menu.restLevel())),
                PocketManager.REST_COST.length - 1, menu.restLevel(), menu.restNextCost());
        upgradeCard(graphics, cx, y + cardY(3), new ItemStack(Items.ENDER_CHEST),
                Component.translatable("gui.quantum_assembly.vault_title"),
                menu.vaultLevel() == 0
                        ? Component.translatable("gui.quantum_assembly.vault_sub_locked")
                        : Component.translatable("gui.quantum_assembly.vault_sub", menu.vaultSlots()),
                PocketManager.VAULT_COST.length - 1, menu.vaultLevel(), menu.vaultNextCost());
        // coming soon
        graphics.drawString(this.font, Component.translatable("gui.quantum_assembly.kinetic_soon"),
                cx + 4, y + cardY(4) + 4, 0xFF6B6478, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 10, 8, 0xFFFFFFFF, false);
    }
}
