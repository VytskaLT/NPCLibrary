package net.VytskaLT.NPCLibrary;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.VytskaLT.NPCLibrary.impl.NPCImpl;
import net.VytskaLT.NPCLibrary.listener.JoinQuitListener;
import net.VytskaLT.NPCLibrary.listener.MoveListener;
import net.VytskaLT.NPCLibrary.npc.NPCEventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NPCLibrary extends JavaPlugin {

    public static final int NPC_RADIUS = 64;

    private static List<NPCImpl> npcList = new ArrayList<>();
    private static ProtocolManager manager;

    @Override
    public void onEnable() {
        manager = ProtocolLibrary.getProtocolManager();

        getServer().getPluginManager().registerEvents(new MoveListener(), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);

        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.LOW, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(event.isCancelled()) {
                    return;
                }
                PacketContainer packet = event.getPacket();
                int target = packet.getIntegers().read(0);
                EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
                if(action == EnumWrappers.EntityUseAction.INTERACT) {
                    return;
                }
                NPCImpl npc = null;
                for (NPCImpl i : npcList) {
                    if(i.entityId == target) {
                        npc = i;
                        break;
                    }
                }
                if(npc == null) {
                    return;
                }
                event.setCancelled(true);
                for (NPCEventHandler handler : npc.eventHandlers) {
                    handler.onInteract(npc, event.getPlayer(), action == EnumWrappers.EntityUseAction.ATTACK ? NPCEventHandler.InteractType.LEFT_CLICK : NPCEventHandler.InteractType.RIGHT_CLICK);
                }
            }
        });
    }

    public static ProtocolManager getManager() {
        return manager;
    }

    public static void addNPC(NPCImpl npc) {
        npcList.add(npc);
    }

    public static List<NPCImpl> getNPCs() {
        return new ArrayList<>(npcList);
    }
}
