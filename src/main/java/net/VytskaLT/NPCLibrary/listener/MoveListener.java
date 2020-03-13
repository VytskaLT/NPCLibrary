package net.VytskaLT.NPCLibrary.listener;

import net.VytskaLT.NPCLibrary.NPCLibrary;
import net.VytskaLT.NPCLibrary.npc.impl.NPCImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void move(PlayerMoveEvent e) {
        Location to = e.getTo();
        Location from = e.getFrom();
        if(to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }
        Player player = e.getPlayer();
        for(NPCImpl npc : NPCLibrary.getNPCs()) {
            if(to.getWorld() != npc.location.getWorld() || !npc.players.contains(player)) {
                continue;
            }
            if(to.distance(npc.getLocation()) > NPCLibrary.NPC_RADIUS) {
                if(npc.rangePlayers.contains(player)) {
                    npc.rangePlayers.remove(player);
                    if(npc.spawned) {
                        npc.rangePlayersUpdated(player);
                    }
                }
            } else {
                if(!npc.rangePlayers.contains(player)) {
                    npc.rangePlayers.add(player);
                    if(npc.spawned) {
                        npc.rangePlayersUpdated(player);
                    }
                }
            }
        }
    }
}
