package baguchan.tfcdelight.block;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.Supplier;

public class WildBurgerBlock extends FeastTFCBlock {
    public static final IntegerProperty SERVINGS = IntegerProperty.create("servings", 0, 1);

    public WildBurgerBlock(ExtendedProperties properties, Supplier<Item> servingItem, boolean hasLeftovers) {
        super(properties, servingItem, hasLeftovers);
    }

    public IntegerProperty getServingsProperty() {
        return SERVINGS;
    }

    public int getMaxServings() {
        return 2;
    }
}
