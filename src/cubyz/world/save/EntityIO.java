package cubyz.world.save;

import java.io.IOException;

import cubyz.utils.Logger;
import cubyz.world.World;
import cubyz.world.entity.Entity;
import cubyz.world.entity.EntityType;
import pixelguys.json.JsonObject;

public class EntityIO {	
	public static Entity loadEntity(JsonObject json, World world) {
		String id = json.getString("id", "");
		Entity ent;
		EntityType type = world.getCurrentRegistries().entityRegistry.getByID(id);
		if (type == null) {
			Logger.warning("Could not load entity with id " + id);
			return null;
		}
		ent = type.newEntity(world);
		ent.loadFrom(json);
		return ent;
	}
	
}
