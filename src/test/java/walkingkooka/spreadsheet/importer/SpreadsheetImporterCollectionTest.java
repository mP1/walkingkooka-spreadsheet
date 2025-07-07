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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.WebEntity;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetImporterCollectionTest implements SpreadsheetImporterTesting<SpreadsheetImporterCollection>,
    HashCodeEqualsDefinedTesting2<SpreadsheetImporterCollection>,
    ToStringTesting<SpreadsheetImporterCollection> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterCollection.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetImporterCollection.with(Lists.empty())
        );
    }

    @Test
    public void testWithOneUnwraps() {
        final SpreadsheetImporter importer = SpreadsheetImporters.fake();
        assertSame(
            importer,
            SpreadsheetImporterCollection.with(
                Lists.of(importer)
            )
        );
    }

    private final static List<SpreadsheetImporterCellValue> IMPORTED = Lists.of(
        SpreadsheetImporterCellValue.cell(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
        )
    );

    private final static WebEntity WEB_ENTITY = WebEntity.empty()
        .setContentType(
            Optional.of(MediaType.TEXT_PLAIN)
        );

    @Test
    public void testDoImport() {
        this.doImportAndCheck(
            WEB_ENTITY,
            IMPORTED
        );
    }

    @Override
    public SpreadsheetImporterCollection createSpreadsheetImporter() {
        return (SpreadsheetImporterCollection)
            SpreadsheetImporterCollection.with(
                Lists.of(
                    SpreadsheetImporters.empty(),
                    new SpreadsheetImporter() {
                        @Override
                        public boolean canImport(final WebEntity cells,
                                                 final SpreadsheetImporterContext context) {
                            checkEquals(WEB_ENTITY, cells, "cells");
                            return true;
                        }

                        @Override
                        public List<SpreadsheetImporterCellValue> doImport(final WebEntity cells,
                                                                           final SpreadsheetImporterContext context) {
                            checkEquals(WEB_ENTITY, cells, "cells");
                            return IMPORTED;
                        }
                    }
                )
            );
    }

    @Override
    public SpreadsheetImporterContext createContext() {
        return SpreadsheetImporterContexts.fake();
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentImporters() {
        this.checkNotEquals(
            SpreadsheetImporterCollection.with(
                Lists.of(
                    SpreadsheetImporters.fake(),
                    SpreadsheetImporters.fake()
                )
            ),
            SpreadsheetImporterCollection.with(
                Lists.of(
                    SpreadsheetImporters.fake(),
                    SpreadsheetImporters.fake(),
                    SpreadsheetImporters.fake()
                )
            )
        );
    }

    @Override
    public SpreadsheetImporterCollection createObject() {
        return (SpreadsheetImporterCollection)
            SpreadsheetImporterCollection.with(
                Lists.of(
                    SpreadsheetImporters.empty(),
                    SpreadsheetImporters.empty()
                )
            );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            "collection(EmptySpreadsheetImporter,EmptySpreadsheetImporter)"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetImporterCollection> type() {
        return SpreadsheetImporterCollection.class;
    }
}
