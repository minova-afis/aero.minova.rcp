package aero.minova.rcp.model.util;

public interface IVariantType {
	/** String Objekt */
	public final static int VARIANT_STRING = 8;

	/** Integer Objekt */
	public final static int VARIANT_INT = 3;

	/** Double Objekt */
	public final static int VARIANT_DOUBLE = 5;

	/** Short Objekt */
	public final static int VARIANT_SHORT = 2;

	/** Boolean Objekt */
	public final static int VARIANT_BOOLEAN = 11;

	/** Objekt - kann nicht gewandelt werden! */
	public final static int VARIANT_OBJECT = 13;

	/** java.util.Date Objekt */
	public final static int VARIANT_DATE = 7;

	/** empty oder null value */
	public final static int VARIANT_EMPTY = 0;
}