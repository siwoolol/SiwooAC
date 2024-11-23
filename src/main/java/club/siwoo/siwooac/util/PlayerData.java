package club.siwoo.siwooac.util;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerData {

    private final Map<UUID, Integer> flightTicks = new HashMap<>();
    private final Map<UUID, Integer> flightSpeedViolationTicks = new HashMap<>();
    private final Map<UUID, Double> lastVerticalVelocity = new HashMap<>();
    private final Map<UUID, Boolean> recentCollision = new HashMap<>();
    private final Map<UUID, Integer> ticksSinceJump = new HashMap<>();
    private final Map<UUID, Boolean> recentlyFlagged = new HashMap<>();
    private final Map<UUID, Integer> criticalHits = new HashMap<>();
    private final Map<UUID, Integer> normalHits = new HashMap<>();
    private final Map<UUID, Integer> scaffoldVL = new HashMap<>();
    private final Map<UUID, Long> lastLeftClick = new HashMap<>();
    private final Map<UUID, Deque<Integer>> cpsSamples = new HashMap<>();
    private final Map<UUID, Long> lastAttackTime = new HashMap<>();
    private final Map<UUID, org.bukkit.util.Vector> lastAimDirection = new HashMap<>();
    private final Map<UUID, Deque<Double>> angleChanges = new HashMap<>();

    public void incrementFlightTicks(Player p) {
        flightTicks.put(p.getUniqueId(), getFlightTicks(p) + 1);
    }

    public void resetFlightTicks(Player p) {
        flightTicks.put(p.getUniqueId(), 0);
    }

    public int getFlightTicks(Player p) {
        return flightTicks.getOrDefault(p.getUniqueId(), 0);
    }

    public void incrementFlightSpeedViolationTicks(Player p) {
        flightSpeedViolationTicks.put(p.getUniqueId(), getFlightSpeedViolationTicks(p) + 1);
    }

    public void resetFlightSpeedViolationTicks(Player p) {
        flightSpeedViolationTicks.put(p.getUniqueId(), 0);
    }

    public int getFlightSpeedViolationTicks(Player p) {
        return flightSpeedViolationTicks.getOrDefault(p.getUniqueId(), 0);
    }

    public void setLastVerticalVelocity(Player p, double velocity) {
        lastVerticalVelocity.put(p.getUniqueId(), velocity);
    }

    public double getLastVerticalVelocity(Player p) {
        return lastVerticalVelocity.getOrDefault(p.getUniqueId(), 0.0);
    }

    public void setHasCollided(Player p, boolean collided) {
        recentCollision.put(p.getUniqueId(), collided);
    }

    public boolean hasCollidedRecently(Player p) {
        return recentCollision.getOrDefault(p.getUniqueId(), false);
    }

    public void setTicksSinceJump(Player p, int ticks) {
        ticksSinceJump.put(p.getUniqueId(), ticks);
    }

    public int getTicksSinceJump(Player p) {
        return ticksSinceJump.getOrDefault(p.getUniqueId(), 0);
    }

    public void setFlagged(Player p, boolean flagged) {
        recentlyFlagged.put(p.getUniqueId(), flagged);
    }

    public boolean wasRecentlyFlagged(Player p) {
        return recentlyFlagged.getOrDefault(p.getUniqueId(), false);
    }

    public double getCriticalHitRate(Player p) {
        int criticals = criticalHits.getOrDefault(p.getUniqueId(), 0);
        int normals = normalHits.getOrDefault(p.getUniqueId(), 0);
        int totalHits = criticals + normals;
        return totalHits == 0 ? 0.0 : (double) criticals / totalHits;
    }

    public void incrementCriticalHits(Player p) {
        criticalHits.put(p.getUniqueId(), getCriticalHits(p) + 1);
    }

    public void incrementNormalHits(Player p) {
        normalHits.put(p.getUniqueId(), getNormalHits(p) + 1);
    }

    public int getCriticalHits(Player p) {
        return criticalHits.getOrDefault(p.getUniqueId(), 0);
    }

    public int getNormalHits(Player p) {
        return normalHits.getOrDefault(p.getUniqueId(), 0);
    }

    public void incrementScaffoldVL(Player player) {
        scaffoldVL.put(player.getUniqueId(), getScaffoldVL(player) + 1);
    }

    public void resetScaffoldVL(Player player) {
        scaffoldVL.put(player.getUniqueId(), 0);
    }

    public int getScaffoldVL(Player player) {
        return scaffoldVL.getOrDefault(player.getUniqueId(), 0);
    }

    public long getLastLeftClick(Player player) {
        return lastLeftClick.getOrDefault(player.getUniqueId(), 0L);
    }

    public void setLastLeftClick(Player player, long time) {
        lastLeftClick.put(player.getUniqueId(), time);
    }

    public void addCPSSample(Player player, int cps) {
        Deque<Integer> playerSamples = cpsSamples.computeIfAbsent(player.getUniqueId(), k -> new LinkedList<>());
        if (playerSamples.size() == 20) {
            playerSamples.removeLast();
        }
        playerSamples.addFirst(cps);
    }

    public Deque<Integer> getCPSSamples(Player player) {
        return cpsSamples.get(player.getUniqueId());
    }

    public double getAverageCPS(Player player) {
        Deque<Integer> samples = getCPSSamples(player);
        if (samples == null || samples.isEmpty()) {
            return 0;
        }
        return samples.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
    }

    public double getStandardDeviationCPS(Player player) {
        Deque<Integer> samples = getCPSSamples(player);
        if (samples == null || samples.size() < 2) {
            return 0;
        }
        double mean = getAverageCPS(player);
        double variance = samples.stream().mapToDouble(i -> Math.pow(i - mean, 2)).average().orElse(0);
        return Math.sqrt(variance);
    }

    public long getLastAttackTime(Player player) {
        return lastAttackTime.getOrDefault(player.getUniqueId(), 0L);
    }

    public void setLastAttackTime(Player player, long time) {
        lastAttackTime.put(player.getUniqueId(), time);
    }

    public org.bukkit.util.Vector getLastAimDirection(Player player) { // Specify the fully qualified name
        return lastAimDirection.getOrDefault(player.getUniqueId(), new org.bukkit.util.Vector()); // Fully qualified name for default Vector
    }

    public void setLastAimDirection(Player player, org.bukkit.util.Vector direction) {
        lastAimDirection.put(player.getUniqueId(), direction);
    }

    public void addAngleChange(Player player, double angleChange) {
        Deque<Double> playerAngleChanges = angleChanges.computeIfAbsent(player.getUniqueId(), k -> new LinkedList<>());
        if (playerAngleChanges.size() == 20) {
            playerAngleChanges.removeLast();
        }
        playerAngleChanges.addFirst(angleChange);
    }

    public Deque<Double> getAngleChanges(Player player) {
        return angleChanges.computeIfAbsent(player.getUniqueId(), k -> new LinkedList<>()); // Ensure the Deque exists
    }

    public double getAverageAngleChange(Player player) {
        Deque<Double> changes = getAngleChanges(player);
        if (changes == null || changes.isEmpty()) {
            return 0;
        }
        return changes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public double getStandardDeviationAngleChange(Player player) {
        Deque<Double> changes = getAngleChanges(player);
        if (changes == null || changes.size() < 2) {
            return 0;
        }
        double mean = getAverageAngleChange(player);
        double variance = changes.stream().mapToDouble(a -> Math.pow(a - mean, 2)).average().orElse(0);
        return Math.sqrt(variance);
    }
}