package net.noobsters.kern.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

public class Gradient {
    static float default_precision = 0.025f;

    public static void main(String[] args) {
        var color1 = Color.decode("#d8bb18");
        var color2 = Color.decode("#d81822");
        // Print both colors to console for log
        System.out.println("Colors are: \n" + (colorizeText(color1, color1.toString())) + ", "
                + colorizeText(color2, color2.toString()));
        // Print gradient to console
        var getTransitionColors = getTransitionColors(color1, color2, default_precision);
        System.out.println("\nTransition colors are: (precision = " + default_precision + "f)");
        printColor(color1);
        getTransitionColors.forEach(Gradient::printColor);
        printColor(color2);
        System.out.println("");
        // Now print a single line of text with the whole gradient.
        // Let's take some input from the user to make it more interesting.
        var input = "";
        while (input.isEmpty()) {
            System.out.print("Please provide a text to colorize: (String) ");
            input = System.console().readLine();
        }
        // Now print the input with the gradient
        System.out.println(applyGradient(input, color1, color2));

    }

    /**
     * Function that applies a gradient of colors from color1 to color2 to any
     * string regardless of the length.
     * 
     * @param input         The string to be colored.
     * @param startingColor The starting color.
     * @param endingColor   The ending color.
     * @return A string with the gradient applied.
     */
    private static String applyGradient(String input, final Color startingColor, final Color endingColor) {
        final var length = input.length();
        /**
         * The amount of colors needed to print whatever amount of characters the input
         * contains should be equals to the distance between the two colors divided by
         * the length of the string So that each step moves you one unit closer to the
         * whole distance needed.
         */

        /** Obtain the HSB data of each RGB Color. */
        final var hsbColor1 = getHSBData(startingColor);
        final var hsbColor2 = getHSBData(endingColor);
        /** Obtain the max and min Hue */
        var max = HSBData.getMaxHue(hsbColor1, hsbColor2);
        var min = HSBData.getMinHue(hsbColor1, hsbColor2);
        var delta = (max.getHue() - min.getHue());

        ArrayList<Color> transition = (ArrayList<Color>) getTransitionColors(startingColor, endingColor,
                delta / length);

        String colorizedText = "";
        int count = 0;
        for (var c : input.toCharArray()) {
            // Obtain the color to be used
            if (count >= transition.size()) {
                break;
            }
            var color = transition.get(count);
            // Checkif not null and continue
            if (color == null) {
                continue;
            }
            // Append the colorized text
            colorizedText += colorizeText(color, "" + c);
            // Increase index
            count++;
        }
        colorizedText += colorizeText(endingColor, input.toCharArray()[input.length() - 1]);
        return colorizedText;
    }

    /**
     * Util function to print any color to console with real color support
     * 
     * @param color Color to print.
     */
    static void printColor(Color color) {
        System.out.println(colorizeText(color, color.toString()));
    }

    private static String colorizeText(final Color color, final Character text) {
        return colorizeText(color, text.toString());
    }

    private static String colorizeText(final Color color, final String text) {
        return Ansi.colorize(text, Attribute.TEXT_COLOR(color.getRed(), color.getGreen(), color.getBlue()));
    }

    /**
     * 
     * @param color1 Starting Color (@see Color)
     * @param color2 Target Color (@see Color)
     * @return A {@link java.util.Collection} of all the {@link java.awt.Color}
     *         object between Color1 and Color2.
     */
    static Collection<Color> getTransitionColors(final Color color1, final Color color2, float precision) {
        /** Obtain the HSB data of each RGB Color. */
        final var hsbColor1 = getHSBData(color1);
        final var hsbColor2 = getHSBData(color2);
        /** Obtain the max and min Hue */
        var max = HSBData.getMaxHue(hsbColor1, hsbColor2);
        var min = HSBData.getMinHue(hsbColor1, hsbColor2);
        var delta = (max.getHue() - min.getHue());
        /** Take a path from color1 to color2 */
        if (hsbColor1.equals(max)) {
            // If Color 1 is max, then go from color 1 down to color 2
            return getPath(hsbColor1, hsbColor2, delta, precision);

        } else if (hsbColor1.equals(min)) {
            // If Color 1 is min, then go from color 1 up to color 2
            return getPath(hsbColor2, hsbColor1, delta, precision);

        } else
            throw new IllegalArgumentException("Something went wrong");/** Strange exception, unsure as of rn. */
    }

    /**
     * Function to get the path between two HSBData objects.
     * 
     * @param hsbColor1 The starting HSBData object.
     * @param hsbColor2 The target HSBData object.
     * @param steps     The difference in degrees between the two HSBData Color
     *                  objects.
     * @param precision The level of precision from one color to the next.
     * @return
     */
    private static Collection<Color> getPath(HSBData hsbColor1, HSBData hsbColor2, float steps, float precision) {
        var path = new ArrayList<Color>();
        var hue = hsbColor1.getHue();
        var saturation = hsbColor1.getSaturation();
        var brightness = hsbColor1.getBrightness();

        /**
         * Use a counter variable and a stepPrecision variable to determine how many
         * colors are needed to get there with the given level of precision.
         */
        var difference = 0.0f;
        while ((difference += (precision)) < steps) {
            var newHue = hue + difference;
            path.add(Color.getHSBColor(newHue, saturation, brightness));

        }
        return path;
    }

    /**
     * Creates a wrapper for the HSB data of a {@link java.awt.Color} object.
     * 
     * @param color The {@link java.awt.Color} object to obtain the HSB data from.
     * @return A {@link HSBData} object containing the HSB data of the provided RGB
     *         Color.
     */
    private static HSBData getHSBData(final Color color) {
        return HSBData.fromArray(getHSBFromRGB(color));
    }

    /**
     * Returns the HSB values of a given RGB Color.
     * 
     * @param color1 The RGB Color Obect {@link java.awt.Color}
     * @return An array of float HSB where index [0, 1, 2] = {hue, saturation,
     *         brigthness}.
     */
    private static float[] getHSBFromRGB(final Color color1) {
        return Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
    }

}
