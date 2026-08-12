package com.chemiofitor.create.rationality.common.recipe.crafting;

import com.chemiofitor.create.rationality.common.item.EnchantingPaperItem;
import com.chemiofitor.create.rationality.index.CRItems;
import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EnchantPaperBindingRecipe extends CustomRecipe {
    public EnchantPaperBindingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasEnchant = false;
        int papers = 0;
        int leathers = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty())
                continue;
            if (stack.getItem() instanceof EnchantingPaperItem) {
                papers++;
                if (stack.has(DataComponents.ENCHANTMENTS)){
                    hasEnchant = true;
                }
            } else if (stack.is(Items.LEATHER)) {
                leathers++;
            } else {
                return false;
            }
        }
        return papers >= 3 && leathers == 1 && hasEnchant;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        int maxRepairCost = 0;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!(stack.getItem() instanceof EnchantingPaperItem))
                continue;

            maxRepairCost = Math.max(maxRepairCost, stack.getOrDefault(DataComponents.REPAIR_COST, 0));

            ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                int level = entry.getIntValue() + merged.getLevel(holder);
                level = Math.min(holder.value().getMaxLevel(), level);
                merged.set(holder, level);
            }
        }

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        book.set(DataComponents.STORED_ENCHANTMENTS, merged.toImmutable());
        book.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        // 绑定 = 一次合并操作：书的累积惩罚按原版铁砧公式 = max(纸惩罚)×2+1（全 0 纸 → 1）
        book.set(DataComponents.REPAIR_COST, AnvilMenu.calculateIncreasedRepairCost(maxRepairCost));
        return book;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public @Nullable RecipeSerializer<?> getSerializer() {
        return CRRecipeTypes.ENCHANT_PAPER_BINDING.getSerializer();
    }
}
