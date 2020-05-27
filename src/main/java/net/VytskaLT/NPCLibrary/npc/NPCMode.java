package net.VytskaLT.NPCLibrary.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;

public enum NPCMode {
    NORMAL, SPECTATOR;

    public EnumWrappers.NativeGameMode getNativeGameMode() {
        return this == NPCMode.SPECTATOR ? EnumWrappers.NativeGameMode.SPECTATOR : EnumWrappers.NativeGameMode.CREATIVE;
    }
}
