package net.VytskaLT.NPCLibrary.npc;

import net.VytskaLT.NPCLibrary.impl.NPCImpl;
import net.VytskaLT.NPCLibrary.util.PacketUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class NPCInventory {

    private static final ItemStack air = new ItemStack(Material.AIR);

    private NPCImpl npc;
    private Map<EquipmentSlot, ItemStack> slots = new HashMap<>();

    public NPCInventory(NPCImpl npc) {
        this.npc = npc;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            slots.put(slot, air);
        }
    }

    public void set(EquipmentSlot slot, ItemStack item) {
        item = item == null ? air : item;
        slots.replace(slot, item);
        if(npc.spawned) {
            PacketUtil.equip(npc, slot.id, item, npc.rangePlayers);
        }
    }

    public ItemStack get(EquipmentSlot slot) {
        return slots.get(slot);
    }

    public void update() {
        slots.forEach((slot, item) -> {
            if(item.getType() != Material.AIR) {
                PacketUtil.equip(npc, slot.id, item, npc.rangePlayers);
            }
        });
    }

    public enum EquipmentSlot {
        HAND(0), BOOTS(1), LEGGINGS(2), CHESTPLATE(3), HELMET(4);

        int id;

        EquipmentSlot(int id) {
            this.id = id;
        }
    }
}
