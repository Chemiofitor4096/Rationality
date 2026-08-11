package com.chemiofitor.create.rationality.mixin;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.data.recipe.ban.RecipeTargets;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(RecipeManager.class)
public class BanRecipes {
    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD")
    )
    private void filterRecipesBeforeParsing(Map<ResourceLocation, JsonElement> object,
                                            ResourceManager resourceManager,
                                            ProfilerFiller profiler,
                                            CallbackInfo ci) {
        Set<ResourceLocation> set = RecipeTargets.getRecipes();

        CreateRationality.LOGGER.info("Rationality trys to remove {} recipes", set.size());
        for (ResourceLocation key : set) {
            if (object.containsKey(key)) {
                object.remove(key);
                CreateRationality.LOGGER.info("Remove recipe {}", key);
            }
        }
    }
}
