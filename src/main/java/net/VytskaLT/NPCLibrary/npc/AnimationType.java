package net.VytskaLT.NPCLibrary.npc;

public enum AnimationType {
    SWING(0), TAKE_DAMAGE(1), LEAVE_BED(2), EAT_FOOD(3), CRITICAL_EFFECT(4), MAGIC_CRITICAL_EFFECT(5);

    public final int id;

    AnimationType(int id) {
        this.id = id;
    }
}
