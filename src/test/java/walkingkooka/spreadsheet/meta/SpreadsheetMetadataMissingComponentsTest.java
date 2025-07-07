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
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.TypeNameTesting;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataMissingComponentsTest implements ClassTesting2<SpreadsheetMetadataMissingComponents>,
    ToStringTesting<SpreadsheetMetadataMissingComponents>,
    TypeNameTesting<SpreadsheetMetadataMissingComponents> {

    // getOrNull........................................................................................................

    @Test
    public void testGetOrNullAbsent() {
        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(SpreadsheetMetadata.EMPTY);
        this.checkEquals(null, components.getOrNull(SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES));
        this.checkMissing(components, SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES);
    }

    @Test
    public void testGetOrNullPresent() {
        final SpreadsheetMetadataPropertyName<Boolean> property = SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES;
        final boolean value = true;

        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(
            SpreadsheetMetadata.EMPTY.set(
                property,
                value
            )
        );
        this.checkEquals(value, components.getOrNull(property));
        this.checkMissing(components);
    }

    // reportIfMissing..................................................................................................

    @Test
    public void testReportIfMissingMissingOne() {
        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.AUDIT_INFO);

        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            components::reportIfMissing
        );
        this.checkEquals(
            "Metadata missing: auditInfo",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testReportIfMissingMissingTwo() {
        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.AUDIT_INFO);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS);

        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            components::reportIfMissing
        );
        this.checkEquals(
            "Metadata missing: auditInfo, decimalNumberSymbols",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testReportIfMissingMissingSorted() {
        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(SpreadsheetMetadata.EMPTY);

        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS);
        components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);
        components.getOrNull(SpreadsheetMetadataPropertyName.LOCALE);
        components.getOrNull(SpreadsheetMetadataPropertyName.AUDIT_INFO);

        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            components::reportIfMissing
        );
        this.checkEquals(
            "Metadata missing: auditInfo, decimalNumberSymbols, locale, roundingMode",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testReportIfMissingMissingDuplicates() {
        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.AUDIT_INFO);
        components.getOrNull(SpreadsheetMetadataPropertyName.ROUNDING_MODE);
        components.getOrNull(SpreadsheetMetadataPropertyName.AUDIT_INFO);

        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            components::reportIfMissing
        );
        this.checkEquals(
            "Metadata missing: auditInfo, roundingMode",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testReportIfMissingNone() {
        final SpreadsheetMetadataPropertyName<AuditInfo> property = SpreadsheetMetadataPropertyName.AUDIT_INFO;
        final AuditInfo value = AuditInfo.with(
            EmailAddress.parse("created@example.com"),
            LocalDateTime.MIN,
            EmailAddress.parse("modified@example.com"),
            LocalDateTime.MAX
        );

        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(
            SpreadsheetMetadata.EMPTY.set(
                property,
                value
            )
        );
        components.getOrNull(property);
        components.reportIfMissing();
    }

    private void checkMissing(final SpreadsheetMetadataMissingComponents components,
                              final SpreadsheetMetadataPropertyName<?>... missings) {
        final Set<SpreadsheetMetadataPropertyName<?>> set = SortedSets.tree();
        set.addAll(Arrays.asList(missings));
        this.checkEquals(set, components.missing, "missing");
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetMetadataMissingComponents components = SpreadsheetMetadataMissingComponents.with(SpreadsheetMetadata.EMPTY);
        components.getOrNull(SpreadsheetMetadataPropertyName.AUDIT_INFO);
        components.getOrNull(SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS);

        this.toStringAndCheck(
            components,
            Lists.of(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS
            ).toString()
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataMissingComponents> type() {
        return SpreadsheetMetadataMissingComponents.class;
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
