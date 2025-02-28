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
        HasUrlFragment {

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
     * No value constant.
     */
    public final static Optional<Object> NO_VALUE = Optional.empty();

    /**
     * A formula with no text, token, expression, value or error.
     */
    public final static SpreadsheetFormula EMPTY = new SpreadsheetFormula(
            "",
            NO_TOKEN,
            NO_EXPRESSION,
            NO_VALUE
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
            ).setValue(
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
                               final Optional<Object> value) {
        super();

        this.text = text;
        this.token = token;
        this.expression = expression;
        this.value = value;
    }

    // Text ....................................................................................................

    @Override
    public String text() {
        final String text = this.text;
        return null != text ?
                text :
                this.token.get().text();
    }

    public SpreadsheetFormula setText(final String text) {
        checkText(text);
        return this.text().equals(text) ?
                this :
                text.isEmpty() ?
                        EMPTY :
                        this.replace(
                                text,
                                NO_TOKEN,
                                NO_EXPRESSION,
                                NO_VALUE
                        );
    }

    /**
     * The plain text form of the formula. This may or may not be valid and thus may or may not be a {@link #expression}
     * which may be executed.
     */
    private final String text;

    private static void checkText(final String text) {
        Objects.requireNonNull(text, "text");

        final int length = text.length();
        if (length >= MAX_FORMULA_TEXT_LENGTH) {
            throw new IllegalArgumentException("Invalid text length " + length + ">= " + MAX_FORMULA_TEXT_LENGTH);
        }
    }


    public final static int MAX_FORMULA_TEXT_LENGTH = 8192;

    // token .............................................................................................

    public Optional<SpreadsheetFormulaParserToken> token() {
        return this.token;
    }

    public SpreadsheetFormula setToken(final Optional<SpreadsheetFormulaParserToken> token) {
        checkToken(token);

        return this.token.equals(token) ?
                this :
                this.replace(
                        token.isPresent() ? null : this.text(), // no need to keep text if token is present.
                        token,
                        NO_EXPRESSION,
                        NO_VALUE
                );
    }

    /**
     * The token parsed parse the text form of this formula. When loading a stored/persisted formula this should be
     * used to reconstruct the text form.
     */
    private final Optional<SpreadsheetFormulaParserToken> token;

    private static void checkToken(final Optional<SpreadsheetFormulaParserToken> token) {
        Objects.requireNonNull(token, "token");
    }

    // expression .............................................................................................

    public Optional<Expression> expression() {
        return this.expression;
    }

    public SpreadsheetFormula setExpression(final Optional<Expression> expression) {
        checkExpression(expression);

        return this.expression.equals(expression) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        expression,
                        NO_VALUE
                );
    }

    /**
     * The expression from the {@link #token()} which was parsed from the {@link #text()}.
     * The expression can be executed to produce a value or error.
     */
    private final Optional<Expression> expression;

    private static void checkExpression(final Optional<Expression> expression) {
        Objects.requireNonNull(expression, "expression");
    }

    // value .............................................................................................

    /**
     * The value when this formula is evaluated.
     */
    public Optional<Object> value() {
        return this.value;
    }

    /**
     * Only returns an {@link SpreadsheetError} if one is present and ignores any non error value.
     */
    public Optional<SpreadsheetError> error() {
        final Optional<Object> value = this.value();

        return value.orElse(null)
                instanceof SpreadsheetError ?
                Cast.to(value) :
                SpreadsheetFormula.NO_ERROR;
    }

    public SpreadsheetFormula setValue(final Optional<Object> value) {
        checkValue(value);

        return this.value.equals(value) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        this.expression,
                        value
                );
    }

    private final Optional<Object> value;

    private static void checkValue(final Optional<Object> value) {
        Objects.requireNonNull(value, "value");
    }

    // magic...... ....................................................................................................

    /**
     * If the value is an error try and convert into a non error value.
     * <br>
     * This handles the special case of turning formulas to missing cells parse #NAME to a value of zero.
     */
    public SpreadsheetFormula replaceErrorWithValueIfPossible(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");

        final SpreadsheetError error = this.error()
                .orElse(null);
        return null == error ?
                this :
                this.setValue(
                        Optional.of(
                                error.replaceWithValueIfPossible(context)
                        )
                );
    }

    // clear ...........................................................................................................

    /**
     * Clears the expression, value or error if any are present. The {@link SpreadsheetFormula} returned will only
     * have text and possibly a token (if one already was presented)
     */
    public SpreadsheetFormula clear() {
        final String text = this.text;

        // text will be cleared when token is present
        return (null == text || false == text.isEmpty()) && (this.expression().isPresent() || this.value().isPresent()) ?
                new SpreadsheetFormula(
                        text,
                        this.token,
                        NO_EXPRESSION,
                        NO_VALUE
                ) :
                this;
    }

    // internal factory .............................................................................................

    private SpreadsheetFormula replace(final String text,
                                       final Optional<SpreadsheetFormulaParserToken> token,
                                       final Optional<Expression> expression,
                                       final Optional<Object> value) {
        return new SpreadsheetFormula(
                text,
                token,
                expression,
                value
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

    // Patchable.......................................................................................................

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
                case VALUE_PROPERTY_STRING:
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
            printer.println("text: " + CharSequences.quoteAndEscape(text));
        } else {
            this.printTree0(
                    "token",
                    this.token(),
                    printer
            );

            this.printTree0(
                    "expression",
                    this.expression(),
                    printer
            );
        }

        final Optional<Object> possibleValue = this.value();
        if (possibleValue.isPresent()) {
            final Object value = possibleValue.get();

            printer.print("value: ");
            if (value instanceof TreePrintable) {

                final TreePrintable treePrintable = Cast.to(value);
                printer.indent();
                treePrintable.printTree(printer);
                printer.outdent();
            } else {
                printer.println(CharSequences.quoteIfChars(value) + " (" + value.getClass().getName() + ")");
            }
        }

        printer.outdent();
    }

    private void printTree0(final String label,
                            final Optional<? extends TreePrintable> printable,
                            final IndentingPrinter printer) {
        if (printable.isPresent()) {
            printer.println(label + ":");
            printer.indent();
            {
                printable.get().printTree(printer);
            }
            printer.outdent();
        }
    }

    // consumeSpreadsheetExpressionReferences............................................................................

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

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormula} parse a {@link JsonNode}.
     */
    static SpreadsheetFormula unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        String text = null;
        SpreadsheetFormulaParserToken token = null;
        Expression expression = null;
        Object value = null;

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
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithType(child);
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
                .setValue(Optional.ofNullable(value));
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

        final Optional<Object> value = this.value;
        if (value.isPresent()) {
            object = object.set(VALUE_PROPERTY, context.marshallWithType(value.get()));
        }

        return object;
    }

    private final static String TEXT_PROPERTY_STRING = "text";
    private final static String TOKEN_PROPERTY_STRING = "token";
    private final static String EXPRESSION_PROPERTY_STRING = "expression";
    private final static String VALUE_PROPERTY_STRING = "value";

    // @VisibleForTesting

    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);
    final static JsonPropertyName TOKEN_PROPERTY = JsonPropertyName.with(TOKEN_PROPERTY_STRING);
    final static JsonPropertyName EXPRESSION_PROPERTY = JsonPropertyName.with(EXPRESSION_PROPERTY_STRING);
    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormula.class),
                SpreadsheetFormula::unmarshall,
                SpreadsheetFormula::marshall,
                SpreadsheetFormula.class
        );
    }

    // HasUrlFragment..................................................................................................

    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.text,
                this.token,
                this.expression,
                this.value
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
                this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.value(this.text());

        if (this.value.isPresent()) {
            builder.surroundValues("(=", ")")
                    .value(new Object[]{this.value});
        }
    }

    // CanBeEmpty.......................................................................................................

    /**
     * Returns true if this {@link SpreadsheetFormula#text} and {@link #value()} are both empty.
     */
    @Override
    public boolean isEmpty() {
        return this.text()
                .isEmpty() &&
                false == this.value()
                        .isPresent();
    }
}


