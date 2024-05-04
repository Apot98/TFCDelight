package baguchan.tfcdelight.block;

import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.Random;
import java.util.function.Supplier;

public class FeastTFCBlock extends FeastBlock implements EntityBlockExtension {
    private final ExtendedProperties extendedProperties;

    public FeastTFCBlock(ExtendedProperties properties, Supplier<Item> servingItem, boolean hasLeftovers) {
        super(properties.properties(), servingItem, hasLeftovers);
        this.extendedProperties = properties;
    }

    @Override
    protected InteractionResult takeServing(LevelAccessor level, BlockPos pos, BlockState state, Player player, InteractionHand hand) {
        int servings = (Integer) state.getValue(this.getServingsProperty());
        if (servings == 0) {
            level.playSound((Player) null, pos, SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 0.8F, 0.8F);
            level.destroyBlock(pos, true);
            return InteractionResult.SUCCESS;
        }
        else {
            if (!isRotten(level, pos)) {
                ItemStack heldStack = player.getItemInHand(hand);
                BlockEntity var7 = level.getBlockEntity(pos);
                if (var7 instanceof DecayingBlockEntity decaying) {
                    ItemStack serving = decaying.getStack().copy();
                    if (servings > 0) {
                        if (!serving.hasContainerItem() || heldStack.sameItem(serving.getContainerItem())) {
                            level.setBlock(pos, (BlockState) state.setValue(this.getServingsProperty(), servings - 1), 3);
                            if (!player.getAbilities().instabuild && serving.hasContainerItem()) {
                                heldStack.shrink(1);
                            }

                            if (!player.getInventory().add(serving)) {
                                player.drop(serving, false);
                            }

                            if ((Integer) level.getBlockState(pos).getValue(this.getServingsProperty()) == 0 && !this.hasLeftovers) {
                                level.removeBlock(pos, false);
                            }

                            level.playSound((Player) null, pos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                            return InteractionResult.SUCCESS;
                        }

                        player.displayClientMessage(TextUtils.getTranslation("block.feast.use_container", new Object[]{serving.getContainerItem().getHoverName()}), true);
                    }
                }

                return InteractionResult.PASS;
            }
            else {
                player.displayClientMessage(TextUtils.getTranslation("block.feast.rotten", new Object[]{level.getBlockState(pos).getBlock().asItem().getDefaultInstance().getHoverName()}), true);
                return InteractionResult.PASS;
            }
        }
    }

    public static boolean isRotten(LevelAccessor level, BlockPos pos) {
        BlockEntity var3 = level.getBlockEntity(pos);
        boolean var10000;
        if (var3 instanceof DecayingBlockEntity decaying) {
            if (decaying.isRotten()) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    @Override
    public void animateTick(BlockState p_49888_, Level p_49889_, BlockPos p_49890_, Random p_49891_) {
        super.animateTick(p_49888_, p_49889_, p_49890_, p_49891_);
        if (isRotten(p_49889_, p_49890_)) {
            p_49889_.addParticle(ParticleTypes.MYCELIUM, (double) p_49890_.getX() + p_49891_.nextDouble(), (double) p_49890_.getY() + p_49891_.nextDouble(), (double) p_49890_.getZ() + p_49891_.nextDouble(), 0.0D, 0.0D, 0.0D);
        }
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        BlockEntity var7 = level.getBlockEntity(pos);
        if (var7 instanceof DecayingBlockEntity decaying) {
            //decaying.setStack(getServingItem(state));
            decaying.setStack(this.getServingItem(state));
            decaying.setStack(FoodCapability.updateFoodFromPrevious(stack, decaying.getStack()));
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if (level.getBlockEntity(pos) instanceof DecayingBlockEntity decaying && player.isCreative())
        {
            decaying.setStack(ItemStack.EMPTY);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!Helpers.isBlock(state, newState.getBlock())) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof DecayingBlockEntity decaying) {
                int servings = (Integer) state.getValue(this.getServingsProperty());
                if (servings == this.getMaxServings() && !decaying.getStack().isEmpty()) {
                    decaying.setStack(FoodCapability.updateFoodFromPrevious(decaying.getStack(), new ItemStack(this)));
                    Helpers.spawnItem(level, pos, decaying.getStack());
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return (BlockState) context.getItemInHand().getCapability(FoodCapability.CAPABILITY).map((cap) -> {
            return cap.isRotten() ? this.getRottedBlock().defaultBlockState() : this.defaultBlockState();
        }).orElse(this.defaultBlockState());
    }

    public Block getRottedBlock() {
        return this;
    }

    @Override
    public ExtendedProperties getExtendedProperties() {
        return this.extendedProperties;
    }
}
