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

package walkingkooka.spreadsheet.formula;

import walkingkooka.CanBeEmpty;
import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.Value;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.CanReplaceReferences;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.ReferenceExpression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.tree.json.patch.Patchable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A spreadsheet formula, including its compiled {@link Expression} and possibly its {@link Object value} or {@link SpreadsheetError}.
 */
public final class SpreadsheetFormula implements CanBeEmpty,
        CanReplaceReferences<SpreadsheetFormula>,
        HasText,
        Patchable<SpreadsheetFormula>,
        TreePrintable,
        UsesToStringBuilder,
        HasUrlFragment,
        Value<Optional<Object>> {

    /**
     * No {@link SpreadsheetFormulaParserToken} constant.
     */
    public final static Optional<SpreadsheetFormulaParserToken> NO_TOKEN = Optional.empty();

    /**
     * No expression constant.
     */
    public final static Optional<Expression> NO_EXPRESSION = Optional.empty();

    /**
     * No error constant.
     */
    public final static Optional<SpreadsheetError> NO_ERROR = Optional.empty();

    /**
     * No expression value constant.
     */
    public final static Optional<Object> NO_EXPRESSION_VALUE = Optional.empty();

    /**
     * A formula with no text, token, expression, value or error.
     */
    public final static SpreadsheetFormula EMPTY = new SpreadsheetFormula(
            "",
            NO_TOKEN,
            NO_EXPRESSION,
            NO_EXPRESSION_VALUE,
            NO_ERROR
    );

    /**
     * Uses the provided {@link Parser} to create a {@link SpreadsheetFormula} with the text and parsed {@link SpreadsheetFormulaParserToken}.
     * If the formula expression is invalid, a {@link SpreadsheetFormula} will be created with its value set to {@link SpreadsheetError}.
     */
    public static SpreadsheetFormula parse(final TextCursor text,
                                           final Parser<SpreadsheetParserContext> parser,
                                           final SpreadsheetParserContext context) {
        Objects.requireNonNull(text, "text");
        Objects.requireNonNull(parser, "parser");
        Objects.requireNonNull(context, "context");

        final TextCursorSavePoint begin = text.save();
        SpreadsheetFormula formula;

        try {
            final Optional<ParserToken> token = parser.orFailIfCursorNotEmpty(
                    ParserReporters.invalidCharacterExceptionAndExpected()
            ).parse(
                    text,
                    context
            );
            formula = EMPTY.setText(
                    begin.textBetween()
                            .toString()
            ).setToken(
                    token.map(t -> t.cast(SpreadsheetFormulaParserToken.class))
            );
        } catch (final Exception cause) {
            text.end();

            formula = EMPTY.setText(
                    begin.textBetween()
                            .toString()
            ).setExpressionValue(
                    Optional.of(
                            SpreadsheetErrorKind.translate(cause)
                    )
            );
        }

        return formula;
    }

    private SpreadsheetFormula(final String text,
                               final Optional<SpreadsheetFormulaParserToken> token,
                               final Optional<Expression> expression,
                               final Optional<Object> expressionValue,
                               final Optional<SpreadsheetError> error) {
        super();

        this.text = text;
        this.token = token;
        this.expression = expression;
        this.expressionValue = expressionValue;
        this.error = error;
    }

    // Text ............................................................................................................

    @Override
    public String text() {
        final String text = this.text;
        return null != text ?
                text :
                this.token.get().text();
    }

    public SpreadsheetFormula setText(final String text) {
        return this.text().equals(text) ?
                this :
                checkText(text)
                        .isEmpty() ?
                        EMPTY :
                        this.replace(
                                text,
                                NO_TOKEN,
                                NO_EXPRESSION,
                                NO_EXPRESSION_VALUE,
                                NO_ERROR
                        );
    }

    /**
     * The plain text form of the formula. This may or may not be valid and thus may or may not be a {@link #expression}
     * which may be executed.
     */
    private final String text;

    /**
     * Verifies that the given text holding a formula is not an excessive length. The syntactical correctness is not validated.
     */
    private static String checkText(final String text) {
        Objects.requireNonNull(text, "text");

        final int length = text.length();
        if (length >= MAX_FORMULA_TEXT_LENGTH) {
            throw new IllegalArgumentException("Invalid text length " + length + ">= " + MAX_FORMULA_TEXT_LENGTH);
        }

        return text;
    }

    public final static int MAX_FORMULA_TEXT_LENGTH = 8192;

    // token ...........................................................................................................

    public Optional<SpreadsheetFormulaParserToken> token() {
        return this.token;
    }

    public SpreadsheetFormula setToken(final Optional<SpreadsheetFormulaParserToken> token) {
        return this.token.equals(token) ?
                this :
                this.replace(
                        Objects.requireNonNull(token, "token")
                                .isPresent() ? null :
                                this.text(), // no need to keep text if token is present.
                        token,
                        NO_EXPRESSION,
                        NO_EXPRESSION_VALUE,
                        NO_ERROR
                );
    }

    /**
     * The token parsed parse the text form of this formula. When loading a stored/persisted formula this should be
     * used to reconstruct the text form.
     */
    private final Optional<SpreadsheetFormulaParserToken> token;

    // expression ......................................................................................................

    public Optional<Expression> expression() {
        return this.expression;
    }

    public SpreadsheetFormula setExpression(final Optional<Expression> expression) {
        return this.expression.equals(expression) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        Objects.requireNonNull(expression, "expression"),
                        NO_EXPRESSION_VALUE,
                        NO_ERROR
                );
    }

    /**
     * The expression from the {@link #token()} which was parsed from the {@link #text()}.
     * The expression can be executed to produce a value or error.
     */
    private final Optional<Expression> expression;

    // expressionValue..................................................................................................

    /**
     * The value when this formula is evaluated.
     */
    public Optional<Object> expressionValue() {
        return this.expressionValue;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetFormula} with the given expression value.
     */
    public SpreadsheetFormula setExpressionValue(final Optional<Object> expressionValue) {
        return this.expressionValue.equals(expressionValue) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        this.expression,
                        Objects.requireNonNull(expressionValue, "expressionValue"),
                        NO_ERROR
                );
    }

    private final Optional<Object> expressionValue;

    // value ............................................................................................................

    /**
     * Returns any value that is present, currently only considers the {@link #expressionValue()}
     */
    @Override
    public Optional<Object> value() {
        final Optional<SpreadsheetError> error = this.error;
        return error.isPresent() ?
                Cast.to(error) :
                this.expressionValue();
    }

    /**
     * Only returns an {@link SpreadsheetError} if one is present and ignores any non error value.
     */
    public Optional<SpreadsheetError> error() {
        return this.value()
                .map(v -> v instanceof SpreadsheetError ?
                        (SpreadsheetError) v :
                        null
                );
    }

    /**
     * Sets or replaces the {@link SpreadsheetError} for this cell.
     */
    public SpreadsheetFormula setError(final Optional<SpreadsheetError> error) {
        return this.error.equals(error) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        this.expression,
                        this.expressionValue,
                        Objects.requireNonNull(error, "error")
                );
    }

    private Optional<SpreadsheetError> error;

    // magic...... ....................................................................................................

    /**
     * If the value is an error try and convert into a non error value.
     * <br>
     * This handles the special case of turning formulas to missing cells parse #NAME to a value of zero.
     */
    public SpreadsheetFormula replaceErrorWithValueIfPossible(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");

        SpreadsheetFormula result = this;
        final Object value = this.value()
                .orElse(null);
        if (value instanceof SpreadsheetError) {
            final SpreadsheetError error = (SpreadsheetError) value;
            final Optional<Object> errorValue = error.replaceWithValueIfPossible(context);

            result = null != errorValue && errorValue.isPresent() ?
                    this.setExpressionValue(errorValue) :
                    this;
        }

        return result;
    }

    // clear ...........................................................................................................

    /**
     * Clears the expression, value or error if any are present. The {@link SpreadsheetFormula} returned will only
     * have text and possibly a token (if one already was presented)
     */
    public SpreadsheetFormula clear() {
        final String text = this.text;

        // text will be cleared when token is present
        return (null == text || false == text.isEmpty()) && (this.expression().isPresent() || this.expressionValue().isPresent()) ?
                new SpreadsheetFormula(
                        text,
                        this.token,
                        NO_EXPRESSION,
                        NO_EXPRESSION_VALUE,
                        NO_ERROR
                ) :
                this;
    }

    // internal factory ................................................................................................

    private SpreadsheetFormula replace(final String text,
                                       final Optional<SpreadsheetFormulaParserToken> token,
                                       final Optional<Expression> expression,
                                       final Optional<Object> expressionValue,
                                       final Optional<SpreadsheetError> error) {
        return new SpreadsheetFormula(
                text,
                token,
                expression,
                expressionValue,
                error
        );
    }

    // replaceReferences................................................................................................

    /**
     * Uses a {@link Function} to replace any existing {@link SpreadsheetCellReference} updating the {@link ParserToken}
     * and {@link Expression}. When a token is present the {@link #text()} will also be updated.
     * If the {@link Function} returns an {@link Optional#empty()}, that {@link SpreadsheetCellReference} will be replaced with
     * a {@link SpreadsheetErrorKind#REF}.
     */
    @Override
    public SpreadsheetFormula replaceReferences(final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper) {
        Objects.requireNonNull(mapper, "mapper");

        return this.setToken(
                this.token.map(
                        t -> t.replaceIf(
                                (tt) -> tt instanceof CellSpreadsheetFormulaParserToken,
                                SpreadsheetFormulaReplaceReferencesFunction.parserToken(mapper)
                        ).cast(SpreadsheetFormulaParserToken.class)
                )
        ).setExpression(
                this.expression.map(
                        (e) -> e.replaceIf(
                                ee -> ee.isReference() && ((ReferenceExpression) ee).value() instanceof SpreadsheetCellReferenceOrRange, // predicate
                                SpreadsheetFormulaReplaceReferencesFunction.expression(mapper)
                        )
                )
        );
    }

    // Patchable........................................................................................................

    @Override
    public SpreadsheetFormula patch(final JsonNode json,
                                    final JsonNodeUnmarshallContext context) {
        Objects.requireNonNull(json, "json");
        Objects.requireNonNull(context, "context");

        SpreadsheetFormula patched = this;

        for (final JsonNode propertyAndValue : json.objectOrFail().children()) {
            final JsonPropertyName propertyName = propertyAndValue.name();
            switch (propertyName.value()) {
                case TEXT_PROPERTY_STRING:
                    patched = patched.setText(
                            propertyAndValue.stringOrFail()
                    );
                    break;
                case TOKEN_PROPERTY_STRING:
                case EXPRESSION_PROPERTY_STRING:
                case EXPRESSION_VALUE_PROPERTY_STRING:
                case ERROR_PROPERTY_STRING:
                    Patchable.invalidPropertyPresent(propertyName, propertyAndValue);
                    break;
                default:
                    Patchable.unknownPropertyPresent(propertyName, propertyAndValue);
                    break;
            }
        }

        return patched;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println("Formula");
        printer.indent();

        final String text = this.text;
        if (false == CharSequences.isNullOrEmpty(text)) {
            this.printTreeLabelAndValue(
                    "text",
                    this.text(),
                    printer
            );
        } else {
            this.printTreeLabelAndValue(
                    "token",
                    this.token(),
                    printer
            );

            this.printTreeLabelAndValue(
                    "expression",
                    this.expression(),
                    printer
            );
        }

        this.printTreeLabelAndValue(
                "expressionValue",
                this.expressionValue(),
                printer
        );

        this.printTreeLabelAndValue(
                "error",
                this.error,
                printer
        );

        printer.outdent();
    }

    private void printTreeLabelAndValue(final String label,
                                        final Object value,
                                        final IndentingPrinter printer) {
        if(null != value) {
            printer.println(label + ":");
            printer.indent();
            {
                TreePrintable.printTreeOrToString(
                        value,
                        printer
                );
            }
            printer.outdent();
        }
    }

    private void printTreeLabelAndValue(final String label,
                                        final Optional<?> printable,
                                        final IndentingPrinter printer) {
        this.printTreeLabelAndValue(
                label,
                printable.orElse(null),
                printer
        );
    }

    // consumeSpreadsheetExpressionReferences...........................................................................

    /**
     * Useful method that walks the {@link SpreadsheetFormulaParserToken} if one is present, passing
     * each and every {@link SpreadsheetExpressionReference} to the provided {@link Consumer}.
     */
    public void consumeSpreadsheetExpressionReferences(final Consumer<SpreadsheetExpressionReference> consumer) {
        Objects.requireNonNull(consumer, "consumer");

        this.token()
                .ifPresent(
                        t -> t.findIf(
                                tt -> tt instanceof SpreadsheetFormulaParserToken && tt instanceof HasSpreadsheetReference,
                                ttt -> {
                                    final HasSpreadsheetReference<?> has = (HasSpreadsheetReference<?>) ttt;
                                    final Object reference = has.reference();
                                    if (reference instanceof SpreadsheetExpressionReference) {
                                        consumer.accept(
                                                (SpreadsheetExpressionReference) reference
                                        );
                                    }
                                }
                        )
                );
    }

    // json.............................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormula} parse a {@link JsonNode}.
     */
    static SpreadsheetFormula unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        String text = null;
        SpreadsheetFormulaParserToken token = null;
        Expression expression = null;
        Object expressionValue = null;
        SpreadsheetError error = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case TEXT_PROPERTY_STRING:
                    try {
                        text = child.stringOrFail();
                    } catch (final JsonNodeException cause) {
                        throw new JsonNodeUnmarshallException("Node " + TEXT_PROPERTY_STRING + " is not a string=" + child, node);
                    }
                    checkText(text);
                    break;
                case TOKEN_PROPERTY_STRING:
                    token = context.unmarshallWithType(child);
                    break;
                case EXPRESSION_PROPERTY_STRING:
                    expression = context.unmarshallWithType(child);
                    break;
                case EXPRESSION_VALUE_PROPERTY_STRING:
                    expressionValue = context.unmarshallWithType(child);
                    break;
                case ERROR_PROPERTY_STRING:
                    error = context.unmarshall(
                            child,
                            SpreadsheetError.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        SpreadsheetFormula formula = EMPTY;
        if (null == text) {
            if (null == token && null == expression) {
                JsonNodeUnmarshallContext.missingProperty(TEXT_PROPERTY, node);
            }
        } else {
            formula = EMPTY.setText(text);
        }

        return formula.setToken(Optional.ofNullable(token))
                .setExpression(Optional.ofNullable(expression))
                .setExpressionValue(Optional.ofNullable(expressionValue))
                .setError(Optional.ofNullable(error));
    }

    /**
     * Creates an JSON object with all the tokens of this formula.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        // always marshall formula text
        JsonObject object = JsonNode.object()
                .set(
                        TEXT_PROPERTY,
                        JsonNode.string(this.text())
                );

        final Optional<SpreadsheetFormulaParserToken> token = this.token;
        if (token.isPresent()) {
            object = object.set(TOKEN_PROPERTY, context.marshallWithType(token.get()));
        }

        final Optional<Expression> expression = this.expression;
        if (expression.isPresent()) {
            object = object.set(EXPRESSION_PROPERTY, context.marshallWithType(expression.get()));
        }

        final Optional<Object> expressionValue = this.expressionValue;
        if (expressionValue.isPresent()) {
            object = object.set(
                    EXPRESSION_VALUE_PROPERTY,
                    context.marshallWithType(
                            expressionValue.get()
                    )
            );
        }

        final Optional<SpreadsheetError> error = this.error;
        if(error.isPresent()) {
            object = object.set(
                    ERROR_PROPERTY,
                    context.marshall(
                            error.get()
                    )
            );
        }

        return object;
    }

    private final static String TEXT_PROPERTY_STRING = "text";
    private final static String TOKEN_PROPERTY_STRING = "token";
    private final static String EXPRESSION_PROPERTY_STRING = "expression";
    private final static String EXPRESSION_VALUE_PROPERTY_STRING = "expressionValue";
    private final static String ERROR_PROPERTY_STRING = "error";

    // @VisibleForTesting

    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);
    final static JsonPropertyName TOKEN_PROPERTY = JsonPropertyName.with(TOKEN_PROPERTY_STRING);
    final static JsonPropertyName EXPRESSION_PROPERTY = JsonPropertyName.with(EXPRESSION_PROPERTY_STRING);
    final static JsonPropertyName EXPRESSION_VALUE_PROPERTY = JsonPropertyName.with(EXPRESSION_VALUE_PROPERTY_STRING);
    final static JsonPropertyName ERROR_PROPERTY = JsonPropertyName.with(ERROR_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormula.class),
                SpreadsheetFormula::unmarshall,
                SpreadsheetFormula::marshall,
                SpreadsheetFormula.class
        );
    }

    // HasUrlFragment...................................................................................................

    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }

    // HashCodeEqualsDefined............................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.text,
                this.token,
                this.expression,
                this.expressionValue,
                this.error
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormula &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormula other) {
        return Objects.equals(this.text, other.text) && // text could be null when token is present
                this.token.equals(other.token) &&
                this.expression.equals(other.expression) &&
                this.expressionValue.equals(other.expressionValue) &&
                this.error.equals(other.error);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.value(this.text());

        if (this.expressionValue.isPresent()) {
            builder.surroundValues("(=", ")")
                    .value(new Object[]{this.expressionValue});
        }

        builder.value(this.error);
    }

    // CanBeEmpty.......................................................................................................

    /**
     * Returns true if this {@link SpreadsheetFormula#text} and {@link #expressionValue()} are both empty.
     */
    @Override
    public boolean isEmpty() {
        return this.text()
                .isEmpty() &&
                false == this.expressionValue()
                        .isPresent();
    }

    // isPure...........................................................................................................

    /**
     * Returns true only if an {@link Expression} exists and the {@link ExpressionPurityContext#isPure(ExpressionFunctionName)}
     * returns true.
     */
    public boolean isPure(final ExpressionPurityContext context) {
        Objects.requireNonNull(context, "context");

        final Expression expression = this.expression.orElse(null);
        return null != expression &&
                expression.isPure(context);
    }
}


