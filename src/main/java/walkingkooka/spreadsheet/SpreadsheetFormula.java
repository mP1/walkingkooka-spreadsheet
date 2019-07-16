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
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.HasText;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.json.FromJsonNodeException;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.JsonObjectNode;

import java.util.Objects;
import java.util.Optional;

/**
 * A spreadsheet formula, including its compiled {@link ExpressionNode} and possibly its {@link Object value} or {@link SpreadsheetError}.
 */
public final class SpreadsheetFormula implements HashCodeEqualsDefined,
        HasJsonNode,
        HasText,
        UsesToStringBuilder {

    /**
     * No expression constant.
     */
    public final static Optional<ExpressionNode> NO_EXPRESSION = Optional.empty();

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
     * Factory that creates a new {@link SpreadsheetFormula}
     */
    public static SpreadsheetFormula with(final String text) {
        checkText(text);

        return new SpreadsheetFormula(text, NO_EXPRESSION, NO_VALUE, NO_ERROR);
    }

    private SpreadsheetFormula(final String text,
                               final Optional<ExpressionNode> expression,
                               final Optional<Object> value,
                               final Optional<SpreadsheetError> error) {
        super();

        this.text = text;
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
                this.replace(text, NO_EXPRESSION, NO_VALUE, NO_ERROR);
    }

    /**
     * The plain text form of the formula. This may or may not be valid and thus may or may not be a {@link #expression}
     * which may be executed.
     */
    private String text;

    private static void checkText(final String text) {
        Objects.requireNonNull(text, "text");
    }

    // expression .............................................................................................

    public Optional<ExpressionNode> expression() {
        return this.expression;
    }

    public SpreadsheetFormula setExpression(final Optional<ExpressionNode> expression) {
        checkExpression(expression);

        return this.expression.equals(expression) ?
                this :
                this.replace(this.text, expression, NO_VALUE, NO_ERROR);
    }

    /**
     * The expression parsed from the text form of this formula. This can then be executed to produce a {@link #value}
     */
    private Optional<ExpressionNode> expression;

    private static void checkExpression(final Optional<ExpressionNode> expression) {
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
                this.replace(this.text, this.expression, value, NO_ERROR);
    }

    /**
     * The value parsed from the text form of this formula.
     */
    private Optional<Object> value;

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
                this.replace(this.text,
                        this.expression,
                        error.isPresent() ? NO_VALUE : this.value, // if error is present clear the value.
                        error);
    }

    /**
     * The error parsed from the text form of this formula.
     */
    private Optional<SpreadsheetError> error;

    private static void checkError(final Optional<SpreadsheetError> error) {
        Objects.requireNonNull(error, "error");
    }

    // clear ....................................................................................................

    /**
     * Clears the value or error or formatted.
     */
    public SpreadsheetFormula clear() {
        return this.setValue(NO_VALUE)
                .setError(NO_ERROR);
    }

    // internal factory .............................................................................................

    private SpreadsheetFormula replace(final String text,
                                       final Optional<ExpressionNode> expression,
                                       final Optional<Object> value,
                                       final Optional<SpreadsheetError> error) {
        return new SpreadsheetFormula(text, expression, value, error);
    }

    // HasJsonNode..........................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormula} from a {@link JsonNode}.
     */
    static SpreadsheetFormula fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        String text = null;
        ExpressionNode expression = null;
        Object value = null;
        SpreadsheetError error = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case TEXT_STRING:
                        try {
                            text = child.stringValueOrFail();
                        } catch (final JsonNodeException cause) {
                            throw new FromJsonNodeException("Node " + TEXT + " is not a string=" + child, node);
                        }
                        checkText(text);
                        break;
                    case EXPRESSION_PROPERTY_STRING:
                        value = child.fromJsonNodeWithType();
                        break;
                    case VALUE_PROPERTY_STRING:
                        value = child.value();
                        break;
                    case ERROR_PROPERTY_STRING:
                        error = SpreadsheetError.fromJsonNode(child);
                        break;
                    default:
                        HasJsonNode.unknownPropertyPresent(name, node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new FromJsonNodeException(cause.getMessage(), node, cause);
        }

        if (null == text) {
            HasJsonNode.requiredPropertyMissing(TEXT, node);
        }
        if (null != value && null != error) {
            throw new FromJsonNodeException("Node contains both " + VALUE_PROPERTY + " and " + ERROR_PROPERTY + " set=" + node, node);
        }

        try {
            return new SpreadsheetFormula(text,
                    Optional.ofNullable(expression),
                    Optional.ofNullable(value),
                    Optional.ofNullable(error));
        } catch (final RuntimeException cause) {
            throw new FromJsonNodeException(cause.getMessage(), node, cause);
        }
    }

    /**
     * Creates an object with potentially text, value and error but not the expression.
     */
    @Override
    public JsonNode toJsonNode() {
        JsonObjectNode object = JsonNode.object();

        object = object.set(TEXT, JsonNode.string(this.text));

        final Optional<ExpressionNode> expression = this.expression;
        if (expression.isPresent()) {
            object = object.set(EXPRESSION_STRING, expression.get().toJsonNodeWithType());
        }

        final Optional<Object> value = this.value;
        if (value.isPresent()) {
            object = object.set(VALUE_PROPERTY, HasJsonNode.toJsonNodeWithType(value.get()));
        }

        final Optional<SpreadsheetError> error = this.error;
        if (error.isPresent()) {
            object = object.set(ERROR_PROPERTY, error.get().toJsonNode());
        }

        return object;
    }

    private final static String TEXT_STRING = "text";
    private final static String EXPRESSION_PROPERTY_STRING = "expression";
    private final static String VALUE_PROPERTY_STRING = "value";
    private final static String ERROR_PROPERTY_STRING = "error";

    // @VisibleForTesting

    final static JsonNodeName TEXT = JsonNodeName.with(TEXT_STRING);
    final static JsonNodeName EXPRESSION_STRING = JsonNodeName.with(EXPRESSION_PROPERTY_STRING);
    final static JsonNodeName VALUE_PROPERTY = JsonNodeName.with(VALUE_PROPERTY_STRING);
    final static JsonNodeName ERROR_PROPERTY = JsonNodeName.with(ERROR_PROPERTY_STRING);

    static {
        HasJsonNode.register("spreadsheet-formula",
                SpreadsheetFormula::fromJsonNode,
                SpreadsheetFormula.class);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormula &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormula other) {
        return this.text.equals(other.text) &&
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


