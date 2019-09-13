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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * Base converter that provides support for handling property values.
 */
abstract class SpreadsheetMetadataPropertyValueHandler<T> {

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerCharacter}
     */
    static SpreadsheetMetadataPropertyValueHandlerCharacter character() {
        return SpreadsheetMetadataPropertyValueHandlerCharacter.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerColor}
     */
    static SpreadsheetMetadataPropertyValueHandlerColor color() {
        return SpreadsheetMetadataPropertyValueHandlerColor.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern dateFormatPattern() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateFormatPattern.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns dateParsePatterns() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateParsePatterns.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeFormatPattern}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeFormatPattern dateTimeFormatPattern() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeFormatPattern.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns dateTimeParsePatterns() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetDateTimeParsePatterns.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerEmailAddress}
     */
    static SpreadsheetMetadataPropertyValueHandlerEmailAddress emailAddress() {
        return SpreadsheetMetadataPropertyValueHandlerEmailAddress.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerInteger}
     */
    static SpreadsheetMetadataPropertyValueHandlerInteger integer(final IntPredicate predicate) {
        return SpreadsheetMetadataPropertyValueHandlerInteger.with(predicate);
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerLocalDateTime}
     */
    static SpreadsheetMetadataPropertyValueHandlerLocalDateTime localDateTime() {
        return SpreadsheetMetadataPropertyValueHandlerLocalDateTime.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerLocale}
     */
    static SpreadsheetMetadataPropertyValueHandlerLocale locale() {
        return SpreadsheetMetadataPropertyValueHandlerLocale.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerLong}
     */
    static SpreadsheetMetadataPropertyValueHandlerLong longHandler(final LongPredicate predicate) {
        return SpreadsheetMetadataPropertyValueHandlerLong.with(predicate);
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberFormatPattern}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberFormatPattern numberFormatPattern() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberFormatPattern.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns numberParsePatterns() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberParsePatterns.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerRoundingMode}
     */
    static SpreadsheetMetadataPropertyValueHandlerRoundingMode roundingMode() {
        return SpreadsheetMetadataPropertyValueHandlerRoundingMode.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetId}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetId spreadsheetId() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetId.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerString}
     */
    static SpreadsheetMetadataPropertyValueHandlerString string(final Predicate<String> predicate) {
        return SpreadsheetMetadataPropertyValueHandlerString.with(predicate);
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern textFormatPattern() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetTextFormatPattern.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetTimeFormatPattern}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetTimeFormatPattern timeFormatPattern() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetTimeFormatPattern.INSTANCE;
    }

    /**
     * {@see SpreadsheetMetadataPropertyValueHandlerSpreadsheetTimeParsePatterns}
     */
    static SpreadsheetMetadataPropertyValueHandlerSpreadsheetTimeParsePatterns timeParsePatterns() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetTimeParsePatterns.INSTANCE;
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
            throw new SpreadsheetMetadataPropertyValueException("Missing value", name, value);
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
        if (textStylePropertyType(typeName)) {
            typeName = typeName.substring(1 + typeName.lastIndexOf('.'));
        }

        return new SpreadsheetMetadataPropertyValueException("Expected " + this.expectedTypeName(type) + " but got " + CharSequences.quoteIfChars(value) + " (" + typeName + ")",
                name,
                value);
    }

    abstract String expectedTypeName(final Class<?> type);

    final boolean textStylePropertyType(final String type) {
        return type.startsWith(PACKAGE) && type.indexOf('.', 1 + PACKAGE.length()) == -1;
    }

    private final static String PACKAGE = "walkingkooka.spreadsheet.meta";

    // fromJsonNode ....................................................................................................

    /**
     * Transforms a {@link JsonNode} into a value.
     */
    abstract T fromJsonNode(final JsonNode node,
                            final SpreadsheetMetadataPropertyName<?> name,
                            final FromJsonNodeContext context);

    /**
     * Transforms a value into json, performing the inverse of {@link #fromJsonNode(JsonNode, SpreadsheetMetadataPropertyName, FromJsonNodeContext)}
     */
    abstract JsonNode toJsonNode(final T value,
                                 final ToJsonNodeContext context);

    // Object .........................................................................................................

    @Override
    abstract public String toString();
}
