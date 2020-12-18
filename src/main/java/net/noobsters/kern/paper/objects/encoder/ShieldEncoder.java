package net.noobsters.kern.paper.objects.encoder;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import java.util.ArrayList;
import java.util.List;

public class ShieldEncoder{
    public DyeColor[] dyeColors() {
        return DyeColor.values();
    }

    public PatternType[] patternTypes() {
        return PatternType.values();
    }

    public int encode(Pattern pattern) {
        StringBuilder encode = new StringBuilder();
        for (int i = 0; i < dyeColors().length; i++) {
            if (dyeColors()[i].equals(pattern.getColor())) {
                if (i > 10)
                    encode.append(0);
                encode.append(i);
            }
        }

        for (int i = 0; i < patternTypes().length; i++) {
            if (patternTypes()[i].equals(pattern.getPattern())) {
                if (i > 10)
                    encode.append(0);
                encode.append(i);
            }
        }
        return Integer.parseInt(encode.toString());
    }

    public int encode(List<Pattern> patterns) {
        StringBuilder encode = new StringBuilder();
        for (Pattern pattern : patterns)
            encode.append(encode(pattern));
        return Integer.parseInt(encode.toString());
    }

    public List<Pattern> decode(int encode) {
        List<Pattern> decoded_packet = new ArrayList<>();
        String[] encode_packets = new String[encode / 4];
        if (encode % 4 != 0)
            throw new IllegalArgumentException("Encode format exception: "+ encode);

        for (int i = 0; i < encode_packets.length; i++) {
            encode_packets[i] = String.valueOf(encode).substring(i, i+4);
        }

        for (String packet : encode_packets) {
            decoded_packet.add(new Pattern(
                    dyeColors()[Integer.parseInt(packet.substring(0, 2))],
                    patternTypes()[Integer.parseInt(packet.substring(2))]
            ));
        }
        return decoded_packet;
    }


}

