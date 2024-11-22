package club.siwoo.siwooac.checks;

import club.siwoo.siwooac.ViolationManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class HitboxCheck implements Listener {

    private final ViolationManager violationManager;

    public HitboxCheck(ViolationManager violationManager) {
        this.violationManager = violationManager;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            if (isHitboxInvalid(attacker, victim)) {
                violationManager.flagPlayer(attacker, "Invalid Hitbox");
                event.setCancelled(true);
            }
        }
    }

    private boolean isHitboxInvalid(Player attacker, Player victim) {
        Location attackerEyeLocation = attacker.getEyeLocation();
        Vector attackerDirection = attackerEyeLocation.getDirection();

        double hitboxWidth = 0.6;
        double hitboxHeight = 1.8;
        Location victimLocation = victim.getLocation().add(0, hitboxHeight / 2, 0);

        Vector toVictim = victimLocation.toVector().subtract(attackerEyeLocation.toVector());
        double angle = attackerDirection.angle(toVictim);
        double distance = attacker.getLocation().distance(victim.getLocation());
        double maxAngle = Math.toDegrees(Math.atan2(hitboxWidth / 2, distance));

        if (angle > maxAngle) {
            return true;
        }

        return false;
    }
}