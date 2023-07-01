package baguchan.tfcdelight;

import baguchan.tfcdelight.registry.ModBlockEntities;
import baguchan.tfcdelight.registry.ModBlocks;
import baguchan.tfcdelight.registry.ModItems;
import baguchan.tfcdelight.registry.ModMenus;
import baguchan.tfcdelight.screen.CeramicCookingPotScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TFCDelight.MODID)
public class TFCDelight
{
    public static final String MODID = "tfcdelight";

    public TFCDelight() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModBlocks.DELIGHT_BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.DELIGHT_ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENU_TYPES.register(modBus);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MenuScreens.register(ModMenus.CERAMIC_COOKING_POT.get(), CeramicCookingPotScreen::new);
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CERAMIC_POT.get(), RenderType.cutout());
    }
}
