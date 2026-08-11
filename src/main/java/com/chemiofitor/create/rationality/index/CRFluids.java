package com.chemiofitor.create.rationality.index;

import com.chemiofitor.create.rationality.CreateRationality;
import com.chemiofitor.create.rationality.common.block.BlackLiquorBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import org.jetbrains.annotations.NotNull;

import static com.chemiofitor.create.rationality.CreateRationality.REGISTRATE;

public class CRFluids {

    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
    private static final DispenseItemBehavior DISPENSE_FLUID = new DefaultDispenseItemBehavior() {
        @Override
        protected @NotNull ItemStack execute(BlockSource pSource, ItemStack pStack) {
            DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) pStack.getItem();
            BlockPos pos = pSource.pos().relative(pSource.state().getValue(DispenserBlock.FACING));
            Level level = pSource.level();
            if (dispensibleContainerItem.emptyContents(null, level, pos, null, pStack)) {
                return new ItemStack(Items.BUCKET);
            }
            return DEFAULT.dispense(pSource, pStack);
        }
    };

    public static final FluidEntry<BaseFlowingFluid.Flowing> PULP =
            REGISTRATE.fluid("pulp", CreateRationality.asResource("block/pulp_still"),
                            CreateRationality.asResource("block/pulp_flow"), CreateRegistrate::defaultFluidType)
                    .lang("Pulp")
                    .tag(CRTagKeys.Fluids.PULP)
                    .properties(b -> b.viscosity(2000)
                            .density(1300))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(40)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(BaseFlowingFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.COLOR_BROWN))
                    .build()
                    .bucket()
                    .onRegister(CRFluids::registerFluidDispenseBehavior)
                    .build()
                    .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> FINE_PULP =
            REGISTRATE.fluid("fine_pulp", CreateRationality.asResource("block/fine_pulp_still"),
                            CreateRationality.asResource("block/fine_pulp_flow"), CreateRegistrate::defaultFluidType)
                    .lang("Fine Pulp")
                    .tag(CRTagKeys.Fluids.FINE_PULP)
                    .properties(b -> b.viscosity(2000)
                            .density(1100))
                    .fluidProperties(p -> p.levelDecreasePerBlock(1)
                            .tickRate(20)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(BaseFlowingFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.COLOR_BROWN))
                    .build()
                    .bucket()
                    .onRegister(CRFluids::registerFluidDispenseBehavior)
                    .build()
                    .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> ASH_LYE =
            REGISTRATE.fluid("ash_lye", CreateRationality.asResource("block/ash_lye_still"),
                            CreateRationality.asResource("block/ash_lye_flow"), CreateRegistrate::defaultFluidType)
                    .lang("Ash Lye")
                    .tag(CRTagKeys.Fluids.ASH_LYE)
                    .properties(b -> b.viscosity(2000)
                            .density(1100))
                    .fluidProperties(p -> p.levelDecreasePerBlock(1)
                            .tickRate(20)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(BaseFlowingFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
                    .build()
                    .bucket()
                    .onRegister(CRFluids::registerFluidDispenseBehavior)
                    .build()
                    .renderType(() -> RenderType::translucent)
                    .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> LYE =
            REGISTRATE.fluid("lye", CreateRationality.asResource("block/lye_still"),
                            CreateRationality.asResource("block/lye_flow"), CreateRegistrate::defaultFluidType)
                    .lang("Lye")
                    .tag(CRTagKeys.Fluids.LYE)
                    .properties(b -> b.viscosity(2000)
                            .density(1100))
                    .fluidProperties(p -> p.levelDecreasePerBlock(1)
                            .tickRate(20)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(BaseFlowingFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
                    .build()
                    .bucket()
                    .onRegister(CRFluids::registerFluidDispenseBehavior)
                    .build()
                    .renderType(() -> RenderType::translucent)
                    .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> BLACK_LIQUOR =
            REGISTRATE.fluid("black_liquor", CreateRationality.asResource("block/black_liquor_still"),
                            CreateRationality.asResource("block/black_liquor_flow"), CreateRegistrate::defaultFluidType)
                    .lang("Black Liquor")
                    .tag(CRTagKeys.Fluids.BLACK_LIQUOR)
                    .properties(b -> b.viscosity(2000)
                            .density(1100))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(20)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(BaseFlowingFluid.Source::new)
                    .block(BlackLiquorBlock::new)
                    .properties(p -> p.mapColor(MapColor.COLOR_BLACK))
                    .build()
                    .bucket()
                    .onRegister(CRFluids::registerFluidDispenseBehavior)
                    .build()
                    .register();

    public static final FluidEntry<BaseFlowingFluid.Flowing> SYRUP =
            REGISTRATE.fluid("syrup", CreateRationality.asResource("block/syrup_still"),
                            CreateRationality.asResource("block/syrup_flow"), CreateRegistrate::defaultFluidType)
                    .lang("Syrup")
                    .tag(CRTagKeys.Fluids.SYRUP)
                    .properties(b -> b.viscosity(2000)
                            .density(1200))
                    .fluidProperties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(20)
                            .slopeFindDistance(3)
                            .explosionResistance(100f))
                    .source(BaseFlowingFluid.Source::new)
                    .block()
                    .properties(p -> p.mapColor(MapColor.TERRACOTTA_WHITE))
                    .build()
                    .bucket()
                    .onRegister(CRFluids::registerFluidDispenseBehavior)
                    .build()
                    .register();

    public static void register() {}

    private static void registerFluidDispenseBehavior(BucketItem bucket) {
        DispenserBlock.registerBehavior(bucket, DISPENSE_FLUID);
    }
}
