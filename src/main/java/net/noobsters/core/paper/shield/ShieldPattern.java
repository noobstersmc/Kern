package net.noobsters.core.paper.shield;

import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShieldPattern {
    String name;
    DyeColor background;
    List<Pattern> patterns;
}
