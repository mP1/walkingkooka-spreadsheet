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

package walkingkooka.spreadsheet.engine;

import walkingkooka.Context;
import walkingkooka.datetime.HasNow;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionPurityContext;

import java.util.Optional;

/**
 * Context that accompanies a value format, holding local sensitive attributes such as the decimal point character.
 */
public interface SpreadsheetEngineContext extends Context, ExpressionPurityContext, HasNow {

    /**
     * Returns the current {@link SpreadsheetMetadata}
     */
    SpreadsheetMetadata metadata();

    /**
     * Resolves a {@link SpreadsheetSelection} if it is a {@link SpreadsheetLabelName} otherwise returning the original.
     */
    SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection);

    /**
     * Parses the formula into an {@link SpreadsheetParserToken} which can then be transformed into an {@link Expression}.
     */
    SpreadsheetParserToken parseFormula(final TextCursor formula);

    /**
     * Evaluates the expression into a value.
     */
    Object evaluate(final Expression node, final Optional<SpreadsheetCell> cell);

    /**
     * Accepts a pattern and returns the equivalent {@link SpreadsheetFormatter}.
     */
    SpreadsheetFormatter parseFormatPattern(final String pattern);

    /**
     * Formats the given value using the provided formatter.
     */
    Optional<SpreadsheetText> format(final Object value,
                                     final SpreadsheetFormatter formatter);

    /**
     * Getter that returns the {@link SpreadsheetStoreRepository} for this spreadsheet.
     */
    SpreadsheetStoreRepository storeRepository();
}
