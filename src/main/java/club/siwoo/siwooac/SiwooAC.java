package club.siwoo.siwooac;

import club.siwoo.siwooac.checks.*;
import club.siwoo.siwooac.commands.Ban;
import club.siwoo.siwooac.commands.Help;
import club.siwoo.siwooac.util.PlayerData;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class SiwooAC extends JavaPlugin implements CommandExecutor {

    private ViolationManager violationManager;
    private PlayerData playerData = null;

    @Override
    public void onEnable() {
        EnableChecks();
        LoadCommands();
    }

    private void EnableChecks() {
        violationManager = new ViolationManager(this);
        playerData = new PlayerData();
        new ReachCheck(violationManager, playerData);
        getServer().getPluginManager().registerEvents(new FlightCheck(violationManager), this);
        getServer().getPluginManager().registerEvents(new AutoClickerCheck(violationManager), this);
        getServer().getPluginManager().registerEvents(new AimAssistCheck(violationManager), this);
        getServer().getPluginManager().registerEvents(new CriticalsCheck(violationManager), this);
        getServer().getPluginManager().registerEvents(new HitboxCheck(violationManager), this);
        getServer().getPluginManager().registerEvents(new ScaffoldCheck(violationManager, playerData), this);
        getServer().getPluginManager().registerEvents(new KillAuraCheck(violationManager, playerData), this);
    }

    private void LoadCommands() {
        getCommand("ban").setExecutor(new Ban());
        getCommand("siwooac").setExecutor(new Help());
    }
}
