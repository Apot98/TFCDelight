package baguchan.tfcdelight.block;

import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RiceRollMedleyTFCBlock extends FeastTFCBlock {

    private ItemStack currentServing;

    public static final IntegerProperty ROLL_SERVINGS = IntegerProperty.create("servings", 0, 8);

    protected static final VoxelShape PLATE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D);
    protected static final VoxelShape FOOD_SHAPE = Shapes.joinUnoptimized(PLATE_SHAPE, Block.box(2.0D, 2.0D, 2.0D, 14.0D, 4.0D, 14.0D), BooleanOp.OR);

    public final List<Supplier<Item>> riceRollServings = Arrays.asList(
            vectorwing.farmersdelight.common.registry.ModItems.COD_ROLL,
            vectorwing.farmersdelight.common.registry.ModItems.COD_ROLL,
            vectorwing.farmersdelight.common.registry.ModItems.SALMON_ROLL,
            vectorwing.farmersdelight.common.registry.ModItems.SALMON_ROLL,
            vectorwing.farmersdelight.common.registry.ModItems.SALMON_ROLL,
            vectorwing.farmersdelight.common.registry.ModItems.KELP_ROLL_SLICE,
            vectorwing.farmersdelight.common.registry.ModItems.KELP_ROLL_SLICE,
            vectorwing.farmersdelight.common.registry.ModItems.KELP_ROLL_SLICE
    );

    public RiceRollMedleyTFCBlock(ExtendedProperties properties) {
        super(properties, vectorwing.farmersdelight.common.registry.ModItems.KELP_ROLL_SLICE, true);
    }

    @Override
    public IntegerProperty getServingsProperty() {
        return ROLL_SERVINGS;
    }

    @Override
    public int getMaxServings() {
        return 8;
    }

    @Override
    public ItemStack getServingItem(BlockState state) {
        return new ItemStack(riceRollServings.get(state.getValue(getServingsProperty()) - 1).get());
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

                            int newServings = (Integer) level.getBlockState(pos).getValue(this.getServingsProperty());
                            if (newServings > 0) {
                                decaying.setStack(FoodCapability.updateFoodFromPrevious(decaying.getStack(), new ItemStack(riceRollServings.get(newServings - 1).get())));
                            } else {
                                decaying.setStack(FoodCapability.setStackNonDecaying(decaying.getStack()));
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

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(getServingsProperty()) == 0 ? PLATE_SHAPE : FOOD_SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROLL_SERVINGS);
    }
}