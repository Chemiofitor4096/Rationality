package com.chemiofitor.create.rationality.mixin;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.index.CRItems;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
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
import java.util.stream.Collectors;

@Mixin(value = SawBlockEntity.class, remap = false)
public class SawInject {
    @Shadow
    public ProcessingInventory inventory;

    @Shadow
    private FilteringBehaviour filtering;

    @Inject(
            method = "getRecipes",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    public void test(CallbackInfoReturnable<List<RecipeHolder<? extends Recipe<?>>>> cir) {
        if (inventory.getStackInSlot(0).is(Items.ENCHANTED_BOOK)) {
            ItemStack bookStack = inventory.getStackInSlot(0);
            ItemEnchantments enchants = bookStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

            // 为每个附魔生成一张纸，每张纸仅携带该附魔（等级为1），数量为原附魔等级
            List<ProcessingOutput> outputs = enchants.entrySet().stream()
                    .map(entry -> {
                        // 每个输出独立创建一个新的 Mutable，避免互相干扰
                        ItemEnchantments.Mutable merged = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                        merged.set(entry.getKey(), 1);

                        ItemStack paper = new ItemStack(CRItems.ENCHANTING_PAPER.get());
                        paper.set(DataComponents.STORED_ENCHANTMENTS, merged.toImmutable());
                        paper.setCount(entry.getIntValue());
                        return new ProcessingOutput(paper, 1.0f);
                    })
                    .collect(Collectors.toList());

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
