package aero.minova.rcp.model.form;

import org.eclipse.nebula.widgets.nattable.data.validate.ValidationFailedException;

public interface IGridValidator {

	/**
	 * True wenn die gegebene Zelle valide ist und gespeichert werden kann, false ansonsten
	 * 
	 * @param columnPosition
	 * @param rowPosition
	 * @return
	 */
	public boolean checkValid(int columnPosition, int rowPosition);

	/**
	 * Überprüft, ob der neue Wert an die gegebene Stelle geschrieben werden darf. Wenn nicht muss eine ValidationFailedException geworfen werden. Im Text kann
	 * der Grund angegeben werden. Dieser wird übersetzt und angezeigt
	 * 
	 * @param columnIndex
	 * @param rowIndex
	 * @param newValue
	 * @throws ValidationFailedException
	 */
	public void validateThrowingException(int columnIndex, int rowIndex, Object newValue) throws ValidationFailedException;

}
