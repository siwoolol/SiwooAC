package club.siwoo.siwooac.util;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerData {

    private final Map<UUID, Integer> flightTicks = new HashMap<>();
    private final Map<UUID, Integer> flightSpeedViolationTicks = new HashMap<>();
    private final Map<UUID, Double> lastVerticalVelocity = new HashMap<>();
    private final Map<UUID, Boolean> recentCollision = new HashMap<>();
    private final Map<UUID, Integer> ticksSinceJump = new HashMap<>();
    private final Map<UUID, Long> lastLeftClickTime = new HashMap<>();
    private final Map<UUID, Long> lastRightClickTime = new HashMap<>();
    private final Map<UUID, Boolean> recentlyFlagged = new HashMap<>();
    private final Map<UUID, Queue<Integer>> leftClickIntervals = new HashMap<>();
    private final Map<UUID, Queue<Integer>> rightClickIntervals = new HashMap<>();
    private final Map<UUID, Integer> criticalHits = new HashMap<>();
    private final Map<UUID, Integer> normalHits = new HashMap<>();

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

    public long getLastClickTime(Player p, boolean isRightClick) {
        return isRightClick ? lastRightClickTime.getOrDefault(p.getUniqueId(), 0L)
                : lastLeftClickTime.getOrDefault(p.getUniqueId(), 0L);
    }

    public void setLastClickTime(Player p, long time, boolean isRightClick) {
        if (isRightClick) {
            lastRightClickTime.put(p.getUniqueId(), time);
        } else {
            lastLeftClickTime.put(p.getUniqueId(), time);
        }
    }

    public void addClickInterval(Player p, int interval, boolean isRightClick) {
        Queue<Integer> intervals = isRightClick ? rightClickIntervals.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>())
                : leftClickIntervals.computeIfAbsent(p.getUniqueId(), k -> new ArrayDeque<>());
        intervals.add(interval);
        if (intervals.size() > 10) {  // Keep a maximum of 10 intervals for analysis
            intervals.poll();
        }
    }

    public int analyzeClickIntervals(Player p, int expectedInterval, int allowedVariation, boolean isRightClick) {
        Queue<Integer> intervals = isRightClick ? rightClickIntervals.get(p.getUniqueId())
                : leftClickIntervals.get(p.getUniqueId());
        if (intervals == null || intervals.size() < 10) {
            return 0;
        }

        int violations = 0;
        for (int interval : intervals) {
            if (Math.abs(interval - expectedInterval) > allowedVariation) {
                violations++;
            }
        }
        return violations;
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
}
