package club.siwoo.siwooac.checks;

import club.siwoo.siwooac.ViolationManager;
import club.siwoo.siwooac.util.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Deque;

public class KillAuraCheck implements Listener {

    private final ViolationManager violationManager;
    private final PlayerData playerData;

    public KillAuraCheck(ViolationManager violationManager, PlayerData playerData) {
        this.violationManager = violationManager;
        this.playerData = playerData;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            if (attacker.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            // KillAura A: Check for impossible attack angles
            if (isImpossibleAngle(attacker, victim)) {
                violationManager.flagPlayer(attacker, "Kill Aura (Impossible Angle)");
                event.setCancelled(true);
            }

            // KillAura B: Check for consistent head snapping
            if (isHeadSnapping(attacker, victim)) {
                violationManager.flagPlayer(attacker, "Kill Aura (Head Snapping)");
                event.setCancelled(true);
            }

            // KillAura C: Check for high attack frequency
            long now = System.currentTimeMillis();
            long lastAttack = playerData.getLastAttackTime(attacker);

            if (lastAttack != 0 && now - lastAttack < 100) { // Adjust threshold as needed
                violationManager.flagPlayer(attacker, "Kill Aura (High APS)");
                event.setCancelled(true);
            }

            playerData.setLastAttackTime(attacker, now);
        }
    }

    private boolean isImpossibleAngle(Player attacker, Player victim) {
        Location attackerEyeLocation = attacker.getEyeLocation();
        Vector attackerDirection = attackerEyeLocation.getDirection();

        double hitboxWidth = 0.6; // Default player width
        double hitboxHeight = 1.8; // Default player height
        Location victimLocation = victim.getLocation().add(0, hitboxHeight / 2, 0); // Center the hitbox

        Vector toVictim = victimLocation.toVector().subtract(attackerEyeLocation.toVector());

        double angle = attackerDirection.angle(toVictim);
        double distance = attacker.getLocation().distance(victim.getLocation());
        double maxAngle = Math.toDegrees(Math.atan2(hitboxWidth / 2, distance));

        return angle > maxAngle + 20; // Add some leniency
    }

    private boolean isHeadSnapping(Player attacker, Player victim) {
        Location attackerEyeLocation = attacker.getEyeLocation();
        Vector victimDirection = victim.getLocation().toVector().subtract(attackerEyeLocation.toVector()).normalize();

        Vector lastLookDirection = playerData.getLastAimDirection(attacker);
        double angleChange = lastLookDirection.angle(attackerEyeLocation.getDirection());

        playerData.setLastAimDirection(attacker, attackerEyeLocation.getDirection());
        playerData.addAngleChange(attacker, angleChange);

        Deque<Double> angleChanges = playerData.getAngleChanges(attacker);
        if (angleChanges.size() >= 20) {
            double averageAngleChange = playerData.getAverageAngleChange(attacker);
            double standardDeviation = playerData.getStandardDeviationAngleChange(attacker);

            if (averageAngleChange < 5 && standardDeviation < 2.5 && angleChange > 20) {
                return true; // Suspicious head snapping
            }
        }

        return false;
    }
}