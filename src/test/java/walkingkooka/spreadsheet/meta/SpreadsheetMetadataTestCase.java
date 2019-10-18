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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterTesting;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetMetadataTestCase<T extends SpreadsheetMetadata> implements ClassTesting2<SpreadsheetMetadata>,
        ConverterTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetMetadata>,
        JsonNodeMarshallingTesting<SpreadsheetMetadata>,
        HateosResourceTesting<SpreadsheetMetadata>,
        ThrowableTesting,
        ToStringTesting<SpreadsheetMetadata> {

    SpreadsheetMetadataTestCase() {
        super();
    }

    // isEmpty...........................................................................................................

    @Test
    public final void testIsEmpty() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertEquals(metadata.value().isEmpty(),
                metadata.isEmpty(),
                () -> "" + metadata);
    }

    // get..............................................................................................................

    @Test
    public final void testGetNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().get(null));
    }

    @Test
    public final void testGetUnknown() {
        this.getAndCheck(this.createObject(),
                SpreadsheetMetadataPropertyName.MODIFIED_BY,
                null);
    }

    final <TT> void getAndCheck(final SpreadsheetMetadata metadata,
                                final SpreadsheetMetadataPropertyName<TT> propertyName,
                                final TT value) {
        assertEquals(Optional.ofNullable(value),
                metadata.get(propertyName),
                () -> metadata + " get " + propertyName);
    }

    // getOrFail........................................................................................................

    @Test
    public final void testGetOrFailFails() {
        final SpreadsheetMetadataPropertyName<String> propertyName = SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL;

        final SpreadsheetMetadataPropertyValueException thrown = assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> this.createObject().getOrFail(propertyName));

        this.checkMessage(thrown, "Required property missing");
        assertEquals(propertyName, thrown.name(), "property name");
        assertEquals(null, thrown.value(), "property value");
    }

    // set..............................................................................................................

    @Test
    public final void testSetNullPropertyNameFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().set(null, "value"));
    }

    @Test
    public final void testSetNullPropertyValueFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> this.createObject().set(SpreadsheetMetadataPropertyName.CREATOR, null));
    }

    @Test
    public final void testSetInvalidPropertyValueFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> {
            final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
            this.createObject().set(propertyName, Cast.to("invalid-expected-EmailAddress"));
        });
    }

    final <TT> SpreadsheetMetadata setAndCheck(final SpreadsheetMetadata metadata,
                                               final SpreadsheetMetadataPropertyName<TT> propertyName,
                                               final TT value,
                                               final SpreadsheetMetadata expected) {
        final SpreadsheetMetadata set = metadata.set(propertyName, value);
        assertEquals(expected,
                set,
                () -> metadata + " set " + propertyName + " and " + CharSequences.quoteIfChars(value));
        return set;
    }

    // remove...........................................................................................................

    @Test
    public final void testRemoveNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().remove(null));
    }

    @Test
    public final void testRemoveUnknown() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertSame(metadata, metadata.remove(SpreadsheetMetadataPropertyName.MODIFIED_BY));
    }

    final SpreadsheetMetadata removeAndCheck(final SpreadsheetMetadata metadata,
                                             final SpreadsheetMetadataPropertyName<?> propertyName,
                                             final SpreadsheetMetadata expected) {
        final SpreadsheetMetadata removed = metadata.remove(propertyName);
        assertEquals(expected,
                removed,
                () -> metadata + " remove " + propertyName);
        return removed;
    }

    // NameToColor......................................................................................................

    @Test
    public final void testNameToColor() {
        this.nameToColorAndCheck(this.createObject(), SpreadsheetColorName.with("unknown"), null);
    }

    final void nameToColorAndCheck(final SpreadsheetMetadata metadata,
                                   final SpreadsheetColorName name,
                                   final Color color) {
        final Function<SpreadsheetColorName, Optional<Color>> nameToColor = metadata.nameToColor();
        assertEquals(Optional.ofNullable(color),
                nameToColor.apply(name),
                () -> name + " to color " + metadata.toString());
    }

    // NumberToColor....................................................................................................

    @Test
    public final void testNumberToColor() {
        this.numberToColorAndCheck(this.createObject(), 99, null);
    }

    final void numberToColorAndCheck(final SpreadsheetMetadata metadata,
                                     final int number,
                                     final Color color) {
        final Function<Integer, Optional<Color>> numberToColor = metadata.numberToColor();
        assertEquals(Optional.ofNullable(color),
                numberToColor.apply(number),
                () -> number + " to color " + metadata.toString());
    }

    // HasConverter.....................................................................................................

    @Test
    public final void testConverterRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> this.createObject().converter());
        checkMessage(thrown,
                "Required properties \"date-format-pattern\", \"date-parse-pattern\", \"date-time-format-pattern\", \"date-time-offset\", \"date-time-parse-patterns\", \"number-format-pattern\", \"number-parse-patterns\", \"text-format-pattern\", \"time-format-pattern\", \"time-parse-patterns\" missing.");
    }

    // HasDateTimeContext...............................................................................................

    @Test
    public final void testHasDateTimeContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> this.createObject().dateTimeContext());
        checkMessage(thrown,
                "Required properties \"locale\", \"two-digit-year\" missing.");
    }

    // HasDecimalNumberContext..........................................................................................

    @Test
    public final void testHasDecimalNumberContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> this.createObject().decimalNumberContext());
        checkMessage(thrown,
                "Required properties \"currency-symbol\", \"decimal-separator\", \"exponent-symbol\", \"grouping-separator\", \"locale\", \"negative-sign\", \"percentage-symbol\", \"positive-sign\", \"precision\", \"rounding-mode\" missing.");
    }

    // HasFormatter.....................................................................................................

    @Test
    public final void testHasFormatterRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> this.createObject().formatter());
        checkMessage(thrown,
                "Required properties \"date-format-pattern\", \"date-time-format-pattern\", \"number-format-pattern\", \"text-format-pattern\", \"time-format-pattern\" missing.");
    }

    // HasMathContext...................................................................................................

    @Test
    public final void testHasMathContextRequiredPropertiesAbsentFails() {
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> this.createObject().mathContext());
        checkMessage(thrown,
                "Required properties \"precision\", \"rounding-mode\" missing.");
    }

    // ClassTesting.....................................................................................................

    @Override
    public final Class<SpreadsheetMetadata> type() {
        return Cast.to(this.metadataType());
    }

    abstract Class<T> metadataType();

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public final SpreadsheetMetadata unmarshall(final JsonNode from,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetMetadata.unmarshall(from, context);
    }

    @Override
    public final SpreadsheetMetadata createJsonNodeMappingValue() {
        return this.createObject();
    }

    // HateosResourceTesting.............................................................................................

    @Override
    public final SpreadsheetMetadata createHateosResource() {
        return this.createObject();
    }
}
