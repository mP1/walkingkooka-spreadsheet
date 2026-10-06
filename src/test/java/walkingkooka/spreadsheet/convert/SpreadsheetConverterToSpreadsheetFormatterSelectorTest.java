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
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public final class SpreadsheetConverterToSpreadsheetFormatterSelectorTest extends SpreadsheetConverterTestCase<SpreadsheetConverterToSpreadsheetFormatterSelector> {

    @Test
    public void testConvertThisToSpreadsheetFormatterSelector() {
        this.convertFails(
            this,
            SpreadsheetFormatterSelector.class
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetFormatterSelector() {
        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.parse("hello");

        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setFormatter(
                    Optional.of(formatter)
                ),
            SpreadsheetFormatterSelector.class,
            formatter
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetFormatterSelectorEmptySpreadsheetFormatterSelector() {
        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            SpreadsheetFormatterSelector.class,
            null
        );
    }

    @Override
    public SpreadsheetConverterToSpreadsheetFormatterSelector createConverter() {
        return SpreadsheetConverterToSpreadsheetFormatterSelector.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createConverter(),
            "to SpreadsheetFormatterSelector"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterToSpreadsheetFormatterSelector> type() {
        return SpreadsheetConverterToSpreadsheetFormatterSelector.class;
    }
}
