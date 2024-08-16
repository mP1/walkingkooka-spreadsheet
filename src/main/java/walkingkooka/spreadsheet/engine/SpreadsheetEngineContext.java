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
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.text.TextNode;

import java.util.Objects;
import java.util.Optional;

/**
 * A context that holds individual values tailored for an individual spreadsheet or user such as their locale,
 * formatting characters such as the selected decimal point and other similar data. The {@link SpreadsheetEngine} itself
 * will refer to this context for these values when it performs its spreadsheet operations such as evaluating a cell,
 * formatting etc. This supports the idea of using a single {@link SpreadsheetEngine} that is shared amongst users and
 * spreadsheets and only the {@link SpreadsheetEngineContext} is different, for circumstances such as those previously
 * mentioned.
 */
public interface SpreadsheetEngineContext extends Context,
        ExpressionPurityContext,
        HasSpreadsheetMetadata,
        HasNow,
        SpreadsheetProvider,
        ProviderContext,
        SpreadsheetLabelNameResolver {

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
                        SpreadsheetMetadataPropertyName.EXPRESSION_CONVERTER,
                        this, // ConverterProvider
                        this::now,
                        this::resolveIfLabel,
                        this // ProviderContext
                ).convertOrFail(
                        this.evaluate(
                                node,
                                cell
                        ),
                        Boolean.class
                );
    }

    // Formatting & SpreadsheetFormatterProvider........................................................................

    /**
     * Formats the given value using the provided formatter.
     */
    Optional<TextNode> formatValue(final Object value,
                                   final SpreadsheetFormatter formatter);

    /**
     * Combines formatting of any present value along with possibly applying conditional rules.
     */
    SpreadsheetCell formatValueAndStyle(final SpreadsheetCell cell,
                                        final Optional<SpreadsheetFormatter> formatter);

    /**
     * Formats the {@link Throwable} into text and styles it as an error.
     */
    default SpreadsheetCell formatThrowableAndStyle(final Throwable cause,
                                                    final SpreadsheetCell cell) {
        Objects.requireNonNull(cause, "cause");
        Objects.requireNonNull(cell, "cell");

        return this.formatValueAndStyle(
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
