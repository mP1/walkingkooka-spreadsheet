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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.test.TypeNameTesting;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataComponentsTest implements ClassTesting2,
        ToStringTesting<SpreadsheetMetadataComponents>,
        TypeNameTesting<SpreadsheetMetadataComponents> {

    // getOrNull........................................................................................................

    @Test
    public void testGetOrNullAbsent() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        assertEquals(null, components.getOrNull(SpreadsheetMetadataPropertyName.CREATOR));
        this.checkMissing(components, SpreadsheetMetadataPropertyName.CREATOR);
    }

    @Test
    public void testGetOrNullPresent() {
        final SpreadsheetMetadataPropertyName<EmailAddress> property = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress value = EmailAddress.parse("user@example.com");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.with(Maps.of(property, value)));
        assertEquals(value, components.getOrNull(property));
        this.checkMissing(components);
    }

    // getOrElse........................................................................................................

    @Test
    public void testGetOrElseAbsentSupplierNull() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        assertEquals(null, components.getOrElse(SpreadsheetMetadataPropertyName.CREATOR, () -> null));
        this.checkMissing(components, SpreadsheetMetadataPropertyName.CREATOR);
    }

    @Test
    public void testGetOrElseSupplierPresent() {
        final SpreadsheetMetadataPropertyName<EmailAddress> property = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress value = EmailAddress.parse("user@example.com");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        assertEquals(value, components.getOrElse(property, () -> value));
        this.checkMissing(components);
    }

    @Test
    public void testGetOrElsePresent() {
        final SpreadsheetMetadataPropertyName<EmailAddress> property = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress value = EmailAddress.parse("user@example.com");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.with(Maps.of(property, value)));
        assertEquals(value, components.getOrElse(property, () -> {
            throw new UnsupportedOperationException();
        }));
        this.checkMissing(components);
    }

    // reportIfMissing..................................................................................................

    @Test
    public void testReportIfMissingMissingOne() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATOR);

        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            components.reportIfMissing();
        });
        assertEquals("Required properties \"creator\" missing.", thrown.getMessage(), "message");
    }

    @Test
    public void testReportIfMissingMissingTwo() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATOR);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_POINT);

        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            components.reportIfMissing();
        });
        assertEquals("Required properties \"creator\", \"decimal-point\" missing.", thrown.getMessage(), "message");
    }

    @Test
    public void testReportIfMissingMissingSorted() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_POINT);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATOR);
        components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);
        components.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);

        final IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            components.reportIfMissing();
        });
        assertEquals("Required properties \"creator\", \"decimal-point\", \"locale\", \"rounding-mode\" missing.", thrown.getMessage(), "message");
    }

    @Test
    public void testReportIfMissingNone() {
        final SpreadsheetMetadataPropertyName<EmailAddress> property = SpreadsheetMetadataPropertyName.CREATOR;
        final EmailAddress value = EmailAddress.parse("user@example.com");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.with(Maps.of(property, value)));
        components.getOrNull(property);
        components.reportIfMissing();
    }

    private void checkMissing(final SpreadsheetMetadataComponents components,
                              final SpreadsheetMetadataPropertyName<?>... missings) {
        assertEquals(Lists.of(missings), components.missing, "missing");
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATOR);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_POINT);

        this.toStringAndCheck(components, Lists.of(SpreadsheetMetadataPropertyName.CREATOR, SpreadsheetMetadataPropertyName.DECIMAL_POINT).toString());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataComponents> type() {
        return SpreadsheetMetadataComponents.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMetadata.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return "";
    }
}
