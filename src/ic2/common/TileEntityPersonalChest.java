package ic2.common;

import forge.ISidedInventory;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.NBTTagCompound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Vector;

public class TileEntityPersonalChest extends TileEntityMachine implements IPersonalBlock, ISidedInventory, IHasGui {
	public String owner = "null";

	public TileEntityPersonalChest() {
		super(54);
	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.owner = nbttagcompound.getString("owner");
	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setString("owner", this.owner);
	}

	public String getName() {
		return "Personal Safe";
	}

	public List getNetworkedFields() {
		Vector vector = new Vector(1);
		vector.add("owner");
		vector.addAll(super.getNetworkedFields());
		return vector;
	}

	public boolean wrenchCanRemove(EntityHuman entityhuman) {
		if (!this.canAccess(entityhuman)) {
			return false;
		}
		else {
			for (int i = 0; i < this.inventory.length; ++i) {
				if (this.inventory[i] != null) {
					Platform.messagePlayer(entityhuman, "Can't wrench non-empty safe");
					return false;
				}
			}

			return true;
		}
	}

	public boolean canAccess(EntityHuman entityhuman) {
		if (this.owner.equals("null")) {
			this.owner = entityhuman.name;
			NetworkManager.updateTileEntityField(this, "owner");
			return true;
		}
		else {
			if (entityhuman.getBukkitEntity() instanceof Player) {
				Player player = (Player) entityhuman.getBukkitEntity();
				if (Platform.isSimulating() && (Platform.isPlayerOp(entityhuman) || player.hasPermission("ic2.personalsafe.access"))) {
					return true;
				}
			}

			if (Platform.isSimulating() && Platform.isPlayerOp(entityhuman)) {
				return true;
			}
			else if (this.owner.equalsIgnoreCase(entityhuman.name)) {
				return true;
			}
			else {
				if (Platform.isSimulating()) {
					Platform.messagePlayer(entityhuman, "This safe is owned by " + this.owner);
				}

				return false;
			}
		}
	}

	public int getStartInventorySide(int i) {
		return 0;
	}

	public int getSizeInventorySide(int i) {
		return Platform.isSimulating() && Platform.isRendering() ? this.inventory.length : 0;
	}

	public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
		return new ContainerPersonalChest(entityhuman, this);
	}

	public String getGuiClassName(EntityHuman entityhuman) {
		return "GuiPersonalChest";
	}

	public void onGuiClosed(EntityHuman entityhuman) {
	}
}
