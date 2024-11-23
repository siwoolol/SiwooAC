package club.siwoo.siwooac;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class ViolationManager {
    public Permission admin = new Permission("siwooac.admin");
    private final JavaPlugin plugin; // Reference to your plugin
    private FileConfiguration logsConfig;
    private File logsFile;

    public ViolationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        logsFile = new File(plugin.getDataFolder(), "aclogs.yml");
        logsConfig = YamlConfiguration.loadConfiguration(logsFile);
    }

    // Customize this based on your desired logging/punishment system
    public void flagPlayer(Player p, String violationType) {

        // Log the flag to the console
        Bukkit.getLogger().warning("[SiwooClub] " + p.getName() + " failed: " + violationType);

        if (p.getName().startsWith("bedrock_")) {
            p.sendMessage(Component.text("[SiwooAC] Bedrock Checks are temporarily disabled. However, you flagged ").color(NamedTextColor.BLUE)
                    .append(Component.text(violationType).color(NamedTextColor.BLUE))
                    .append(Component.text(" in terms of java checks. This was logged and we will keep an eye on you.").color(NamedTextColor.BLUE)
                    ));

            logViolation(p, violationType);
        } else {
            Bukkit.broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + "[SiwooAC] " + ChatColor.RED + p.getName() + " failed " + violationType + ChatColor.GOLD + " (Client: " + "e" + ")", String.valueOf(admin));

            p.sendMessage(Component.text("[SiwooAC] You have been flagged for: ").color(NamedTextColor.RED)
                    .append(Component.text(violationType))
                    .append(Component.text(". ").color(NamedTextColor.RED))
                    .append(Component.text("This Has Been Logged").color(NamedTextColor.GOLD))
                    .append(Component.text("and If you Think this is False, Please Contact an Administrator").color(NamedTextColor.RED))
            );

            logViolation(p, violationType);
        }
    }

    private void logViolation(Player p, String violationType) {
        String timestamp = LocalDateTime.now().toString(); // Get current timestamp
        String path = p.getUniqueId() + "." + timestamp;
        logsConfig.set(path + ".violation", violationType);
        logsConfig.set(path + ".timestamp", timestamp);

        try {
            logsConfig.save(logsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save violation log to aclogs.yml: " + e.getMessage());
        }
    }
}
