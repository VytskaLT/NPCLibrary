package net.VytskaLT.NPCLibrary.skin;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import lombok.Getter;

public class NPCTextures {

    @Getter
    private String value, signature;

    public NPCTextures(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public WrappedSignedProperty toProperty() {
        return new WrappedSignedProperty("textures", value, signature);
    }
}
