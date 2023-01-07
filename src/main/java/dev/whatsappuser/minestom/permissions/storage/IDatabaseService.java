package dev.whatsappuser.minestom.permissions.storage;

/**
 * development by TimoH created on 21:41:26 | 06.01.2023
 */

public interface IDatabaseService {

    void loadDatabase();

    void unloadDatabase();

    boolean isDatabaseLoaded();

    IDatabase getDatabase();
}
