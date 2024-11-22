package club.siwoo.siwooac.checks;

import club.siwoo.siwooac.ViolationManager;
import club.siwoo.siwooac.util.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

public class ScaffoldCheck implements Listener {

    private final ViolationManager violationManager;
    private final PlayerData playerData;

    public ScaffoldCheck(ViolationManager violationManager, PlayerData playerData) {
        this.violationManager = violationManager;
        this.playerData = playerData;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // Check if the player is placing a block below them while falling
        if (isScaffolding(player, event.getBlockPlaced().getLocation())) {
            playerData.incrementScaffoldVL(player);

            if (playerData.getScaffoldVL(player) > 3) {
                violationManager.flagPlayer(player, "Scaffold");
                event.setCancelled(true);
            }

        } else {
            playerData.resetScaffoldVL(player);
        }
    }

    private boolean isScaffolding(Player player, Location blockLocation) {
        Location playerLocation = player.getLocation();

        // Check if the block is placed below the player
        if (blockLocation.getBlockY() < playerLocation.getBlockY()
                && player.getVelocity().getY() < 0) { // Check if falling

            // Check if the player is looking in the general direction of the placed block
            Vector toBlock = blockLocation.toVector().subtract(playerLocation.toVector());
            double angle = playerLocation.getDirection().angle(toBlock);

            if (angle < 45) {
                // Check if the block is placed adjacent to the player's current block
                Location blockBelow = playerLocation.getBlock().getRelative(BlockFace.DOWN).getLocation();
                return blockLocation.getBlockX() == blockBelow.getBlockX() && blockLocation.getBlockZ() == blockBelow.getBlockZ();
            }
        }
        return false;
    }
}