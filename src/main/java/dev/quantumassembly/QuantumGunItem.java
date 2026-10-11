package dev.quantumassembly;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Quantum Gun. It is linked to a Pocket Controls (press "Link a Quantum Gun" there, then right-click with the gun).
 * After that, right-click a block to open a gate in front of you, and right-click again (anywhere, or on the gate) to
 * close it. You walk through the gate to enter the pocket. The gun remembers its link in the item's custom data, so
 * no extra data component has to be registered.
 */
public class QuantumGunItem extends Item {
    private static final String LINK_DIM = "qa_link_dim";
    private static final String LINK_POS = "qa_link_pos";
    private static final String GATE_DIM = "qa_gate_dim";
    private static final String GATE_POS = "qa_gate_pos";

    public QuantumGunItem(Properties properties) {
        super(properties);
    }

    private static CompoundTag read(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    private static boolean isLinked(CompoundTag tag) {
        return tag.contains(LINK_DIM) && tag.contains(LINK_POS);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isLinked(read(stack));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide && context.getPlayer() instanceof ServerPlayer player && level instanceof ServerLevel serverLevel) {
            handle(player, context.getItemInHand(), serverLevel, context);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && level instanceof ServerLevel serverLevel) {
            handle(serverPlayer, stack, serverLevel, null);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /** One click: link (if a link is armed), close the open gate, or open a new one. */
    private void handle(ServerPlayer player, ItemStack stack, ServerLevel level, UseOnContext context) {
        if (level.dimension().equals(PocketManager.POCKET)) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gun_not_here"), true);
            return;
        }
        player.getCooldowns().addCooldown(this, 8);

        // 1. "Link a Quantum Gun" was pressed on the Pocket Controls: this click links the gun.
        PocketManager.Pending pending = PocketManager.takePending(player);
        if (pending != null) {
            CompoundTag tag = read(stack);
            tag.putString(LINK_DIM, pending.dim());
            tag.putLong(LINK_POS, pending.pos().asLong());
            tag.remove(GATE_DIM);
            tag.remove(GATE_POS);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gun_linked"), true);
            level.playSound(null, player.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.PLAYERS, 0.8F, 1.2F);
            return;
        }

        CompoundTag tag = read(stack);
        if (!isLinked(tag)) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gun_unlinked"), true);
            return;
        }

        // 2. Clicking a gate closes it.
        if (context != null && level.getBlockState(context.getClickedPos()).is(ModBlocks.QUANTUM_GATE.get())) {
            close(level, context.getClickedPos());
            forgetGate(stack, tag);
            return;
        }

        // 3. A gate from this gun is still open: close it.
        if (tag.contains(GATE_DIM) && tag.contains(GATE_POS)) {
            ServerLevel gateLevel = player.getServer().getLevel(
                    ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(GATE_DIM))));
            BlockPos gatePos = BlockPos.of(tag.getLong(GATE_POS));
            boolean closed = false;
            if (gateLevel != null && gateLevel.getBlockState(gatePos).is(ModBlocks.QUANTUM_GATE.get())) {
                close(gateLevel, gatePos);
                closed = true;
            }
            forgetGate(stack, tag);
            if (closed) {
                return;
            }
        }

        // 4. Open a new gate where you point.
        if (context == null) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gun_aim"), true);
            return;
        }
        BlockPlaceContext place = new BlockPlaceContext(context);
        BlockPos pos = place.getClickedPos();
        if (!place.canPlace() || pos.getY() + 1 >= level.getMaxBuildHeight() || !level.getBlockState(pos.above()).canBeReplaced()) {
            player.displayClientMessage(Component.translatable("message.quantum_assembly.gun_no_room"), true);
            return;
        }
        BlockState state = ModBlocks.QUANTUM_GATE.get().getStateForPlacement(place);
        if (state == null) {
            state = ModBlocks.QUANTUM_GATE.get().defaultBlockState()
                    .setValue(HorizontalDirectionalBlock.FACING, player.getDirection().getOpposite());
        }
        level.setBlock(pos, state, Block.UPDATE_ALL);
        if (level.getBlockEntity(pos) instanceof GateBlockEntity gate) {
            gate.link(tag.getString(LINK_DIM), BlockPos.of(tag.getLong(LINK_POS)));
        }
        tag.putString(GATE_DIM, level.dimension().location().toString());
        tag.putLong(GATE_POS, pos.asLong());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.7F, 1.5F);
        level.sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 30, 0.3, 0.8, 0.3, 0.2);
    }

    private static void close(ServerLevel level, BlockPos pos) {
        level.removeBlock(pos, false);
        level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 0.8F, 1.4F);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 30, 0.3, 0.8, 0.3, 0.1);
    }

    private static void forgetGate(ItemStack stack, CompoundTag tag) {
        tag.remove(GATE_DIM);
        tag.remove(GATE_POS);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = read(stack);
        tooltip.add(Component.translatable("tooltip.quantum_assembly.gun_use").withStyle(ChatFormatting.GRAY));
        if (isLinked(tag)) {
            tooltip.add(Component.translatable("tooltip.quantum_assembly.gun_linked",
                    BlockPos.of(tag.getLong(LINK_POS)).toShortString()).withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable("tooltip.quantum_assembly.gun_unlinked").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
