package net.VytskaLT.NPCLibrary.npc.impl;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import lombok.Getter;
import net.VytskaLT.NPCLibrary.NPCLibrary;
import net.VytskaLT.NPCLibrary.npc.*;
import net.VytskaLT.NPCLibrary.skin.NPCTextures;
import net.VytskaLT.NPCLibrary.skin.SkinLayer;
import net.VytskaLT.NPCLibrary.skin.SkinLayerHandler;
import net.VytskaLT.NPCLibrary.util.DistanceUtil;
import net.VytskaLT.NPCLibrary.util.PacketUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class NPCImpl implements NPC {

    public final NPCLibrary plugin;
    public final ProtocolManager manager;

    @Getter
    public String name;
    @Getter
    public UUID uuid;
    @Getter
    public int entityId;
    @Getter
    public Location location;
    @Getter
    public NPCTextures textures;
    public final SkinLayerHandler skinLayers;
    public final NPCStateHandler state;
    public final NPCInventory inventory;
    @Getter
    public String effectParticles;
    @Getter
    public NPCMode mode;
    @Getter
    public boolean spawned;
    public boolean removed;
    public WrappedGameProfile profile;
    public PlayerInfoData infoData;
    @Getter
    public List<Player> players;
    @Getter
    public List<Player> rangePlayers;
    @Getter
    public List<UUID> offlinePlayers;
    @Getter
    public List<NPCEventHandler> eventHandlers;

    public NPCImpl(NPCLibrary plugin) {
        NPCLibrary.getNPCs().add(this);

        this.plugin = plugin;
        this.manager = NPCLibrary.getInstance().manager;
        this.skinLayers = new SkinLayerHandler();
        this.state = new NPCStateHandler();
        this.inventory = new NPCInventory(this);
        this.mode = NPCMode.NORMAL;
        this.players = new ArrayList<>();
        this.rangePlayers = new ArrayList<>();
        this.offlinePlayers = new ArrayList<>();
        this.eventHandlers = new ArrayList<>();
    }

    public void rangePlayersUpdated(Player player) {
        if(rangePlayers.contains(player)) {
            PacketUtil.addNPC(this, Collections.singletonList(player));
            PacketUtil.showNPC(this, Collections.singletonList(player));
        } else {
            PacketUtil.removeNPC(this, Collections.singletonList(player));
        }
    }

    public void refreshNPC() {
        refreshNPC(rangePlayers);
    }

    public void refreshNPC(List<Player> players) {
        if(spawned) {
            PacketUtil.removeNPC(this, players);
            PacketUtil.addNPC(this, players);
            PacketUtil.showNPC(this, players);
        }
    }

    public void remove() {
        if(!removed) {
            setSpawned(false);
            NPCLibrary.getNPCs().remove(this);
            removed = true;
        }
    }

    public void checkRange(Player player) {
        if(DistanceUtil.isInRange(player, location) && spawned && !rangePlayers.contains(player)) {
            rangePlayers.add(player);
            rangePlayersUpdated(player);
        }
    }

    public void addEventHandler(NPCEventHandler eventHandler) {
        if(!eventHandlers.contains(eventHandler)) {
            eventHandlers.add(eventHandler);
        }
    }

    public void removeEventHandler(NPCEventHandler eventHandler) {
        eventHandlers.remove(eventHandler);
    }

    public void addPlayer(Player player) {
        if(!players.contains(player)) {
            players.add(player);
            checkRange(player);
        }
    }

    public void removePlayer(Player player) {
        if(players.contains(player)) {
            players.remove(player);
            rangePlayers.remove(player);
            offlinePlayers.remove(player.getUniqueId());
            if(player.isOnline()) {
                rangePlayersUpdated(player);
            }
        }
    }

    public void playAnimation(AnimationType type) {
        if(spawned) {
            PacketUtil.animation(this, type, rangePlayers);
        }
    }

    public void setEffectParticles(String hex) {
        effectParticles = hex;
        if(spawned) {
            PacketUtil.updateMetadata(this, rangePlayers);
        }
    }

    public boolean isSkinLayerVisible(SkinLayer layer) {
        return skinLayers.isVisible(layer);
    }

    public void setSkinLayerVisible(SkinLayer layer, boolean visible) {
        skinLayers.setLayer(layer, visible);
        if(spawned) {
            PacketUtil.updateMetadata(this, rangePlayers);
        }
    }

    public ItemStack getEquipmentSlot(NPCInventory.EquipmentSlot slot) {
        return inventory.get(slot);
    }

    public void setEquipmentSlot(NPCInventory.EquipmentSlot slot, ItemStack item) {
        inventory.set(slot, item);
    }

    public boolean isSneaking() {
        return state.isSneaking();
    }

    public void setSneaking(boolean sneaking) {
        state.setSneaking(sneaking);
        if(spawned) {
            PacketUtil.updateMetadata(this, rangePlayers);
        }
    }

    public boolean isOnFire() {
        return state.isOnFire();
    }

    public void setOnFire(boolean fire) {
        state.setOnFire(fire);
        if(spawned) {
            PacketUtil.updateMetadata(this, rangePlayers);
        }
    }

    public void setMode(NPCMode mode) {
        this.mode = mode;
        if(spawned) {
            refreshNPC();
        }
    }

    public void setSpawned(boolean spawned) {
        if(removed) {
            throw new IllegalStateException("NPC is removed");
        }
        if(spawned && !this.spawned) {
            for(Player player : players) {
                if(DistanceUtil.isInRange(player, location)) {
                    rangePlayers.add(player);
                } else {
                    rangePlayers.remove(player);
                }
            }
            PacketUtil.addNPC(this, rangePlayers);
            PacketUtil.showNPC(this, rangePlayers);
            this.spawned = true;
        } else if(!spawned && this.spawned) {
            PacketUtil.removeNPC(this, rangePlayers);
            this.spawned = false;
        }
    }

    public void setName(String name) {
        if(name.equals(this.name)) {
            return;
        }
        if(name.length() > 16) {
            throw new IllegalArgumentException("NPC name cannot be longer than 16 characters");
        }
        this.name = name;
        if(spawned) {
            refreshNPC();
        }
    }

    public void setUuid(UUID uuid) {
        if(spawned) {
            setSpawned(false);
            this.uuid = uuid;
            setSpawned(true);
        } else {
            this.uuid = uuid;
        }
    }

    public void setEntityId(int entityId) {
        if(spawned) {
            setSpawned(false);
            this.entityId = entityId;
            setSpawned(true);
        } else {
            this.entityId = entityId;
        }
    }

    public void setLocation(Location location) {
        this.location = location;
        if(spawned) {
            PacketUtil.teleport(this, location, rangePlayers);
            List<Player> list = new ArrayList<>(rangePlayers);
            for (Player player : list) {
                if(!DistanceUtil.isInRange(player, location)) {
                    rangePlayers.remove(player);
                    rangePlayersUpdated(player);
                }
            }
        }
    }

    public void setTextures(NPCTextures textures) {
        this.textures = textures;
        if(spawned) {
            refreshNPC();
        }
    }
}
