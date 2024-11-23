package club.siwoo.siwooac;

import club.siwoo.siwooac.checks.*;
import club.siwoo.siwooac.commands.Ban;
import club.siwoo.siwooac.util.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SiwooAC extends JavaPlugin implements CommandExecutor {

    private ViolationManager violationManager;
    private PlayerData playerData = null;

    private final SiwooAC plugin;

    public SiwooAC(SiwooAC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEnable() {
        EnableChecks();
        LoadCommands();
    }

    private void EnableChecks() {
        violationManager = new ViolationManager(this); // You might want to pass 'this' to it if it needs the plugin instance
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
        getServer().getPluginManager().registerEvents(new Ban(), this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(Component
                    .text("")
                    .append(Component.text("SiwooAC - v1.0.1").color(NamedTextColor.GOLD))
                    .append(Component
                            .text("\n /siwooac help"))
            );
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(Component
                    .text("")
                    .append(Component.text("SiwooAC Commands").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                    .append(Component.text(" - ").color(NamedTextColor.GOLD))
                    .append(Component.text("Page 1/1").color(NamedTextColor.AQUA))
                    .append(Component.text("\n"))

                    // /ban
                    .append(Component.text("/ban").color(NamedTextColor.AQUA))
                    .append(Component.text(" - ").color(NamedTextColor.GOLD))
                    .append(Component.text("Bans a player").color(NamedTextColor.AQUA))
            );
            return true;
        }
        return false;
    }
}
