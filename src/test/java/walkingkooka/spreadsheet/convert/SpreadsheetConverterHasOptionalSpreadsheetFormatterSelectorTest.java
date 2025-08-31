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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public final class SpreadsheetConverterHasOptionalSpreadsheetFormatterSelectorTest extends SpreadsheetConverterTestCase<SpreadsheetConverterHasOptionalSpreadsheetFormatterSelector> {

    @Test
    public void testConvertThisToSpreadsheetFormatterSelector() {
        this.convertFails(
            this,
            SpreadsheetFormatterSelector.class
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetFormatterSelector() {
        final SpreadsheetFormatterSelector formatter = SpreadsheetFormatterSelector.parse("Hello");

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
    public SpreadsheetConverterHasOptionalSpreadsheetFormatterSelector createConverter() {
        return SpreadsheetConverterHasOptionalSpreadsheetFormatterSelector.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterHasOptionalSpreadsheetFormatterSelector> type() {
        return SpreadsheetConverterHasOptionalSpreadsheetFormatterSelector.class;
    }
}
