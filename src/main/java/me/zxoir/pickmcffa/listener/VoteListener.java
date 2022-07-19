package me.zxoir.pickmcffa.listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/9/2022
 */
public class VoteListener implements Listener {
    @Getter
    private static final HashMap<UUID, Integer> pendingVoteReward = new HashMap<>();
    @Getter
    private static final ItemStack flintAndSteelItemStack = new ItemStackBuilder(Material.FLINT_AND_STEEL).withName("&eFlint and Steel").withLore("&7Can only be placed on &aGrass &7or &aDirt &7blocks.").withDurability(64).build();

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onVote(@NotNull VotifierEvent event) {
        Vote vote = event.getVote();

        OfflinePlayer voter = Bukkit.getOfflinePlayer(vote.getUsername());
        if (voter == null || !voter.hasPlayedBefore())
            return;

        if (voter.isOnline()) {
            if (Utils.isInPvpArea(voter.getPlayer())) {
                voter.getPlayer().sendMessage(ConfigManager.getVotePendingMessage());
                addPendingVoteReward(voter.getUniqueId());
            } else {
                voter.getPlayer().sendMessage(ConfigManager.getVoteReceivedMessage());
                addFlintAndSteelToInventory(voter.getPlayer().getInventory(), 0);
            }

        } else addPendingVoteReward(voter.getUniqueId());

    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!pendingVoteReward.containsKey(player.getUniqueId()))
            return;

        int amount = pendingVoteReward.get(player.getUniqueId());
        ItemStack itemStack = flintAndSteelItemStack.clone();
        short durability = (short) (64 - amount);
        itemStack.setDurability(durability);
        addFlintAndSteelToInventory(player.getInventory(), amount);
        pendingVoteReward.remove(player.getUniqueId());

        player.sendMessage(ConfigManager.getVoteReceivedOfflineMessage());
    }

    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (!pendingVoteReward.containsKey(player.getUniqueId()))
            return;

        int amount = pendingVoteReward.get(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> addFlintAndSteelToInventory(player.getInventory(), amount), 1);
        pendingVoteReward.remove(player.getUniqueId());
    }

    private void addPendingVoteReward(UUID uuid) {
        if (!pendingVoteReward.containsKey(uuid)) {
            pendingVoteReward.put(uuid, 0);
            return;
        }

        pendingVoteReward.put(uuid, pendingVoteReward.get(uuid) + 1);
    }

    private void addFlintAndSteelToInventory(@NotNull PlayerInventory inventory, int amount) {
        ItemStack flintAndSteel = getFlintAndSteel(inventory);

        if (!inventory.contains(Material.FLINT_AND_STEEL) || flintAndSteel == null) {
            ItemStack itemStack = flintAndSteelItemStack.clone();
            itemStack.setDurability((short) (64 - amount));
            inventory.addItem(itemStack);
        } else {
            int currentAmount = (64 - flintAndSteel.getDurability()) + 1;
            short durability = (short) (64 - (amount + currentAmount));

            if (durability < 0)
                durability = 0;

            flintAndSteel.setDurability(durability);
        }

    }

    @Nullable
    private ItemStack getFlintAndSteel(@NotNull PlayerInventory inventory) {
        boolean flintAndSteelFound = false;
        ItemStack flintAndSteel = null;

        for (ItemStack itemStack : inventory.getContents()) {

            if (itemStack != null && itemStack.getType().equals(Material.FLINT_AND_STEEL)) {

                if (!flintAndSteelFound) {
                    flintAndSteelFound = true;
                    flintAndSteel = itemStack;
                } else {
                    inventory.remove(itemStack);
                    flintAndSteel.setDurability((short) (flintAndSteel.getDurability() - 1));
                }

            }

        }

        return flintAndSteel;
    }

}
