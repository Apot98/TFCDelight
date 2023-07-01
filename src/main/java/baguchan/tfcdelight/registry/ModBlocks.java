package baguchan.tfcdelight.registry;

import baguchan.tfcdelight.TFCDelight;
import baguchan.tfcdelight.block.CeramicCookingPotBlock;
import baguchan.tfcdelight.block.FeastTFCBlock;
import baguchan.tfcdelight.block.RoastChickenTFCBlock;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TFCDelight.MODID);
    public static final DeferredRegister<Block> DELIGHT_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FarmersDelight.MODID);

    public static final RegistryObject<Block> CERAMIC_POT = register("ceramic_cooking_pot", () -> new CeramicCookingPotBlock());
    public static final RegistryObject<Block> ROAST_CHICKEN = noItemDelightRegister("roast_chicken_block", () -> new RoastChickenTFCBlock(ExtendedProperties.of(Blocks.WHITE_WOOL).noDrops().randomTicks().blockEntity(TFCBlockEntities.DECAYING).serverTicks(DecayingBlockEntity::serverTick), vectorwing.farmersdelight.common.registry.ModItems.ROAST_CHICKEN, true));
    public static final RegistryObject<Block> STUFFED_PUMPKIN_BLOCK = noItemDelightRegister("stuffed_pumpkin_block", () -> new FeastTFCBlock(ExtendedProperties.of(Blocks.PUMPKIN).noDrops().randomTicks().blockEntity(TFCBlockEntities.DECAYING).serverTicks(DecayingBlockEntity::serverTick), vectorwing.farmersdelight.common.registry.ModItems.STUFFED_PUMPKIN_BLOCK, false));
    public static final RegistryObject<Block> SHEPHERDS_PIE_BLOCK = noItemDelightRegister("shepherds_pie_block", () -> new FeastTFCBlock(ExtendedProperties.of(Blocks.CAKE).noDrops().randomTicks().blockEntity(TFCBlockEntities.DECAYING).serverTicks(DecayingBlockEntity::serverTick), vectorwing.farmersdelight.common.registry.ModItems.SHEPHERDS_PIE_BLOCK, true));

    private static <T extends Block> RegistryObject<T> baseRegister(String name, Supplier<? extends T> block, Function<RegistryObject<T>, Supplier<? extends Item>> item) {
        RegistryObject<T> register = BLOCKS.register(name, block);
        Supplier<? extends Item> itemSupplier = item.apply(register);
        ModItems.ITEMS.register(name, itemSupplier);
        return register;
    }

    private static <B extends Block> RegistryObject<B> registerDelight(String name, Supplier<? extends Block> block) {
        return (RegistryObject<B>) baseDelightRegister(name, block, (object) -> ModBlocks.registerBlockItem(object));
    }

    private static <T extends Block> RegistryObject<T> baseDelightRegister(String name, Supplier<? extends T> block, Function<RegistryObject<T>, Supplier<? extends Item>> item) {
        RegistryObject<T> register = DELIGHT_BLOCKS.register(name, block);
        Supplier<? extends Item> itemSupplier = item.apply(register);
        ModItems.DELIGHT_ITEMS.register(name, itemSupplier);
        return register;
    }

    private static <T extends Block> RegistryObject<T> noItemDelightRegister(String name, Supplier<? extends T> block) {
        RegistryObject<T> register = DELIGHT_BLOCKS.register(name, block);
        return register;
    }

    private static <T extends Block> RegistryObject<T> noItemRegister(String name, Supplier<? extends T> block) {
        RegistryObject<T> register = BLOCKS.register(name, block);
        return register;
    }

    private static <B extends Block> RegistryObject<B> register(String name, Supplier<? extends Block> block) {
        return (RegistryObject<B>) baseRegister(name, block, (object) -> ModBlocks.registerBlockItem(object));
    }

    private static <T extends Block> Supplier<BlockItem> registerBlockItem(final RegistryObject<T> block) {
        return () -> {
            return new BlockItem(Objects.requireNonNull(block.get()), new Item.Properties());
        };
    }
}
