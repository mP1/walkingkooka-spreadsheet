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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;

public final class SpreadsheetConverterSelectionConverterTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSelectionConverter> implements ConverterTesting2<SpreadsheetConverterSelectionConverter, ExpressionNumberConverterContext> {

    @Test
    public void testCellToCellRange() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCell("Z99" ),
                SpreadsheetCellRange.class,
                SpreadsheetSelection.parseCellRange("Z99" )
        );
    }

    @Test
    public void testCellRangeToCell() {
        this.convertAndCheck(
                SpreadsheetSelection.parseCellRange("B2:C3" ),
                SpreadsheetCellReference.class,
                SpreadsheetSelection.parseCell("B2" )
        );
    }

    @Override
    public SpreadsheetConverterSelectionConverter createConverter() {
        return SpreadsheetConverterSelectionConverter.INSTANCE;
    }

    @Override
    public ExpressionNumberConverterContext createContext() {
        return ExpressionNumberConverterContexts.fake();
    }

    @Override
    public Class<SpreadsheetConverterSelectionConverter> type() {
        return SpreadsheetConverterSelectionConverter.class;
    }
}
