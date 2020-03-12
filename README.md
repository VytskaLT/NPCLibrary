# NPCLibrary
A simple NPC library for Spigot 1.8 that uses ProtocolLib.

# Usage
Here's how to create a simple NPC:
```java
NPC npc = NPCLibrary.createNPC();
npc.setName("NPC");
npc.setUuid(UUID.randomUUID());
npc.setEntityId(new Random().nextInt(1000)); // This is not the best way to do this

npc.setTextures(new NPCTextures(value, signature)); // This sets the NPCs skin
npc.setSneaking(true); // This makes the NPC sneak
npc.setLocation(location); // This sets the NPCs location
npc.addPlayer(player); // This adds a player that should see the NPC
npc.addEventHandler(new NPCEventHandler() { // This adds a event handler for the NPC
    // This is called when the NPC is interacted with
    @Override
    public void onInteract(NPC npc, Player player, InteractType type) {
        // We can check the interact type
        if(type == InteractType.LEFT_CLICK) {
            // This is called when someone left clicks the NPC
        } else if(type == InteractType.RIGHT_CLICK) {
            // This is called when someone right clicks the NPC
        }
    }
});
npc.setSpawned(true); // This shows the NPC
```
