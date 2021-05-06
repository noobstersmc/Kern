package net.noobsters.kern.paper.shield.jcedeno;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import lombok.Data;

@Data
public class CustomPattern {
    DyeColor color;
    PatternType patternType;

    /**
     * Constructor for mongo pojoc.
     */
    public CustomPattern() {

    }

    /**
     * Default constructor
     * 
     * @param color
     * @param patternType
     */
    public CustomPattern(DyeColor color, PatternType patternType) {
        this.color = color;
        this.patternType = patternType;
    }

    /**
     * Constructor that turns a normal pattern into a custom pattern
     * 
     * @param pattern
     */
    public CustomPattern(Pattern pattern) {
        this.color = pattern.getColor();
        this.patternType = pattern.getPattern();
    }

    /**
     * Utility function to convert the custom pattern into a bukkit pattern
     * 
     * @return
     */
    public Pattern toBukkitPattern() {
        return new Pattern(color, patternType);
    }

}
