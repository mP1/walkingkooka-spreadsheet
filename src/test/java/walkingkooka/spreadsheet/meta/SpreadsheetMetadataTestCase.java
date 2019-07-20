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
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetMetadataTestCase<T extends SpreadsheetMetadata> implements ClassTesting2<SpreadsheetMetadata>,
        HashCodeEqualsDefinedTesting<SpreadsheetMetadata>,
        HasJsonNodeTesting<SpreadsheetMetadata>,
        HateosResourceTesting<SpreadsheetMetadata>,
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
        assertThrows(NullPointerException.class, () -> {
            this.createObject().get(null);
        });
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

    // set..............................................................................................................

    @Test
    public final void testSetNullPropertyNameFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().set(null, "value");
        });
    }

    @Test
    public final void testSetNullPropertyValueFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> {
            this.createObject().set(SpreadsheetMetadataPropertyName.CREATOR, null);
        });
    }

    @Test
    public final void testSetInvalidPropertyValueFails() {
        assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> {
            final SpreadsheetMetadataPropertyName<?> propertyName = SpreadsheetMetadataPropertyName.CREATOR;
            this.createObject().set(propertyName, Cast.to("invalid-expected-EmailAddress"));
        });
    }

    final <T> SpreadsheetMetadata setAndCheck(final SpreadsheetMetadata metadata,
                                              final SpreadsheetMetadataPropertyName<T> propertyName,
                                              final T value,
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
        assertThrows(NullPointerException.class, () -> {
            this.createObject().remove(null);
        });
    }

    @Test
    public final void testRemoveUnknown() {
        final SpreadsheetMetadata metadata = this.createObject();
        assertSame(metadata, metadata.remove(SpreadsheetMetadataPropertyName.MODIFIED_BY));
    }

    final <T> SpreadsheetMetadata removeAndCheck(final SpreadsheetMetadata metadata,
                                                 final SpreadsheetMetadataPropertyName<T> propertyName,
                                                 final SpreadsheetMetadata expected) {
        final SpreadsheetMetadata removed = metadata.remove(propertyName);
        assertEquals(expected,
                removed,
                () -> metadata + " remove " + propertyName);
        return removed;
    }

    // HasMathContext...................................................................................................

    @Test
    public final void testHasMathContextRequiredPropertiesAbsentFails() {
        assertThrows(IllegalStateException.class, () -> {
            this.createObject().mathContext();
        });
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

    // HasJsonNodeTesting................................................................................................

    @Override
    public final SpreadsheetMetadata fromJsonNode(final JsonNode from) {
        return SpreadsheetMetadata.fromJsonNode(from);
    }

    @Override
    public final SpreadsheetMetadata createHasJsonNode() {
        return this.createObject();
    }

    // HateosResourceTesting.............................................................................................

    @Override
    public final SpreadsheetMetadata createHateosResource() {
        return this.createObject();
    }
}
