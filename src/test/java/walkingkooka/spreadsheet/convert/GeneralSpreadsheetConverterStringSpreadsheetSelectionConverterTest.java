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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.function.Function;

public final class GeneralSpreadsheetConverterStringSpreadsheetSelectionConverterTest extends GeneralSpreadsheetConverterTestCase<GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter>
        implements ConverterTesting2<GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter, SpreadsheetConverterContext> {

    @Test
    public void testStringToCell() {
        this.convertAndCheck2(
                "A1",
                SpreadsheetSelection::parseCell
        );
    }

    @Test
    public void testStringToCellRange() {
        this.convertAndCheck2(
                "B2:C3",
                SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testStringToColumn() {
        this.convertAndCheck2(
                "D",
                SpreadsheetSelection::parseColumn
        );
    }

    @Test
    public void testStringToColumnRange() {
        this.convertAndCheck2(
                "E:F",
                SpreadsheetSelection::parseColumnRange
        );
    }

    @Test
    public void testStringToLabel() {
        this.convertAndCheck2(
                "Label123",
                SpreadsheetSelection::labelName
        );
    }

    @Test
    public void testStringToRow() {
        this.convertAndCheck2(
                "6",
                SpreadsheetSelection::parseRow
        );
    }

    @Test
    public void testStringToRowRange() {
        this.convertAndCheck2(
                "7:8",
                SpreadsheetSelection::parseRowRange
        );
    }

    private void convertAndCheck2(final String string,
                                  final Function<String, ? extends SpreadsheetSelection> parse) {
        this.convertAndCheck(
                string,
                parse.apply(string)
        );
    }

    @Override
    public GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter createConverter() {
        return GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    @Override
    public Class<GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter> type() {
        return GeneralSpreadsheetConverterStringSpreadsheetSelectionConverter.class;
    }
}
