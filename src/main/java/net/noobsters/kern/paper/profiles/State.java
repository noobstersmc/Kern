package net.noobsters.kern.paper.profiles;

import lombok.Getter;
import lombok.Setter;

public class State {
    private @Getter @Setter Long time;
    private @Getter @Setter boolean type;

    public State(Long time, boolean type) {
        this.time = time;
        this.type = type;
    }

    public State() {

    }

    public static State connected() {
        return new State(System.currentTimeMillis(), true);
    }

    public static State disconnected() {
        return new State(System.currentTimeMillis(), false);
    }

}
