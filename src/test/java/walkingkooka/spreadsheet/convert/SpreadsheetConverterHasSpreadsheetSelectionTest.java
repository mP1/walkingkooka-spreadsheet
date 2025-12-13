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
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;

public final class SpreadsheetConverterHasSpreadsheetSelectionTest extends SpreadsheetConverterTestCase<SpreadsheetConverterHasSpreadsheetSelection> {

    @Test
    public void testConvertThisToSpreadsheetFormatterSelector() {
        this.convertFails(
            this,
            SpreadsheetSelection.class
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetSelection() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        this.convertAndCheck(
            cell,
            SpreadsheetSelection.class,
            cell.reference()
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetCellReference() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        this.convertAndCheck(
            cell,
            cell.reference()
        );
    }

    @Override
    public SpreadsheetConverterHasSpreadsheetSelection createConverter() {
        return SpreadsheetConverterHasSpreadsheetSelection.INSTANCE;
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
                                                 final Class<T> type) {
                return this.converter.convert(
                    value,
                    type,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.spreadsheetSelectionToSpreadsheetSelection();
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterHasSpreadsheetSelection> type() {
        return SpreadsheetConverterHasSpreadsheetSelection.class;
    }
}
