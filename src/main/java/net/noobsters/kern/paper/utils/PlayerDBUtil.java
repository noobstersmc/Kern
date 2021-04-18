package net.noobsters.kern.paper.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * More info of the contents of the responses here https://playerdb.co
 */
public class PlayerDBUtil {
    private static HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build();
    private static Gson gson = new Gson();

    public static String getUser(String username) throws IOException, InterruptedException {
        var uri = URI.create("https://playerdb.co/api/player/minecraft/" + username);
        var request = HttpRequest.newBuilder(uri).header("accept", "application/json").build();

        var response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    public static String getUser(UUID uuid) throws IOException, InterruptedException {
        return getUser(uuid.toString());
    }

    public static JsonObject getPlayerObject(String nameOrID) {
        try {
            var request = getUser(nameOrID);
            var response = gson.fromJson(request, JsonObject.class);
            return response.getAsJsonObject("data").getAsJsonObject("player");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CompletableFuture<JsonObject> getPlayerObjectAsync(String nameOrID) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getPlayerObject(nameOrID);
            } catch (Exception e) {
                return null;
            }
        });
    }
}
