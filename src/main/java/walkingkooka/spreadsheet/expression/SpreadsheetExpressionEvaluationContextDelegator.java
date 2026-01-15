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

import walkingkooka.convert.ConverterContext;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContextDelegator;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.ExpressionEvaluationContextDelegator;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.validation.expression.ValidatorExpressionEvaluationContextDelegator;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.expression.FormHandlerExpressionEvaluationContextDelegator;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Delegates all {@link ExpressionEvaluationContextDelegator} and most {@link SpreadsheetExpressionEvaluationContext}
 * except for cell getter and loaders:
 * <ul>
 *     <li>{@link #cell()}</li>
 *     <li>{@link #loadCell(SpreadsheetCellReference)}</li>
 *     <li>{@link #loadCellRange(SpreadsheetCellRangeReference)}</li>
 * </ul>
 */
public interface SpreadsheetExpressionEvaluationContextDelegator extends SpreadsheetExpressionEvaluationContext,
    SpreadsheetConverterContextDelegator,
    FormHandlerExpressionEvaluationContextDelegator<SpreadsheetExpressionReference, SpreadsheetDelta>,
    ValidatorExpressionEvaluationContextDelegator<SpreadsheetExpressionReference>,
    SpreadsheetEnvironmentContextDelegator,
    StorageExpressionEvaluationContextDelegator,
    TerminalContextDelegator {

    @Override
    default LocaleContext localeContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    @Override
    default CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetExpressionEvaluationContext.super.stringEqualsCaseSensitivity();
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    default Locale locale() {
        return this.spreadsheetExpressionEvaluationContext()
            .locale();
    }

    @Override
    default SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    @Override
    default ConverterContext converterContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    @Override
    default ExpressionNumberKind expressionNumberKind() {
        return spreadsheetExpressionEvaluationContext()
            .expressionNumberKind();
    }

    @Override
    default boolean isText(final Object value) {
        return SpreadsheetExpressionEvaluationContext.super.isText(value);
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    default <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        return this.spreadsheetExpressionEvaluationContext()
            .environmentValue(name);
    }

    @Override
    default <T> SpreadsheetExpressionEvaluationContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                                           final T value) {
        this.spreadsheetExpressionEvaluationContext()
            .setEnvironmentValue(
                name,
                value
            );
        return this;
    }

    @Override
    default SpreadsheetExpressionEvaluationContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.spreadsheetExpressionEvaluationContext()
            .removeEnvironmentValue(name);
        return this;
    }

    @Override
    default SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.storageExpressionEvaluationContext()
            .setLineEnding(lineEnding);
        return this;
    }

    @Override
    default void setLocale(final Locale locale) {
        this.spreadsheetExpressionEvaluationContext()
            .setLocale(locale);
    }

    @Override
    default AbsoluteUrl serverUrl() {
        return this.spreadsheetExpressionEvaluationContext()
            .serverUrl();
    }

    @Override
    default SpreadsheetId spreadsheetId() {
        return this.spreadsheetExpressionEvaluationContext()
            .spreadsheetId();
    }

    @Override
    default SpreadsheetExpressionEvaluationContext setSpreadsheetId(final SpreadsheetId id) {
        this.spreadsheetExpressionEvaluationContext()
            .setSpreadsheetId(id);
        return this;
    }

    @Override
    default Optional<EmailAddress> user() {
        return this.spreadsheetExpressionEvaluationContext()
            .user();
    }

    @Override
    default SpreadsheetExpressionEvaluationContext setUser(final Optional<EmailAddress> user) {
        this.spreadsheetExpressionEvaluationContext()
            .setUser(user);
        return this;
    }

    @Override
    default SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    // SpreadsheetExpressionEvaluationContextDelegator..................................................................

    @Override
    default EnvironmentContext environmentContext() {
        return this.spreadsheetEnvironmentContext();
    }

    @Override
    default SpreadsheetExpressionEvaluationContext expressionEvaluationContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext();

    // StorageExpressionEvaluationContextDelegator......................................................................

    @Override
    default StorageExpressionEvaluationContext storageExpressionEvaluationContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    // TerminalContextDelegator.........................................................................................

    @Override
    default SpreadsheetExpressionEvaluationContext exitTerminal() {
        return this.spreadsheetExpressionEvaluationContext()
            .exitTerminal();
    }

    @Override
    default TerminalContext terminalContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    // SpreadsheetExpressionEvaluationContext...........................................................................

    @Override
    default Object evaluate(final String expression) {
        return SpreadsheetExpressionEvaluationContext.super.evaluate(expression);
    }

    @Override
    default SpreadsheetFormulaParserToken parseExpression(final TextCursor formula) {
        return this.spreadsheetExpressionEvaluationContext()
            .parseExpression(formula);
    }

    @Override
    default SpreadsheetFormulaParserToken parseValueOrExpression(final TextCursor formula) {
        return this.spreadsheetExpressionEvaluationContext()
            .parseValueOrExpression(formula);
    }

    @Override
    default Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        return this.spreadsheetExpressionEvaluationContext()
            .loadCell(cell);
    }

    @Override
    default Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        return this.spreadsheetExpressionEvaluationContext()
            .loadCellRange(range);
    }

    @Override
    default Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetExpressionEvaluationContext()
            .loadLabel(labelName);
    }

    @Override
    default SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        return this.spreadsheetExpressionEvaluationContext()
            .setCell(cell);
    }

    @Override
    default Optional<SpreadsheetCell> cell() {
        return this.spreadsheetExpressionEvaluationContext()
            .cell();
    }

    @Override
    default void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        this.spreadsheetExpressionEvaluationContext()
            .setSpreadsheetMetadata(metadata);
    }

    // FormHandlerExpressionEvaluationContextDelegator..................................................................

    @Override
    default Form<SpreadsheetExpressionReference> form() {
        return this.spreadsheetExpressionEvaluationContext()
            .form();
    }

    @Override
    default Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        return this.spreadsheetExpressionEvaluationContext()
            .nextEmptyColumn(row);
    }

    @Override
    default Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        return this.spreadsheetExpressionEvaluationContext()
            .nextEmptyRow(column);
    }

    @Override
    default Optional<Optional<Object>> reference(final ExpressionReference reference) {
        return this.spreadsheetExpressionEvaluationContext()
            .reference(reference);
    }

    @Override
    default SpreadsheetExpressionReference validationReference() {
        return this.spreadsheetExpressionEvaluationContext()
            .validationReference();
    }

    @Override
    default SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return this.spreadsheetExpressionEvaluationContext()
            .validatorContext(reference);
    }
}
