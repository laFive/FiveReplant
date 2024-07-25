package me.lafive.fivereplant;

import me.lafive.fivereplant.event.BlockBreakListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FiveReplant extends JavaPlugin {

    @Override
    public void onEnable() {

        // Registering the listener
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        getLogger().info("Plugin enabled successfully! *kisses isaac*");

    }

    @Override
    public void onDisable() {

        getLogger().info("Plugin disabled successfully! *cries to isaac*");

    }


}
