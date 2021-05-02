package net.noobsters.kern.paper.condor;

import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

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

    /**
     * Default constructor for a CondorProfile object.
     * 
     * @param token         {@link String} with the unique identifier for the
     *                      profile
     * @param name          {@link String} display name for the token
     * @param credits       {@link Double} initial credit ammount for the profile
     * @param instanceLimit {@link Integer} amount of instances a profile can use
     *                      simultaneously.
     * @param superToken    {@link Boolean} weather this token is admin or not.
     */
    public CondorProfile(String token, String name, double credits, int instanceLimit, boolean superToken) {
        this.token = token;
        this.name = name;
        this.credits = credits;
        this.instanceLimit = instanceLimit;
        this.superToken = superToken;
    }

    /**
     * Static builder for Condor Profile. Shortcut to the constructor
     * {@link CondorProfile#CondorProfile(String, String, double, int, boolean)}
     * 
     * @param token         {@link String} with the unique identifier for the
     *                      profile
     * @param name          {@link String} display name for the token
     * @param credits       {@link Double} initial credit ammount for the profile
     * @param instanceLimit {@link Integer} amount of instances a profile can use
     *                      simultaneously.
     * @param superToken    {@link Boolean} weather this token is admin or not.
     * @return A new instance of a {@link CondorProfile}
     */
    public static CondorProfile create(String token, String name, Double credits, Integer instanceLimit,
            Boolean superToken) {
        return new CondorProfile(token, name, credits, instanceLimit, superToken);
    }

    /**
     * Helper function to obtain a human-readable stringified summary of the
     * profile.
     * 
     * @return Stringified {@link CondorProfile}
     */
    public String stringifiedSummary() {
        return String.format("%7$s\n%5$sToken:%6$s %s\n%5$sDisplayname:%6$s %s\n%5$sCredits:%6$s %d\n%5$sInstance Limit:%6$s %d\n%7$s", token, name,
                (int) ((double) credits), instanceLimit, ChatColor.GREEN.toString(), ChatColor.WHITE.toString(), "========================");
    }

    /**
     * Builder for Mongo Pojoc.
     */
    public CondorProfile() {

    }

}
