package dev.quantumassembly;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The Pocket Controls screen. A light panel with a dark board. The upgrades sit in colored groups as small
 * icon nodes (bright = unlocked, gold border = the next one you can buy, dim = locked). Hover a node to read
 * what it does, click the gold one to buy it.
 */
public class PocketControlsScreen extends AbstractContainerScreen<PocketControlsMenu> {
    private static final int W = 276;
    private static final int H = 194;
    private static final int BOARD_X = 8;
    private static final int BOARD_Y = 20;
    private static final int BOARD_W = 260;
    private static final int BOARD_H = 142;
    private static final int NODE = 20;
    private static final int NODE_PITCH = 22;
    private static final int BOX_W = 122;

    private static final int SPACE = 0;
    private static final int GUESTS = 1;
    private static final int REST = 2;
    private static final int VAULT = 3;
    private static final int POWER = 4;

    private static final int STATE_UNLOCKED = 0;
    private static final int STATE_NEXT = 1;
    private static final int STATE_LOCKED = 2;

    private static final String[] ROMAN = {"", "I", "II", "III", "IV", "V", "VI"};

    /** One colored group on the board. x and y are relative to the screen's top-left corner. */
    private static final class Cat {
        final int id;
        final String nameKey;
        final int color;
        final int levels;
        final int x;
        final int y;
        final int h;
        final int buttonId;
        ItemStack icon = ItemStack.EMPTY;

        Cat(int id, String nameKey, int color, int levels, int x, int y, int h, int buttonId) {
            this.id = id;
            this.nameKey = nameKey;
            this.color = color;
            this.levels = levels;
            this.x = x;
            this.y = y;
            this.h = h;
            this.buttonId = buttonId;
        }
    }

    private Cat[] cats;
    private Button vaultOpenButton;

    public PocketControlsScreen(PocketControlsMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = W;
        this.imageHeight = H;
    }

    @Override
    protected void init() {
        super.init();
        int left = BOARD_X + 6;
        int right = BOARD_X + 6 + BOX_W + 8;
        int top = BOARD_Y + 6;
        cats = new Cat[]{
                new Cat(SPACE, "gui.quantum_assembly.cat_space", 0xFF3AA6A0, PocketManager.RADIUS.length - 1, left, top, 40, PocketControlsMenu.BUTTON_UPGRADE),
                new Cat(GUESTS, "gui.quantum_assembly.cat_guests", 0xFF3A5FCD, PocketManager.GUEST_MAX.length - 1, right, top, 40, PocketControlsMenu.BUTTON_GUESTS),
                new Cat(REST, "gui.quantum_assembly.cat_rest", 0xFFC49A00, PocketManager.REST_COST.length - 1, left, top + 44, 40, PocketControlsMenu.BUTTON_REST),
                new Cat(VAULT, "gui.quantum_assembly.cat_vault", 0xFF8A4FD0, PocketManager.VAULT_COST.length - 1, right, top + 44, 58, PocketControlsMenu.BUTTON_VAULT_UPGRADE),
                new Cat(POWER, "gui.quantum_assembly.cat_power", 0xFFB02020, 1, left, top + 88, 40, -1)
        };
        cats[SPACE].icon = new ItemStack(ModItems.STARGLASS.get());
        cats[GUESTS].icon = new ItemStack(Items.PLAYER_HEAD);
        cats[REST].icon = new ItemStack(Items.GOLDEN_APPLE);
        cats[VAULT].icon = new ItemStack(Items.ENDER_CHEST);
        cats[POWER].icon = new ItemStack(Items.REDSTONE);

        int x = this.leftPos;
        int y = this.topPos;
        Cat vault = cats[VAULT];
        vaultOpenButton = addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.btn_open"),
                b -> press(PocketControlsMenu.BUTTON_VAULT_OPEN)).bounds(x + vault.x + 6, y + vault.y + 40, 70, 14).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.link"),
                b -> press(PocketControlsMenu.BUTTON_LINK)).bounds(x + 8, y + H - 26, 120, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.quantum_assembly.disassemble"),
                b -> press(PocketControlsMenu.BUTTON_DISASSEMBLE)).bounds(x + W - 128, y + H - 26, 120, 20).build());
    }

    private void press(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    // ---------- upgrade state ----------

    private int levelOf(int id) {
        switch (id) {
            case SPACE:
                return menu.level();
            case GUESTS:
                return menu.guestLevel();
            case REST:
                return menu.restLevel();
            case VAULT:
                return menu.vaultLevel();
            default:
                return 0;
        }
    }

    private int nextCostOf(int id) {
        switch (id) {
            case SPACE:
                return menu.nextCost();
            case GUESTS:
                return menu.guestNextCost();
            case REST:
                return menu.restNextCost();
            case VAULT:
                return menu.vaultNextCost();
            default:
                return 0;
        }
    }

    /** k is the 1-based level of the node. */
    private int stateOf(int id, int k) {
        int level = levelOf(id);
        if (level >= k) {
            return STATE_UNLOCKED;
        }
        if (id != POWER && level + 1 == k && nextCostOf(id) > 0) {
            return STATE_NEXT;
        }
        return STATE_LOCKED;
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

    // ---------- node positions, hover and click ----------

    private int nodeX(Cat cat, int k) {
        return this.leftPos + cat.x + 6 + (k - 1) * NODE_PITCH;
    }

    private int nodeY(Cat cat) {
        return this.topPos + cat.y + 16;
    }

    /** Returns {category index, level} of the node under the mouse, or null. */
    private int[] nodeAt(double mx, double my) {
        if (cats == null) {
            return null;
        }
        for (int c = 0; c < cats.length; c++) {
            Cat cat = cats[c];
            int ny = nodeY(cat);
            if (my < ny || my >= ny + NODE) {
                continue;
            }
            for (int k = 1; k <= cat.levels; k++) {
                int nx = nodeX(cat, k);
                if (mx >= nx && mx < nx + NODE) {
                    return new int[]{c, k};
                }
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            int[] hit = nodeAt(mouseX, mouseY);
            if (hit != null && stateOf(hit[0], hit[1]) == STATE_NEXT && cats[hit[0]].buttonId >= 0) {
                if (this.minecraft != null) {
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                press(cats[hit[0]].buttonId);
                return true;
            }
        }
        return false;
    }

    // ---------- tooltip ----------

    private List<Component> tooltipFor(int c, int k) {
        Cat cat = cats[c];
        List<Component> lines = new ArrayList<>();
        String title = cat.id == POWER
                ? Component.translatable("gui.quantum_assembly.kinetic_title").getString()
                : Component.translatable(cat.nameKey).getString() + " " + ROMAN[Math.min(k, ROMAN.length - 1)];
        lines.add(Component.literal(title).withStyle(ChatFormatting.LIGHT_PURPLE));
        switch (cat.id) {
            case SPACE:
                lines.add(Component.translatable("gui.quantum_assembly.tip_space", PocketManager.RADIUS[k] * 2 + 1).withStyle(ChatFormatting.GRAY));
                break;
            case GUESTS:
                lines.add(Component.translatable("gui.quantum_assembly.tip_guests", PocketManager.GUEST_MAX[k]).withStyle(ChatFormatting.GRAY));
                break;
            case REST:
                lines.add(Component.translatable("gui.quantum_assembly.tip_rest_" + Math.min(3, k)).withStyle(ChatFormatting.GRAY));
                break;
            case VAULT:
                lines.add(Component.translatable("gui.quantum_assembly.tip_vault", PocketManager.VAULT_ROWS[k] * 9).withStyle(ChatFormatting.GRAY));
                break;
            default:
                lines.add(Component.translatable("gui.quantum_assembly.tip_power").withStyle(ChatFormatting.GRAY));
                break;
        }
        int state = stateOf(cat.id, k);
        if (state == STATE_UNLOCKED) {
            lines.add(Component.translatable("gui.quantum_assembly.tip_unlocked").withStyle(ChatFormatting.GREEN));
        } else if (state == STATE_NEXT) {
            int cost = nextCostOf(cat.id);
            int have = starglassInInventory();
            if (have >= cost || (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.isCreative())) {
                lines.add(Component.translatable("gui.quantum_assembly.tip_click", cost).withStyle(ChatFormatting.YELLOW));
            } else {
                lines.add(Component.translatable("gui.quantum_assembly.tip_need", cost, have).withStyle(ChatFormatting.RED));
            }
        } else if (cat.id == POWER) {
            lines.add(Component.translatable("gui.quantum_assembly.tip_soon").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            lines.add(Component.translatable("gui.quantum_assembly.tip_locked").withStyle(ChatFormatting.DARK_GRAY));
        }
        return lines;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        vaultOpenButton.active = menu.vaultLevel() > 0;
        super.render(graphics, mouseX, mouseY, partialTick);
        int[] hit = nodeAt(mouseX, mouseY);
        if (hit != null) {
            graphics.renderComponentTooltip(this.font, tooltipFor(hit[0], hit[1]), mouseX, mouseY);
        }
    }

    // ---------- drawing ----------

    /** A light, bevelled Minecraft-style panel. */
    private void bevel(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xFF000000);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, 0xFFFFFFFF);
        g.fill(x + 3, y + 3, x + w - 1, y + h - 1, 0xFF555555);
        g.fill(x + 3, y + 3, x + w - 3, y + h - 3, 0xFFC6C6C6);
    }

    private void drawNode(GuiGraphics g, Cat cat, int k, boolean hovered) {
        int nx = nodeX(cat, k);
        int ny = nodeY(cat);
        int state = stateOf(cat.id, k);
        int border = state == STATE_UNLOCKED ? cat.color : state == STATE_NEXT ? 0xFFFFD34D : 0xFF3A3A40;
        int fill = state == STATE_LOCKED ? 0xFF222226 : 0xFF3A3A44;
        g.fill(nx, ny, nx + NODE, ny + NODE, border);
        g.fill(nx + 1, ny + 1, nx + NODE - 1, ny + NODE - 1, fill);
        g.renderItem(cat.icon, nx + 2, ny + 2);
        if (state == STATE_LOCKED) {
            g.fill(nx + 1, ny + 1, nx + NODE - 1, ny + NODE - 1, 0xAA18181C);
        }
        if (state == STATE_NEXT) {
            g.fill(nx + 1, ny + 1, nx + NODE - 1, ny + 3, 0x66FFD34D);
        }
        if (hovered) {
            g.fill(nx + 1, ny + 1, nx + NODE - 1, ny + NODE - 1, 0x30FFFFFF);
        }
    }

    private void drawCat(GuiGraphics g, Cat cat, int mouseX, int mouseY) {
        int x = this.leftPos + cat.x;
        int y = this.topPos + cat.y;
        // box
        g.fill(x, y, x + BOX_W, y + cat.h, 0xFF4A4A52);
        g.fill(x + 1, y + 1, x + BOX_W - 1, y + cat.h - 1, 0xFF18181C);
        // colored header with a small icon, the name and level/max
        g.fill(x + 1, y + 1, x + BOX_W - 1, y + 13, cat.color);
        PoseStack pose = g.pose();
        pose.pushPose();
        pose.translate(x + 3, y + 2, 0);
        pose.scale(0.625F, 0.625F, 1.0F);
        g.renderItem(cat.icon, 0, 0);
        pose.popPose();
        g.drawString(this.font, Component.translatable(cat.nameKey), x + 16, y + 3, 0xFFFFFFFF, true);
        String lv = levelOf(cat.id) + "/" + cat.levels;
        g.drawString(this.font, lv, x + BOX_W - 4 - this.font.width(lv), y + 3, 0xFFFFFFFF, true);
        int[] hit = nodeAt(mouseX, mouseY);
        for (int k = 1; k <= cat.levels; k++) {
            boolean hovered = hit != null && cats[hit[0]] == cat && hit[1] == k;
            drawNode(g, cat, k, hovered);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        bevel(graphics, x, y, W, H);
        // sunken dark board
        graphics.fill(x + BOARD_X - 1, y + BOARD_Y - 1, x + BOARD_X + BOARD_W + 1, y + BOARD_Y + BOARD_H + 1, 0xFF373737);
        graphics.fill(x + BOARD_X, y + BOARD_Y, x + BOARD_X + BOARD_W + 1, y + BOARD_Y + BOARD_H + 1, 0xFFFFFFFF);
        graphics.fill(x + BOARD_X, y + BOARD_Y, x + BOARD_X + BOARD_W, y + BOARD_Y + BOARD_H, 0xFF1E1E22);
        if (cats == null) {
            return;
        }
        for (Cat cat : cats) {
            drawCat(graphics, cat, mouseX, mouseY);
        }
        // overall progress bar and the Starglass you carry, next to the title
        int total = 0;
        int done = 0;
        for (Cat cat : cats) {
            if (cat.id != POWER) {
                total += cat.levels;
                done += levelOf(cat.id);
            }
        }
        int barW = 70;
        int barX = x + W - 10 - barW;
        int barY = y + 7;
        graphics.fill(barX - 1, barY - 1, barX + barW + 1, barY + 7, 0xFF000000);
        graphics.fill(barX, barY, barX + barW, barY + 6, 0xFF373737);
        graphics.fill(barX, barY, barX + (total == 0 ? 0 : barW * done / total), barY + 6, 0xFFB18CF0);
        Component have = Component.translatable("gui.quantum_assembly.have", starglassInInventory());
        graphics.drawString(this.font, have, barX - 8 - this.font.width(have), y + 7, 0xFF404040, false);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, 8, 7, 0xFF404040, false);
    }
}
