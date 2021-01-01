package net.noobsters.kern.paper.twitter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import kong.unirest.Unirest;

/**
 * LairTwitter
 */
public class LairTwitter {
    private static Gson gson = new Gson();
    private static String CONDOR_URL = "http://condor.jcedeno.us:420";

    static {
        Unirest.config().connectTimeout(1000);
    }

    public static String tweet(String tweet) {
        // Create the tweet as json
        var tweet_json = LairTweet.of(tweet).toJson(gson);
        // Create the request and call for response
        var response = Unirest.post(CONDOR_URL + "/tweet").header("auth", "Condor-Secreto")
                .header("Content-Type", "application/json").body(tweet_json).asString();
        // Return response's body
        return response.getBody();
    }

    public static void main(String[] args) throws Exception {
        // Hora que quieres
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        final String gameDate = "2021-01-01T19:45:00Z";
        // Cosas para que funcione
        final var apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        apiFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        final Date dateOfGame = apiFormat.parse(gameDate);
        final long millis = dateOfGame.getTime() - System.currentTimeMillis();

        final String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        // Cuanto queda en milisegundos y en formato de hora dia fecha
        /*
         * System.out.println(dateOfGame.getTime() - System.currentTimeMillis());
         * System.out.println(hms);
         * 
         * System.out.println(getFutureDate(10));
         * 
         */
        // timeDiff(18);

        for (int i = 0; i < 60; i++) {
            timeDiff(i);
        }

    }

    public static void timeUntil(String str) throws Exception {
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");

        Date answeredTime = ft.parse(str);
        long asSecondsSince1970 = answeredTime.getTime();
        System.out.println(asSecondsSince1970);
    }

    static String getFutureDate(long minutes) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .format(LocalDateTime.now(ZoneId.of("America/New_York")).plusMinutes(minutes));
    }

    static void timeDiff(long m) {
        var time = LocalDateTime.now(ZoneId.of("America/New_York")).plusMinutes(m).withSecond(0);
        var min = time.getMinute();
        var ceil_min = (int) Math.ceil(min / 10.0) * 10;
        var module = (int) min % 10;

        //System.out.println(min + " ceiled min = " + ceil_min + " modulo diff = " + module);

        if (ceil_min == 60) {
            time = time.withMinute(0).plusHours(1);
        }
        else if ( module == 0){

        } 
        else if (module >= 1 && module <= 2) {

            time = time.withMinute(ceil_min - 10);

        } else if (module >= 3 && module <= 6) {
            time = time.withMinute(ceil_min - 5);

        } else if (module >= 7 && module <= 9) {
            time = time.withMinute(ceil_min);
        }

        System.out.println(m + "." + ceil_min + "." + ""+time.toString());

    }

}