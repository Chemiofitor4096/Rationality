package com.chemiofitor.create.rationality.index;

import com.chemiofitor.create.rationality.common.item.ColorPaperItem;
import com.chemiofitor.create.rationality.common.item.EnchantingPaperItem;
import com.chemiofitor.create.rationality.common.item.FuelItem;
import com.chemiofitor.create.rationality.data.model.PollutionIndicatorModels;
import com.simibubi.create.AllTags;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.chemiofitor.create.rationality.CreateRationality.REGISTRATE;

public class CRItems {
    public static final Map<DyeColor, ItemEntry<ColorPaperItem>> COLOR_PAPERS = Arrays.stream(DyeColor.values())
            .filter(dyeColor -> !dyeColor.equals(DyeColor.WHITE))
            .collect(Collectors.toMap(
                    Function.identity(),
                    CRItems::colorPaper
            ));

    public static final ItemEntry<Item> PLANT_ASH = REGISTRATE.item("plant_ash", Item::new)
            .tag(CRTagKeys.Items.ALKALINE)
            .register();

    public static final ItemEntry<Item> COAL_ASH = REGISTRATE.item("coal_ash", Item::new)
            .register();

    public static final ItemEntry<FuelItem> CRUSHED_COAL_GANGUE = REGISTRATE.item("crushed_coal_gangue", FuelItem::new)
            .tag(AllTags.AllItemTags.CRUSHED_RAW_MATERIALS.tag)
            .register();

    public static final ItemEntry<Item> CALCIUM_CARBONATE = REGISTRATE.item("calcium_carbonate", Item::new)
            .register();

    public static final ItemEntry<Item> QUICKLIME = REGISTRATE.item("quicklime", Item::new)
            .tag(CRTagKeys.Items.ALKALINE)
            .register();

    public static final ItemEntry<Item> SLAKED_LIME = REGISTRATE.item("slaked_lime", Item::new)
            .tag(CRTagKeys.Items.ALKALINE)
            .register();

    public static final ItemEntry<Item> FINE_PULP  = REGISTRATE.item("fine_pulp", Item::new)
            .register();

    public static final ItemEntry<Item> RAW_PAPER = REGISTRATE.item("raw_paper", Item::new)
            .register();

    public static final ItemEntry<EnchantingPaperItem> ENCHANTING_PAPER =
            REGISTRATE.item("enchanting_paper", EnchantingPaperItem::new)
                    .properties(prop -> prop.rarity(Rarity.UNCOMMON))
                    .register();

    public static final ItemEntry<Item> OILPAPER = REGISTRATE.item("oilpaper", Item::new)
            .properties(p -> p.rarity(Rarity.UNCOMMON))
            .register();

    /** 污染指示器：根据所在区块污染状态在两种材质间切换（见 PollutionIndicatorModels） */
    public static final ItemEntry<Item> POLLUTION_INDICATOR =
            REGISTRATE.item("pollution_indicator", Item::new)
                    .model(PollutionIndicatorModels::build)
                    .register();

    private static ItemEntry<ColorPaperItem> colorPaper(DyeColor color) {
        return REGISTRATE.item(color.getName() + "_paper", properties -> new ColorPaperItem(color, properties))
                .tag(CRTagKeys.Items.COLOR_PAPER)
                .register();
    }

    public static void register() {}
}
