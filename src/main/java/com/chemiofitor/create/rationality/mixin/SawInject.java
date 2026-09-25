package com.chemiofitor.create.rationality.mixin;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.index.CRItems;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SawBlockEntity.class, remap = false)
public class SawInject {
    @Shadow
    public ProcessingInventory inventory;

    @Inject(
            method = "getRecipes",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void injectEnchantedBookRecipes(CallbackInfoReturnable<List<RecipeHolder<? extends Recipe<?>>>> cir) {
        if (inventory.getStackInSlot(0).is(Items.ENCHANTED_BOOK)) {
            ItemStack bookStack = inventory.getStackInSlot(0);
            ItemEnchantments enchants = bookStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

            // 书的累积惩罚按张数均摊到每张纸（向上取整）：书(7) 切 4 张 → 每张 2
            int totalPapers = 0;
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
                totalPapers += entry.getIntValue();
            }
            int perPaperCost = totalPapers > 0
                    ? Math.ceilDiv(bookStack.getOrDefault(DataComponents.REPAIR_COST, 0), totalPapers)
                    : 0;

            // 每级生成一张独立的附魔纸：附魔纸不可堆叠（stacksTo(1)），1 张 = 1 格 = 1 级，
            // 与绑定配方“每格 1 张纸计 1 级”的计数方式一致（张数守恒，不降级）
            List<ProcessingOutput> outputs = new ArrayList<>();
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
                ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                merged.set(entry.getKey(), 1);
                ItemEnchantments enchantments = merged.toImmutable();

                for (int level = 0; level < entry.getIntValue(); level++) {
                    ItemStack paper = new ItemStack(CRItems.ENCHANTING_PAPER.get());
                    // 统一用 ENCHANTMENTS：附魔台对非书物品也写该组件（ENCHANTED_BOOK 才用 STORED），
                    // 绑定配方、铁砧合并与 glint 因此走同一条原版路径
                    paper.set(DataComponents.ENCHANTMENTS, enchantments);
                    paper.set(DataComponents.REPAIR_COST, perPaperCost);
                    outputs.add(new ProcessingOutput(paper, 1.0f));
                }
            }

            // 只有存在有效输出时才注册配方
            if (!outputs.isEmpty()) {
                ResourceLocation location = CreateRationality.asResource("cutting/enchanted_book");
                var recipe = new StandardProcessingRecipe.Builder<>(CuttingRecipe::new, location)
                        .withItemOutputs(NonNullList.copyOf(outputs))
                        .duration(120)
                        .build();
                cir.setReturnValue(List.of(new RecipeHolder<>(location, recipe)));
            }
            // 如果 outputs 为空（附魔书无附魔），则可根据业务需求返回空列表或做其他处理
        }
    }
}
