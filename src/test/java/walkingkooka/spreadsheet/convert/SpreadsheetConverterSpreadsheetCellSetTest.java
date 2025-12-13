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

package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.engine.collection.SpreadsheetCellSet;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

import java.util.Set;

public final class SpreadsheetConverterSpreadsheetCellSetTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetCellSet> {

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetCellSet() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        this.convertAndCheck(
            cell,
            SpreadsheetCellSet.with(
                Sets.of(cell)
            )
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSet() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        this.convertAndCheck(
            cell,
            Set.class,
            SpreadsheetCellSet.with(
                Sets.of(cell)
            )
        );
    }

    @Test
    public void testConvertSetOfSpreadsheetCellToSpreadsheetCellSet() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        this.convertAndCheck(
            Sets.of(cell),
            SpreadsheetCellSet.with(
                Sets.of(cell)
            )
        );
    }

    @Test
    public void testConvertSpreadsheetCellSetToSpreadsheetCellSet() {
        final SpreadsheetCellSet cells = SpreadsheetCellSet.with(
            Sets.of(
                SpreadsheetSelection.A1.setFormula(
                    SpreadsheetFormula.EMPTY.setText("=1")
                )
            )
        );

        this.convertAndCheck(
            cells
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetCellSet createConverter() {
        return SpreadsheetConverterSpreadsheetCellSet.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterSpreadsheetCellSet.INSTANCE,
            SpreadsheetCellSet.class.getSimpleName()
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetCellSet> type() {
        return SpreadsheetConverterSpreadsheetCellSet.class;
    }
}
