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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContextDelegator;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContext;
import walkingkooka.storage.expression.function.StorageExpressionEvaluationContextDelegator;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContextDelegator;
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
    StorageExpressionEvaluationContextDelegator,
    TerminalContextDelegator {

    @Override
    default LocaleContext localeContext() {
        return this.spreadsheetConverterContext();
    }

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    default Locale locale() {
        return this.spreadsheetConverterContext()
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
        return SpreadsheetConverterContextDelegator.super.expressionNumberKind();
    }

    @Override
    default boolean isText(final Object value) {
        return SpreadsheetExpressionEvaluationContext.super.isText(value);
    }

    // HasLineEnding....................................................................................................

    @Override
    default SpreadsheetExpressionEvaluationContext setLineEnding(final LineEnding lineEnding) {
        this.storageExpressionEvaluationContext()
            .setLineEnding(lineEnding);
        return this;
    }

    // SpreadsheetExpressionEvaluationContextDelegator..................................................................

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
    default Set<EnvironmentValueName<?>> environmentValueNames() {
        return this.spreadsheetExpressionEvaluationContext()
            .environmentValueNames();
    }

    @Override
    default Optional<EmailAddress> user() {
        return this.spreadsheetExpressionEvaluationContext()
            .user();
    }

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
    default SpreadsheetFormulaParserToken parseFormula(final TextCursor formula) {
        return this.spreadsheetExpressionEvaluationContext()
            .parseFormula(formula);
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
    default AbsoluteUrl serverUrl() {
        return this.spreadsheetExpressionEvaluationContext()
            .serverUrl();
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
