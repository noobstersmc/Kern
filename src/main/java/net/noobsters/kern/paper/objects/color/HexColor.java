package net.noobsters.kern.paper.objects.color;

import java.util.Map;
import java.util.MissingFormatArgumentException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HexColor {
    String red, green, blue;

    public HexColor(int red, int green, int blue) {
        this.red = toHex(red);
        this.green = toHex(green);
        this.blue = toHex(blue);
    }

    private static Map<String, String> hex_format = Stream.of(new Object[][] {
            {"0", "0000"}, {"1", "0001"}, {"2", "0010"}, {"3", "0011"}, {"4", "0100"},
            {"5", "0101"}, {"6", "0110"}, {"7", "0111"}, {"8", "1000"}, {"9", "1001"},
            {"A", "1010"}, {"B", "1011"}, {"C", "1100"}, {"D", "1101"}, {"E", "1110"},
            {"F", "1111"}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (String) data[1]));

    private static String toHex(int color) {
        String color_bin = Integer.toBinaryString(color);
        String returned = "";

        if (!(color >= 0 && color <= 255))
            throw new IllegalArgumentException("Number mut be between 0 and 255");

        if (color_bin.toCharArray().length != 8) {
            for (int i = 1; i < 8; i++) {
                if (color_bin.toCharArray().length != 8) {
                    color_bin = 0+ color_bin;
                    continue;
                }break;
            }
        }

        for (Map.Entry<String, String> entry : hex_format.entrySet()) {
            if (entry.getValue().equals(color_bin.substring(0, 4)))
                returned = returned+ entry.getKey();
        }

        for (Map.Entry<String, String> entry : hex_format.entrySet()) {
            if (entry.getValue().equals(color_bin.substring(4)))
                returned = returned+ entry.getKey();
        }

        if (returned.length() == 2)
            return returned;
        throw new MissingFormatArgumentException("An error ocurred while performing the action");
    }

    @Override
    public String toString() {
        return "#"+ red+ green+ blue;
    }
}
