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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.function.Function;

public final class SpreadsheetConverterTextToSpreadsheetSelectionTest extends SpreadsheetConverterTestCase<SpreadsheetConverterTextToSpreadsheetSelection> {

    @Test
    public void testConvertStringToCell() {
        this.convertAndCheck2(
                "A1",
                SpreadsheetSelection::parseCell
        );
    }

    @Test
    public void testConvertStringToSpreadsheetExpressionReferenceCell() {
        this.convertSpreadsheetExpressionReferenceAndCheck(
                "A1",
                SpreadsheetSelection::parseCell
        );
    }

    @Test
    public void testConvertCharSequenceToCell() {
        this.convertAndCheck(
                new StringBuilder("A1"),
                SpreadsheetSelection.A1
        );
    }

    @Test
    public void testConvertStringLabelToSpreadsheetCellWithLabel() {
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
    public void testConvertStringToCellRange() {
        this.convertAndCheck2(
                "B2:C3",
                SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testConvertStringToSpreadsheetExpressionReferenceCellRange() {
        this.convertSpreadsheetExpressionReferenceAndCheck(
                "B2:C3",
                SpreadsheetSelection::parseCellRange
        );
    }

    @Test
    public void testConvertStringToSpreadsheetCellOrCellRangeWithCell() {
        this.convertAndCheck(
                "A1",
                SpreadsheetCellReferenceOrRange.class,
                SpreadsheetSelection.A1
        );
    }

    @Test
    public void testConvertStringLabelToSpreadsheetCellOrCellRangeWithCell() {
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
    public void testConvertStringLabelToSpreadsheetCellOrCellRangeWithCellRange() {
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
    public void testConvertStringLabelToSpreadsheetExpressionReference() {
        this.convertSpreadsheetExpressionReferenceAndCheck(
                "Label123",
                SpreadsheetSelection::labelName
        );
    }

    @Test
    public void testConvertStringToSpreadsheetCellOrCellRangeWithCellRange() {
        this.convertAndCheck(
                "B2:C3",
                SpreadsheetCellReferenceOrRange.class,
                SpreadsheetSelection.parseCellRange("B2:C3")
        );
    }

    @Test
    public void testConvertStringToSpreadsheetCellOrCellRangeWithCellRangeSingle() {
        this.convertAndCheck(
                "D4:D4",
                SpreadsheetCellReferenceOrRange.class,
                SpreadsheetSelection.parseCellRange("D4")
        );
    }

    @Test
    public void testConvertStringLabelToSpreadsheetCellRangeWithCellRange() {
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
    public void testConvertStringToColumn() {
        this.convertAndCheck2(
                "D",
                SpreadsheetSelection::parseColumn
        );
    }

    @Test
    public void testConvertStringToColumnRange() {
        this.convertAndCheck2(
                "E:F",
                SpreadsheetSelection::parseColumnRange
        );
    }

    @Test
    public void testConvertStringToLabel() {
        this.convertAndCheck2(
                "Label123",
                SpreadsheetSelection::labelName
        );
    }

    @Test
    public void testConvertStringToRow() {
        this.convertAndCheck2(
                "6",
                SpreadsheetSelection::parseRow
        );
    }

    @Test
    public void testConvertStringToRowRange() {
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
