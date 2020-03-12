package net.VytskaLT.NPCLibrary.listener;

import net.VytskaLT.NPCLibrary.NPCLibrary;
import net.VytskaLT.NPCLibrary.impl.NPCImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for(NPCImpl npc : NPCLibrary.getNPCs()) {
            if(npc.offlinePlayers.contains(p.getUniqueId())) {
                npc.offlinePlayers.remove(p.getUniqueId());
                npc.players.add(p);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        npc.checkRange(p);
                    }
                }.runTaskLater(npc.plugin, 3);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void quit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        for(NPCImpl npc : NPCLibrary.getNPCs()) {
            if(npc.players.contains(p)) {
                npc.players.remove(p);
                npc.rangePlayers.remove(p);
                npc.offlinePlayers.add(p.getUniqueId());
                System.out.println();
            }
        }
    }
}
