package baguchan.tfcdelight.registry;

import baguchan.tfcdelight.TFCDelight;
import baguchan.tfcdelight.menu.CeramicCookingPotMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, TFCDelight.MODID);
    public static final RegistryObject<MenuType<CeramicCookingPotMenu>> CERAMIC_COOKING_POT = MENU_TYPES.register("ceramic_cooking_pot", () -> {
        return IForgeMenuType.create(CeramicCookingPotMenu::new);
    });
}
