package net.noobsters.kern.demo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class HSBData {
    private float hue;
    private float saturation;
    private float brightness;

    public static HSBData fromArray(float[] hsb) {
        return new HSBData(hsb[0], hsb[1], hsb[2]);
    }

    public static HSBData getMinHue(HSBData hsbData1, HSBData hsbData2) {
        return hsbData1.hue < hsbData2.hue ? hsbData1 : hsbData2;
    }
    public static HSBData getMaxHue(HSBData hsbData1, HSBData hsbData2) {
        return hsbData1.hue > hsbData2.hue ? hsbData1 : hsbData2;
    }
}
