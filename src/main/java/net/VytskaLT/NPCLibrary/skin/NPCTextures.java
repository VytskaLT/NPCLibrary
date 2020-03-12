package net.VytskaLT.NPCLibrary.skin;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;

public class NPCTextures {

    private String value, signature;

    public NPCTextures(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    public WrappedSignedProperty toProperty() {
        return new WrappedSignedProperty("textures", value, signature);
    }
}
