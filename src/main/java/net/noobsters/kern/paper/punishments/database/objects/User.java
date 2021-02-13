package net.noobsters.kern.paper.punishments.database.objects;

import java.util.List;
import java.util.UUID;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import lombok.AllArgsConstructor;

@Entity("users")
@Indexes(@Index(fields = @Field("points")))
@AllArgsConstructor(staticName = "of")
public class User {
    @Id
    UUID uuid;
    @Property("name")
    String name;
    @Reference
    List<Mute> mute;
    @Reference
    List<Ban> bans;
    @Property("points")
    Double points;
}
