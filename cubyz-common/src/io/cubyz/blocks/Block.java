package io.cubyz.blocks;

import io.cubyz.IRenderablePair;
import io.cubyz.api.IRegistryElement;
import io.cubyz.api.Resource;
import io.cubyz.items.Item;

public class Block implements IRegistryElement {

	IRenderablePair pair;
	boolean transparent;
	boolean texConverted;
	public int ID;			// Stores the numerical ID. This ID is generated by the registry. There is no need to fill it manually.
	private Resource id = new Resource("empty:empty");
	private String texture;
	private float hardness;
	private boolean solid = true;
	private boolean selectable = true;
	private Item blockDrop;
	protected boolean degradable = false; // Meaning undegradable parts of trees or other structures can grow through this block.
	
	public String getTexture() {
		return texture;
	}
	
	protected void setTexture(String texture) {
		this.texture = texture;
	}
	
	public boolean isDegradable() {
		return degradable;
	}
	
	public boolean isTransparent() {
		return transparent;
	}
	
	public Block setSolid(boolean solid) {
		this.solid = solid;
		return this;
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public Block setSelectable(boolean selectable) {
		this.selectable = selectable;
		return this;
	}
	
	public boolean isSelectable() {
		return selectable;
	}
	
	public IRenderablePair getBlockPair() {
		return pair;
	}
	
	public void setBlockPair(IRenderablePair pair) {
		this.pair = pair;
	}
	
	public boolean isTextureConverted() {
		return texConverted;
	}
	
	public void init() {}
	
	public String getID() {
		return id.getID();
	}
	
	public Resource getRegistryID() {
		return id;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	/**
	 * The ID can only be changed <b>BEFORE</b> registering the block.
	 * @param id
	 */
	public Block setID(String id) {
		return setID(new Resource(id));
	}
	
	public Block setID(Resource id) {
		this.id = id;
		return this;
	}
	
	public void setBlockDrop(Item bd) {
		blockDrop = bd;
	}
	
	public Item getBlockDrop() {
		return blockDrop;
	}
	
	public float getHardness() {
		return hardness;
	}
	
	public Block setHardness(float hardness) {
		this.hardness = hardness;
		return this;
	}
	
	public boolean isUnbreakable() {
		return hardness == -1f;
	}
	
	public Block setUnbreakable() {
		hardness = -1f;
		return this;
	}
	
	public boolean hasTileEntity() {
		return false;
	}
	
}
