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

import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContextDelegator;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetTemplateContext} that sources values and functionality from {@link SpreadsheetParserContext},
 * {@link SpreadsheetExpressionEvaluationContext} and template values from a {@link Function}.
 */
final class BasicSpreadsheetTemplateContext implements SpreadsheetTemplateContext,
        SpreadsheetParserContextDelegator,
        SpreadsheetExpressionEvaluationContextDelegator {

    static BasicSpreadsheetTemplateContext with(final SpreadsheetParserContext spreadsheetParserContext,
                                                final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext,
                                                final Function<TemplateValueName, Expression> templateValueNameToExpression) {
        return new BasicSpreadsheetTemplateContext(
                Objects.requireNonNull(spreadsheetParserContext, "spreadsheetParserContext"),
                Objects.requireNonNull(spreadsheetExpressionEvaluationContext, "spreadsheetExpressionEvaluationContext"),
                Objects.requireNonNull(templateValueNameToExpression, "templateValueNameToExpression")
        );
    }

    private BasicSpreadsheetTemplateContext(final SpreadsheetParserContext spreadsheetParserContext,
                                            final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext,
                                            final Function<TemplateValueName, Expression> templateValueNameToExpression) {
        this.spreadsheetParserContext = spreadsheetParserContext;
        this.spreadsheetExpressionEvaluationContext = spreadsheetExpressionEvaluationContext.enterScope(
                this::reference
        );
        this.templateValueNameToExpression = templateValueNameToExpression;
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return SpreadsheetTemplateContext.super.loadLabel(labelName);
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
    public SpreadsheetParserContext spreadsheetParserContext() {
        return this.spreadsheetParserContext;
    }

    private final SpreadsheetParserContext spreadsheetParserContext;

    // ExpressionEvaluationContext......................................................................................

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        Optional<Optional<Object>> value;

        // TemplateValueName gets passed into a SpreadsheetLabelName by SpreadsheetFormulaParsers#expression
        if (reference instanceof TemplateValueName) {
            final TemplateValueName templateValueName = (TemplateValueName) reference;

            final Expression expression = this.templateValueNameToExpression.apply(templateValueName);
            if(null == expression) {
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
    public void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        this.spreadsheetExpressionEvaluationContext.setSpreadsheetMetadata(metadata);
    }

    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext() {
        return this.spreadsheetExpressionEvaluationContext;
    }

    private final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext;

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.spreadsheetParserContext.expressionNumberKind();
    }

    @Override
    public Locale locale() {
        return this.spreadsheetParserContext.locale();
    }

    @Override
    public DateTimeContext dateTimeContext() {
        return this.spreadsheetParserContext;
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return this.spreadsheetParserContext;
    }

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

    // toString.........................................................................................................

    @Override
    public String toString() {
        return this.spreadsheetExpressionEvaluationContext +
                " " +
                this.spreadsheetParserContext +
                " " +
                this.templateValueNameToExpression;
    }
}
