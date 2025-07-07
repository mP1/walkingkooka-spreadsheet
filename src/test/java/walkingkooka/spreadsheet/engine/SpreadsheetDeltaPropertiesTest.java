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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetDeltaPropertiesTest implements ParseStringTesting<Set<SpreadsheetDeltaProperties>>,
    ClassTesting<SpreadsheetDeltaProperties> {

    // with...................................................................................................

    @Test
    public void testWithCells() {
        this.withAndCheck(
            "cells",
            SpreadsheetDeltaProperties.CELLS
        );
    }

    @Test
    public void testWithDeleted_Cells() {
        this.withAndCheck(
            "deleted-cells",
            SpreadsheetDeltaProperties.DELETED_CELLS
        );
    }

    private void withAndCheck(final String kebabCase,
                              final SpreadsheetDeltaProperties properties) {
        this.checkEquals(
            properties,
            SpreadsheetDeltaProperties.with(kebabCase),
            () -> "with " + kebabCase
        );
    }

    // parse............................................................................................................

    @Override
    public void testParseStringNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseWithNull() {
        this.parseStringAndCheck(
            null,
            EnumSet.allOf(SpreadsheetDeltaProperties.class)
        );
    }

    @Test
    public void testParseWithEmptyString() {
        this.parseStringAndCheck(
            "",
            EnumSet.allOf(SpreadsheetDeltaProperties.class)
        );
    }

    @Test
    public void testParseWithCells() {
        this.parseStringAndCheck(
            "cells",
            EnumSet.of(SpreadsheetDeltaProperties.CELLS)
        );
    }

    @Test
    public void testParseWithDeletedCells() {
        this.parseStringAndCheck(
            "deleted-cells",
            EnumSet.of(SpreadsheetDeltaProperties.DELETED_CELLS)
        );
    }

    @Test
    public void testParseWithSeveral() {
        this.parseStringAndCheck(
            "cells,columns,deleted-cells",
            EnumSet.of(
                SpreadsheetDeltaProperties.CELLS,
                SpreadsheetDeltaProperties.COLUMNS,
                SpreadsheetDeltaProperties.DELETED_CELLS
            )
        );
    }

    @Test
    public void testParseWithSeveralReadOnly() {
        final Set<SpreadsheetDeltaProperties> properties = this.parseStringAndCheck(
            "cells,columns,deleted-cells",
            EnumSet.of(
                SpreadsheetDeltaProperties.CELLS,
                SpreadsheetDeltaProperties.COLUMNS,
                SpreadsheetDeltaProperties.DELETED_CELLS
            )
        );

        assertThrows(
            UnsupportedOperationException.class,
            properties::clear
        );
    }

    @Test
    public void testParseWithStar() {
        this.parseStringAndCheck(
            "*",
            EnumSet.allOf(SpreadsheetDeltaProperties.class)
        );
    }

    @Override
    public Set<SpreadsheetDeltaProperties> parseString(final String text) {
        return SpreadsheetDeltaProperties.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // extract..........................................................................................................

    @Test
    public void testExtractNullParametersFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetDeltaProperties.extract(
                null
            )
        );
    }

    @Test
    public void testExtractMissing() {
        this.extractAndCheck(
            Maps.empty(),
            SpreadsheetDeltaProperties.ALL
        );
    }

    @Test
    public void testExtractEmpty() {
        this.extractAndCheck(
            Maps.of(
                SpreadsheetDeltaProperties.PROPERTIES,
                Lists.empty()
            ),
            SpreadsheetDeltaProperties.ALL
        );
    }

    @Test
    public void testExtractAll() {
        this.extractAndCheck(
            Maps.of(
                SpreadsheetDeltaProperties.PROPERTIES,
                Lists.of("*")
            ),
            SpreadsheetDeltaProperties.ALL
        );
    }

    @Test
    public void testExtractPresent() {
        this.extractAndCheck(
            Maps.of(
                SpreadsheetDeltaProperties.PROPERTIES,
                Lists.of("cells,labels,column-count")
            ),
            Sets.of(
                SpreadsheetDeltaProperties.CELLS,
                SpreadsheetDeltaProperties.COLUMN_COUNT,
                SpreadsheetDeltaProperties.LABELS
            )
        );
    }

    private void extractAndCheck(final Map<HttpRequestAttribute<?>, Object> parameters,
                                 final Set<SpreadsheetDeltaProperties> expected) {
        this.checkEquals(
            expected,
            SpreadsheetDeltaProperties.extract(
                parameters
            ),
            parameters::toString
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetDeltaProperties> type() {
        return SpreadsheetDeltaProperties.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
