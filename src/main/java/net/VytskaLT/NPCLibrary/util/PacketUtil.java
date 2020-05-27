package net.VytskaLT.NPCLibrary.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.VytskaLT.NPCLibrary.npc.AnimationType;
import net.VytskaLT.NPCLibrary.npc.impl.NPCImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class PacketUtil {
    private PacketUtil() {}

    public static void showNPC(NPCImpl npc, List<Player> players) {
        PacketContainer spawn = npc.manager.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawn.getIntegers().write(0, npc.entityId);
        spawn.getUUIDs().write(0, npc.uuid);
        spawn.getIntegers().write(1, CoordinateUtil.getFixedNumber(npc.location.getX()));
        spawn.getIntegers().write(2, CoordinateUtil.getFixedNumber(npc.location.getY()));
        spawn.getIntegers().write(3, CoordinateUtil.getFixedNumber(npc.location.getZ()));
        spawn.getBytes().write(0, CoordinateUtil.getByteAngle(npc.location.getYaw()));
        spawn.getBytes().write(1, CoordinateUtil.getByteAngle(npc.location.getPitch()));
        spawn.getDataWatcherModifier().write(0, createWatcher(npc));

        PacketContainer rotation = npc.manager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        rotation.getIntegers().write(0, npc.entityId);
        rotation.getBytes().write(0, CoordinateUtil.getByteAngle(npc.location.getYaw()));

        PacketContainer remove = npc.manager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        remove.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        remove.getPlayerInfoDataLists().write(0, Collections.singletonList(npc.infoData));

        try {
            for(Player p : players) {
                npc.manager.sendServerPacket(p, spawn);
                npc.manager.sendServerPacket(p, rotation);
                npc.inventory.update();
            }
        } catch(InvocationTargetException e) {
            npc.plugin.getLogger().log(Level.WARNING, "Could not send npc packets", e);
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for(Player p : players) {
                        if(npc.rangePlayers.contains(p)) {
                            npc.manager.sendServerPacket(p, remove);
                        }
                    }
                } catch(InvocationTargetException e) {
                    npc.plugin.getLogger().log(Level.WARNING, "Could not send remove packet", e);
                }
            }
        }.runTaskLater(npc.plugin, 10);
    }

    public static void addNPC(NPCImpl npc, List<Player> players) {
        PacketContainer info = npc.manager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        WrappedGameProfile gameProfile = new WrappedGameProfile(npc.uuid, npc.name);
        if(npc.getTextures() != null) {
            gameProfile.getProperties().put("textures", npc.textures.toProperty());
        }
        npc.profile = gameProfile;

        npc.infoData = new PlayerInfoData(gameProfile, 0, npc.mode.getNativeGameMode(), null);
        info.getPlayerInfoDataLists().write(0, Collections.singletonList(npc.infoData));

        try {
            for(Player p : players) {
                npc.manager.sendServerPacket(p, info);
            }
        } catch(InvocationTargetException e) {
            npc.plugin.getLogger().log(Level.WARNING, "Could not send add player packet", e);
        }
    }

    public static void removeNPC(NPCImpl npc, List<Player> players) {
        PacketContainer destroyPacket = npc.manager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroyPacket.getIntegerArrays().write(0, new int[]{npc.entityId});

        try {
            for(Player p : players) {
                npc.manager.sendServerPacket(p, destroyPacket);
            }
        } catch(InvocationTargetException e) {
            npc.plugin.getLogger().log(Level.WARNING, "Could not send destroy npc packet", e);
        }
    }

    public static void updateMetadata(NPCImpl npc, List<Player> players) {
        PacketContainer metadataPacket = npc.manager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        metadataPacket.getIntegers().write(0, npc.entityId);
        metadataPacket.getWatchableCollectionModifier().write(0, createWatcher(npc).getWatchableObjects());

        try {
            for(Player p : players) {
                npc.manager.sendServerPacket(p, metadataPacket);
            }
        } catch(InvocationTargetException e) {
            npc.plugin.getLogger().log(Level.WARNING, "Could not send metadata packet", e);
        }
    }

    public static void teleport(NPCImpl npc, Location location, List<Player> players) {
        PacketContainer teleport = npc.manager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        teleport.getIntegers().write(0, npc.entityId);
        teleport.getIntegers().write(1, CoordinateUtil.getFixedNumber(location.getX())); // X
        teleport.getIntegers().write(2, CoordinateUtil.getFixedNumber(location.getY())); // Y
        teleport.getIntegers().write(3, CoordinateUtil.getFixedNumber(location.getZ())); // Z
        teleport.getBytes().write(0, CoordinateUtil.getByteAngle(location.getYaw())); // Yaw
        teleport.getBytes().write(1, CoordinateUtil.getByteAngle(location.getPitch())); // Pitch
        teleport.getBooleans().write(0, true);

        PacketContainer rotation = npc.manager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        rotation.getIntegers().write(0, npc.entityId);
        rotation.getBytes().write(0, CoordinateUtil.getByteAngle(npc.location.getYaw()));

        try {
            for(Player p : players) {
                npc.manager.sendServerPacket(p, teleport);
                npc.manager.sendServerPacket(p, rotation);
            }
        } catch(InvocationTargetException e) {
            npc.plugin.getLogger().log(Level.WARNING, "Could not send teleport packet", e);
        }
    }

    public static void equip(NPCImpl npc, int slot, ItemStack item, List<Player> players) {
        PacketContainer equip = npc.manager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
        equip.getIntegers().write(0, npc.entityId);
        equip.getIntegers().write(1, slot);
        equip.getItemModifier().write(0, item);

        try {
            for(Player p : players) {
                npc.manager.sendServerPacket(p, equip);
            }
        } catch(InvocationTargetException e) {
            npc.plugin.getLogger().log(Level.WARNING, "Could not send equipment packet", e);
        }
    }

    public static void animation(NPCImpl npc, AnimationType type, List<Player> players) {
        PacketContainer animation = npc.manager.createPacket(PacketType.Play.Server.ANIMATION);
        animation.getIntegers().write(0, npc.entityId);
        animation.getIntegers().write(1, type.id);

        try {
            for(Player p : players) {
                npc.manager.sendServerPacket(p, animation);
            }
        } catch(InvocationTargetException e) {
            npc.plugin.getLogger().log(Level.WARNING, "Could not send animation packet", e);
        }
    }

    private static WrappedDataWatcher createWatcher(NPCImpl npc) {
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        watcher.setObject(0, (byte) npc.state.getId());
        if(npc.particleEffectColor != -1) {
            watcher.setObject(7, npc.particleEffectColor);
        }
        watcher.setObject(10, npc.skinLayers.getFlags()); // Skin flags

        return watcher;
    }
}
