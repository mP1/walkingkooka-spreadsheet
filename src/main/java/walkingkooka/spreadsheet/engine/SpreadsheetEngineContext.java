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
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionPurityContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Context that accompanies a value format, holding local sensitive attributes such as the decimal point character.
 */
public interface SpreadsheetEngineContext extends Context,
        ExpressionPurityContext,
        HasSpreadsheetMetadata,
        HasNow {

    /**
     * Resolves a {@link SpreadsheetSelection} if it is a {@link SpreadsheetLabelName} otherwise returning the original.
     */
    SpreadsheetSelection resolveIfLabel(final SpreadsheetSelection selection);

    /**
     * Parses the formula into an {@link SpreadsheetParserToken} which can then be transformed into an {@link Expression}.
     */
    SpreadsheetParserToken parseFormula(final TextCursor formula);

    /**
     * Helps by converting the given {@link SpreadsheetParserToken} into its {@link Expression} equivalent.
     */
    Optional<Expression> toExpression(final SpreadsheetParserToken token);

    /**
     * Evaluates the expression into a value.
     * The cell parameter is optional because not all {@link Expression expressions} that may need evaluation belong to a cell.
     * If a {@link RuntimeException} is thrown while evaluating the {@link Expression} it will be translated into a {@link walkingkooka.spreadsheet.SpreadsheetError}.
     */
    Object evaluate(final Expression node,
                    final Optional<SpreadsheetCell> cell);

    /**
     * Helper that converts the result of the {@link Expression} evaluation into a {@link Boolean} value.
     */
    default boolean evaluateAsBoolean(final Expression node,
                                      final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetMetadata()
                .converterContext(
                        this::now,
                        this::resolveIfLabel
                ).convertOrFail(
                        this.evaluate(
                                node,
                                cell
                        ),
                        Boolean.class
                );
    }

    /**
     * Formats the given value using the provided formatter.
     */
    Optional<SpreadsheetText> formatValue(final Object value,
                                          final SpreadsheetFormatter formatter);

    /**
     * Combines formatting of any present value along with possibly applying conditional rules.
     */
    SpreadsheetCell formatAndStyle(final SpreadsheetCell cell,
                                   final Optional<SpreadsheetFormatter> formatter);

    /**
     * Formats the {@link Throwable} into text and styles it as an error.
     */
    default SpreadsheetCell formatThrowableAndStyle(final Throwable cause,
                                                    final SpreadsheetCell cell) {
        Objects.requireNonNull(cause, "cause");
        Objects.requireNonNull(cell, "cell");

        return this.formatAndStyle(
                cell.setFormula(
                        cell.formula()
                                .setValue(
                                        Optional.of(
                                                SpreadsheetErrorKind.translate(cause)
                                                        .replaceWithValueIfPossible(this)
                                        )
                                )
                ),
                Optional.empty() // ignore cell formatter
        );
    }

    /**
     * Getter that returns the {@link SpreadsheetStoreRepository} for this spreadsheet.
     */
    SpreadsheetStoreRepository storeRepository();
}
