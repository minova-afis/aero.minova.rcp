package aero.minova.rcp.rcp.field;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MTextField;

public class MFieldTests {

	MTextField mTextField;
	MBooleanField mBooleanField;
	MNumberField mNumberField;
	MLookupField mLookupField;

	// MTestField
	@Test
	public void textFieldsetValue() {
		mTextField = new MTextField();
		String testString = "test";
		mTextField.setValue(new Value(testString), false);
		assertEquals(mTextField.getValue().getStringValue(), testString);
	}

	@Test(expected = IllegalArgumentException.class)
	public void textFieldsetWrongValueType() {
		textFieldsetValue();
		Double testDouble = 3.0;
		mTextField.setValue(new Value(testDouble), false);
	}

	@Test
	public void textFieldsetSameValueAsBefore() {
		textFieldsetValue();
		String testString = "test";
		mTextField.setValue(new Value(testString), false);
		assertEquals(mTextField.getValue().getStringValue(), testString);
	}

	@Test
	public void textFieldsetValueNull() {
		textFieldsetValue();
		mTextField.setValue(null, false);
		assertEquals(mTextField.getValue(), null);
	}

	@Test
	public void textFieldetDifferent() {
		textFieldsetValue();
		String testString = "othertest";
		mTextField.setValue(new Value(testString), false);
		assertEquals(mTextField.getValue().getStringValue(), testString);
	}

	// MBooleanField
	@Test
	public void booleanFieldsetValue() {
		mBooleanField = new MBooleanField();
		Boolean testBoolean = true;
		mBooleanField.setValue(new Value(testBoolean), false);
		assertEquals(mBooleanField.getValue().getBooleanValue(), testBoolean);
	}

	@Test(expected = IllegalArgumentException.class)
	public void booleanFieldsetWrongValueType() {
		booleanFieldsetValue();
		Double testDouble = 3.0;
		mBooleanField.setValue(new Value(testDouble), false);
	}

	@Test
	public void booleanFieldsetSameValueAsBefore() {
		booleanFieldsetValue();
		Boolean testBoolean = true;
		mBooleanField.setValue(new Value(testBoolean), false);
		assertEquals(mBooleanField.getValue().getBooleanValue(), testBoolean);
	}

	@Test
	/**
	 * Boolean Felder dürfen keinen null Value haben (kann nicht dargestellt werden)
	 */
	public void booleanFieldsetValueNull() {
		booleanFieldsetValue();
		mBooleanField.setValue(null, false);
		assertEquals(new Value(false), mBooleanField.getValue());
	}

	@Test
	public void booleanFieldetDifferent() {
		booleanFieldsetValue();
		Boolean testBoolean = false;
		mBooleanField.setValue(new Value(testBoolean), false);
		assertEquals(mBooleanField.getValue().getBooleanValue(), testBoolean);
	}

	// MNumberField
	@Test
	public void numberFieldsetValue() {
		mNumberField = new MNumberField(2);
		Number testNumber = 1.00;
		mNumberField.setValue(new Value(testNumber), false);
		assertEquals(mNumberField.getValue().getDoubleValue(), testNumber);
	}

	@Test(expected = IllegalArgumentException.class)
	public void numberFieldsetWrongValueType() {
		numberFieldsetValue();
		String testString = "testString";
		mNumberField.setValue(new Value(testString), false);
	}

	@Test
	public void numberFieldsetSameValueAsBefore() {
		numberFieldsetValue();
		Number testNumber = 1.00;
		mNumberField.setValue(new Value(testNumber), false);
		assertEquals(mNumberField.getValue().getDoubleValue(), testNumber);
	}

	@Test
	public void numberFieldsetValueNull() {
		numberFieldsetValue();
		mNumberField.setValue(null, false);
		assertEquals(mNumberField.getValue(), null);
	}

	@Test
	public void numberFieldetDifferent() {
		numberFieldsetValue();
		Number testNumber = 2.00;
		mNumberField.setValue(new Value(testNumber), false);
		assertEquals(mNumberField.getValue().getDoubleValue(), testNumber);
	}

	// MLookupField

	@Test
	public void lookupFieldsetValue() {
		mLookupField = new MLookupField();
		Integer testNumber = 1;
		mLookupField.setValue(new Value(testNumber), false);
		assertEquals(mLookupField.getValue().getIntegerValue(), testNumber);
	}

	@Test(expected = IllegalArgumentException.class)
	@Ignore("Fails currently")
	public void lookupFieldsetWrongValueType() {
		lookupFieldsetValue();
		String testString = "testString";
		mLookupField.setValue(new Value(testString), false);
	}

	@Test
	public void lookupFieldsetSameValueAsBefore() {
		lookupFieldsetValue();
		Integer testNumber = 1;
		mLookupField.setValue(new Value(testNumber), false);
		assertEquals(mLookupField.getValue().getIntegerValue(), testNumber);
	}

	@Test
	public void lookupFieldsetValueNull() {
		lookupFieldsetValue();
		mLookupField.setValue(null, false);
		assertEquals(mLookupField.getValue(), null);
	}

	@Test
	public void lookupFieldetDifferent() {
		lookupFieldsetValue();
		Integer testNumber = 2;
		mLookupField.setValue(new Value(testNumber), false);
		assertEquals(mLookupField.getValue().getIntegerValue(), testNumber);
	}

}
