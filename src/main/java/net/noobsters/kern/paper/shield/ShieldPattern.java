package net.noobsters.kern.paper.shield;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;

public class ShieldPattern {
    String name;
    DyeColor background;
    List<Pattern> patterns;

    public ShieldPattern(String key, DyeColor background, List<Pattern> patterns) {
        this.name = key;
        this.background = background;
        this.patterns = patterns;
    }

    public String getName() {
        return name;
    }

    public DyeColor getBackground() {
        return background;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }
}
