package ic2.api;

public interface IElectricItem {
	boolean canProvideEnergy();

	int getChargedItemId();

	int getEmptyItemId();

	int getMaxCharge();

	int getTier();

	int getTransferLimit();
}
