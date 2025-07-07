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

import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.template.Template;
import walkingkooka.template.TemplateContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.template.Templates;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link TemplateContext} that uses {@link SpreadsheetFormulaParsers#expression()} to parse expressions within a template.
 * Expression {@link SpreadsheetLabelName} are then converted into {@link TemplateValueName} for value lookups.
 */
final class SpreadsheetTemplateContext implements TemplateContext {

    static SpreadsheetTemplateContext with(final SpreadsheetParserContext spreadsheetParserContext,
                                           final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext,
                                           final Function<TemplateValueName, Expression> templateValueNameToExpression) {
        return new SpreadsheetTemplateContext(
            Objects.requireNonNull(spreadsheetParserContext, "spreadsheetParserContext"),
            Objects.requireNonNull(spreadsheetExpressionEvaluationContext, "spreadsheetExpressionEvaluationContext"),
            Objects.requireNonNull(templateValueNameToExpression, "templateValueNameToExpression")
        );
    }

    private SpreadsheetTemplateContext(final SpreadsheetParserContext spreadsheetParserContext,
                                       final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext,
                                       final Function<TemplateValueName, Expression> templateValueNameToExpression) {
        this.spreadsheetParserContext = spreadsheetParserContext;

        this.spreadsheetExpressionEvaluationContext = spreadsheetExpressionEvaluationContext.enterScope(
            this::expressionReferenceToTemplateValue
        );

        this.templateValueNameToExpression = templateValueNameToExpression;
    }

    @Override
    public Template parseTemplate(final TextCursor text) {
        return this.parseTemplateWithBackslashEscaping(text);
    }

    /**
     * Uses the {@link SpreadsheetFormulaParsers#expression()} to parse the expression text into a {@link Expression}.
     */
    @Override
    public Template parseTemplateExpression(final TextCursor text) {
        Objects.requireNonNull(text, "text");

        final SpreadsheetFormula formula = SpreadsheetFormula.parse(
            TextCursors.charSequence(
                this.parseTemplateExpression0(text)
            ),
            EXPRESSION_PARSER,
            this.spreadsheetParserContext
        );

        formula.error()
            .ifPresent(e -> {
                    throw new IllegalArgumentException(e.message());
                }
            );

        return Templates.expression(
            formula.token()
                .orElseThrow(() -> new IllegalArgumentException("Failed to parse expression"))
                .toExpression(this.spreadsheetExpressionEvaluationContext)
                .orElseThrow(() -> new IllegalArgumentException("Failed to parse expression"))
        );
    }

    /**
     * Consumes the expression text, ready to be parsed by {@link SpreadsheetFormulaParsers#expression()}.
     */
    private CharSequence parseTemplateExpression0(final TextCursor text) {
        final TextCursorSavePoint save = text.save();

        while (true) {
            if (text.isEmpty()) {
                throw new IllegalArgumentException("Expression missing closing '}'");
            }

            if (DOUBLE_QUOTE.parse(
                text,
                this.spreadsheetParserContext
            ).isPresent()
            ) {
                continue;
            }

            final char c = text.at();
            if ('}' == c) {
                break;
            }

            text.next();
        }

        final CharSequence expression = save.textBetween();

        text.next();
        return expression;
    }

    private final static Parser<SpreadsheetParserContext> DOUBLE_QUOTE = Parsers.doubleQuoted();

    private final static Parser<SpreadsheetParserContext> EXPRESSION_PARSER = SpreadsheetFormulaParsers.templateExpression()
        .orFailIfCursorNotEmpty(
            ParserReporters.basic()
        ).cast();

    @Override
    public String evaluateAsString(final Expression expression) {
        Objects.requireNonNull(expression, "expression");

        final SpreadsheetExpressionEvaluationContext evaluationContext = this.spreadsheetExpressionEvaluationContext;

        Object value = evaluationContext.evaluateExpression(expression);

        boolean tryAgain;
        do {
            tryAgain = false;

            if (value instanceof Expression) {
                value = evaluationContext.evaluateExpression(
                    (Expression) value
                );
                tryAgain = true;
            }
            if (value instanceof ExpressionReference) {
                value = evaluationContext.referenceOrFail(
                    (ExpressionReference) value
                );
                tryAgain = true;
            }
        } while (tryAgain);

        return evaluationContext.convertOrFail(
            value,
            String.class
        );
    }

    private Optional<Optional<Object>> expressionReferenceToTemplateValue(final ExpressionReference reference) {
        final Optional<Optional<Object>> value;

        if (reference instanceof TemplateValueName) {
            value = Optional.of(
                Optional.of(
                    this.templateValue(
                        (TemplateValueName) reference
                    )
                )
            );
        } else {
            value = Optional.empty();
        }

        return value;
    }

    @Override
    public String templateValue(final TemplateValueName name) {
        return this.spreadsheetExpressionEvaluationContext.convertOrFail(
            this.spreadsheetExpressionEvaluationContext.evaluateExpression(
                this.templateValueNameToExpression.apply(name)
            ),
            String.class
        );
    }

    private final Function<TemplateValueName, Expression> templateValueNameToExpression;

    @Override
    public String toString() {
        return "spreadsheetParserContext: " + this.spreadsheetParserContext +
            ", spreadsheetExpressionEvaluationContext: " + this.spreadsheetExpressionEvaluationContext;
    }

    private final SpreadsheetParserContext spreadsheetParserContext;
    private final SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext;
}
