package net.noobsters.kern.paper.objects.color.encoder;

import java.util.List;
import java.util.regex.Pattern;

public abstract class ShieldEncoder implements AbstractEncoder {

    @Override
    public List<Object> key() {
        return null;
    }

    @Override
    public List<Object> value() {
        return null;
    }

    public int encode(Pattern pattern) {
        return 0;
    }

    int encode(List<Pattern> pattern) {
       return 0;
    }

    Pattern decode(int encode) {
        return null;
    }
}

