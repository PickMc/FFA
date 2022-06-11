package me.zxoir.pickmcffa.database;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/23/2022
 */
@SuppressWarnings("UnusedReturnValue")
public class UsersDBManager {
    private static final Gson adapter = new GsonBuilder().serializeNulls().create();

    @NotNull
    public static ConcurrentLinkedQueue<User> getUsers() {
        ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<>();
        long start = System.currentTimeMillis();

        try {
            FFADatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM users");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    users.add(dbToUser(resultSet));
                }

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: UDBM_GU'S.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: UDBM_GU'S.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        PickMcFFA.getFfaLogger().debug("Fetched DB results in " + finish + " seconds.");

        return users;
    }

    @Nullable
    public static User getUser(UUID uuid) {

        AtomicReference<User> user = new AtomicReference<>(null);
        long start = System.currentTimeMillis();

        try {
            FFADatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE uuid = ? LIMIT 1");
                statement.setString(1, uuid.toString());
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next())
                    return;

                user.set(dbToUser(resultSet));

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: UDBM_GU.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: UDBM_GU.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        PickMcFFA.getFfaLogger().debug("Fetched DB result in " + finish + " seconds.");

        return user.get();
    }

    @Nullable
    public static User getUser(String uuid) {

        AtomicReference<User> user = new AtomicReference<>(null);
        long start = System.currentTimeMillis();

        try {
            FFADatabase.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE uuid = ? LIMIT 1");
                statement.setString(1, uuid);
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next())
                    return;

                user.set(dbToUser(resultSet));

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: UDBM_GU.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: UDBM_GU.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        PickMcFFA.getFfaLogger().debug("Fetched DB result in " + finish + " seconds.");

        return user.get();
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> saveToDB(User user) {
        return FFADatabase.execute(conn -> {
            long start = System.currentTimeMillis();
            System.out.println(1);

            PreparedStatement statement = conn.prepareStatement("INSERT INTO users VALUES(?, ?)");
            statement.setString(1, user.getUuid().toString());
            System.out.println(2);

            statement.setString(2, adapter.toJson(user.getStats()));
            System.out.println(3);

            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            PickMcFFA.getFfaLogger().debug("Saved User ('" + user.getUuid() + " (" + user.getOfflinePlayer().getName() + ")') to DB in " + finish + " seconds.");
        });
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> deleteFromDB(User user) {
        return FFADatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement("DELETE FROM users WHERE uuid = ?");
            statement.setString(1, user.getUuid().toString());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            PickMcFFA.getFfaLogger().debug("Deleted user ('" + user.getUuid() + " (" + user.getOfflinePlayer().getName() + ")') from DB in " + finish + " seconds.");
        });
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> deleteFromDB(UUID uuid) {
        return FFADatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement("DELETE FROM users WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            PickMcFFA.getFfaLogger().debug("Deleted user ('" + uuid + "') from DB in " + finish + " seconds.");
        });
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> deleteFromDB(String uuid) {
        return FFADatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement("DELETE FROM users WHERE uuid = ?");
            statement.setString(1, uuid);
            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            PickMcFFA.getFfaLogger().debug("Deleted user ('" + uuid + "') from DB in " + finish + " seconds.");
        });
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> updateDB(User user) {
        return FFADatabase.execute(conn -> {
            long start = System.currentTimeMillis();

            PreparedStatement statement = conn.prepareStatement(
                    "UPDATE users SET stats = ? WHERE uuid = ?");

            statement.setString(1, adapter.toJson(user.getStats()));

            statement.setString(2, user.getUuid().toString());

            statement.execute();

            double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
            PickMcFFA.getFfaLogger().debug("Updated User to DB in " + finish + " seconds.");
        });
    }

    @NotNull
    private static User dbToUser(@NotNull ResultSet resultSet) throws SQLException {
        String uuid = resultSet.getString("uuid");
        Stats stats = adapter.fromJson(resultSet.getString("stats"), new TypeToken<Stats>() {
        }.getType());

        return new User(uuid, stats);
    }
}
