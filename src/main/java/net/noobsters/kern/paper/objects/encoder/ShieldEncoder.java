package net.noobsters.kern.paper.objects.encoder;

import net.noobsters.kern.paper.shield.ShieldPattern;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import java.util.ArrayList;
import java.util.List;

public class ShieldEncoder {
    private static DyeColor[] dyeColors = DyeColor.values();
    private static PatternType[] patternTypes = PatternType.values();

    public static String encode(DyeColor background, Pattern pattern) {
        StringBuilder encode = new StringBuilder();
        encode.append(encodeColor(background));

        encode.append(encodePattern(pattern));
        return encode.toString();
    }

    public static String encode(DyeColor background, List<Pattern> patterns) {
        StringBuilder encode = new StringBuilder();
        encode.append(encodeColor(background));

        for (Pattern pattern : patterns)
            encode.append(encodePattern(pattern));

        return encode.toString();
    }

    public static ShieldPattern decode(String encode) {//15 1135 731
        List<Pattern> decoded_patterns = new ArrayList<>();
        String[] encode_packets = new String[String.valueOf(encode).length() / 4];

        if (String.valueOf(encode).length() % 4 != 2)
            throw new IllegalArgumentException("Encode format exception: "+ encode);

        for (int i = 0; i < encode_packets.length; i++) {
            encode_packets[i] = String.valueOf(encode).substring(i*3+i+2, i*3+i+6);
            System.out.println(encode_packets[i]);
        }

        for (String packet : encode_packets) {
            decoded_patterns.add(new Pattern(
                    dyeColors[Integer.parseInt(packet.substring(0, 2))],
                    patternTypes[Integer.parseInt(packet.substring(2))]
            ));

        }
        return new ShieldPattern(null,
                dyeColors[Integer.parseInt(String.valueOf(encode).substring(0, 2))],
                decoded_patterns);
    }

    private static String encodeColor(DyeColor color) {
        StringBuilder encode = new StringBuilder();
        for (int i = 0; i < dyeColors.length; i++) {
            if (dyeColors[i].equals(color)) {
                if (i < 10)
                    encode.append("0");
                encode.append(i);
            }
        }
        return encode.toString();
    }

    private static String encodePattern(Pattern pattern) {
        StringBuilder encode = new StringBuilder();
        encode.append(encodeColor(pattern.getColor()));

        for (int i = 0; i < patternTypes.length; i++) {
            if (patternTypes[i].equals(pattern.getPattern())) {
                if (i < 10) encode.append("0");
                encode.append(i);
            }
        }
        return encode.toString();
    }
}

