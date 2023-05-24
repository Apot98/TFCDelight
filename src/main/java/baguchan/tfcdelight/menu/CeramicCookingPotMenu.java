package baguchan.tfcdelight.menu;

import baguchan.tfcdelight.blockentity.CeramicCookingPotBlockEntity;
import baguchan.tfcdelight.registry.ModBlocks;
import baguchan.tfcdelight.registry.ModMenus;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMealSlot;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

import java.util.Objects;

public class CeramicCookingPotMenu extends RecipeBookMenu<RecipeWrapper> {
    public static final ResourceLocation EMPTY_CONTAINER_SLOT_BOWL = new ResourceLocation("farmersdelight", "item/empty_container_slot_bowl");
    public final CeramicCookingPotBlockEntity tileEntity;
    public final ItemStackHandler inventory;
    private final ContainerData cookingPotData;
    private final ContainerLevelAccess canInteractWithCallable;
    protected final Level level;

    public CeramicCookingPotMenu(int windowId, Inventory playerInventory, CeramicCookingPotBlockEntity tileEntity, ContainerData cookingPotDataIn) {
        super((MenuType) ModMenus.CERAMIC_COOKING_POT.get(), windowId);
        this.tileEntity = tileEntity;
        this.inventory = tileEntity.getInventory();
        this.cookingPotData = cookingPotDataIn;
        this.level = playerInventory.player.level;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());
        int startX = 8;
        int startY = 18;
        int inputStartX = 30;
        int inputStartY = 17;
        int borderSlotSize = 18;

        int startPlayerInvY;
        int column;
        for (startPlayerInvY = 0; startPlayerInvY < 2; ++startPlayerInvY) {
            for (column = 0; column < 3; ++column) {
                this.addSlot(new SlotItemHandler(this.inventory, startPlayerInvY * 3 + column, inputStartX + column * borderSlotSize, inputStartY + startPlayerInvY * borderSlotSize));
            }
        }

        this.addSlot(new CookingPotMealSlot(this.inventory, 6, 124, 26));
        this.addSlot(new SlotItemHandler(this.inventory, 7, 92, 55) {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, CookingPotMenu.EMPTY_CONTAINER_SLOT_BOWL);
            }
        });
        this.addSlot(new CeramicCookingPotResultSlot(playerInventory.player, tileEntity, this.inventory, 8, 124, 55));
        startPlayerInvY = startY * 4 + 12;

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, startX + j * 18, startPlayerInvY + i * 18));
            }
        }

        for (column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, startX + column * borderSlotSize, 142));
        }

        this.addDataSlots(cookingPotDataIn);
    }

    private static CeramicCookingPotBlockEntity getTileEntity(Inventory playerInventory, FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CeramicCookingPotBlockEntity) {
            return (CeramicCookingPotBlockEntity) tileAtPos;
        } else {
            throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
        }
    }

    public CeramicCookingPotMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(4));
    }

    public boolean stillValid(Player playerIn) {
        return stillValid(this.canInteractWithCallable, playerIn, (Block) ModBlocks.CERAMIC_POT.get());
    }

    public ItemStack quickMoveStack(Player playerIn, int index) {
        int indexMealDisplay = 6;
        int indexContainerInput = 7;
        int indexOutput = 8;
        int startPlayerInv = indexOutput + 1;
        int endPlayerInv = startPlayerInv + 36;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == indexOutput) {
                if (!this.moveItemStackTo(itemstack1, startPlayerInv, endPlayerInv, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index > indexOutput) {
                if (itemstack1.getItem() == Items.BOWL && !this.moveItemStackTo(itemstack1, indexContainerInput, indexContainerInput + 1, false)) {
                    return ItemStack.EMPTY;
                }

                if (!this.moveItemStackTo(itemstack1, 0, indexMealDisplay, false)) {
                    return ItemStack.EMPTY;
                }

                if (!this.moveItemStackTo(itemstack1, indexContainerInput, indexOutput, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, startPlayerInv, endPlayerInv, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @OnlyIn(Dist.CLIENT)
    public int getCookProgressionScaled() {
        int i = this.cookingPotData.get(0);
        int j = this.cookingPotData.get(1);
        return j != 0 && i != 0 ? i * 24 / j : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isHeated() {
        return this.tileEntity.isHeated();
    }

    public void fillCraftSlotsStackedContents(StackedContents helper) {
        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            helper.accountSimpleStack(this.inventory.getStackInSlot(i));
        }

    }

    public void clearCraftingContent() {
        for (int i = 0; i < 6; ++i) {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

    }

    public boolean recipeMatches(Recipe<? super RecipeWrapper> recipe) {
        return recipe.matches(new RecipeWrapper(this.inventory), this.level);
    }

    public int getResultSlotIndex() {
        return 7;
    }

    public int getGridWidth() {
        return 3;
    }

    public int getGridHeight() {
        return 2;
    }

    public int getSize() {
        return 7;
    }

    public RecipeBookType getRecipeBookType() {
        return FarmersDelight.RECIPE_TYPE_COOKING;
    }

    public boolean shouldMoveToInventory(int slot) {
        return slot < this.getGridWidth() * this.getGridHeight();
    }
}
