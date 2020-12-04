package io.cubyz.blocks;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.joml.Vector3i;

import io.cubyz.ClientOnly;
import io.cubyz.api.CubyzRegistries;
import io.cubyz.api.RegistryElement;
import io.cubyz.api.Resource;
import io.cubyz.items.BlockDrop;
import io.cubyz.items.Inventory;
import io.cubyz.items.ItemBlock;
import io.cubyz.world.Surface;
import io.cubyz.world.World;

/**
 * Defines the properties and behaviour of Cubyz's basic building units.
 */

public class Block implements RegistryElement {
	
	public static enum BlockClass {
		WOOD, STONE, SAND, UNBREAKABLE, LEAF, FLUID
	};

	private boolean transparent;
	boolean trulyTransparent;
	/**
	 * Used for rendering optimization.<br/>
	 * Do not edit or rely on, as it is not an ID to actually describe the block on a persistent state.
	 */
	public int ID;			// Stores the numerical ID. This ID is generated by the registry. There is no need to fill it manually.

	private Resource id = Resource.EMPTY;
	/**Time in seconds to break this block by hand.*/
	private float hardness;
	private boolean solid = true;
	private boolean selectable = true;
	private BlockDrop[] blockDrops;
	/**Meaning undegradable parts of trees or other structures can grow through this block.*/
	protected boolean degradable = false;
	protected BlockClass blockClass;
	private int light = 0;
	public int atlasX = 0, atlasY = 0;
	/**How much light this block absorbs if it is transparent.*/
	int absorption = 0;
	/**GUI that is opened on click.*/
	String gui;
	public RotationMode mode = CubyzRegistries.ROTATION_MODE_REGISTRY.getByID("cubyz:no_rotation");
	public final short color;
	public Class<? extends BlockEntity> blockEntity;
	
	public Block() {
		blockDrops = new BlockDrop[0];
		color = -1;
	}
	
	public Block(String id, float hardness, BlockClass bc) {
		setID(id);
		blockClass = bc;
		ItemBlock bd = new ItemBlock(this);
		blockDrops = new BlockDrop[1];
		blockDrops[0] = new BlockDrop(bd, 1);
		this.hardness = hardness;
		color = -1;
	}
	
	public Block(Resource id, Properties props, String bc) {
		this.id = id;
		hardness = Float.parseFloat(props.getProperty("hardness", "1"));
		blockClass = BlockClass.valueOf(bc);
		light = Integer.decode(props.getProperty("emittedLight", "0"));
		absorption = Integer.decode(props.getProperty("absorbedLight", "-1"));
		transparent = props.getProperty("absorbedLight", "").length() != 0;
		degradable = props.getProperty("degradable", "no").equalsIgnoreCase("yes");
		selectable = props.getProperty("selectable", "yes").equalsIgnoreCase("yes");
		solid = props.getProperty("solid", "yes").equalsIgnoreCase("yes");
		gui = props.getProperty("GUI", null);
		mode = CubyzRegistries.ROTATION_MODE_REGISTRY.getByID(props.getProperty("rotation", "cubyz:no_rotation"));
		trulyTransparent = props.getProperty("transparent", "no").equalsIgnoreCase("yes");
		blockDrops = new BlockDrop[0];
		color = (short)(Integer.decode(props.getProperty("color", "0x0000")) & 65535);
	}
	
	public void setDegradable(Boolean deg) {
		degradable = deg;
	}
	
	public boolean isDegradable() {
		return degradable;
	}
	
	/**
	 * @return Whether this block is transparent to the lighting system.
	 */
	public boolean isTransparent(byte data) { // TODO: Better directional light management.
		return transparent || mode.checkTransparency(data, 0);
	}
	
	/**
	 * @return Whether this block should be rendered as a transparent block.
	 */
	public boolean isTrulyTransparent() {
		return trulyTransparent;
	}
	
	public Block setSolid(boolean solid) {
		this.solid = solid;
		return this;
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	
	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public void setBlockClass(BlockClass bc) {
		blockClass = bc;
	}

	public void setAbsorption(int absorption) {
		this.absorption = absorption;
	}

	public boolean isSelectable() {
		return selectable;
	}
	
	public boolean generatesModelAtRuntime() {
		return false;
	}
	
	public void init() {}
	
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
	public void setID(String id) {
		setID(new Resource(id));
	}
	
	public void setID(Resource id) {
		this.id = id;
	}
	
	public void addBlockDrop(BlockDrop bd) {
		BlockDrop[] newDrops = new BlockDrop[blockDrops.length+1];
		System.arraycopy(blockDrops, 0, newDrops, 0, blockDrops.length);
		newDrops[blockDrops.length] = bd;
		blockDrops = newDrops;
	}
	
	public BlockDrop[] getBlockDrops() {
		return blockDrops;
	}
	
	public float getHardness() {
		return hardness;
	}
	
	public void setHardness(float hardness) {
		this.hardness = hardness;
	}
	
	public int getLight() {
		return light;
	}
	
	public void setLight(int light) {
		this.light = light;
	}
	
	public BlockEntity createBlockEntity(Surface surface, Vector3i pos) {
		if (blockEntity != null) {
			try {
				return blockEntity.getConstructor(Surface.class, Vector3i.class).newInstance(surface, pos);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public boolean hasBlockEntity() {
		return blockEntity != null;
	}
	
	public BlockClass getBlockClass() {
		return blockClass;
	}
	
	public void setGUI(String id) {
		gui = id;
	}
	
	/**
	 * Fires the blocks on click event(usually nothing or GUI opening).
	 * @param world
	 * @param pos
	 * @return if the block did something on click.
	 */
	public boolean onClick(World world, Vector3i pos) {
		if(gui != null) {
			ClientOnly.client.openGUI("cubyz:workbench", new Inventory(10)); // TODO: Care about the inventory.
			return true;
		}
		return false;
	}
	
	public int getAbsorption() {
		return absorption;
	}
}
