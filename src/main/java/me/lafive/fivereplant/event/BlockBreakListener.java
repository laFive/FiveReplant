package me.lafive.fivereplant.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockBreakListener implements Listener {

    private Map<UUID, Long> breakTimes;

    public BlockBreakListener() {
        this.breakTimes = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(BlockBreakEvent e) {

        // in case tries building in a disallowed region/something else cancels the event
        if (e.isCancelled()) return;
        Block broken = e.getBlock();

        /*
         * Checks if it's a crop
         * afaik crops are the only blocks where blockdata can be cast to ageable
         */
        if (!(broken.getBlockData() instanceof Ageable)) return;
        ItemStack item = e.getPlayer().getInventory().getItem(e.getPlayer().getInventory().getHeldItemSlot());

        /*
         * Accounts for bukkit's shitty inventory API
         * Not sure if these are redundant but better safe than sorry
         */
        if (item == null || item.getType() == null) return;
        if (item.getType().equals(Material.NETHERITE_HOE)) {

            // Cast is fine as checked for instanceof above
            Ageable crop = (Ageable) broken.getBlockData();
            // Max age = fully grown
            if (!(crop.getAge() == crop.getMaximumAge())) {

                /*
                 * We do this so that players dont accidentally break newly
                 * planted crops
                 */
                if ((System.currentTimeMillis() - breakTimes.getOrDefault(e.getPlayer().getUniqueId(), 0L)) < 1000L) {

                    e.setCancelled(true);

                }
                return;

            }
            // Stop the block from being broken
            e.setCancelled(true);

            /*
             * This method gets the drops that would occur from breaking
             * with the itemstack the player is holding. We then loop
             * through the drops and add to inv
             */
            broken.getDrops(item).forEach(drop -> {
                e.getPlayer().getInventory().addItem(drop);
            });
            // resets the growth of the ageable instance
            crop.setAge(0);
            // sets the ageable instance of the crop to the new one
            e.getBlock().setBlockData(crop);
            // Reset the timer
            breakTimes.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());

        }


    }

}
