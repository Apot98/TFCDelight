package baguchan.tfcdelight.registry;

import baguchan.tfcdelight.TFCDelight;
import net.dries007.tfc.common.TFCItemGroup;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.FarmersDelight;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TFCDelight.MODID);
    public static final DeferredRegister<Item> DELIGHT_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FarmersDelight.MODID);

    public static final RegistryObject<Item> SPOON = ITEMS.register("spoon", () -> new Item(new Item.Properties().tab(TFCItemGroup.MISC)));
    public static final RegistryObject<Item> WILD_BURGER_SLICE = ITEMS.register("wild_burger_slice", () -> new Item(new Item.Properties().tab(FarmersDelight.CREATIVE_TAB)));
}
