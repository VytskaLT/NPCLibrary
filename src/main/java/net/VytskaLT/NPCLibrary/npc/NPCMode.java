package net.VytskaLT.NPCLibrary.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;

public enum NPCMode {
    NORMAL, SPECTATOR;

    public EnumWrappers.NativeGameMode getNativeGameMode() {
        if (this == NPCMode.SPECTATOR) {
            return EnumWrappers.NativeGameMode.SPECTATOR;
        }
        return EnumWrappers.NativeGameMode.CREATIVE;
    }
}
