package net.VytskaLT.NPCLibrary.util;

import net.VytskaLT.NPCLibrary.NPCLibrary;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DistanceUtil {
    private DistanceUtil() {}

    public static boolean isInRange(Player player, Location location) {
        if(player.getWorld() != location.getWorld()) {
            return false;
        }
        return !(location.distance(player.getLocation()) > NPCLibrary.NPC_RADIUS);
    }
}
