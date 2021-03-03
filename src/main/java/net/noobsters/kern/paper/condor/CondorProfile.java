package net.noobsters.kern.paper.condor;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.Setter;

public class CondorProfile {
    @BsonProperty("token")
    private @Getter @Setter String token;
    @BsonProperty("name")
    private @Getter @Setter String name;
    @BsonProperty("credits")
    private @Getter @Setter Double credits;
    @BsonProperty("instance_limit")
    private @Getter @Setter Integer instanceLimit;
    @BsonProperty("super_token")
    private @Getter @Setter Boolean superToken = false;

    public CondorProfile() {

    }

}
