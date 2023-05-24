package baguchan.tfcdelight.registry;

import baguchan.tfcdelight.TFCDelight;
import baguchan.tfcdelight.blockentity.CeramicCookingPotBlockEntity;
import com.mojang.datafixers.types.Type;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, TFCDelight.MODID);
    public static final RegistryObject<BlockEntityType<CeramicCookingPotBlockEntity>> CERAMIC_COOKING_POT = BLOCK_ENTITIES.register("ceramic_cooking_pot", () -> register("tfcdelight:ceramic_cooking_pot", BlockEntityType.Builder.of(CeramicCookingPotBlockEntity::new, ModBlocks.CERAMIC_POT.get())));

    private static <T extends BlockEntity> BlockEntityType<T> register(String p_200966_0_, BlockEntityType.Builder<T> p_200966_1_) {
        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, p_200966_0_);
        return p_200966_1_.build(type);
    }
}
