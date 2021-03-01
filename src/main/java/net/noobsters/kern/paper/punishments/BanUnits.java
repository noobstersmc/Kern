package net.noobsters.kern.paper.punishments;

import java.util.ArrayList;

public enum BanUnits {
    SECOND("s", 1), MINUTE("m", 60), HOUR("h", 3600), DAY("d", 3600 * 24), WEEK("w", (3600 * 24) * 7),
    MONTH("M", (3600 * 24) * 30), YEAR("y", (3600 * 24) * 365);

    private String name;
    private long multiple;

    BanUnits(String name, int seconds) {
        this.name = name;
        /** Multiply by 1K to obtain as MS */
        this.multiple = seconds * 1000;
    }

    /**
     * Helper function to obtain timeUnits as milliseconds
     * 
     * @param unit of time to multiply against.
     * @return The unit times the multiple of the timeUnit.
     */
    public long getAsMilli(int unit) {
        return unit * multiple;
    }

    /**
     * Helper function to obtain milliseconds from a string given unit
     * 
     * @param unitName Name of the time unit or the short hand equivalent.
     * @param units    of time to multiply against.
     * @return The unit times the multiple of the timeUnit.
     */
    public static long getAsMilli(String unitName, int units) {
        for (var bUnit : BanUnits.values())
            if (bUnit.name.equals(unitName) || bUnit.name().equals(unitName.toUpperCase()))
                return bUnit.multiple * units;

        return units;
    }

    /**
     * Parser function to transform a string of units of time and timeUntis into ms.
     * 
     * @param timeString Examples: 1day, 3h, 1M3h
     * @return The equivalent to seconds, if timeunit isn't parseable, then it
     *         retuns the unit of time itself. (1Pinche = 1ms)
     */
    public static long parseString(String timeString) {
        var timeUnits = timeString.split("[^A-Za-z]");
        var units = new ArrayList<String>();
        // Split to obtain only the time units
        for (var str : timeUnits)
            if (str.length() > 0)
                units.add(str);

        // Split to obtain only units of time
        var unitsOfTime = timeString.split("[A-Za-z]");
        var un = new ArrayList<Integer>();
        for (var str : unitsOfTime)
            if (str.length() > 0)
                un.add(Integer.parseInt(str));

        // Now combine them
        long totaltime = 0;
        for (int i = 0; i < units.size(); i++)
            totaltime += (BanUnits.getAsMilli(units.get(i), un.get(i)));

        return totaltime;

    }

}
