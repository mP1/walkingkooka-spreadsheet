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

package walkingkooka.spreadsheet.reference;

import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;

import java.math.MathContext;

/**
 * A {@link SpreadsheetParserContext} that only implements {@link #mathContext()}, all other properties/methods
 * should never be called by {@link SpreadsheetCellReference} or {@link SpreadsheetColumnOrRowReference} parse method.
 */
final class SpreadsheetReferenceSpreadsheetParserContext extends FakeSpreadsheetParserContext {

    final static SpreadsheetReferenceSpreadsheetParserContext INSTANCE = new SpreadsheetReferenceSpreadsheetParserContext();

    private SpreadsheetReferenceSpreadsheetParserContext() {
        super();
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }
}
