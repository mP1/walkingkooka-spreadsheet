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
import walkingkooka.convert.ConverterTesting2;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class GeneralSpreadsheetConverterSelectionStringConverterTest extends GeneralSpreadsheetConverterTestCase<GeneralSpreadsheetConverterSelectionStringConverter>
        implements ConverterTesting2<GeneralSpreadsheetConverterSelectionStringConverter, SpreadsheetConverterContext> {

    @Test
    public void testCellToSpreadsheetSelection() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCell("Z99"),
                SpreadsheetSelection.class
        );
    }

    @Test
    public void testCellToCellRange() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCell("Z99"),
                SpreadsheetSelection.parseCellRange("Z99")
        );
    }

    @Test
    public void testCellRangeToCell() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3"),
                SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testCellRangeToSpreadsheetSelection() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3"),
                SpreadsheetSelection.class
        );
    }

    @Test
    public void testCellToExpressionReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCell("Z99"),
                SpreadsheetExpressionReference.class
        );
    }

    @Test
    public void testCellRangeToExpressionReference() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3" ),
                SpreadsheetExpressionReference.class
        );
    }

    @Override
    public GeneralSpreadsheetConverterSelectionStringConverter createConverter() {
        return GeneralSpreadsheetConverterSelectionStringConverter.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    @Override
    public Class<GeneralSpreadsheetConverterSelectionStringConverter> type() {
        return GeneralSpreadsheetConverterSelectionStringConverter.class;
    }
}
