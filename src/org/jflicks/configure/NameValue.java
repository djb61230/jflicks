/*
    This file is part of JFLICKS.

    JFLICKS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JFLICKS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JFLICKS.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.jflicks.configure;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This class contains encapsulates the notion of a Property.
 *
 * @author Doug Barnum
 * @version 1.0
 */
public class NameValue implements Serializable {

    /**
     * The value expected is a String.
     */
    public static final int STRING_TYPE = 0;

    /**
     * The value expected is a Integer.
     */
    public static final int INTEGER_TYPE = 1;

    /**
     * The value expected is a Double.
     */
    public static final int DOUBLE_TYPE = 2;

    /**
     * The value expected is a Boolean.
     */
    public static final int BOOLEAN_TYPE = 3;

    /**
     * The value expected is a list of Strings.
     */
    public static final int STRING_FROM_CHOICE_TYPE = 4;

    /**
     * The value expected is a list of Integer.
     */
    public static final int INTEGER_FROM_CHOICE_TYPE = 5;

    /**
     * The value expected is a list of Double.
     */
    public static final int DOUBLE_FROM_CHOICE_TYPE = 6;

    /**
     * The value expected is a list of Booleans.
     */
    public static final int BOOLEAN_FROM_CHOICE_TYPE = 7;

    /**
     * The value is a colon separated list of Strings.
     */
    public static final int STRINGLIST_TYPE = 8;

    /**
     * The value is a colon separated list of Integers.
     */
    public static final int INTEGERLIST_TYPE = 9;

    /**
     * The value is a colon separated list of Doubles.
     */
    public static final int DOUBLELIST_TYPE = 10;

    /**
     * The value is a colon separated list of Booleans.
     */
    public static final int BOOLEANLIST_TYPE = 11;

    private String name;
    private String description;
    private String value;
    private String defaultValue;
    private int type;
    private String[] choices;
    private int min;
    private int max;
    private int step;

    /**
     * Simple empty constructor.
     */
    public NameValue() {

        setMin(Integer.MIN_VALUE);
        setMax(Integer.MAX_VALUE);
        setStep(Integer.MIN_VALUE);
    }

    /**
     * Simple constructor to "clone" a NameValue instance.
     *
     * @param nv A given NameValue to "clone".
     */
    public NameValue(NameValue nv) {

        this();
        if (nv != null) {

            setName(nv.getName());
            setDescription(nv.getDescription());
            setValue(nv.getValue());
            setDefaultValue(nv.getDefaultValue());
            setType(nv.getType());
            setChoices(nv.getChoices());
            setMin(nv.getMin());
            setMax(nv.getMax());
            setStep(nv.getStep());
        }
    }

    /**
     * The name of the property.
     *
     * @return The name.
     */
    public String getName() {
        return (name);
    }

    /**
     * The name of the property.
     *
     * @param s The name.
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * The description of the property.
     *
     * @return The description.
     */
    public String getDescription() {
        return (description);
    }

    /**
     * The description of the property.
     *
     * @param s The description.
     */
    public void setDescription(String s) {
        description = s;
    }

    /**
     * The value of the property.
     *
     * @return The value.
     */
    public String getValue() {
        return (value);
    }

    /**
     * The value of the property.
     *
     * @param s The value.
     */
    public void setValue(String s) {
        value = s;
    }

    /**
     * The default value of the property.
     *
     * @return The default value.
     */
    public String getDefaultValue() {
        return (defaultValue);
    }

    /**
     * The default value of the property.
     *
     * @param s The default value.
     */
    public void setDefaultValue(String s) {
        defaultValue = s;
    }

    /**
     * The data type of the property.  All properties are Strings but this
     * helps determine how the String is interpreted.
     *
     * @return The type as an int.
     */
    public int getType() {
        return (type);
    }

    /**
     * The data type of the property.  All properties are Strings but this
     * helps determine how the String is interpreted.
     *
     * @param i The type as an int.
     */
    public void setType(int i) {
        type = i;
    }

    /**
     * Sometimes a Property value is a finite set of values.  The Choices
     * property defines these.
     *
     * @return An array of String values.
     */
    public String[] getChoices() {

        String[] result = null;

        if (choices != null) {

            result = Arrays.copyOf(choices, choices.length);
        }

        return (result);
    }

    /**
     * Sometimes a Property value is a finite set of values.  The Choices
     * property defines these.
     *
     * @param array An array of String values.
     */
    public void setChoices(String[] array) {

        if (array != null) {
            choices = Arrays.copyOf(array, array.length);
        } else {
            choices = null;
        }
    }

    /**
     * When the type is INTEGER_TYPE, we can have a property called Min which
     * defines the minimum value that the Integer can be.
     *
     * @return An int value.
     */
    public int getMin() {
        return (min);
    }

    /**
     * When the type is INTEGER_TYPE, we can have a property called Min which
     * defines the minimum value that the Integer can be.
     *
     * @param i An int value.
     */
    public void setMin(int i) {
        min = i;
    }

    /**
     * When the type is INTEGER_TYPE, we can have a property called Max which
     * defines the maximum value that the Integer can be.
     *
     * @return An int value.
     */
    public int getMax() {
        return (max);
    }

    /**
     * When the type is INTEGER_TYPE, we can have a property called Max which
     * defines the maximum value that the Integer can be.
     *
     * @param i An int value.
     */
    public void setMax(int i) {
        max = i;
    }

    /**
     * When the type is INTEGER_TYPE, we can have a property called Step which
     * defines the number that defines a "discrete" step in value.  Usually
     * we think of this being 1 but by having this property a different value
     * like 5 or 10 can be defined.
     *
     * @return An int value.
     */
    public int getStep() {
        return (step);
    }

    /**
     * When the type is INTEGER_TYPE, we can have a property called Step which
     * defines the number that defines a "discrete" step in value.  Usually
     * we think of this being 1 but by having this property a different value
     * like 5 or 10 can be defined.
     *
     * @param i An int value.
     */
    public void setStep(int i) {
        step = i;
    }

    /**
     * Parse out the String value into an array of Strings based upon the
     * ":" character.
     *
     * @return An array of String instances.
     */
    public String[] valueToArray() {
        return (valueToArray("\\|"));
    }

    /**
     * Parse out the String value into an array of Strings based upon the
     * given token.
     *
     * @param token A token to break up the String with.
     * @return An array of String instances.
     */
    public String[] valueToArray(String token) {

        String[] result = null;

        String s = getValue();
        if ((s != null) && (token != null)) {

            result = s.split(token);
        }

        return (result);
    }

    /**
     * Convenience method to determine if the type is a String type.
     *
     * @return True if the type is a String type.
     */
    public boolean isStringType() {
        return (getType() == STRING_TYPE);
    }

    /**
     * Convenience method to determine if the type is a Integer type.
     *
     * @return True if the type is a Integer type.
     */
    public boolean isIntegerType() {
        return (getType() == INTEGER_TYPE);
    }

    /**
     * Convenience method to determine if the type is a Double type.
     *
     * @return True if the type is a Double type.
     */
    public boolean isDoubleType() {
        return (getType() == DOUBLE_TYPE);
    }

    /**
     * Convenience method to determine if the type is a Boolean type.
     *
     * @return True if the type is a Boolean type.
     */
    public boolean isBooleanType() {
        return (getType() == BOOLEAN_TYPE);
    }

    /**
     * Convenience method to determine if the type is from a choice list type.
     *
     * @return True if the type is a list type.
     */
    public boolean isFromChoiceType() {

        boolean result = false;

        int t = getType();

        switch (t) {
        case NameValue.STRING_FROM_CHOICE_TYPE:
        case NameValue.INTEGER_FROM_CHOICE_TYPE:
        case NameValue.DOUBLE_FROM_CHOICE_TYPE:
        case NameValue.BOOLEAN_FROM_CHOICE_TYPE:
            result = true;
            break;

        default:
            break;
        }

        return (result);
    }

    /**
     * Convenience method to determine if the type is a colon list type.
     *
     * @return True if the type is a colon list type.
     */
    public boolean isColonListType() {

        boolean result = false;

        int t = getType();

        switch (t) {
        case NameValue.STRINGLIST_TYPE:
        case NameValue.INTEGERLIST_TYPE:
        case NameValue.DOUBLELIST_TYPE:
        case NameValue.BOOLEANLIST_TYPE:
            result = true;
            break;

        default:
            break;
        }

        return (result);
    }

    /**
     * Convenience method to covert a type as a String to it's appropriate
     * int value.
     *
     * @param s A given String to convert.
     * @return A type as an int.  Defaults to STRING_TYPE.
     */
    public int toType(String s) {

        int result = STRING_TYPE;

        if (s != null) {

            if (s.equals("STRING_TYPE")) {

                result = STRING_TYPE;

            } else if (s.equals("INTEGER_TYPE")) {

                result = INTEGER_TYPE;

            } else if (s.equals("DOUBLE_TYPE")) {

                result = DOUBLE_TYPE;

            } else if (s.equals("BOOLEAN_TYPE")) {

                result = BOOLEAN_TYPE;

            } else if (s.equals("STRING_FROM_CHOICE_TYPE")) {

                result = STRING_FROM_CHOICE_TYPE;

            } else if (s.equals("INTEGER_FROM_CHOICE_TYPE")) {

                result = INTEGER_FROM_CHOICE_TYPE;

            } else if (s.equals("DOUBLE_FROM_CHOICE_TYPE")) {

                result = DOUBLE_FROM_CHOICE_TYPE;

            } else if (s.equals("BOOLEAN_FROM_CHOICE_TYPE")) {

                result = BOOLEAN_FROM_CHOICE_TYPE;

            } else if (s.equals("STRINGLIST_TYPE")) {

                result = STRINGLIST_TYPE;

            } else if (s.equals("INTEGERLIST_TYPE")) {

                result = INTEGERLIST_TYPE;

            } else if (s.equals("DOUBLELIST_TYPE")) {

                result = DOUBLELIST_TYPE;

            } else if (s.equals("BOOLEANLIST_TYPE")) {

                result = BOOLEANLIST_TYPE;
            }
        }

        return (result);
    }

    /**
     * Convenience method to covert a type as an int to it's appropriate
     * String value.
     *
     * @param i A given int to convert.
     * @return A type as an String.  Defaults to STRING_TYPE.
     */
    public String fromType(int i) {

        String result = null;

        switch (i) {
        default:
        case STRING_TYPE:
            result = "STRING_TYPE";
            break;
        case INTEGER_TYPE:
            result = "INTEGER_TYPE";
            break;
        case DOUBLE_TYPE:
            result = "DOUBLE_TYPE";
            break;
        case BOOLEAN_TYPE:
            result = "BOOLEAN_TYPE";
            break;
        case STRING_FROM_CHOICE_TYPE:
            result = "STRING_FROM_CHOICE_TYPE";
            break;
        case INTEGER_FROM_CHOICE_TYPE:
            result = "INTEGER_FROM_CHOICE_TYPE";
            break;
        case DOUBLE_FROM_CHOICE_TYPE:
            result = "DOUBLE_FROM_CHOICE_TYPE";
            break;
        case BOOLEAN_FROM_CHOICE_TYPE:
            result = "BOOLEAN_FROM_CHOICE_TYPE";
            break;
        case STRINGLIST_TYPE:
            result = "STRINGLIST_TYPE";
            break;
        case INTEGERLIST_TYPE:
            result = "INTEGERLIST_TYPE";
            break;
        case DOUBLELIST_TYPE:
            result = "DOUBLELIST_TYPE";
            break;
        case BOOLEANLIST_TYPE:
            result = "BOOLEANLIST_TYPE";
            break;
        }

        return (result);
    }

    /**
     * The equals override method.
     *
     * @param o A gven object to check.
     * @return True if the objects are equal.
     */
    public boolean equals(Object o) {

        boolean result = false;

        if (o == this) {

            result = true;

        } else if (!(o instanceof NameValue)) {

            result = false;

        } else {

            NameValue nv = (NameValue) o;
            String nme = nv.getName();
            if (nme != null) {
                result = nme.equals(getName());
                if (result) {
                    Serializable ser = nv.getValue();
                    if (ser != null) {
                        result = ser.equals(getValue());
                    }
                }
            }
        }

        return (result);
    }

    /**
     * The standard hashcode override.
     *
     * @return An int value.
     */
    public int hashCode() {
        return (getName().hashCode());
    }

    /**
     * The comparable interface.
     *
     * @param nv The given NameValue instance to compare.
     * @throws ClassCastException on the input argument.
     * @return An int representing their "equality".
     */
    public int compareTo(NameValue nv) throws ClassCastException {

        int result = 0;

        if (nv == null) {

            throw new NullPointerException();
        }

        if (nv == this) {

            result = 0;

        } else {

            result = getName().compareTo(nv.getName());
        }

        return (result);
    }

}

