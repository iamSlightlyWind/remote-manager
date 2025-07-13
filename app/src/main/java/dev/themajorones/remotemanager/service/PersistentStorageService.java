package dev.themajorones.remotemanager.service;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.themajorones.remotemanager.entity.Device;

public class PersistentStorageService {
    private static volatile PersistentStorageService INSTANCE;

    @Dao
    interface DeviceDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void save(Device device);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void saveAll(List<Device> devices);

        @Update
        void update(Device device);

        @Delete
        void delete(Device device);

        @Query("DELETE FROM Device WHERE name = :name")
        void deleteByName(String name);

        @Query("SELECT * FROM Device WHERE name = :name LIMIT 1")
        Device findByName(String name);

        @Query("SELECT * FROM Device")
        List<Device> findAll();

        @Query("SELECT * FROM Device WHERE host = :host")
        List<Device> findByHost(String host);

        @Query("SELECT COUNT(*) FROM Device")
        int count();

        @Query("DELETE FROM Device")
        void deleteAll();
    }

    @Database(entities = {Device.class}, version = 3, exportSchema = false)
    abstract static class AppDatabase extends RoomDatabase {
        abstract DeviceDao deviceDao();

        private static volatile AppDatabase INSTANCE;

        static AppDatabase getDatabase(Context context) {
            if (INSTANCE == null) {
                synchronized (AppDatabase.class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                        context.getApplicationContext(),
                                        AppDatabase.class,
                                        "device_database"
                                )
                                .fallbackToDestructiveMigration()
                                .build();
                    }
                }
            }
            return INSTANCE;
        }

    }

    private final DeviceDao dao;
    private final ExecutorService executor;

    private PersistentStorageService(Context context) {
        this.dao = AppDatabase.getDatabase(context).deviceDao();
        this.executor = Executors.newFixedThreadPool(4);
    }

    public static void init(Context context) {
        if (INSTANCE == null) {
            synchronized (PersistentStorageService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PersistentStorageService(context.getApplicationContext());
                }
            }
        }
    }

    public static PersistentStorageService get() {
        if (INSTANCE == null)
            throw new IllegalStateException("PersistentStorageService not initialized. Call init(context) first.");
        return INSTANCE;
    }

    public static List<Device> findAllManagingDevices() {
        List<Device> devices = get().findAll();
        List<Device> managingDevices = new ArrayList<>();
        for (Device device : devices) {
            if (device.managingDevices == null || device.managingDevices.isEmpty()) {
                continue;
            }

            for (Device managingDevice : device.managingDevices) {
                if (!managingDevices.contains(managingDevice)) {
                    managingDevices.add(managingDevice);
                }
            }
        }

        return managingDevices;
    }

    public static boolean isManagingDevice(Device device) {
        List<Device> managingDevices = findAllManagingDevices();
        return managingDevices.contains(device);
    }

    public static List<Device> findManagedDevices(Device device) {
        List<Device> devices = get().findAll();
        List<Device> managedDevices = new ArrayList<>();
        for (Device d : devices) {
            if (d.managingDevices != null && d.managingDevices.contains(device)) {
                managedDevices.add(d);
            }
        }
        return managedDevices;
    }

    public Device save(Device device) {
        try {
            executor.submit(() -> {
                dao.save(device);
                return null;
            }).get();
            return device;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save device", e);
        }
    }

    public void saveAll(List<Device> devices) {
        try {
            executor.submit(() -> {
                dao.saveAll(devices);
                return null;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save devices", e);
        }
    }

    public Device update(Device device) {
        try {
            executor.submit(() -> {
                dao.update(device);
                return null;
            }).get();
            return device;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update device", e);
        }
    }

    public void delete(Device device) {
        try {
            executor.submit(() -> {
                dao.delete(device);
                return null;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete device", e);
        }
    }

    public void deleteByName(String name) {
        try {
            executor.submit(() -> {
                dao.deleteByName(name);
                return null;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete device by name", e);
        }
    }

    public Device findByName(String name) {
        try {
            return executor.submit(() -> dao.findByName(name)).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find device by name", e);
        }
    }

    public List<Device> findAll() {
        try {
            return executor.submit(dao::findAll).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find devices", e);
        }
    }

    public List<Device> findByHost(String host) {
        try {
            return executor.submit(() -> dao.findByHost(host)).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find devices by host", e);
        }
    }

    public int count() {
        try {
            return executor.submit(dao::count).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count devices", e);
        }
    }

    public void deleteAll() {
        try {
            executor.submit(() -> {
                dao.deleteAll();
                return null;
            }).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete all devices", e);
        }
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}