package net.VytskaLT.NPCLibrary.npc;

public class NPCStateHandler {
    private boolean fire, sneak;

    public boolean isOnFire() {
        return fire;
    }

    public void setOnFire(boolean fire) {
        this.fire = fire;
    }

    public boolean isSneaking() {
        return sneak;
    }

    public void setSneaking(boolean sneak) {
        this.sneak = sneak;
    }

    public int getId() {
        if(fire && sneak) {
            return 3;
        } else if(fire) {
            return 1;
        } else if(sneak) {
            return 2;
        }
        return 0;
    }
}
