package net.noobsters.kern.paper.profiles;

import lombok.Getter;

public class State {
    private @Getter Long time;
    private @Getter StateType type;

    public enum StateType {
        CONNECT, DISCONNECT;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setType(StateType type) {
        this.type = type;
    }

    public State(Long time, StateType type) {
        this.time = time;
        this.type = type;
    }

    public State() {
    }

}
