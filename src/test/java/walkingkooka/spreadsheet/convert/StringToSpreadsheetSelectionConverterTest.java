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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.function.Function;

public final class StringToSpreadsheetSelectionConverterTest implements ConverterTesting2<StringToSpreadsheetSelectionConverter, SpreadsheetConverterContext> {

    @Test
    public void testStringToCell() {
        this.convertAndCheck2(
                "A1",
                SpreadsheetSelection::parseCell
        );
    }

    @Test
    public void testStringLabelToSpreadsheetCellWithLabel() {
        final String label = "Label123";
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        this.convertAndCheck(
                label,
                SpreadsheetCellReference.class,
                this.createContext(label, cell),
                cell
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
    public void testStringToSpreadsheetCellOrCellRangeWithCell() {
        this.convertAndCheck(
                "A1",
                SpreadsheetCellReferenceOrRange.class,
                SpreadsheetSelection.parseCell("A1")
        );
    }

    @Test
    public void testStringLabelToSpreadsheetCellOrCellRangeWithCell() {
        final String label = "Label123";
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("Z99");

        this.convertAndCheck(
                label,
                SpreadsheetCellReferenceOrRange.class,
                this.createContext(label, cell),
                cell
        );
    }

    @Test
    public void testStringLabelToSpreadsheetCellOrCellRangeWithCellRange() {
        final String label = "Label123";
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:C3");

        this.convertAndCheck(
                label,
                SpreadsheetCellReferenceOrRange.class,
                this.createContext(label, range),
                range
        );
    }

    @Test
    public void testStringToSpreadsheetCellOrCellRangeWithCellRange() {
        this.convertAndCheck(
                "B2:C3",
                SpreadsheetCellReferenceOrRange.class,
                SpreadsheetSelection.parseCellRange("B2:C3")
        );
    }

    @Test
    public void testStringToSpreadsheetCellOrCellRangeWithCellRangeSingle() {
        this.convertAndCheck(
                "D4:D4",
                SpreadsheetCellReferenceOrRange.class,
                SpreadsheetSelection.parseCellRange("D4")
        );
    }

    @Test
    public void testStringLabelToSpreadsheetCellRangeWithCellRange() {
        final String label = "Label123";
        final SpreadsheetCellRange range = SpreadsheetSelection.parseCellRange("B2:C3");

        this.convertAndCheck(
                label,
                SpreadsheetCellRange.class,
                this.createContext(label, range),
                range
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
    public StringToSpreadsheetSelectionConverter createConverter() {
        return StringToSpreadsheetSelectionConverter.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return this.createContext(
                (s) -> {
                    throw new UnsupportedOperationException();
                }
        );
    }

    private SpreadsheetConverterContext createContext(final String label,
                                                      final SpreadsheetSelection cellOrLabel) {
        return this.createContext(
                (s) -> {
                    this.checkEquals(SpreadsheetSelection.labelName(label), s, "label");
                    return cellOrLabel;
                }
        );
    }

    private SpreadsheetConverterContext createContext(final Function<SpreadsheetSelection, SpreadsheetSelection> resolveIfLabel) {
        return new FakeSpreadsheetConverterContext() {
            @Override
            public SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection) {
                return resolveIfLabel.apply(selection);
            }
        };
    }

    @Override
    public Class<StringToSpreadsheetSelectionConverter> type() {
        return StringToSpreadsheetSelectionConverter.class;
    }
}
