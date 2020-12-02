package net.noobsters.core.paper.shield;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;

import java.util.List;

public class ShieldPattern {
    List<Pattern> patterns;
    DyeColor color;
    String name;

    public ShieldPattern(String name, DyeColor color, List<Pattern> patterns) {
        this.name = name;
        this.patterns = patterns;
        this.color = color;
    }

    public DyeColor getBackground() {
        return color;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public String getName() {
        return name;
    }
}
