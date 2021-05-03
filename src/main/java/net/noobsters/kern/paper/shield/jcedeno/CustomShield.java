package net.noobsters.kern.paper.shield.jcedeno;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;

public class CustomShield {
    @BsonId
    private String name;
    @BsonProperty("patterns")
    private List<CustomPattern> patterns;
    @BsonProperty("banner_color")
    private DyeColor bannerDyeColor;
    @BsonProperty("custom_model_data")
    private Integer customModelData;

    /**
     * Normal constructor for the Custom Shield object
     * 
     * @param name
     * @param patterns
     * @param bannerDyeColor
     * @param customModelData
     */
    public CustomShield(String name, List<CustomPattern> patterns, DyeColor bannerDyeColor, Integer customModelData) {
        this.name = name;
        this.patterns = patterns;
        this.bannerDyeColor = bannerDyeColor;
        this.customModelData = customModelData;
    }

    /**
     * Helper function that maps list of patterns onto a list of custom patterns.
     * 
     * @param name
     * @param patterns
     * @param bannerDyeColor
     * @param customModelData
     * @return new Instance of a Custom Shield with normalized data
     */
    public static CustomShield normalized(String name, List<Pattern> patterns, DyeColor bannerDyeColor,
            Integer customModelData) {
        return new CustomShield(name, patterns.stream().map(e -> new CustomPattern(e)).collect(Collectors.toList()),
                bannerDyeColor, customModelData);
    }

    public ItemStack applyCustomBannerData(ItemStack orginalItem) {
        assertFalse("The provided item can only be a shield", (orginalItem.getType() != Material.SHIELD));
        // TODO: Implement method to quickly transform a shield into a custom shield
        return null;
    }

    /**
     * Empty constructor for pojocs.
     */
    public CustomShield() {

    }

    /**
     * From here on, there's just a lot of boiler-plate getters and setters for
     * pojocs to work. It can, for the most part, be ignored.
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<CustomPattern> patterns) {
        this.patterns = patterns;
    }

    public DyeColor getBannerDyeColor() {
        return bannerDyeColor;
    }

    public void setBannerDyeColor(DyeColor bannerDyeColor) {
        this.bannerDyeColor = bannerDyeColor;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
    }

}
