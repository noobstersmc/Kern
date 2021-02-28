package net.noobsters.kern.paper.shield;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;

import lombok.Data;

@Data
public class CustomShield {

    String name;
    List<Pattern> patterns;
    DyeColor color;
    Integer customModelData;

    public CustomShield(String name, List<Pattern> patterns, DyeColor color, int customModelData) {
        this.name = name;
        this.patterns = patterns;
        this.color = color;
        this.customModelData = customModelData;
    }

}