package net.VytskaLT.NPCLibrary;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import net.VytskaLT.NPCLibrary.listener.JoinQuitListener;
import net.VytskaLT.NPCLibrary.listener.MoveListener;
import net.VytskaLT.NPCLibrary.npc.NPC;
import net.VytskaLT.NPCLibrary.npc.NPCEventHandler;
import net.VytskaLT.NPCLibrary.npc.impl.NPCImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class NPCLibrary extends JavaPlugin {

    public static final int NPC_RADIUS = 64;

    private static NPCLibrary instance;
    private static List<NPCImpl> npcs = new ArrayList<>();
    public ProtocolManager manager;

    @Override
    public void onLoad() {
        instance = this;
        manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
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
                for (NPCImpl i : npcs) {
                    if(i.entityId == target) {
                        npc = i;
                        break;
                    }
                }
                if(npc == null) {
                    return;
                }
                event.setCancelled(true);
                NPCImpl finalNpc = npc;
                Bukkit.getScheduler().runTask(NPCLibrary.this, () -> {
                    for (NPCEventHandler handler : finalNpc.eventHandlers) {
                        handler.onInteract(finalNpc, event.getPlayer(), action == EnumWrappers.EntityUseAction.ATTACK ? NPCEventHandler.InteractType.LEFT_CLICK : NPCEventHandler.InteractType.RIGHT_CLICK);
                    }
                });
            }
        });
    }

    public static NPCLibrary getInstance() {
        return instance;
    }

    public static NPC createNPC() {
        return new NPCImpl(instance);
    }

    public static List<NPCImpl> getNPCs() {
        return npcs;
    }
}
