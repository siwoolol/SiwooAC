package club.siwoo.siwooac.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class Help implements CommandExecutor, Listener {

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
