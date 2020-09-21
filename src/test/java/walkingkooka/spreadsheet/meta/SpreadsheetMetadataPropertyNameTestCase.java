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
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.CharSequences;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetMetadataPropertyNameTestCase<N extends SpreadsheetMetadataPropertyName<V>, V> extends SpreadsheetMetadataTestCase2<N>
        implements ToStringTesting<N> {

    SpreadsheetMetadataPropertyNameTestCase() {
        super();
    }

    @Test
    public final void testCheckValueNullFails() {
        this.checkValueFails(null,
                "Missing value, but got null for " + CharSequences.quote(this.createName().value()));
    }

    @Test
    public final void testCheckValueInvalidFails() {
        this.checkValueFails(this,
                "Expected " + this.propertyValueType() + ", but got " + this + " for " + CharSequences.quote(this.createName().value()));
    }

    @Test
    public final void testCheckInvalidValueFails2() {
        final StringBuilder value = new StringBuilder("123abc");
        this.checkValueFails(value,
                "Expected " + this.propertyValueType() + ", but got \"123abc\" for " + CharSequences.quote(this.createName().value()));
    }

    @Test
    public final void testCheckValue() {
        this.checkValue(this.propertyValue());
    }

    final void checkValue(final Object value) {
        this.createName().checkValue(value);
    }

    final void checkValueFails(final Object value, final String message) {
        final SpreadsheetMetadataPropertyName<?> propertyName = this.createName();

        final SpreadsheetMetadataPropertyValueException thrown = assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> propertyName.checkValue(value));
        this.checkSpreadsheetMetadataPropertyValueException(thrown, message, propertyName, value);

        final SpreadsheetMetadataPropertyValueException thrown2 = assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> propertyName.checkValue(value));
        this.checkSpreadsheetMetadataPropertyValueException(thrown2, message, propertyName, value);
    }

    private void checkSpreadsheetMetadataPropertyValueException(final SpreadsheetMetadataPropertyValueException thrown,
                                                                final String message,
                                                                final SpreadsheetMetadataPropertyName<?> propertyName,
                                                                final Object value) {
        if (null != message) {
            assertEquals(message, thrown.getMessage(), "message");
        }
        assertEquals(propertyName, thrown.name(), "propertyName");
        assertEquals(value, thrown.value(), "value");
    }

    // extractLocaleValue...............................................................................................

    final void extractLocaleValueAndCheck(final Locale locale,
                                          final V value) {
        final N propertyName = this.createName();
        assertEquals(Optional.ofNullable(value),
                propertyName.extractLocaleValue(locale),
                propertyName + " extractLocaleValue " + locale);
    }

    // NameTesting......................................................................................................

    abstract N createName();

    abstract V propertyValue();

    abstract String propertyValueType();

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetMetadataPropertyName.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
