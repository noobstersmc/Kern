package net.noobsters.kern.paper.twitter;

import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class LairTweet {
    String tweet;

    public String toJson(Gson gson) {
        return gson.toJson(this);
    }
}
