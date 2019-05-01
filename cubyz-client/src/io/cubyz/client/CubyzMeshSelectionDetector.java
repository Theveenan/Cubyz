package io.cubyz.client;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.jungle.renderers.IRenderer;
import io.cubyz.blocks.BlockInstance;
import io.cubyz.math.Vector3fi;
import io.cubyz.world.BlockSpatial;
import io.cubyz.world.Chunk;

public class CubyzMeshSelectionDetector {

	protected IRenderer render;
	protected Vector3f dir = new Vector3f();
	protected Vector3f min = new Vector3f(), max = new Vector3f();
	protected Vector2f nearFar = new Vector2f();
	protected BlockInstance selectedSpatial;
	
	public CubyzMeshSelectionDetector(IRenderer render) {
		this.render = render;
	}
	
	/**
	 * Return selected block instance
	 * @return selected block instance, or null if none.
	 */
	public BlockInstance getSelectedBlockInstance() {
		return selectedSpatial;
	}
	
	public void selectSpatial(Chunk[] chunks, Vector3fi position, Vector3f dir) {
		Vector3f transformedPosition = new Vector3f(position.relX, position.y+1.5F, position.relZ);
	    float closestDistance = Float.POSITIVE_INFINITY;
	    selectedSpatial = null;
	    //position.x = position.z = 0;
	    for (Chunk ch : chunks) {
	    	synchronized (ch) {
	    		// using an array speeds up things and reduce Concurrent Modification Exceptions
	    		BlockInstance[] array = ch.getVisibles().toArray(new BlockInstance[ch.getVisibles().size()]);
			    for (BlockInstance bi : array) {
			    	if(!bi.getBlock().isSolid())
			    		continue;
			        ((BlockSpatial) bi.getSpatial()).setSelected(false);
			        min.set(new Vector3f(bi.getX() - position.x, bi.getY(), bi.getZ() - position.z));
			        max.set(min);
			        min.add(-0.5f, -0.5f, -0.5f); // -scale, -scale, -scale
			        max.add(0.5f, 0.5f, 0.5f); // scale, scale, scale
			        if (Intersectionf.intersectRayAab(transformedPosition, dir, min, max, nearFar) && nearFar.x < closestDistance) {
			            closestDistance = nearFar.x;
			            selectedSpatial = bi;
			        }
			    }
	    	}
	    }
	    if (selectedSpatial != null) {
	        ((BlockSpatial) selectedSpatial.getSpatial()).setSelected(true);
	        //System.out.println(selectedSpatial.getPosition());
	    }
	}
	
	// Returns the free block right next to the currently selected block.
	public Vector3i getEmptyPlace(Vector3f position) {
		position = new Vector3f(position.x, position.y+1.5F, position.z);
		if(selectedSpatial != null) {
			Vector3i pos = selectedSpatial.getPosition();
			pos.add(-(int)Math.signum(dir.x), 0, 0);
			min.set(pos);
	        max.set(pos);
	        min.add(-0.5f, -0.5f, -0.5f); // -scale, -scale, -scale
	        max.add(0.5f, 0.5f, 0.5f); // scale, scale, scale
	        if (Intersectionf.intersectRayAab(position, dir, min, max, nearFar)) {
	        	return pos;
	        }
			pos.add((int)Math.signum(dir.x), 0, 0);
			pos.add(0, -(int)Math.signum(dir.y), 0);
			min.set(pos);
	        max.set(pos);
	        min.add(-0.5f, -0.5f, -0.5f); // -scale, -scale, -scale
	        max.add(0.5f, 0.5f, 0.5f); // scale, scale, scale
	        if (Intersectionf.intersectRayAab(position, dir, min, max, nearFar)) {
	            return pos;
	        }
			pos.add(0, (int)Math.signum(dir.y), 0);
			pos.add(0, 0, -(int)Math.signum(dir.z));
	        return pos;
		}
		return null;
	}
	
}
