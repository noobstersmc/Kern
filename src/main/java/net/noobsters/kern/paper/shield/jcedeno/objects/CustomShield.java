package net.noobsters.kern.paper.shield.jcedeno.objects;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;

import lombok.Data;
import net.noobsters.kern.paper.shield.jcedeno.exceptions.ShieldInvalidTypeException;

public @Data class CustomShield {
    @BsonId
    private String name;
    @BsonProperty("patterns")
    private List<CustomPattern> patterns;
    @BsonProperty("banner_color")
    private DyeColor bannerDyeColor;
    @BsonProperty("custom_model_data")
    private Integer customModelData = 0;

    /**
     * Constructor for the Custom Shield object
     * 
     * @param name            Name to be used as the unique identifier in mongo.
     * @param patterns        List of {@link CustomPattern} objects.
     * @param bannerDyeColor  {@link DyeColor} to be used on the custom shield.
     * @param customModelData CustomModelData Integer, 0 by default.
     */
    public CustomShield(String name, List<CustomPattern> patterns, DyeColor bannerDyeColor, Integer customModelData) {
        this.name = name;
        this.patterns = patterns;
        this.bannerDyeColor = bannerDyeColor;
        this.customModelData = customModelData;
    }

    /**
     * Constructor that uses {@link ItemStack} with banner meta to swiftly create a
     * CustomShield Object.
     * 
     * @param name      Name to be used as the unique identifier in mongo.
     * @param itemStack to be used to create a CustomShield object from.
     * @throws ShieldInvalidTypeException if the given object doesn't contain any
     *                                    BannerMeta
     */
    public static CustomShield fromStack(String name, ItemStack itemStack) throws ShieldInvalidTypeException {
        final var type = itemStack.getType();
        final var itemMeta = itemStack.getItemMeta();
        final var modelId = (itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : 0);

        if (type == Material.SHIELD) {
            /** Obtain the BlockStateMeta of the Shield. */
            var blockStateMeta = (BlockStateMeta) itemMeta;
            /** Obtain the BlockState and cast it as a Banner. */
            var banner = (Banner) blockStateMeta.getBlockState();
            /** Create and return a normalized shield with the banner object. */
            return CustomShield.normalized(name, banner.getPatterns(), banner.getBaseColor(), modelId);

        } else if (type.toString().contains("BANNER")) {
            /**
             * Split the material's name by '_' use the first part and match to DyeColor.
             */
            var color = DyeColor.valueOf(type.toString().split("_")[0]);
            var bannerMeta = (BannerMeta) itemMeta;

            /** Create and return a normalized shield. */
            return CustomShield.normalized(name, bannerMeta.getPatterns(), color, modelId);

        } else /** throw an exception if not parseable. */
            throw new ShieldInvalidTypeException("Object " + itemStack.getItemMeta().getDisplayName() + "("
                    + itemStack.getType() + ") doesn't contain any banner meta.");

    }

    /**
     * Helper function that maps list of patterns onto a list of custom patterns.
     * 
     * @param name            Name for the unique _id mongo field.
     * @param patterns        List of Bukkit {@link Pattern}(s) to be transformed to
     *                        custom patterns.
     * @param bannerDyeColor  {@link DyeColor} enum for the base color.
     * @param customModelData Optional CustomModelData integer for resourcepacks.
     * @return new instance of a {@link CustomShield} with normalized data
     */
    public static CustomShield normalized(String name, List<Pattern> patterns, DyeColor bannerDyeColor,
            Integer customModelData) {
        return new CustomShield(name, patterns.stream().map(e -> new CustomPattern(e)).collect(Collectors.toList()),
                bannerDyeColor, customModelData);
    }

    /**
     * Helper function to quickly add the custom shield data to an ItemStack of type
     * {@link Material#SHIELD}
     * 
     * @param orginalItem An item stack of type SHIELD.
     * @return {@link ItemStack} aggregating the provided shield item with this
     *         CustomShield banner data.
     */
    public ItemStack applyCustomBannerData(final ItemStack orginalItem) {
        assertFalse("The provided item can only be a shield", (orginalItem.getType() != Material.SHIELD));
        System.out.println("Applying custom shield data");

        var meta = orginalItem.getItemMeta();
        var bmeta = (BlockStateMeta) meta;
        var banner = (Banner) bmeta.getBlockState();

        banner.setPatterns(
                (this.patterns.stream().map(patterns -> patterns.toBukkitPattern())).collect(Collectors.toList()));
        banner.setBaseColor(this.bannerDyeColor);

        banner.update();
        bmeta.setBlockState(banner);
        orginalItem.setItemMeta(bmeta);

        return orginalItem;
    }

    /**
     * Empty constructor for pojocs.
     */
    public CustomShield() {

    }

    /**
     * Utility function to avoid null-pointer exceptions when printing objects.
     * 
     * @param o Nullable Object of any type with a {@link #toString()} method.
     * @return A string containing "null" or the provided object's
     *         {@link #toString()} string.
     */
    private String objectOrNull(Object o) {
        return o != null ? o.toString() : "null";
    }

    @Override
    public String toString() {
        return String.format("CustomShield(name=%s, patterns=%s, color=%s, data=%s)", objectOrNull(this.name),
                objectOrNull(this.patterns), objectOrNull(this.bannerDyeColor), objectOrNull(this.customModelData));
    }

}
