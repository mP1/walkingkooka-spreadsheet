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

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.form.Form;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps another {@link SpreadsheetExpressionEvaluationContext}, but holds a local {@link SpreadsheetCell} property.
 */
final class SpreadsheetExpressionEvaluationContextCell implements SpreadsheetExpressionEvaluationContextDelegator {

    static SpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                       final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        return cell.equals(context.cell()) ?
            context :
            new SpreadsheetExpressionEvaluationContextCell(
                cell,
                context
            );
    }

    private SpreadsheetExpressionEvaluationContextCell(final Optional<SpreadsheetCell> cell,
                                                       final SpreadsheetExpressionEvaluationContext context) {
        this.cell = cell;
        this.context = context;
    }

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return SpreadsheetExpressionEvaluationContextDelegator.super.stringEqualsCaseSensitivity();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        return with(
            cell,
            this.context
        );
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCell> cell;

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        return this.context.loadCell(cell);
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        return this.context.loadCellRange(range);
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.context.resolveLabel(labelName);
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        return this.context.reference(reference);
    }

    @Override
    public SpreadsheetFormatterContext spreadsheetFormatterContext(final Optional<SpreadsheetCell> cell) {
        return this.context.spreadsheetFormatterContext(cell);
    }

    // HasLineEnding....................................................................................................

    @Override
    public LineEnding lineEnding() {
        return this.context.lineEnding();
    }

    // SpreadsheetExpressionEvaluationContextDelegator..................................................................

    @Override
    public Form<SpreadsheetExpressionReference> form() {
        return this.context.form();
    }

    // SpreadsheetEnvironmentContext....................................................................................

    /**
     * All other {@link SpreadsheetExpressionEvaluationContext} methods are delegated to the wrapped.
     */
    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext() {
        return this.context;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext cloneEnvironment() {
        final SpreadsheetExpressionEvaluationContext context = this.context;
        final SpreadsheetExpressionEvaluationContext cloned = context.cloneEnvironment();

        // Recreate only if different cloned EnvironmentContext, cloned environment should be equals
        return context == cloned ?
            this :
            new SpreadsheetExpressionEvaluationContextCell(
                this.cell,
                Objects.requireNonNull(cloned, "environmentContext")
            );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setEnvironmentContext(final EnvironmentContext environmentContext) {
        final SpreadsheetExpressionEvaluationContext before = this.context;
        final SpreadsheetExpressionEvaluationContext after = before.setEnvironmentContext(environmentContext);

        return before == after ?
            this :
            new SpreadsheetExpressionEvaluationContextCell(
                this.cell,
                Objects.requireNonNull(after, "environmentContext")
            );
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.context.serverUrl();
    }

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.context.spreadsheetId();
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setSpreadsheetId(final SpreadsheetId spreadsheetId) {
        this.context.setSpreadsheetId(spreadsheetId);
        return this;
    }

    @Override
    public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
        return this.context;
    }

    // JsonNodeMarshallUnmarshallContext................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        final SpreadsheetExpressionEvaluationContext before = this.context;
        final SpreadsheetExpressionEvaluationContext after = before.setObjectPostProcessor(processor);

        return before.equals(after) ?
            this :
            new SpreadsheetExpressionEvaluationContextCell(
                this.cell,
                after
            );
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final SpreadsheetExpressionEvaluationContext before = this.context;
        final SpreadsheetExpressionEvaluationContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
            this :
            new SpreadsheetExpressionEvaluationContextCell(
                this.cell,
                after
            );
    }

    // 2025-05-25T06:25:48.9704295Z [DEBUG]         Originally at:
    // 2025-05-25T06:25:48.9706780Z [DEBUG]         /home/runner/work/walkingkooka-spreadsheet-expression-function/walkingkooka-spreadsheet-expression-function/target/it-tests/j2cl-test/target/walkingkooka-j2cl-maven-plugin-cache/walkingkooka--walkingkooka.spreadsheet.expression.function.ittest--jar--1.0-382f02da09e5aff2ae595ff39e411f70680e6c57/7-closure-compile/sources/walkingkooka/spreadsheet/expression/SpreadsheetExpressionEvaluationContextCell.java:36: ERROR - [JSC_DUPLICATE_CLASS_METHODS] Class contains duplicate method name "m_validatorContext__walkingkooka_spreadsheet_reference_SpreadsheetExpressionReference"
    // 2025-05-25T06:25:48.9709413Z [DEBUG]           36| final class SpreadsheetExpressionEvaluationContextCell implements SpreadsheetExpressionEvaluationContextDelegator {
    // 2025-05-25T06:25:48.9710024Z [DEBUG]                           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    // 2025-05-25T06:25:48.9710315Z [DEBUG]         1 error(s), 0 warning(s)
    // 2025-05-25T06:25:48.9710546Z [INFO]               Closure compiler
    // 2025-05-25T06:25:48.9710772Z [INFO]                 Exit code
    @Override
    public SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        return this.context.validatorContext(reference);
    }

    // EnvironmentContext...............................................................................................

    private final SpreadsheetExpressionEvaluationContext context;

    @Override
    public String toString() {
        return this.cell.toString();
    }
}
