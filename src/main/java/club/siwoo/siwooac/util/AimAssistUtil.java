package club.siwoo.siwooac.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class AimAssistUtil {

    private static final int MAX_SAMPLES = 20; // Number of angle changes to store
    private static final Map<UUID, Deque<Double>> angleChangeHistory = new HashMap<>();
    private static final Map<UUID, Long> lastAttackTime = new HashMap<>();

    public static boolean isSuspiciouslyAccurate(Player attacker, Player victim) {
        UUID attackerUUID = attacker.getUniqueId();
        Location attackerEyes = attacker.getEyeLocation();
        Location victimHead = victim.getEyeLocation().add(0, -0.5, 0); // Approximate head location
        Vector victimDirection = victimHead.toVector().subtract(attackerEyes.toVector());

        // Calculate the angle between the attacker's look direction and the victim
        double angle = attackerEyes.getDirection().angle(victimDirection);

        // Add the angle change to the history
        Deque<Double> playerAngleHistory = angleChangeHistory.computeIfAbsent(attackerUUID, k -> new LinkedList<>());
        if (playerAngleHistory.size() == MAX_SAMPLES) {
            playerAngleHistory.removeLast();
        }
        playerAngleHistory.addFirst(angle);

        // Calculate the average and standard deviation of the angle changes
        double averageAngle = playerAngleHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double standardDeviation = Math.sqrt(playerAngleHistory.stream()
                .mapToDouble(a -> Math.pow(a - averageAngle, 2))
                .average().orElse(0.0));

        // Check for consistent small angle changes (AimA)
        if (standardDeviation < 3.5 && playerAngleHistory.stream().allMatch(a -> a < 10)) {
            return true;
        }

        // Check for sudden large angle changes (AimB)
        long lastAttack = lastAttackTime.getOrDefault(attackerUUID, 0L);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttack < 500 && angle > 60) { // 500ms threshold
            return true;
        }

        lastAttackTime.put(attackerUUID, currentTime);

        return false;
    }
}
