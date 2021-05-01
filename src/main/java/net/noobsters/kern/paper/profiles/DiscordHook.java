package net.noobsters.kern.paper.profiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DiscordHook {
    /** A Jackson ObjectMapper here is not required, but it is convenient. */
    private static ObjectMapper MAPPER = new ObjectMapper();
    /** Hard coded discord URI */
    public static URI DISCORD_HOOK = URI.create(
            "https://discord.com/api/webhooks/830870173522722816/N8UdsUkkmBFisU4NfFGBTRC2ja4AT97uI302_nLZognX8ge-1zuRGxSorUwpOutbczFf");

    /**
     * Utility function to send a Discord message using a Discord Webhook URI.
     * 
     * @param discordHook {@link URI} object of the discord webhook.
     * @param message     {@link String} containing the message to be sent.
     * @throws JsonProcessingException If the message contains an unparsable
     *                                 character.
     */
    public static void sendDiscordMessage(URI discordHook, String message) throws JsonProcessingException {
        /** Create a request with the Hook URI and with message in the POST Body */
        var request = HttpRequest.newBuilder().uri(discordHook)
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(Map.of("content", message))))
                .setHeader("Content-Type", "application/json").build();
        /**
         * Obtain a new client and execute the request async. The response of the call
         * doesn't matter to us.
         */
        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Overload function that calls {@link #sendDiscordMessage(URI, String)} parsing
     * the Hook String to URI.
     * 
     * @param discordHook {@link String} representation of the Webhook URI.
     * @param message     {@link String} containing the message to be sent.
     * @throws JsonProcessingException If the message contains an unparsable
     *                                 character.
     */
    public static void sendDiscordMessage(String discordHook, String message) throws JsonProcessingException {
        sendDiscordMessage(URI.create(discordHook), message);
    }

    /**
     * Utility function that calls {@link #sendDiscordMessage(URI, String)} using
     * the default Hook URI; PunizioneBot.
     * 
     * @param message {@link String} containing the message to be sent.
     * @throws JsonProcessingException
     */
    public static void sendPunizioneMessage(String message) throws JsonProcessingException {
        sendDiscordMessage(DISCORD_HOOK, message);
    }

}
