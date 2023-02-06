package aero.minova.rcp.rcp.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MTextField;

class MFieldTests {

	MTextField mTextField;
	MBooleanField mBooleanField;
	MNumberField mNumberField;
	MLookupField mLookupField;

	// MTestField
	@Test
	void textFieldsetValue() {
		mTextField = new MTextField();
		String testString = "test";
		mTextField.setValue(new Value(testString), false);
		assertEquals(mTextField.getValue().getStringValue(), testString);
	}

	@Test
	void textFieldsetWrongValueType() {
		textFieldsetValue();
		Double testDouble = 3.0;
		Value value = new Value(testDouble);
		assertThrows(IllegalArgumentException.class, () -> mTextField.setValue(value, false));

	}

	@Test
	void textFieldsetSameValueAsBefore() {
		textFieldsetValue();
		String testString = "test";
		mTextField.setValue(new Value(testString), false);
		assertEquals(testString, mTextField.getValue().getStringValue());
	}

	@Test
	void textFieldsetValueNull() {
		textFieldsetValue();
		mTextField.setValue(null, false);
		assertEquals(null, mTextField.getValue());
	}

	@Test
	void textFieldetDifferent() {
		textFieldsetValue();
		String testString = "othertest";
		mTextField.setValue(new Value(testString), false);
		assertEquals(mTextField.getValue().getStringValue(), testString);
	}

	// MBooleanField
	@Test
	void booleanFieldsetValue() {
		mBooleanField = new MBooleanField();
		Boolean testBoolean = true;
		mBooleanField.setValue(new Value(testBoolean), false);
		assertEquals(mBooleanField.getValue().getBooleanValue(), testBoolean);
	}

	@Test
	void booleanFieldsetWrongValueType() {
		booleanFieldsetValue();
		Double testDouble = 3.0;
		Value value = new Value(testDouble);
		assertThrows(IllegalArgumentException.class, () -> mBooleanField.setValue(value, false));
	}

	@Test
	void booleanFieldsetSameValueAsBefore() {
		booleanFieldsetValue();
		Boolean testBoolean = true;
		mBooleanField.setValue(new Value(testBoolean), false);
		assertEquals(mBooleanField.getValue().getBooleanValue(), testBoolean);
	}

	@Test
	/**
	 * Boolean Felder dürfen keinen null Value haben (kann nicht dargestellt werden)
	 */
	void booleanFieldsetValueNull() {
		booleanFieldsetValue();
		mBooleanField.setValue(null, false);
		assertEquals(new Value(false), mBooleanField.getValue());
	}

	@Test
	void booleanFieldetDifferent() {
		booleanFieldsetValue();
		Boolean testBoolean = false;
		mBooleanField.setValue(new Value(testBoolean), false);
		assertEquals(mBooleanField.getValue().getBooleanValue(), testBoolean);
	}

	// MNumberField
	@Test
	void numberFieldsetValue() {
		mNumberField = new MNumberField(2);
		Number testNumber = 1.00;
		mNumberField.setValue(new Value(testNumber), false);
		assertEquals(mNumberField.getValue().getDoubleValue(), testNumber);
	}

	@Test
	void numberFieldsetWrongValueType() {
		numberFieldsetValue();
		String testString = "testString";
		Value value = new Value(testString);
		assertThrows(IllegalArgumentException.class, () -> mNumberField.setValue(value, false));
	}

	@Test
	void numberFieldsetSameValueAsBefore() {
		numberFieldsetValue();
		Number testNumber = 1.00;
		mNumberField.setValue(new Value(testNumber), false);
		assertEquals(mNumberField.getValue().getDoubleValue(), testNumber);
	}

	@Test
	void numberFieldsetValueNull() {
		numberFieldsetValue();
		mNumberField.setValue(null, false);
		assertEquals(null, mNumberField.getValue());
	}

	@Test
	void numberFieldetDifferent() {
		numberFieldsetValue();
		Number testNumber = 2.00;
		mNumberField.setValue(new Value(testNumber), false);
		assertEquals(mNumberField.getValue().getDoubleValue(), testNumber);
	}

	// MLookupField

	@Test
	void lookupFieldsetValue() {
		mLookupField = new MLookupField();
		Integer testNumber = 1;
		mLookupField.setValue(new Value(testNumber), false);
		assertEquals(mLookupField.getValue().getIntegerValue(), testNumber);
	}

	@Test
	@Disabled("Fails currently")
	void lookupFieldsetWrongValueType() {
		lookupFieldsetValue();
		String testString = "testString";
		Value value = new Value(testString);
		assertThrows(IllegalArgumentException.class, () -> mLookupField.setValue(value, false));
	}

	@Test
	void lookupFieldsetSameValueAsBefore() {
		lookupFieldsetValue();
		Integer testNumber = 1;
		mLookupField.setValue(new Value(testNumber), false);
		assertEquals(mLookupField.getValue().getIntegerValue(), testNumber);
	}

	@Test
	void lookupFieldsetValueNull() {
		lookupFieldsetValue();
		mLookupField.setValue(null, false);
		assertEquals(null, mLookupField.getValue());
	}

	@Test
	void lookupFieldetDifferent() {
		lookupFieldsetValue();
		Integer testNumber = 2;
		mLookupField.setValue(new Value(testNumber), false);
		assertEquals(mLookupField.getValue().getIntegerValue(), testNumber);
	}

}
