package net.noobsters.core.paper.shield;

import net.noobsters.core.paper.Core;
import net.noobsters.core.paper.YML;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import java.util.ArrayList;
import java.util.List;

public class Shields {
    private Core plugin;
    private YML shields;
    private static List<ShieldPattern> patterns = new ArrayList<>();

    public Shields(Core plugin) {
        this.plugin = plugin;
        shields = new YML(plugin.getDataFolder(),"shields", false);
        importPatterns();
    }

    public void importPatterns() {
        if (shields.getFile().get("shields") != null) {
            for (String key : shields.getFile().getConfigurationSection("shields").getKeys(false)) {
                DyeColor background = DyeColor.valueOf(shields.getFile().getString("shields."+ key+ ".color").toUpperCase());
                List<Pattern> patternList = new ArrayList<>();

                for (String pattern : shields.getFile().getStringList("shields."+ key+ ".patterns")) {
                    patternList.add(new Pattern(DyeColor.valueOf(pattern.split(",")[1].toUpperCase()), PatternType.valueOf(pattern.split(",")[0].toUpperCase())));
                }
                patterns.add(new ShieldPattern(key, background, patternList));
            }
        }
    }
    
    public static List<ShieldPattern> getPatterns() {
        return patterns;
    }

    public static void addPattern(ShieldPattern pattern) {
        patterns.add(pattern);
    }

    public static void removePatterns(ShieldPattern pattern) {
        patterns.remove(pattern);
    }
    
    
}
