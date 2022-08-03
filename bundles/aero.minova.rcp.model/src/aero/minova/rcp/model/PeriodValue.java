package aero.minova.rcp.model;

import java.time.Instant;
import java.util.Objects;

public class PeriodValue extends Value {

	private static final long serialVersionUID = 202206011131L;

	private String userInput;
	private Value dueDate;

	public PeriodValue(Instant baseDate, String userInput, Instant dueDate) {
		super(baseDate, DataType.PERIOD);
		this.userInput = userInput;
		this.dueDate = new Value(dueDate);
	}

	@Override
	public String toString() {
		return ValueSerializer.serialize(this).toString();
	}

	public String getUserInput() {
		return userInput;
	}

	public Value getDueDate() {
		return dueDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dueDate == null) ? 0 : dueDate.hashCode());
		result = prime * result + ((userInput == null) ? 0 : userInput.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PeriodValue)) {
			return false;
		}

		PeriodValue other = (PeriodValue) obj;

		if (!Objects.equals(userInput, other.getUserInput())) {
			return false;
		}

		if (!Objects.equals(dueDate, other.getDueDate())) {
			return false;
		}

		return super.equals(obj);
	}
}
