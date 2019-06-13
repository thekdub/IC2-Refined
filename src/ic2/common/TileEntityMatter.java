package ic2.common;

import forge.ISidedInventory;
import ic2.api.Direction;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

public class TileEntityMatter extends TileEntityElecMachine implements IHasGui, ISidedInventory {
	public static Vector amplifiers = new Vector();
	private final int StateIdle = 0;
	private final int StateRunning = 1;
	private final int StateRunningScrap = 2;
	public int soundTicker;
	public int scrap = 0;
	private int state = 0;
	private int prevState = 0;
	private AudioSource audioSource;
	private AudioSource audioSourceScrap;

	public TileEntityMatter() {
		super(2, 0, 1100000, 512);
		this.soundTicker = mod_IC2.random.nextInt(32);
	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);

		try {
			this.scrap = nbttagcompound.getInt("scrap");
		} catch (Throwable var3) {
			this.scrap = nbttagcompound.getShort("scrap");
		}

	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("scrap", this.scrap);
	}

	public String getName() {
		return "Mass Fabricator";
	}

	public void q_() {
		super.q_();
		if (!this.isRedstonePowered() && this.energy > 0) {
			this.setState(this.scrap <= 0 ? 1 : 2);
			this.setActive(true);
			boolean flag = false;
			if (this.scrap < 1000 && this.inventory[0] != null) {
				if (this.inventory[0].doMaterialsMatch(Ic2Items.scrap)) {
					--this.inventory[0].count;
					if (this.inventory[0].count <= 0) {
						this.inventory[0] = null;
					}

					this.scrap += 5000;
				}
				else {
					Iterator iterator = amplifiers.iterator();

					while (iterator.hasNext()) {
						Entry entry = (Entry) iterator.next();
						if (this.inventory[0].doMaterialsMatch((ItemStack) entry.getKey())) {
							if (((ItemStack) entry.getKey()).getItem().k()) {
								this.inventory[0] = new ItemStack(((ItemStack) entry.getKey()).getItem().j());
							}
							else {
								--this.inventory[0].count;
								if (this.inventory[0].count <= 0) {
									this.inventory[0] = null;
								}
							}

							this.scrap += (Integer) entry.getValue();
							break;
						}
					}
				}
			}

			if (this.energy >= 1000000) {
				flag = this.attemptGeneration();
			}

			if (flag) {
				this.update();
			}
		}
		else {
			this.setState(0);
			this.setActive(false);
		}

	}

	public void j() {
		if (Platform.isRendering() && this.audioSource != null) {
			AudioManager.removeSources(this);
			this.audioSource = null;
			this.audioSourceScrap = null;
		}

		super.j();
	}

	public boolean attemptGeneration() {
		if (this.inventory[1] == null) {
			this.inventory[1] = Ic2Items.matter.cloneItemStack();
			this.energy -= 1000000;
			return true;
		}
		else if (this.inventory[1].doMaterialsMatch(Ic2Items.matter) && this.inventory[1].count < this.inventory[1].getMaxStackSize()) {
			this.energy -= 1000000;
			++this.inventory[1].count;
			return true;
		}
		else {
			return false;
		}
	}

	public boolean demandsEnergy() {
		if (this.isRedstonePowered()) {
			return false;
		}
		else {
			return this.energy < this.maxEnergy;
		}
	}

	public int injectEnergy(Direction direction, int i) {
		if (i > 512) {
			this.world.setTypeId(this.x, this.y, this.z, 0);
			ExplosionIC2 explosionic2 = new ExplosionIC2(this.world, null, (double) this.x, (double) this.y, (double) this.z, 15.0F, 0.01F, 1.5F);
			explosionic2.doExplosion();
			return 0;
		}
		else {
			int j = i;
			if (i > this.scrap) {
				j = this.scrap;
			}

			this.scrap -= j;
			this.energy += i + 5 * j;
			int k = 0;
			if (this.energy > this.maxEnergy) {
				k = this.energy - this.maxEnergy;
				this.energy = this.maxEnergy;
			}

			return k;
		}
	}

	public String getProgressAsString() {
		int i = this.energy / 10000;
		if (i > 100) {
			i = 100;
		}

		return "" + i + "%";
	}

	public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
		return new ContainerMatter(entityhuman, this);
	}

	public String getGuiClassName(EntityHuman entityhuman) {
		return "GuiMatter";
	}

	public void onGuiClosed(EntityHuman entityhuman) {
	}

	private void setState(int i) {
		this.state = i;
		if (this.prevState != i) {
			NetworkManager.updateTileEntityField(this, "state");
		}

		this.prevState = i;
	}

	public List getNetworkedFields() {
		Vector vector = new Vector(1);
		vector.add("state");
		return vector;
	}

	public void onNetworkUpdate(String s) {
		if (s.equals("state") && this.prevState != this.state) {
			switch (this.state) {
				case 0:
					if (this.audioSource != null) {
						this.audioSource.stop();
					}

					if (this.audioSourceScrap != null) {
						this.audioSourceScrap.stop();
					}
					break;
				case 1:
					if (this.audioSource == null) {
						this.audioSource = AudioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, AudioManager.defaultVolume);
					}

					if (this.audioSource != null) {
						this.audioSource.play();
					}

					if (this.audioSourceScrap != null) {
						this.audioSourceScrap.stop();
					}
					break;
				case 2:
					if (this.audioSource == null) {
						this.audioSource = AudioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, AudioManager.defaultVolume);
					}

					if (this.audioSourceScrap == null) {
						this.audioSourceScrap = AudioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabScrapSolo.ogg", true, false, AudioManager.defaultVolume);
					}

					if (this.audioSource != null) {
						this.audioSource.play();
					}

					if (this.audioSourceScrap != null) {
						this.audioSourceScrap.play();
					}
			}

			this.prevState = this.state;
		}

		super.onNetworkUpdate(s);
	}

	public int getStartInventorySide(int i) {
		switch (i) {
			case 0:
				return 0;
			case 1:
			default:
				return 1;
		}
	}

	public int getSizeInventorySide(int i) {
		return 1;
	}

	public float getWrenchDropRate() {
		return 0.7F;
	}
}
