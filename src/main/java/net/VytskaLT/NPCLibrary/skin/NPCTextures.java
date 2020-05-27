package net.VytskaLT.NPCLibrary.skin;

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NPCTextures {

    @Getter
    private final String value, signature;

    public WrappedSignedProperty toProperty() {
        return new WrappedSignedProperty("textures", value, signature);
    }
}
