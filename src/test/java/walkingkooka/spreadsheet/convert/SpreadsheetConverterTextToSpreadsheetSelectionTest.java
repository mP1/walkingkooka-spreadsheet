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
import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.function.Function;

public final class SpreadsheetConverterTextToSpreadsheetSelectionTest extends SpreadsheetConverterTestCase<SpreadsheetConverterTextToSpreadsheetSelection> {

    @Test
    public void testConvertWithStringToSpreadsheetSelectionFails() {
        this.convertFails(
            "A1",
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertWithStringToCell() {
        this.convertAndCheck2(
            "A1",
            SpreadsheetSelection::parseCell
        );
    }

    @Test
    public void testConvertWithStringToCellAndTargetSpreadsheetExpressionReference() {
        this.convertSpreadsheetExpressionReferenceAndCheck(
            "A1",
            SpreadsheetSelection::parseCell
        );
    }

    @Test
    public void testConvertWithCharSequenceToCell() {
        this.convertAndCheck(
            new StringBuilder("A1"),
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testConvertWithStringToLabelAndSpreadsheetCellWithLabel() {
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
    public void testConvertWithStringToCellRange() {
        this.convertAndCheck2(
            "B2:C3",
            SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testConvertWithStringToCellRangeAndTargetSpreadsheetExpressionReference() {
        this.convertSpreadsheetExpressionReferenceAndCheck(
            "B2:C3",
            SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testConvertWithStringToCellOrCellRangeWithCell() {
        this.convertAndCheck(
            "A1",
            SpreadsheetCellReferenceOrRange.class,
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testConvertWithStringToLabelToCellOrCellRangeWithCell() {
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
    public void testConvertWithStringToLabelToCellOrCellRangeWithCellRange() {
        final String label = "Label123";
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:C3");

        this.convertAndCheck(
            label,
            SpreadsheetCellReferenceOrRange.class,
            this.createContext(label, range),
            range
        );
    }

    @Test
    public void testConvertWithStringToLabelAndSpreadsheetExpressionReference() {
        this.convertSpreadsheetExpressionReferenceAndCheck(
            "Label123",
            SpreadsheetSelection::labelName
        );
    }

    @Test
    public void testConvertWithStringToCellOrCellRangeWithCellRange() {
        this.convertAndCheck(
            "B2:C3",
            SpreadsheetCellReferenceOrRange.class,
            SpreadsheetSelection.parseCellRange("B2:C3")
        );
    }

    @Test
    public void testConvertWithStringToCellOrCellRangeWithCellRangeSingle() {
        this.convertAndCheck(
            "D4:D4",
            SpreadsheetCellReferenceOrRange.class,
            SpreadsheetSelection.parseCellRange("D4")
        );
    }

    @Test
    public void testConvertWithStringToLabelToSpreadsheetCellRangeWithCellRange() {
        final String label = "Label123";
        final SpreadsheetCellRangeReference range = SpreadsheetSelection.parseCellRange("B2:C3");

        this.convertAndCheck(
            label,
            SpreadsheetCellRangeReference.class,
            this.createContext(label, range),
            range
        );
    }

    @Test
    public void testConvertWithStringToColumn() {
        this.convertAndCheck2(
            "D",
            SpreadsheetSelection::parseColumn
        );
    }

    @Test
    public void testConvertWithStringToColumnWithCellFails() {
        this.convertFails(
            "A1",
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testConvertWithStringToColumnWithCellRangeFails() {
        this.convertFails(
            "A1:B2",
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testConvertWithStringToColumnWithRowFails() {
        this.convertFails(
            "1",
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testConvertWithStringToColumnWithRowRangeFails() {
        this.convertFails(
            "2:3",
            SpreadsheetColumnReference.class
        );
    }

    @Test
    public void testConvertWithStringToColumnRange() {
        this.convertAndCheck2(
            "E:F",
            SpreadsheetSelection::parseColumnRange
        );
    }

    @Test
    public void testConvertWithStringToLabel() {
        this.convertAndCheck2(
            "Label123",
            SpreadsheetSelection::labelName
        );
    }

    @Test
    public void testConvertWithStringToRow() {
        this.convertAndCheck2(
            "6",
            SpreadsheetSelection::parseRow
        );
    }

    @Test
    public void testConvertWithStringToRowRange() {
        this.convertAndCheck2(
            "7:8",
            SpreadsheetSelection::parseRowRange
        );
    }

    @Test
    public void testConvertWithStringToRowWithCellFails() {
        this.convertFails(
            "A1",
            SpreadsheetRowReference.class
        );
    }

    @Test
    public void testConvertWithStringToRowRangeWithCellRangeFails() {
        this.convertFails(
            "B2:C3",
            SpreadsheetRowReference.class
        );
    }

    @Test
    public void testConvertWithStringToRowWithColumnFails() {
        this.convertFails(
            "A",
            SpreadsheetRowReference.class
        );
    }

    @Test
    public void testConvertWithStringToRowRangeWithColumnRangeFails() {
        this.convertFails(
            "B:C",
            SpreadsheetRowReference.class
        );
    }

    private void convertAndCheck2(final String string,
                                  final Function<String, ? extends SpreadsheetSelection> parse) {
        this.convertAndCheck(
            string,
            parse.apply(string)
        );
    }

    private void convertSpreadsheetExpressionReferenceAndCheck(final String string,
                                                               final Function<String, ? extends SpreadsheetExpressionReference> parser) {
        this.convertAndCheck(
            string,
            SpreadsheetExpressionReference.class,
            parser.apply(string)
        );
    }

    @Override
    public SpreadsheetConverterTextToSpreadsheetSelection createConverter() {
        return SpreadsheetConverterTextToSpreadsheetSelection.INSTANCE;
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
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.textToText();

            @Override
            public SpreadsheetSelection resolveIfLabelOrFail(final SpreadsheetSelection selection) {
                return resolveIfLabel.apply(selection);
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterTextToSpreadsheetSelection> type() {
        return SpreadsheetConverterTextToSpreadsheetSelection.class;
    }
}
