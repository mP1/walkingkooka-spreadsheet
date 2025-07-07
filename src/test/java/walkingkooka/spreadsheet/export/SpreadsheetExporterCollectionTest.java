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

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.WebEntity;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetCellValueKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExporterCollectionTest implements SpreadsheetExporterTesting<SpreadsheetExporterCollection>,
    HashCodeEqualsDefinedTesting2<SpreadsheetExporterCollection>,
    ToStringTesting<SpreadsheetExporterCollection> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExporterCollection.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetExporterCollection.with(Lists.empty())
        );
    }

    @Test
    public void testWithOneUnwraps() {
        final SpreadsheetExporter exporter = SpreadsheetExporters.fake();
        assertSame(
            exporter,
            SpreadsheetExporterCollection.with(
                Lists.of(exporter)
            )
        );
    }

    private final static SpreadsheetCellRange CELL_RANGE = SpreadsheetCellRange.with(
        SpreadsheetSelection.parseCellRange("A1:C3"),
        Sets.empty()
    );

    private final static WebEntity WEB_ENTITY = WebEntity.empty()
        .setContentType(
            Optional.of(MediaType.TEXT_PLAIN)
        );

    @Test
    public void testExport() {
        this.exportAndCheck(
            CELL_RANGE,
            SpreadsheetCellValueKind.CELL,
            WEB_ENTITY
        );
    }

    @Override
    public SpreadsheetExporterCollection createSpreadsheetExporter() {
        return (SpreadsheetExporterCollection)
            SpreadsheetExporterCollection.with(
                Lists.of(
                    SpreadsheetExporters.empty(),
                    new SpreadsheetExporter() {
                        @Override
                        public boolean canExport(final SpreadsheetCellRange cells,
                                                 final SpreadsheetCellValueKind kind,
                                                 final SpreadsheetExporterContext context) {
                            checkEquals(CELL_RANGE, cells, "cells");
                            return true;
                        }

                        @Override
                        public WebEntity export(final SpreadsheetCellRange cells,
                                                final SpreadsheetCellValueKind kind,
                                                final SpreadsheetExporterContext context) {
                            checkEquals(CELL_RANGE, cells, "cells");
                            return WEB_ENTITY;
                        }
                    }
                )
            );
    }

    @Override
    public SpreadsheetExporterContext createContext() {
        return SpreadsheetExporterContexts.fake();
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentExporters() {
        this.checkNotEquals(
            SpreadsheetExporterCollection.with(
                Lists.of(
                    SpreadsheetExporters.fake(),
                    SpreadsheetExporters.fake()
                )
            ),
            SpreadsheetExporterCollection.with(
                Lists.of(
                    SpreadsheetExporters.fake(),
                    SpreadsheetExporters.fake(),
                    SpreadsheetExporters.fake()
                )
            )
        );
    }

    @Override
    public SpreadsheetExporterCollection createObject() {
        return (SpreadsheetExporterCollection)
            SpreadsheetExporterCollection.with(
                Lists.of(
                    SpreadsheetExporters.empty(),
                    SpreadsheetExporters.empty()
                )
            );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            "collection(EmptySpreadsheetExporter,EmptySpreadsheetExporter)"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExporterCollection> type() {
        return SpreadsheetExporterCollection.class;
    }
}
