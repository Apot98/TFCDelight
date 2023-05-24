package baguchan.tfcdelight.block;

import baguchan.tfcdelight.blockentity.CeramicCookingPotBlockEntity;
import baguchan.tfcdelight.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;
import vectorwing.farmersdelight.common.registry.ModSounds;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.MathUtils;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/*
 * https://github.com/vectorwing/FarmersDelight/blob/1.18.2/src/main/java/vectorwing/farmersdelight/common/block/CookingPotBlock.java
 * thanks vectorwing
 */
public class CeramicCookingPotBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final EnumProperty<CookingPotSupport> SUPPORT;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE;
    protected static final VoxelShape SHAPE_WITH_TRAY;

    public CeramicCookingPotBlock() {
        super(Properties.of(Material.CLAY).strength(0.3F, 5.0F).sound(SoundType.DEEPSLATE_BRICKS));
        this.registerDefaultState((BlockState) ((BlockState) ((BlockState) ((BlockState) this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(SUPPORT, CookingPotSupport.NONE)).setValue(WATERLOGGED, false));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.isEmpty() && player.isShiftKeyDown()) {
            level.setBlockAndUpdate(pos, (BlockState) state.setValue(SUPPORT, ((CookingPotSupport) state.getValue(SUPPORT)).equals(CookingPotSupport.HANDLE) ? this.getTrayState(level, pos) : CookingPotSupport.HANDLE));
            level.playSound((Player) null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
        } else if (!level.isClientSide) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof CeramicCookingPotBlockEntity) {
                CeramicCookingPotBlockEntity cookingPotEntity = (CeramicCookingPotBlockEntity) tileEntity;
                ItemStack servingStack = cookingPotEntity.useHeldItemOnMeal(heldStack);
                if (servingStack != ItemStack.EMPTY) {
                    if (!player.getInventory().add(servingStack)) {
                        player.drop(servingStack, false);
                    }

                    level.playSound((Player) null, pos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                } else {
                    NetworkHooks.openGui((ServerPlayer) player, cookingPotEntity, pos);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return ((CookingPotSupport) state.getValue(SUPPORT)).equals(CookingPotSupport.TRAY) ? SHAPE_WITH_TRAY : SHAPE;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        FluidState fluid = level.getFluidState(context.getClickedPos());
        BlockState state = (BlockState) ((BlockState) this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
        return context.getClickedFace().equals(Direction.DOWN) ? (BlockState) state.setValue(SUPPORT, CookingPotSupport.HANDLE) : (BlockState) state.setValue(SUPPORT, this.getTrayState(level, pos));
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if ((Boolean) state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return facing.getAxis().equals(Direction.Axis.Y) && !((CookingPotSupport) state.getValue(SUPPORT)).equals(CookingPotSupport.HANDLE) ? (BlockState) state.setValue(SUPPORT, this.getTrayState(level, currentPos)) : state;
    }

    private CookingPotSupport getTrayState(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(ModTags.TRAY_HEAT_SOURCES) ? CookingPotSupport.TRAY : CookingPotSupport.NONE;
    }

    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        CeramicCookingPotBlockEntity cookingPotEntity = (CeramicCookingPotBlockEntity) level.getBlockEntity(pos);
        if (cookingPotEntity != null) {
            CompoundTag nbt = cookingPotEntity.writeMeal(new CompoundTag());
            if (!nbt.isEmpty()) {
                stack.addTagElement("BlockEntityTag", nbt);
            }

            if (cookingPotEntity.hasCustomName()) {
                stack.setHoverName(cookingPotEntity.getCustomName());
            }
        }

        return stack;
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof CeramicCookingPotBlockEntity) {
                CeramicCookingPotBlockEntity cookingPotEntity = (CeramicCookingPotBlockEntity) tileEntity;
                Containers.dropContents(level, pos, cookingPotEntity.getDroppableInventory());
                cookingPotEntity.getUsedRecipesAndPopExperience(level, Vec3.atCenterOf(pos));
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        ItemStack mealStack = CeramicCookingPotBlockEntity.getMealFromItem(stack);
        MutableComponent textServingsOf;
        if (!mealStack.isEmpty()) {
            textServingsOf = mealStack.getCount() == 1 ? TextUtils.getTranslation("tooltip.cooking_pot.single_serving", new Object[0]) : TextUtils.getTranslation("tooltip.cooking_pot.many_servings", new Object[]{mealStack.getCount()});
            tooltip.add(textServingsOf.withStyle(ChatFormatting.GRAY));
            MutableComponent textMealName = mealStack.getHoverName().copy();
            tooltip.add(textMealName.withStyle(mealStack.getRarity().color));
        } else {
            textServingsOf = TextUtils.getTranslation("tooltip.cooking_pot.empty", new Object[0]);
            tooltip.add(textServingsOf.withStyle(ChatFormatting.GRAY));
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{FACING, SUPPORT, WATERLOGGED});
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof CeramicCookingPotBlockEntity) {
                ((CeramicCookingPotBlockEntity) tileEntity).setCustomName(stack.getHoverName());
            }
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof CeramicCookingPotBlockEntity cookingPotEntity) {
            if (cookingPotEntity.isHeated()) {
                SoundEvent boilSound = !cookingPotEntity.getMeal().isEmpty() ? (SoundEvent) ModSounds.BLOCK_COOKING_POT_BOIL_SOUP.get() : (SoundEvent) ModSounds.BLOCK_COOKING_POT_BOIL.get();
                double x = (double) pos.getX() + 0.5;
                double y = (double) pos.getY();
                double z = (double) pos.getZ() + 0.5;
                if (rand.nextInt(10) == 0) {
                    level.playLocalSound(x, y, z, boilSound, SoundSource.BLOCKS, 0.5F, rand.nextFloat() * 0.2F + 0.9F, false);
                }
            }
        }

    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof CeramicCookingPotBlockEntity) {
            ItemStackHandler inventory = ((CeramicCookingPotBlockEntity) tileEntity).getInventory();
            return MathUtils.calcRedstoneFromItemHandler(inventory);
        } else {
            return 0;
        }
    }

    public FluidState getFluidState(BlockState state) {
        return (Boolean) state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ((BlockEntityType) ModBlockEntities.CERAMIC_COOKING_POT.get()).create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntity) {
        return level.isClientSide ? createTickerHelper(blockEntity, (BlockEntityType) ModBlockEntities.CERAMIC_COOKING_POT.get(), CeramicCookingPotBlockEntity::animationTick) : createTickerHelper(blockEntity, (BlockEntityType) ModBlockEntities.CERAMIC_COOKING_POT.get(), CeramicCookingPotBlockEntity::cookingTick);
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        SUPPORT = EnumProperty.create("support", CookingPotSupport.class);
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);
        SHAPE_WITH_TRAY = Shapes.or(SHAPE, Block.box(0.0, -1.0, 0.0, 16.0, 0.0, 16.0));
    }
}
