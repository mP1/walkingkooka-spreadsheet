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

package walkingkooka.spreadsheet;

import walkingkooka.Cast;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringBuilderOption;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.spreadsheet.function.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.Expression;
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

/**
 * A spreadsheet formula, including its compiled {@link Expression} and possibly its {@link Object value} or {@link SpreadsheetError}.
 */
public final class SpreadsheetFormula implements HasText,
        Patchable<SpreadsheetFormula>,
        TreePrintable,
        UsesToStringBuilder {

    /**
     * No {@link SpreadsheetParserToken} constant.
     */
    public final static Optional<SpreadsheetParserToken> NO_TOKEN = Optional.empty();

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
     * A function that replaces cell references with expressions that become invalid due to a deleted row or column.
     */
    public final static SpreadsheetFunctionName INVALID_CELL_REFERENCE = SpreadsheetFunctionName.with("InvalidCellReference");

    /**
     * A {@link SpreadsheetParserToken} that holds the {@link #INVALID_CELL_REFERENCE} function name.
     */
    public final static SpreadsheetParserToken INVALID_CELL_REFERENCE_PARSER_TOKEN = SpreadsheetParserToken.functionName(INVALID_CELL_REFERENCE, INVALID_CELL_REFERENCE.toString());

    /**
     * A formula with no text, token, expression, value or error.
     */
    public final static SpreadsheetFormula EMPTY = new SpreadsheetFormula(
            "",
            NO_TOKEN,
            NO_EXPRESSION,
            NO_VALUE,
            NO_ERROR
    );

    private SpreadsheetFormula(final String text,
                               final Optional<SpreadsheetParserToken> token,
                               final Optional<Expression> expression,
                               final Optional<Object> value,
                               final Optional<SpreadsheetError> error) {
        super();

        this.text = text;
        this.token = token;
        this.expression = expression;
        this.value = value;
        this.error = error;
    }

    // Text ....................................................................................................

    @Override
    public String text() {
        return this.text;
    }

    public SpreadsheetFormula setText(final String text) {
        checkText(text);
        return this.text.equals(text) ?
                this :
                text.equals("") ?
                        EMPTY :
                        this.replace(
                                text,
                                NO_TOKEN,
                                NO_EXPRESSION,
                                NO_VALUE,
                                NO_ERROR
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

    public Optional<SpreadsheetParserToken> token() {
        return this.token;
    }

    public SpreadsheetFormula setToken(final Optional<SpreadsheetParserToken> token) {
        checkToken(token);

        return this.token.equals(token) ?
                this :
                this.replace(
                        this.text,
                        token,
                        NO_EXPRESSION,
                        NO_VALUE,
                        NO_ERROR
                );
    }

    /**
     * The token parsed from the text form of this formula. When loading a stored/persisted formula this should be
     * used to reconstruct the text form.
     */
    private final Optional<SpreadsheetParserToken> token;

    private static void checkToken(final Optional<SpreadsheetParserToken> token) {
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
                        NO_VALUE,
                        NO_ERROR
                );
    }

    /**
     * The expression parsed from the text form of this formula. This can then be executed to produce a {@link #value}
     */
    private final Optional<Expression> expression;

    private static void checkExpression(final Optional<Expression> expression) {
        Objects.requireNonNull(expression, "expression");
    }

    // value .............................................................................................

    public Optional<Object> value() {
        return this.value;
    }

    public SpreadsheetFormula setValue(final Optional<Object> value) {
        checkValue(value);

        return this.value.equals(value) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        this.expression,
                        value,
                        NO_ERROR
                );
    }

    /**
     * The value parsed from the text form of this formula.
     */
    private final Optional<Object> value;

    private static void checkValue(final Optional<Object> value) {
        Objects.requireNonNull(value, "value");
    }

    // error .............................................................................................

    public Optional<SpreadsheetError> error() {
        return this.error;
    }

    public SpreadsheetFormula setError(final Optional<SpreadsheetError> error) {
        checkError(error);

        return this.error.equals(error) ?
                this :
                this.replace(
                        this.text,
                        this.token,
                        this.expression,
                        error.isPresent() ? NO_VALUE : this.value, // if error is present clear the value.
                        error
                );
    }

    /**
     * The error parsed from the text form of this formula.
     */
    private final Optional<SpreadsheetError> error;

    private static void checkError(final Optional<SpreadsheetError> error) {
        Objects.requireNonNull(error, "error");
    }

    // clear ....................................................................................................

    /**
     * Clears the expression, value or error if any are present. The {@link SpreadsheetFormula} returned will only
     * have text and possibly a token (if one already was presented)
     */
    public SpreadsheetFormula clear() {
        return this.expression().isPresent() || this.value().isPresent() || this.error().isPresent() ?
                new SpreadsheetFormula(this.text, this.token, NO_EXPRESSION, NO_VALUE, NO_ERROR) :
                this;
    }

    // internal factory .............................................................................................

    private SpreadsheetFormula replace(final String text,
                                       final Optional<SpreadsheetParserToken> token,
                                       final Optional<Expression> expression,
                                       final Optional<Object> value,
                                       final Optional<SpreadsheetError> error) {
        return new SpreadsheetFormula(
                text,
                token,
                expression,
                value,
                error
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
                    final String text;
                    try {
                        text = propertyAndValue.stringOrFail();
                    } catch (final JsonNodeException cause) {
                        throw new JsonNodeUnmarshallException("Node " + TEXT_PROPERTY_STRING + " is not a string=" + propertyAndValue, propertyAndValue);
                    }
                    patched = patched.setText(text);
                    break;
                case TOKEN_PROPERTY_STRING:
                case EXPRESSION_PROPERTY_STRING:
                case VALUE_PROPERTY_STRING:
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

        printer.println("text: " + CharSequences.quoteAndEscape(this.text()));

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


        final Optional<Object> possibleValue = this.value();
        if (possibleValue.isPresent()) {
            final Object value = possibleValue.get();
            printer.println("value: " + CharSequences.quoteIfChars(value) + " (" + value.getClass().getName() + ")");
        }

        final Optional<SpreadsheetError> error = this.error();
        if (error.isPresent()) {
            printer.println("error: " + CharSequences.quoteAndEscape(error.get().value()));
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

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormula} from a {@link JsonNode}.
     */
    static SpreadsheetFormula unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        String text = null;
        SpreadsheetParserToken token = null;
        Expression expression = null;
        Object value = null;
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
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithType(child);
                    break;
                case ERROR_PROPERTY_STRING:
                    error = context.unmarshall(child, SpreadsheetError.class);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
            }
        }

        if (null == text) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(TEXT_PROPERTY, node);
        }
        if (null != value && null != error) {
            throw new JsonNodeUnmarshallException("Node contains both " + VALUE_PROPERTY + " and " + ERROR_PROPERTY + " set=" + node, node);
        }

        return new SpreadsheetFormula(text,
                Optional.ofNullable(token),
                Optional.ofNullable(expression),
                Optional.ofNullable(value),
                Optional.ofNullable(error));
    }

    /**
     * Creates an object with potentially text, value and error but not the expression.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object();

        object = object.set(TEXT_PROPERTY, JsonNode.string(this.text));

        final Optional<SpreadsheetParserToken> token = this.token;
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

        final Optional<SpreadsheetError> error = this.error;
        if (error.isPresent()) {
            object = object.set(ERROR_PROPERTY, context.marshall(error.get()));
        }

        return object;
    }

    private final static String TEXT_PROPERTY_STRING = "text";
    private final static String TOKEN_PROPERTY_STRING = "token";
    private final static String EXPRESSION_PROPERTY_STRING = "expression";
    private final static String VALUE_PROPERTY_STRING = "value";
    private final static String ERROR_PROPERTY_STRING = "error";

    // @VisibleForTesting

    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);
    final static JsonPropertyName TOKEN_PROPERTY = JsonPropertyName.with(TOKEN_PROPERTY_STRING);
    final static JsonPropertyName EXPRESSION_PROPERTY = JsonPropertyName.with(EXPRESSION_PROPERTY_STRING);
    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);
    final static JsonPropertyName ERROR_PROPERTY = JsonPropertyName.with(ERROR_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormula.class),
                SpreadsheetFormula::unmarshall,
                SpreadsheetFormula::marshall,
                SpreadsheetFormula.class
        );
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.text,
                this.token,
                this.expression,
                this.value,
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
        return this.text.equals(other.text) &&
                this.token.equals(other.token) &&
                this.expression.equals(other.expression) &&
                this.value.equals(other.value) &&
                this.error.equals(other.error);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.disable(ToStringBuilderOption.QUOTE);
        builder.value(this.text);

        if (this.value.isPresent()) {
            builder.surroundValues("(=", ")")
                    .value(new Object[]{this.value});
        }
        if (this.error.isPresent()) {
            builder.surroundValues("(", ")")
                    .value(new Object[]{this.error});
        }
    }
}


