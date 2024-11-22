package club.siwoo.siwooac.checks;

import club.siwoo.siwooac.ViolationManager;
import club.siwoo.siwooac.util.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AutoClickerCheck implements Listener {

    private final ViolationManager violationManager;
    private final PlayerData playerData;

    private static final int MAX_CPS = 18;            // Max clicks per second
    private static final int MAX_RIGHT_CLICK_CPS = 5;  // Max right clicks per second
    private static final int MAX_CLICK_VARIATION = 2;   // Allowed variation in click intervals

    public AutoClickerCheck(ViolationManager violationManager) {
        this.violationManager = violationManager;
        playerData = new PlayerData();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            long now = System.currentTimeMillis();
            long lastClick = playerData.getLastLeftClick(player);

            if (lastClick != 0) {
                int cps = (int) (1000.0 / (now - lastClick));

                playerData.addCPSSample(player, cps);

                if (playerData.getCPSSamples(player).size() >= 20) {
                    double averageCPS = playerData.getAverageCPS(player);
                    double deviation = playerData.getStandardDeviationCPS(player);

                    if (averageCPS > 15 && deviation < 2.5) { // Adjust thresholds as needed
                        violationManager.flagPlayer(player, "AutoClicker");
                        event.setCancelled(true);
                    }
                }
            }

            playerData.setLastLeftClick(player, now);
        }
    }
}