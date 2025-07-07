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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

public final class SpreadsheetConverterSpreadsheetSelectionToTextTest extends SpreadsheetConverterTestCase<SpreadsheetConverterSpreadsheetSelectionToText> {

    @Test
    public void testConvertSpreadsheetCellToString() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCell("$A$1"),
            "$A$1"
        );
    }

    @Test
    public void testConvertSpreadsheetCellToCharacter() {
        this.convertAndCheck(
            SpreadsheetSelection.parseColumn("A"),
            'A'
        );
    }

    @Test
    public void testConvertSpreadsheetCellRangeToString() {
        this.convertAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            "A1:B2"
        );
    }

    @Test
    public void testConvertSpreadsheetLabelNameToString() {
        this.convertAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            "Label123"
        );
    }

    @Test
    public void testConvertStringToSpreadsheetCellFails() {
        this.convertFails(
            "A1",
            SpreadsheetCell.class
        );
    }

    @Override
    public SpreadsheetConverterSpreadsheetSelectionToText createConverter() {
        return SpreadsheetConverterSpreadsheetSelectionToText.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return this.converter.canConvert(
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
        };
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetConverterSpreadsheetSelectionToText.INSTANCE,
            "Selection to String"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterSpreadsheetSelectionToText> type() {
        return SpreadsheetConverterSpreadsheetSelectionToText.class;
    }
}
