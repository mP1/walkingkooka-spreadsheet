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

package walkingkooka.spreadsheet.expression;

import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterSelector;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.HasSpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.terminal.expression.TerminalExpressionEvaluationContext;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionAliasSet;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.tree.text.TextNode;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.ValidationReference;
import walkingkooka.validation.Validator;
import walkingkooka.validation.expression.ValidatorExpressionEvaluationContext;
import walkingkooka.validation.form.expression.FormHandlerExpressionEvaluationContext;

import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * An enhanced {@link ExpressionEvaluationContext} adding a few extra methods required by a spreadsheet during
 * expression execution.
 * <br>
 * This context can be used when executing functions each with different goals.
 * <ul>
 * <li>A formula which produces a value</li>
 * <li>A {@link Converter} trying to convert values</li>
 * <li>A {@link SpreadsheetFormatter} which produces {@link TextNode}</li>
 * <li>A {@link Validator} which may return zero or more {@link ValidationError}</li>
 * </ul>
 */
public interface SpreadsheetExpressionEvaluationContext extends StorageExpressionEvaluationContext,
    SpreadsheetConverterContext,
    HasSpreadsheetCell,
    HasSpreadsheetMetadata,
    FormHandlerExpressionEvaluationContext<SpreadsheetExpressionReference, SpreadsheetDelta>,
    SpreadsheetEnvironmentContext,
    TerminalExpressionEvaluationContext,
    ValidatorExpressionEvaluationContext<SpreadsheetExpressionReference> {

    EnvironmentValueName<ConverterSelector> CONVERTER = EnvironmentValueName.with("converter");

    EnvironmentValueName<SpreadsheetParserSelector> DATE_PARSER = SpreadsheetMetadataPropertyName.DATE_PARSER.toEnvironmentValueName();

    EnvironmentValueName<Long> DATE_TIME_OFFSET = SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET.toEnvironmentValueName();

    EnvironmentValueName<SpreadsheetParserSelector> DATE_TIME_PARSER = SpreadsheetMetadataPropertyName.DATE_TIME_PARSER.toEnvironmentValueName();

    EnvironmentValueName<DateTimeSymbols> DATE_TIME_SYMBOLS = SpreadsheetMetadataPropertyName.DATE_TIME_SYMBOLS.toEnvironmentValueName();

    EnvironmentValueName<Integer> DECIMAL_NUMBER_DIGIT_COUNT = SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_DIGIT_COUNT.toEnvironmentValueName();

    EnvironmentValueName<DecimalNumberSymbols> DECIMAL_NUMBER_SYMBOLS = SpreadsheetMetadataPropertyName.DECIMAL_NUMBER_SYMBOLS.toEnvironmentValueName();

    EnvironmentValueName<Integer> DEFAULT_YEAR = SpreadsheetMetadataPropertyName.DEFAULT_YEAR.toEnvironmentValueName();

    EnvironmentValueName<ExpressionNumberKind> EXPRESSION_NUMBER_KIND = SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND.toEnvironmentValueName();

    EnvironmentValueName<ExpressionFunctionAliasSet> FUNCTIONS = SpreadsheetMetadataPropertyName.FUNCTIONS.toEnvironmentValueName();

    EnvironmentValueName<SpreadsheetParserSelector> NUMBER_PARSER = SpreadsheetMetadataPropertyName.NUMBER_PARSER.toEnvironmentValueName();

    EnvironmentValueName<Integer> PRECISION = SpreadsheetMetadataPropertyName.PRECISION.toEnvironmentValueName();

    EnvironmentValueName<RoundingMode> ROUNDING_MODE = SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName();

    EnvironmentValueName<SpreadsheetParserSelector> TIME_PARSER = SpreadsheetMetadataPropertyName.TIME_PARSER.toEnvironmentValueName();

    EnvironmentValueName<Integer> TWO_DIGIT_YEAR = SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR.toEnvironmentValueName();

    EnvironmentValueName<Character> VALUE_SEPARATOR = SpreadsheetMetadataPropertyName.VALUE_SEPARATOR.toEnvironmentValueName();

    /**
     * Tests if the given {@link EnvironmentValueName} is one of the above constants.
     */
    static boolean isSpreadsheetEnvironmentContextEnvironmentValueName(final EnvironmentValueName<?> name) {
        return null != name &&
            (
                CONVERTER.equals(name) ||
                    DATE_PARSER.equals(name) ||
                    DATE_TIME_OFFSET.equals(name) ||
                    DATE_TIME_PARSER.equals(name) ||
                    DATE_TIME_SYMBOLS.equals(name) ||
                    DECIMAL_NUMBER_DIGIT_COUNT.equals(name) ||
                    DECIMAL_NUMBER_SYMBOLS.equals(name) ||
                    DEFAULT_YEAR.equals(name) ||
                    EXPRESSION_NUMBER_KIND.equals(name) ||
                    FUNCTIONS.equals(name) ||
                    LOCALE.equals(name) ||
                    NUMBER_PARSER.equals(name) ||
                    PRECISION.equals(name) ||
                    ROUNDING_MODE.equals(name) ||
                    TIME_PARSER.equals(name) ||
                    TWO_DIGIT_YEAR.equals(name) ||
                    VALUE_SEPARATOR.equals(name)
            );
    }

    @Override
    default CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetStrings.CASE_SENSITIVITY;
    }

    @Override
    default boolean isText(final Object value) {
        return SpreadsheetStrings.isText(value);
    }

    /**
     * Helper that makes it easy to add a variable with a value. This is especially useful when executing a {@link Expression}
     * with a parameter such as a Validator.
     */
    default SpreadsheetExpressionEvaluationContext addLocalVariable(final ExpressionReference reference,
                                                                    final Optional<Object> value) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(value, "value");

        return this.enterScope(
            (final ExpressionReference expressionReference) -> Optional.ofNullable(
                expressionReference.equals(reference) ?
                    value :
                    null
            )
        );
    }

    @Override
    default SpreadsheetExpressionEvaluationContext enterScope(final Function<ExpressionReference, Optional<Optional<Object>>> scoped) {
        return SpreadsheetExpressionEvaluationContexts.localReferences(
            scoped,
            this
        );
    }

    /**
     * Returns a {@link SpreadsheetExpressionEvaluationContext} with the given {@link SpreadsheetCell} as the current cell.
     */
    SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell);

    /**
     * Saves or replaces the current {@link SpreadsheetMetadata} with a new copy.
     * This is necessary to support a function that allows updating/replacing a {@link SpreadsheetMetadata}.
     */
    void setSpreadsheetMetadata(final SpreadsheetMetadata metadata);

    /**
     * If the {@link ExpressionReference} cannot be found an {@link SpreadsheetError} is created with {@link SpreadsheetError#referenceNotFound(ExpressionReference)}.
     */
    @Override
    default Object referenceOrFail(final ExpressionReference reference) {
        Object result;
        try {
            result = this.reference(reference)
                .orElseGet(
                    () -> Optional.of(
                        SpreadsheetError.referenceNotFound(reference)
                    )
                ).orElse(null);
        } catch (final RuntimeException exception) {
            result = this.handleException(exception);
        }

        return result;
    }

    /**
     * Loads the cell for the given {@link SpreadsheetCellReference}, note that the formula is not evaluated.
     */
    Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell);

    /**
     * Attempts to load the given {@link SpreadsheetCellReference} throwing a {@link SpreadsheetError#selectionNotFound(SpreadsheetExpressionReference)}
     * if it is missing.
     */
    default SpreadsheetCell loadCellOrFail(final SpreadsheetCellReference cell) {
        return this.loadCell(cell)
            .orElseThrow(
                () -> SpreadsheetError.selectionNotFound(cell)
                    .exception()
            );
    }

    /**
     * Loads all the cells present in the given {@link SpreadsheetCellRangeReference}.
     */
    Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range);

    /**
     * Loads the {@link SpreadsheetLabelMapping} for the given {@link SpreadsheetLabelName}.
     */
    Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName);

    /**
     * Attempts to load the given {@link SpreadsheetLabelMapping} throwing a {@link SpreadsheetError#selectionNotFound(SpreadsheetExpressionReference)}
     * if it is missing.
     */
    default SpreadsheetLabelMapping loadLabelOrFail(final SpreadsheetLabelName labelName) {
        return this.loadLabel(labelName)
            .orElseThrow(
                () -> SpreadsheetError.selectionNotFound(labelName).exception()
            );
    }

    /**
     * Parses the given text into an {@link Expression} and then evaluates the {@link Expression} into a value.
     * Thrown {@link Expression} should be converted into a {@link SpreadsheetError}.
     */
    @Override
    default Object evaluate(final String text) {
        Objects.requireNonNull(text, "text");

        Object value;
        try {
            final SpreadsheetFormulaParserToken token = this.parseValueOrExpression(
                TextCursors.charSequence(text)
            );

            value = this.evaluateExpression(
                token.toExpressionOrFail(this)
            );
        } catch (final UnsupportedOperationException rethrow) {
            throw rethrow;
        } catch (final RuntimeException exception) {
            value = this.handleException(exception);
        }

        return value;
    }

    /**
     * Parses the {@link TextCursor formula expression} into an {@link SpreadsheetFormulaParserToken} which can then be transformed into an {@link Expression}.
     * Note a formula here is an expression without the leading equals sign. Value literals such as date like 1/2/2000 will actually probably
     * be parsed into a series of division operations and not an actual date. Apostrophe string literals will fail,
     * date/times and times will not actually return date/time or time values.
     */
    SpreadsheetFormulaParserToken parseExpression(final TextCursor formula);

    /**
     * Parses the given {@link TextCursor} which may contain value literals or expression preceeded by equals sign.
     */
    SpreadsheetFormulaParserToken parseValueOrExpression(final TextCursor formula);

    // formatting.......................................................................................................

    /**
     * Within a {@link SpreadsheetExpressionEvaluationContext} contains the name of value being formatted.
     * This may be passed to {@link SpreadsheetExpressionEvaluationContext#reference(ExpressionReference)}.
     */
    SpreadsheetLabelName FORMAT_VALUE = SpreadsheetSelection.labelName("VALUE");

    /**
     * Getter that retrieves the value being formatted by a {@link walkingkooka.spreadsheet.format.SpreadsheetFormatter}
     */
    default Optional<Object> formatValue() {
        return this.reference(FORMAT_VALUE)
            .orElse(Optional.empty());
    }

    /**
     * Creates a {@link SpreadsheetFormatterContext} which can be used by a function to format a value.
     */
    SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell);

    // EnvironmentContext...............................................................................................

    @Override
    SpreadsheetExpressionEvaluationContext cloneEnvironment();

    @Override
    SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext);

    @Override
    <T> SpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                   final T value);

    @Override
    SpreadsheetExpressionEvaluationContext removeEnvironmentValue(final EnvironmentValueName<?> name);

    @Override
    SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding);

    @Override
    SpreadsheetExpressionEvaluationContext setLocale(final Locale locale);

    @Override
    SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user);

    // validation.......................................................................................................

    /**
     * A label that may be used to get the value being validated.
     */
    SpreadsheetLabelName VALIDATION_VALUE = SpreadsheetValidatorContext.VALUE;

    /**
     * Returns the next empty column for the requested {@link SpreadsheetRowReference}.
     */
    Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row);

    /**
     * Returns the next empty {@link SpreadsheetRowReference} for the requested {@link SpreadsheetColumnReference}.
     */
    Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column);

    /**
     * {@link walkingkooka.validation.form.FormHandlerContext#validatorContext(ValidationReference)}.
     */
    @Override
    SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference);

    // JsonNodeConverterContext.........................................................................................

    @Override
    SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor);

    @Override
    SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor);

    // TerminalContext..................................................................................................

    @Override
    SpreadsheetExpressionEvaluationContext exitTerminal();
}
