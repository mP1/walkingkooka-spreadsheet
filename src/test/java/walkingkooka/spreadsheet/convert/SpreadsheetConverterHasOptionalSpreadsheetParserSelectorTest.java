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
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

public final class SpreadsheetConverterHasOptionalSpreadsheetParserSelectorTest extends SpreadsheetConverterTestCase<SpreadsheetConverterHasOptionalSpreadsheetParserSelector> {

    @Test
    public void testConvertThisToSpreadsheetParserSelector() {
        this.convertFails(
            this,
            SpreadsheetParserSelector.class
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetParserSelector() {
        final SpreadsheetParserSelector parser = SpreadsheetParserSelector.parse("Hello");

        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
                .setParser(
                    Optional.of(parser)
                ),
            SpreadsheetParserSelector.class,
            parser
        );
    }

    @Test
    public void testConvertSpreadsheetCellToSpreadsheetParserSelectorEmptySpreadsheetParserSelector() {
        this.convertAndCheck(
            SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY),
            SpreadsheetParserSelector.class,
            null
        );
    }

    @Override
    public SpreadsheetConverterHasOptionalSpreadsheetParserSelector createConverter() {
        return SpreadsheetConverterHasOptionalSpreadsheetParserSelector.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return SpreadsheetConverterContexts.fake();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterHasOptionalSpreadsheetParserSelector> type() {
        return SpreadsheetConverterHasOptionalSpreadsheetParserSelector.class;
    }
}
