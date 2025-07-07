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

package walkingkooka.spreadsheet.formula;

import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Function;

final class SpreadsheetFormulaReplaceReferencesFunctionParserToken extends SpreadsheetFormulaReplaceReferencesFunction<ParserToken> {

    static SpreadsheetFormulaReplaceReferencesFunctionParserToken with(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        return new SpreadsheetFormulaReplaceReferencesFunctionParserToken(mapper);
    }

    private SpreadsheetFormulaReplaceReferencesFunctionParserToken(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        super(mapper);
    }

    @Override
    public ParserToken apply(final ParserToken token) {
        return this.mapper.apply(
                token.cast(CellSpreadsheetFormulaParserToken.class).cell()
            ).map(t -> (ParserToken) t.toParserToken())
            .orElse(token);
    }
}
