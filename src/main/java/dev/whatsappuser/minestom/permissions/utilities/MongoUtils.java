package dev.whatsappuser.minestom.permissions.utilities;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.UUID;

/**
 * development by TimoH created on 21:59:39 | 06.01.2023
 */

public class MongoUtils {

    public static Bson createUserFilter(UUID uuid) {
        return Filters.eq("uuid", uuid.toString());
    }

    public static Bson createGroupFilter(String name) {
        return Filters.eq("name", name);
    }
}
