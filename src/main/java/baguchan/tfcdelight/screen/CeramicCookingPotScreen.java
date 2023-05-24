package baguchan.tfcdelight.screen;

import baguchan.tfcdelight.blockentity.CeramicCookingPotBlockEntity;
import baguchan.tfcdelight.menu.CeramicCookingPotMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import vectorwing.farmersdelight.client.gui.CookingPotRecipeBookComponent;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CeramicCookingPotScreen extends AbstractContainerScreen<CeramicCookingPotMenu> implements RecipeUpdateListener {
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("farmersdelight", "textures/gui/cooking_pot.png");
    private static final Rectangle HEAT_ICON = new Rectangle(47, 55, 17, 15);
    private static final Rectangle PROGRESS_ARROW = new Rectangle(89, 25, 0, 17);
    private final CookingPotRecipeBookComponent recipeBookComponent = new CookingPotRecipeBookComponent();
    private boolean widthTooNarrow;

    public CeramicCookingPotScreen(CeramicCookingPotMenu screenContainer, Inventory inv, net.minecraft.network.chat.Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.titleLabelX = 28;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu) this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        if ((Boolean) Configuration.ENABLE_RECIPE_BOOK_COOKING_POT.get()) {
            this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) -> {
                this.recipeBookComponent.toggleVisibility();
                this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                ((ImageButton) button).setPosition(this.leftPos + 5, this.height / 2 - 49);
            }));
        } else {
            this.recipeBookComponent.hide();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        }

        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
    }

    protected void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(ms, partialTicks, mouseX, mouseY);
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
            super.render(ms, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.renderGhostRecipe(ms, this.leftPos, this.topPos, false, partialTicks);
        }

        this.renderMealDisplayTooltip(ms, mouseX, mouseY);
        this.renderHeatIndicatorTooltip(ms, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(ms, this.leftPos, this.topPos, mouseX, mouseY);
    }

    private void renderHeatIndicatorTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.isHovering(HEAT_ICON.x, HEAT_ICON.y, HEAT_ICON.width, HEAT_ICON.height, (double) mouseX, (double) mouseY)) {
            java.util.List<net.minecraft.network.chat.Component> tooltip = new ArrayList();
            String key = "container.cooking_pot." + (((CeramicCookingPotMenu) this.menu).isHeated() ? "heated" : "not_heated");
            tooltip.add(TextUtils.getTranslation(key, new Object[]{this.menu}));
            this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
        }

    }

    protected void renderMealDisplayTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null && ((CeramicCookingPotMenu) this.menu).getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 6) {
                List<Component> tooltip = new ArrayList();
                ItemStack mealStack = this.hoveredSlot.getItem();
                tooltip.add(((MutableComponent) mealStack.getItem().getDescription()).withStyle(mealStack.getRarity().color));
                ItemStack containerStack = ((CeramicCookingPotMenu) this.menu).tileEntity.getContainer();
                String container = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";
                tooltip.add(TextUtils.getTranslation("container.cooking_pot.served_on", new Object[]{container}).withStyle(ChatFormatting.GRAY));
                this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
            } else {
                this.renderTooltip(ms, this.hoveredSlot.getItem(), mouseX, mouseY);
            }
        }

    }

    protected void renderLabels(PoseStack ms, int mouseX, int mouseY) {
        super.renderLabels(ms, mouseX, mouseY);
        this.font.draw(ms, this.playerInventoryTitle, 8.0F, (float) (this.imageHeight - 96 + 2), 4210752);
    }

    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.minecraft != null) {
            RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
            this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
            if (((CeramicCookingPotMenu) this.menu).isHeated()) {
                this.blit(ms, this.leftPos + HEAT_ICON.x, this.topPos + HEAT_ICON.y, 176, 0, HEAT_ICON.width, HEAT_ICON.height);
            }

            int l = ((CeramicCookingPotMenu) this.menu).getCookProgressionScaled();
            this.blit(ms, this.leftPos + PROGRESS_ARROW.x, this.topPos + PROGRESS_ARROW.y, 176, 15, l + 1, PROGRESS_ARROW.height);
        }
    }

    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, buttonId)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, buttonId);
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int buttonIdx) {
        boolean flag = mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + this.imageWidth) || mouseY >= (double) (y + this.imageHeight);
        return flag && this.recipeBookComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, buttonIdx);
    }

    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
        super.slotClicked(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    public void removed() {
        this.recipeBookComponent.removed();
        super.removed();
    }

    @Nonnull
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}
