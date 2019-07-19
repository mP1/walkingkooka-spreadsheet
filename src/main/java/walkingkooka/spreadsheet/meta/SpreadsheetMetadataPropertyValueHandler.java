/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.meta;

import walkingkooka.Cast;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

/**
 * Base converter that provides support for handling property values.
 */
abstract class SpreadsheetMetadataPropertyValueHandler<T> {

    /**
     * {@see EmailAddressSpreadsheetMetadataPropertyValueHandler}
     */
    static EmailAddressSpreadsheetMetadataPropertyValueHandler emailAddress() {
        return EmailAddressSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    /**
     * {@see LocalDateTimeSpreadsheetMetadataPropertyValueHandler}
     */
    static LocalDateTimeSpreadsheetMetadataPropertyValueHandler localDateTime() {
        return LocalDateTimeSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    /**
     * {@see LocaleSpreadsheetMetadataPropertyValueHandler}
     */
    static LocaleSpreadsheetMetadataPropertyValueHandler locale() {
        return LocaleSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    /**
     * {@see NonEmptyStringSpreadsheetMetadataPropertyValueHandler}
     */
    static NonEmptyStringSpreadsheetMetadataPropertyValueHandler nonEmpty() {
        return NonEmptyStringSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    /**
     * {@see SpreadsheetIdSpreadsheetMetadataPropertyValueHandler}
     */
    static SpreadsheetIdSpreadsheetMetadataPropertyValueHandler spreadsheetId() {
        return SpreadsheetIdSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    /**
     * {@see StringSpreadsheetMetadataPropertyValueHandler}
     */
    static StringSpreadsheetMetadataPropertyValueHandler string() {
        return StringSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetMetadataPropertyValueHandler() {
        super();
    }

    // checkValue...........................................................

    final T check(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        if (null == value) {
            throw new SpreadsheetMetadataPropertyValueException("Property " + name.inQuotes + " missing value",
                    name,
                    value);
        }

        this.check0(value, name);
        return Cast.to(value);
    }

    abstract void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name);

    /**
     * Checks the type of the given value and throws a {@link SpreadsheetMetadataPropertyValueException} if this test fails.
     */
    final <U> U checkType(final Object value, final Class<U> type, final SpreadsheetMetadataPropertyName<?> name) {
        if (!type.isInstance(value)) {
            throw this.spreadsheetMetadataPropertyValueException(value, name);
        }
        return type.cast(value);
    }

    /**
     * Creates a {@link SpreadsheetMetadataPropertyValueException} used to report an invalid value.
     */
    final SpreadsheetMetadataPropertyValueException spreadsheetMetadataPropertyValueException(final Object value,
                                                                                              final SpreadsheetMetadataPropertyName<?> name) {
        final Class<?> type = value.getClass();

        String typeName = type.getName();
        if (textStylePropertyType(typeName) || hasJsonType(type)) {
            typeName = typeName.substring(1 + typeName.lastIndexOf('.'));
        }

        return new SpreadsheetMetadataPropertyValueException("Property " + name.inQuotes + " value " + CharSequences.quoteIfChars(value) + "(" + typeName + ") is not a " + this.expectedTypeName(type),
                name,
                value);
    }

    abstract String expectedTypeName(final Class<?> type);

    final boolean textStylePropertyType(final String type) {
        return type.startsWith(PACKAGE) && type.indexOf('.', 1 + PACKAGE.length()) == -1;
    }

    final boolean hasJsonType(final Class<?> type) {
        return HasJsonNode.typeName(type).isPresent();
    }

    private final static String PACKAGE = "walkingkooka.spreadsheet.meta";

    // fromJsonNode ....................................................................................................

    /**
     * Transforms a {@link JsonNode} into a value.
     */
    abstract T fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name);

    /**
     * Transforms a value into json, performing the inverse of {@link #fromJsonNode(JsonNode, SpreadsheetMetadataPropertyName)}
     */
    abstract JsonNode toJsonNode(final T value);

    // Object .........................................................................................................

    @Override
    abstract public String toString();
}
