package net.noobsters.kern.paper.portal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class PortalCount {
    int count;
    long timestamp;

    public void add(int i) {
        count += i;
    }

    public boolean valid() {
        return (System.currentTimeMillis() - timestamp) < 3000;
    }
}
