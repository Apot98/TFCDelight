package baguchan.tfcdelight.block;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

public class WildBurgerBlock extends FeastTFCBlock {
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.box(2, 0, 2, 8, 12, 14), Block.box(2, 0, 2, 8, 12, 14), Block.box(2, 0, 2, 14, 12, 14)};

    public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 2);

    public WildBurgerBlock(ExtendedProperties properties, Supplier<Item> servingItem, boolean hasLeftovers) {
        super(properties, servingItem, hasLeftovers);
        this.registerDefaultState((BlockState) ((BlockState) ((BlockState) this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(this.getServingsProperty(), this.getMaxServings()));

    }


    public IntegerProperty getServingsProperty() {
        return SERVINGS;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[(Integer) state.getValue(SERVINGS)];
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, SERVINGS});
    }

    public int getMaxServings() {
        return 2;
    }
}
