package net.noobsters.kern.paper.shield.jcedeno.demo;

import java.util.List;
import java.util.Random;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;
import net.noobsters.kern.paper.shield.jcedeno.objects.CustomShield;

public class CustomShieldDemo {

    /** Useful random constant */
    private static Random random = new Random();

    public static void main(String[] args) {
        /** Obtain mongo hynix object of database.json file */
        MongoHynix mongoHynix = MongoHynix.createFromJson(DatabasesConfig.of("databases"));
        /** Obtain mongo client, and then condor database with the necessary codec. */
        MongoDatabase condorDatabase = mongoHynix.getMongoClient().getDatabase("jcedeno")
                .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));
        /** Obtain the punishments collection as a collection of PlayerStats */
        MongoCollection<CustomShield> shields = condorDatabase.getCollection("shield", CustomShield.class);

        /** :::::: Demo time :::::: */

        /** Print out all the contents of the collection */
        printAllContents(shields);

        /** Insert a random shield to the database */
        var randomId = randomWord();
        insertShield(shields, randomId);

        /** Now query the shield that you just inserted */
        var shieldFound = queryShield(shields, randomId);
        /** Print out the message */
        System.out.println(String.format("%3$s\nQuery for %s returned: %s\n%3$s", randomId,
                (shieldFound != null ? shieldFound : "null"), "=============="));

        /** Delete a shield */
        var deleteResult = deleteShield(shields, randomId);
        /** Print out the message */
        System.out.println(String.format("%3$s\nDelete query %s returned: %s\n%3$s", randomId,
                (deleteResult != null ? deleteResult : "null"), "=============="));

    }

    /**
     * Demo function to insert a shield to the provided collection
     * 
     * @param collection Custom shield mongo collection.
     * @param uniqueName Unique name for the shield's database _id
     */
    private static void insertShield(MongoCollection<CustomShield> collection, String uniqueName) {
        /** Just create a simple demo object with one pattern */
        var shield = CustomShield.normalized(uniqueName, List.of(randomPattern()), randomDyeColor(), 0);
        /** Insert onto database */
        var result = collection.insertOne(shield);
        /** Print out the reuslt of the insertion */
        System.out.println("==============\nInserted " + shield + " and result was:\n" + result + "\n==============");
    }

    /**
     * Demo function to pull a shield from the database with a provided id
     * 
     * @param collection Custom shield mongo collection.
     * @param id         Shield's unique _id on mongo
     * @return CustomShield object if query is met, null object if not.
     */
    private static CustomShield queryShield(MongoCollection<CustomShield> collection, String id) {
        return collection.find(Filters.eq(id)).first();
    }

    /**
     * Function that returns the delete result of a deletion query.
     * 
     * @param collection Custom shield mongo collection.
     * @param uniqueName Unique name for the shield's database _id
     * @return {@link DeleteResult} object
     */
    private static DeleteResult deleteShield(MongoCollection<CustomShield> collection, String uniqueName) {
        return collection.deleteOne(Filters.eq(uniqueName));
    }

    /**
     * Demo utility function to print all shields of a database
     * 
     * @param collection Custom shield mongo collection.
     */
    private static void printAllContents(MongoCollection<CustomShield> collection) {

        var iter = collection.find().iterator();
        var count = 0;

        String message = "==============";
        while (iter.hasNext()) {
            count++;
            message += ("\n" + count + ". " + iter.next());
        }
        message += ("\n==============");

        System.out.println(message);
    }

    /**
     * Utility function to quickly obtain random dye color
     * 
     * @return {@link DyeColor} random enum
     */
    private static DyeColor randomDyeColor() {
        var dyes = DyeColor.values();
        return dyes[random.nextInt(dyes.length) + 1];
    }

    /**
     * Utility function to quickly obtain random pattern type
     * 
     * @return {@link PatternType} random enum
     */
    private static PatternType randomPatternType() {
        var patternTypes = PatternType.values();
        return patternTypes[random.nextInt(patternTypes.length) + 1];
    }

    /**
     * Utility function to quickly obtain random pattern.
     * 
     * @return {@link Pattern} with random {@link DyeColor} and {@link PatternType}
     */
    private static Pattern randomPattern() {
        return new Pattern(randomDyeColor(), randomPatternType());
    }

    /**
     * Utility function to create randon name
     * 
     * @return random word of leng 5 - 10
     */
    private static String randomWord() {
        char[] word = new char[random.nextInt(10) + 5];
        for (int j = 0; j < word.length; j++) {
            word[j] = (char) ('a' + random.nextInt(26));
        }
        return String.valueOf(word);
    }

}
