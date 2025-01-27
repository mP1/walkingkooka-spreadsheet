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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.expression.Expression;

import java.util.Optional;
import java.util.function.Function;

/**
 * Base class that acts an adapter between {@link ParserToken} or {@link Expression} and a {@link Function}
 * that handles {@link SpreadsheetCellReference}.
 */
abstract class SpreadsheetFormulaReplaceReferencesFunction<T> implements Function<T, T> {

    static Function<Expression, Expression> expression(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        return SpreadsheetFormulaReplaceReferencesFunctionExpression.with(mapper);
    }

    static Function<ParserToken, ParserToken> parserToken(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        return SpreadsheetFormulaReplaceReferencesFunctionParserToken.with(mapper);
    }

    SpreadsheetFormulaReplaceReferencesFunction(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        this.mapper = mapper;
    }

    final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper;

    @Override
    public final String toString() {
        return this.mapper.toString();
    }
}
