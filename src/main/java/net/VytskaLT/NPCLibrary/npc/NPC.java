package net.VytskaLT.NPCLibrary.npc;

import net.VytskaLT.NPCLibrary.skin.NPCTextures;
import net.VytskaLT.NPCLibrary.skin.SkinLayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface NPC {
    String getName();
    void setName(String name);

    UUID getUuid();
    void setUuid(UUID uuid);

    int getEntityId();
    void setEntityId(int entityId);

    Location getLocation();
    void setLocation(Location location);

    NPCTextures getTextures();
    void setTextures(NPCTextures textures);

    NPCMode getMode();
    void setMode(NPCMode mode);

    boolean isSpawned();
    void setSpawned(boolean spawned);

    List<Player> getPlayers();
    List<Player> getRangePlayers();
    List<UUID> getOfflinePlayers();

    List<NPCEventHandler> getEventHandlers();
    void addEventHandler(NPCEventHandler eventHandler);
    void removeEventHandler(NPCEventHandler eventHandler);

    void addPlayer(Player player);
    void removePlayer(Player player);

    boolean isSkinLayerVisible(SkinLayer layer);
    void setSkinLayerVisible(SkinLayer layer, boolean visible);
    boolean isSneaking();
    void setSneaking(boolean sneaking);
    boolean isOnFire();
    void setOnFire(boolean fire);
}
