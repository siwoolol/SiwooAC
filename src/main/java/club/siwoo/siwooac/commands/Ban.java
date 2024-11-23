package club.siwoo.siwooac.commands;

import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Ban implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender.hasPermission("siwooac.ban"))) {
            sender.sendMessage(Component.text("You don't have permission to use this command!").color(NamedTextColor.RED));
            return true;
        }

        if (args.length < 0) {
            sender.sendMessage(Component.text("Usage: /ban [player] [reason]").color(NamedTextColor.RED));
            return true;
        } else if (args.length >= 2) {
            sender.sendMessage(Component.text("Usage: /ban [player] [reason]").color(NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        Bukkit.getBanList(BanListType.PROFILE);

        String reason = args[1];

        assert target != null;
        if (target.isBanned()) {
            sender.sendMessage(Component.text("Player " + target + " is already banned!").color(NamedTextColor.RED));
            return true;
        }

        // Format the ban reason
        String formattedreason = ChatColor.RED.toString() + ChatColor.BOLD + "You were permanently banned from the server\n" + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + reason + ChatColor.GRAY + "\nIssued By: " + ChatColor.WHITE + sender;

        target.kickPlayer(formattedreason);
        Bukkit.getBanList(BanListType.PROFILE).addBan(target.getName(), formattedreason, null, sender.getName());

        return true;
    }
}
