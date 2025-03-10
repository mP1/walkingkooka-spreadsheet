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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataComponentsTest implements ClassTesting2<SpreadsheetMetadataComponents>,
        ToStringTesting<SpreadsheetMetadataComponents>,
        TypeNameTesting<SpreadsheetMetadataComponents> {

    // getOrNull........................................................................................................

    @Test
    public void testGetOrNullAbsent() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        this.checkEquals(null, components.getOrNull(SpreadsheetMetadataPropertyName.CREATED_BY));
        this.checkMissing(components, SpreadsheetMetadataPropertyName.CREATED_BY);
    }

    @Test
    public void testGetOrNullPresent() {
        final SpreadsheetMetadataPropertyName<EmailAddress> property = SpreadsheetMetadataPropertyName.CREATED_BY;
        final EmailAddress value = EmailAddress.parse("user@example.com");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY.set(property, value));
        this.checkEquals(value, components.getOrNull(property));
        this.checkMissing(components);
    }

    // reportIfMissing..................................................................................................

    @Test
    public void testReportIfMissingMissingOne() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATED_BY);

        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                components::reportIfMissing
        );
        this.checkEquals(
                "Metadata missing: created-by",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testReportIfMissingMissingTwo() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATED_BY);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR);

        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                components::reportIfMissing
        );
        this.checkEquals(
                "Metadata missing: created-by, decimal-separator",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testReportIfMissingMissingSorted() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATED_BY);
        components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);
        components.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);

        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                components::reportIfMissing
        );
        this.checkEquals(
                "Metadata missing: created-by, decimal-separator, locale, rounding-mode",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testReportIfMissingMissingDuplicates() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATED_BY);
        components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATED_BY);

        final IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                components::reportIfMissing
        );
        this.checkEquals(
                "Metadata missing: created-by, rounding-mode",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testReportIfMissingNone() {
        final SpreadsheetMetadataPropertyName<EmailAddress> property = SpreadsheetMetadataPropertyName.CREATED_BY;
        final EmailAddress value = EmailAddress.parse("user@example.com");

        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY.set(property, value));
        components.getOrNull(property);
        components.reportIfMissing();
    }

    private void checkMissing(final SpreadsheetMetadataComponents components,
                              final SpreadsheetMetadataPropertyName<?>... missings) {
        final Set<SpreadsheetMetadataPropertyName<?>> set = SortedSets.tree();
        set.addAll(Arrays.asList(missings));
        this.checkEquals(set, components.missing, "missing");
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetMetadataComponents components = SpreadsheetMetadataComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.CREATED_BY);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR);

        this.toStringAndCheck(components, Lists.of(SpreadsheetMetadataPropertyName.CREATED_BY, SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR).toString());
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
