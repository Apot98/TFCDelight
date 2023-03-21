package baguchan.tfcdelight.mixin;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "fillItemCategory", at = @At("HEAD"), cancellable = true)
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items, CallbackInfo callbackInfo) {
        Item item = (Item) ((Object) this);
        if (this.allowdedIn(category) && FoodCapability.get(new ItemStack(item)) != null) {
            items.add(FoodCapability.setStackNonDecaying(new ItemStack(item)));
            callbackInfo.cancel();
        }

    }

    @Shadow
    protected boolean allowdedIn(CreativeModeTab p_41390_) {
        return false;
    }
}
