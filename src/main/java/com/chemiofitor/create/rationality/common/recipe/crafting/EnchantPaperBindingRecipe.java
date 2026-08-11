package com.chemiofitor.create.rationality.common.recipe.crafting;

import com.chemiofitor.create.rationality.index.CRItems;
import com.chemiofitor.create.rationality.index.CRRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * 附魔纸绑定配方：≥3 张附魔纸 + 1 皮革 → 附魔书。
 * <p>
 * 附魔书继承所有附魔纸的附魔总和（铁砧式合并）：
 * - 同名附魔等级相加，不超过 {@link Enchantment#getMaxLevel()}
 * - 互斥附魔（exclusiveSet）按输入顺序保留先出现的，跳过冲突项
 * <p>
 * 动态输出配方，CustomRecipe 的 isSpecial 默认 true，JEI 不展示具体输出。
 */
public class EnchantPaperBindingRecipe extends CustomRecipe {
    public EnchantPaperBindingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int papers = 0;
        int leathers = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty())
                continue;
            if (stack.is(CRItems.ENCHANTED_PAPER.get())) {
                papers++;
            } else if (stack.is(Items.LEATHER)) {
                leathers++;
            } else {
                return false;
            }
        }
        return papers >= 3 && leathers == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
        ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.is(CRItems.ENCHANTED_PAPER.get()))
                continue;

            ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                int level = entry.getIntValue();

                // 互斥附魔按输入顺序保留先出现的，跳过冲突项
                boolean conflicts = merged.keySet().stream()
                        .anyMatch(e -> e.value().exclusiveSet().contains(holder));
                if (conflicts)
                    continue;

                int existing = merged.getLevel(holder);
                int target = existing == 0
                        ? level
                        : Math.min(existing + level, holder.value().getMaxLevel());
                merged.set(holder, target);
            }
        }

        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        book.set(DataComponents.ENCHANTMENTS, merged.toImmutable());
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
