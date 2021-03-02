package net.noobsters.kern.paper.utils;

import lombok.NoArgsConstructor;

/**
 * Helper class to quickly count how much time has passed since starting.
 */
@NoArgsConstructor(staticName = "start")
public class HTimer {
    private long startTime = System.currentTimeMillis();

    public long stop() {
        return System.currentTimeMillis() - startTime;
    }
}
