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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import java.util.EnumSet;
import java.util.Set;

public final class SpreadsheetDeltaPropertiesTest implements ClassTesting<SpreadsheetDeltaProperties> {

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

    // csv.............................................................................................................

    @Test
    public void testCsvWithNull() {
        this.csvAndCheck(
                null,
                EnumSet.allOf(SpreadsheetDeltaProperties.class)
        );
    }

    @Test
    public void testCsvWithEmptyString() {
        this.csvAndCheck(
                "",
                EnumSet.allOf(SpreadsheetDeltaProperties.class)
        );
    }

    @Test
    public void testCsvWithCells() {
        this.csvAndCheck(
                "cells",
                EnumSet.of(SpreadsheetDeltaProperties.CELLS)
        );
    }

    @Test
    public void testCsvWithDeletedCells() {
        this.csvAndCheck(
                "deleted-cells",
                EnumSet.of(SpreadsheetDeltaProperties.DELETED_CELLS)
        );
    }

    @Test
    public void testCsvWithSeveral() {
        this.csvAndCheck(
                "cells,columns,deleted-cells",
                EnumSet.of(
                        SpreadsheetDeltaProperties.CELLS,
                        SpreadsheetDeltaProperties.COLUMNS,
                        SpreadsheetDeltaProperties.DELETED_CELLS
                )
        );
    }

    @Test
    public void testCsvWithStar() {
        this.csvAndCheck(
                "*",
                EnumSet.allOf(SpreadsheetDeltaProperties.class)
        );
    }

    private void csvAndCheck(final String csv,
                             final Set<SpreadsheetDeltaProperties> properties) {
        this.checkEquals(
                SpreadsheetDeltaProperties.csv(csv),
                properties,
                () -> "csv " + csv
        );
    }

    @Override
    public Class<SpreadsheetDeltaProperties> type() {
        return SpreadsheetDeltaProperties.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
