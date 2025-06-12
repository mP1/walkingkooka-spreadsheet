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

package walkingkooka.spreadsheet.template;

import walkingkooka.Either;
import walkingkooka.InvalidCharacterException;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContextDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.storage.StorageStore;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContextDelegator;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link SpreadsheetTemplateContext} that sources values and functionality from {@link SpreadsheetParserContext},
 * {@link SpreadsheetExpressionEvaluationContext} and template values from a {@link Function}.
 */
final class BasicSpreadsheetTemplateContext implements SpreadsheetTemplateContext,
        SpreadsheetParserContextDelegator,
        EnvironmentContextDelegator,
        JsonNodeMarshallUnmarshallContextDelegator {

    static BasicSpreadsheetTemplateContext with(final SpreadsheetParserContext spreadsheetParserContext,
                                                final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext,
                                                final Function<TemplateValueName, Expression> templateValueNameToExpression,
                                                final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext) {
        return new BasicSpreadsheetTemplateContext(
                Objects.requireNonNull(spreadsheetParserContext, "spreadsheetParserContext"),
                Objects.requireNonNull(spreadsheetExpressionEvaluationContext, "spreadsheetExpressionEvaluationContext"),
                Objects.requireNonNull(templateValueNameToExpression, "templateValueNameToExpression"),
                Objects.requireNonNull(jsonNodeMarshallUnmarshallContext, "jsonNodeMarshallUnmarshallContext")
        );
    }

    private BasicSpreadsheetTemplateContext(final SpreadsheetParserContext spreadsheetParserContext,
                                            final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext,
                                            final Function<TemplateValueName, Expression> templateValueNameToExpression,
                                            final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext) {
        this.spreadsheetParserContext = spreadsheetParserContext;
        this.spreadsheetExpressionEvaluationContext = spreadsheetExpressionEvaluationContext.enterScope(
                this::reference
        );
        this.templateValueNameToExpression = templateValueNameToExpression;
        this.jsonNodeMarshallUnmarshallContext = jsonNodeMarshallUnmarshallContext;
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.spreadsheetExpressionEvaluationContext.cell();
    }

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        return this.spreadsheetExpressionEvaluationContext.loadCell(cell);
    }

    @Override
    public Set<SpreadsheetCell> loadCellRange(final SpreadsheetCellRangeReference range) {
        return this.spreadsheetExpressionEvaluationContext.loadCellRange(range);
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetExpressionEvaluationContext.loadLabel(labelName);
    }

    @Override
    public MathContext mathContext() {
        return this.spreadsheetExpressionEvaluationContext.mathContext();
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetExpressionEvaluationContext.resolveLabel(labelName);
    }

    @Override
    public Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        return SpreadsheetTemplateContext.super.nextEmptyColumn(row);
    }

    @Override
    public Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        return SpreadsheetTemplateContext.super.nextEmptyRow(column);
    }

    // SpreadsheetParserContext.........................................................................................

    @Override
    public InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                               final TextCursor cursor) {
        return this.spreadsheetParserContext()
                .invalidCharacterException(
                        parser,
                        cursor
                );
    }

    @Override
    public SpreadsheetParserContext spreadsheetParserContext() {
        return this.spreadsheetParserContext;
    }

    private final SpreadsheetParserContext spreadsheetParserContext;

    // EnvironmentContext...............................................................................................

    @Override
    public EnvironmentContext environmentContext() {
        return this.spreadsheetExpressionEvaluationContext;
    }

    @Override
    public LocalDateTime now() {
        return this.spreadsheetExpressionEvaluationContext.now();
    }

    // ExpressionEvaluationContext......................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.spreadsheetExpressionEvaluationContext.canConvert(
                value,
                type
        );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.spreadsheetExpressionEvaluationContext.convert(
                value,
                type
        );
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetExpressionEvaluationContext.converter();
    }

    @Override
    public DateTimeContext dateTimeContext() {
        return this.spreadsheetParserContext;
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return this.spreadsheetParserContext;
    }

    @Override
    public long dateOffset() {
        return this.spreadsheetExpressionEvaluationContext.dateOffset();
    }

    @Override
    public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name) {
        return this.spreadsheetExpressionEvaluationContext.expressionFunction(name);
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetParserContext.expressionNumberKind();
    }

    @Override
    public <T> T prepareParameter(final ExpressionFunctionParameter<T> parameter,
                                  final Object value) {
        return this.spreadsheetExpressionEvaluationContext.prepareParameter(
                parameter,
                value
        );
    }

    @Override
    public Object handleException(final RuntimeException caught) {
        return this.spreadsheetExpressionEvaluationContext.handleException(caught);
    }

    @Override
    public boolean isPure(final ExpressionFunctionName name) {
        return this.spreadsheetExpressionEvaluationContext.isPure(name);
    }

    @Override
    public Locale locale() {
        return this.spreadsheetParserContext.locale();
    }

    @Override
    public SpreadsheetFormulaParserToken parseFormula(final TextCursor formula) {
        return this.spreadsheetExpressionEvaluationContext.parseFormula(formula);
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        Optional<Optional<Object>> value;

        // TemplateValueName gets passed into a SpreadsheetLabelName by SpreadsheetFormulaParsers#expression
        if (reference instanceof TemplateValueName) {
            final TemplateValueName templateValueName = (TemplateValueName) reference;

            final Expression expression = this.templateValueNameToExpression.apply(templateValueName);
            if (null == expression) {
                throw new IllegalStateException("Missing expression for " + templateValueName);
            }

            value = Optional.of(
                    Optional.ofNullable(
                            this.evaluateExpression(expression)
                    )
            );
        } else {
            value = Optional.empty(); // unknown references have no value
        }

        return value;
    }

    @Override
    public AbsoluteUrl serverUrl() {
        return this.spreadsheetExpressionEvaluationContext.serverUrl();
    }

    @Override
    public void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        this.spreadsheetExpressionEvaluationContext.setSpreadsheetMetadata(metadata);
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetExpressionEvaluationContext.spreadsheetMetadata();
    }

    @Override
    public StorageStore storage() {
        return this.spreadsheetExpressionEvaluationContext.storage();
    }

    @Override
    public CaseSensitivity stringEqualsCaseSensitivity() {
        return this.spreadsheetExpressionEvaluationContext.stringEqualsCaseSensitivity();
    }

    private final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext;

    // templateValue....................................................................................................

    @Override
    public String templateValue(final TemplateValueName name) {
        return this.convertOrFail(
                this.evaluateExpression(
                        this.templateValueNameToExpression.apply(name)
                ),
                String.class
        );
    }

    private final Function<TemplateValueName, Expression> templateValueNameToExpression;

    // JsonNodeMarshallUnmarshallContext................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        final JsonNodeMarshallUnmarshallContext before = this.jsonNodeMarshallUnmarshallContext;
        final JsonNodeMarshallUnmarshallContext after = before.setPreProcessor(processor);

        return before.equals(after) ?
                this :
                BasicSpreadsheetTemplateContext.with(
                        this.spreadsheetParserContext,
                        this.spreadsheetExpressionEvaluationContext,
                        this.templateValueNameToExpression,
                        after
                );
    }

    @Override
    public JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext() {
        return this.jsonNodeMarshallUnmarshallContext;
    }

    private final JsonNodeMarshallUnmarshallContext jsonNodeMarshallUnmarshallContext;

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetExpressionEvaluationContext +
                " " +
                this.spreadsheetParserContext +
                " " +
                this.templateValueNameToExpression +
                " " +
                this.jsonNodeMarshallUnmarshallContext;
    }
}
