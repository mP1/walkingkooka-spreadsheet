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
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallException;
import walkingkooka.tree.json.patch.Patchable;
import walkingkooka.validation.ValidationValueTypeName;

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
    Value<Optional<Object>>,
    Patchable<SpreadsheetFormula>,
    TreePrintable,
    UsesToStringBuilder,
    HasUrlFragment {

    /**
     * A constant for an absent formula text.
     */
    public final static String NO_TEXT = "";

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
     * Input value type is absent constant.
     */
    public final static Optional<ValidationValueTypeName> NO_VALUE_TYPE = Optional.empty();

    /**
     * Input value is absent constant.
     */
    public final static Optional<Object> NO_VALUE = Optional.empty();

    /**
     * A formula with no text, token, expression, value or error.
     */
    public final static SpreadsheetFormula EMPTY = new SpreadsheetFormula(
        NO_TEXT,
        NO_TOKEN,
        NO_EXPRESSION,
        NO_VALUE_TYPE,
        NO_VALUE,
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
                ParserReporters.basic()
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
                               final Optional<ValidationValueTypeName> valueType,
                               final Optional<Object> value,
                               final Optional<SpreadsheetError> error) {
        super();

        this.text = text;
        this.token = token;
        this.expression = expression;

        this.valueType = valueType;
        this.value = value;

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

    /**
     * Sets or replaces the text, clearing any {@link #expression()}, {@link #value()}, {@link #value()}
     * and {@link #error()}.
     */
    public SpreadsheetFormula setText(final String text) {
        return this.text().equals(text) ? this : checkText(text)
            .isEmpty() ? EMPTY : new SpreadsheetFormula(
            text,
            NO_TOKEN,
            NO_EXPRESSION,
            NO_VALUE_TYPE,
            NO_VALUE,
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

    /**
     * Would be setter that sets or replaces the token, also clearing any {@link #value()} and {@link #error()}.
     */
    public SpreadsheetFormula setToken(final Optional<SpreadsheetFormulaParserToken> token) {
        if (this.token.equals(token)) {
            return this;
        } else {// no need to keep text if token is present.
            final String text1 = Objects.requireNonNull(token, "token")
                .isPresent() ? null :
                this.text();
            return new SpreadsheetFormula(
                text1,
                token,
                NO_EXPRESSION,
                NO_VALUE_TYPE,
                NO_VALUE,
                NO_ERROR
            );
        }
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

    /**
     * Would be setter that sets or replaces the {@link Expression}, also clearing any {@link #value()} and {@link #error()}.
     */
    public SpreadsheetFormula setExpression(final Optional<Expression> expression) {
        return this.expression.equals(expression) ? this : new SpreadsheetFormula(
            this.text,
            this.token,
            Objects.requireNonNull(expression, "expression"),
            NO_VALUE_TYPE,
            NO_VALUE,
            NO_ERROR
        );
    }

    /**
     * The expression from the {@link #token()} which was parsed from the {@link #text()}.
     * The expression can be executed to produce a value or error.
     */
    private final Optional<Expression> expression;

    // valueType........................................................................................................

    /**
     * A hint that is used by the UI to select an appropriate picker forcing the user to enter a matching {@link #value}.
     */
    public Optional<ValidationValueTypeName> valueType() {
        return this.valueType;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetFormula} with the input value hint.
     */
    public SpreadsheetFormula setValueType(final Optional<ValidationValueTypeName> valueType) {
        return this.valueType.equals(valueType) ? this : new SpreadsheetFormula(
            NO_TEXT,
            NO_TOKEN,
            NO_EXPRESSION,
            Objects.requireNonNull(valueType, "valueType"),
            NO_VALUE,
            NO_ERROR
        );
    }

    /**
     * A type hint for the {@link #value}.
     */
    private final Optional<ValidationValueTypeName> valueType;

    // value..................................................................................................

    /**
     * The given value, not this is not computed from the formula text or expression is simply a value probably
     * selected by the user from a picker.
     */
    @Override
    public Optional<Object> value() {
        return this.value;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetFormula} with the given value and also clears any error.
     */
    public SpreadsheetFormula setValue(final Optional<Object> value) {
        return this.value.equals(value) ? this : new SpreadsheetFormula(
            this.text,
            this.token,
            this.expression,
            this.valueType,
            Objects.requireNonNull(value, "value"),
            NO_ERROR
        );
    }

    /**
     * A value that is entered by the user not using a expression but probably via a picker.
     */
    private final Optional<Object> value;

    // errorOrValue ....................................................................................................

    /**
     * Returns any value that is present, first using any {@link #error()} or {@link #value()}.
     */
    public Optional<Object> errorOrValue() {
        // GWT's Optional#or is not implemented.
        Optional<SpreadsheetError> errorOrValue = this.error;
        return errorOrValue.isPresent() ?
            Cast.to(errorOrValue) :
            this.value;
    }

    // error ...........................................................................................................

    /**
     * Only returns an {@link SpreadsheetError} if one is present and ignores any non error value.
     */
    public Optional<SpreadsheetError> error() {
        return this.errorOrValue()
            .map(v -> v instanceof SpreadsheetError ?
                (SpreadsheetError) v :
                null
            );
    }

    /**
     * Sets or replaces the {@link SpreadsheetError}. Note any text, token, expression or input value is not lost
     * only the error is possibly updated if different.
     */
    public SpreadsheetFormula setError(final Optional<SpreadsheetError> error) {
        return this.error.equals(error) ? this : new SpreadsheetFormula(
            this.text,
            this.token,
            this.expression,
            this.valueType,
            this.value,
            Objects.requireNonNull(error, "error")
        );
    }

    private final Optional<SpreadsheetError> error;

    // magic...... ....................................................................................................

    /**
     * If the value is an error try and convert into a non error value.
     * <br>
     * This handles the special case of turning formulas to missing cells parse #NAME to a value of zero.
     */
    public SpreadsheetFormula setValueIfError(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");

        SpreadsheetFormula result = this;
        final Object value = this.errorOrValue()
            .orElse(null);
        if (value instanceof SpreadsheetError) {
            final SpreadsheetError error = (SpreadsheetError) value;
            final Optional<Object> errorValue = error.replaceWithValueIfPossible(context);

            result = null != errorValue && errorValue.isPresent() ?
                this.setValue(errorValue) :
                this;
        }

        return result;
    }

    // clear ...........................................................................................................

    /**
     * Clears any parsed {@link #expression()}, {@link #value()}  and {@link #error()}, but any {@link #text()}.
     */
    public SpreadsheetFormula clear() {
        final String text = this.text;

        // text will be cleared when token is present
        return (null == text || false == text.isEmpty()) &&
            (this.expression().isPresent() || this.value().isPresent()) ?
            new SpreadsheetFormula(
                text,
                this.token,
                NO_EXPRESSION,
                NO_VALUE_TYPE,
                NO_VALUE,
                NO_ERROR
            ) :
            this.setError(NO_ERROR); // when value then clear error
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

    // XXXPatch.........................................................................................................

    /**
     * Creates a PATCH for {@link SpreadsheetFormula#text()}
     */
    public static JsonNode textPatch(final String text) {
        Objects.requireNonNull(text, "text");

        return JsonNode.object()
            .set(
                TEXT_PROPERTY,
                JsonNodeMarshallContexts.basic()
                    .marshall(text)
            );
    }

    /**
     * Creates a PATCH for a formula to replace the {@link #value()}.
     */
    public static JsonNode valuePatch(final Optional<Object> value,
                                      final JsonNodeMarshallContext context) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(context, "context");

        return JsonNode.object()
            .set(
                VALUE_PROPERTY,
                context.marshallOptionalWithType(value)
            );
    }

    /**
     * Creates a PATCH for a formula to replace the {@link #valueType()}.
     */
    public static JsonNode valueTypePatch(final Optional<ValidationValueTypeName> valueType,
                                          final JsonNodeMarshallContext context) {
        Objects.requireNonNull(valueType, "valueType");
        Objects.requireNonNull(context, "context");

        return JsonNode.object()
            .set(
                VALUE_TYPE_PROPERTY,
                context.marshallOptional(valueType)
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
                case VALUE_TYPE_PROPERTY_STRING:
                    patched = patched.setValueType(
                        Optional.ofNullable(
                            context.unmarshall(
                                propertyAndValue,
                                ValidationValueTypeName.class
                            )
                        )
                    );
                    break;
                case VALUE_PROPERTY_STRING:
                    patched = patched.setValue(
                        Optional.ofNullable(
                            context.unmarshallWithType(propertyAndValue)
                        )
                    );
                    break;
                case TOKEN_PROPERTY_STRING:
                case EXPRESSION_PROPERTY_STRING:
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
            "valueType",
            this.valueType(),
            printer
        );

        this.printTreeLabelAndValue(
            "value",
            this.value(),
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
        if (null != value) {
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
        String text = NO_TEXT;
        SpreadsheetFormulaParserToken token = null;
        Expression expression = null;
        Optional<ValidationValueTypeName> valueType = NO_VALUE_TYPE;
        Object value = null;
        Optional<SpreadsheetError> error = NO_ERROR;

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
                case VALUE_TYPE_PROPERTY_STRING:
                    valueType = context.unmarshallOptional(
                        child,
                        ValidationValueTypeName.class
                    );
                    break;
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithType(child);
                    break;
                case ERROR_PROPERTY_STRING:
                    error = context.unmarshallOptional(
                        child,
                        SpreadsheetError.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return EMPTY.setText(text)
            .setToken(Optional.ofNullable(token))
            .setExpression(Optional.ofNullable(expression))
            .setValueType(valueType)
            .setValue(Optional.ofNullable(value))
            .setError(error);
    }

    /**
     * Creates an JSON object with all the non null properties of this formula.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object();

        final Optional<Object> value = this.value;

        // dont marshall text if input value is present.
        final String text = this.text();
        if (false == text.isEmpty()) {
            object = object.set(
                TEXT_PROPERTY,
                JsonNode.string(text)
            );
        }

        final Optional<SpreadsheetFormulaParserToken> token = this.token;
        if (token.isPresent()) {
            object = object.set(TOKEN_PROPERTY, context.marshallWithType(token.get()));
        }

        final Optional<Expression> expression = this.expression;
        if (expression.isPresent()) {
            object = object.set(EXPRESSION_PROPERTY, context.marshallWithType(expression.get()));
        }

        if (value.isPresent()) {
            object = object.set(
                VALUE_PROPERTY,
                context.marshallWithType(
                    value.get()
                )
            );
        }

        final Optional<ValidationValueTypeName> valueType = this.valueType;
        if (valueType.isPresent()) {
            object = object.set(
                VALUE_TYPE_PROPERTY,
                context.marshallOptional(valueType)
            );
        }

        final Optional<SpreadsheetError> error = this.error;
        if (error.isPresent()) {
            object = object.set(
                ERROR_PROPERTY,
                context.marshallOptional(error)
            );
        }

        return object;
    }

    private final static String TEXT_PROPERTY_STRING = "text";
    private final static String TOKEN_PROPERTY_STRING = "token";
    private final static String EXPRESSION_PROPERTY_STRING = "expression";
    private final static String VALUE_TYPE_PROPERTY_STRING = "valueType";
    private final static String VALUE_PROPERTY_STRING = "value";

    private final static String ERROR_PROPERTY_STRING = "error";

    // @VisibleForTesting

    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);
    final static JsonPropertyName TOKEN_PROPERTY = JsonPropertyName.with(TOKEN_PROPERTY_STRING);
    final static JsonPropertyName EXPRESSION_PROPERTY = JsonPropertyName.with(EXPRESSION_PROPERTY_STRING);

    final static JsonPropertyName VALUE_TYPE_PROPERTY = JsonPropertyName.with(VALUE_TYPE_PROPERTY_STRING);
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
            this.valueType,
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
        return Objects.equals(this.text, other.text) && // text could be null when token is present
            this.token.equals(other.token) &&
            this.expression.equals(other.expression) &&
            this.valueType.equals(other.valueType) &&
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
        builder.value(this.text());

        builder.value(this.valueType);
        builder.value(this.value);

        builder.value(this.error);
    }

    // CanBeEmpty.......................................................................................................

    /**
     * Returns true if this has no {@link #text()}, no {@link #value()} and no {@link #valueType()}.
     */
    @Override
    public boolean isEmpty() {
        return this.equals(EMPTY);
    }

    // isPure...........................................................................................................

    /**
     * Returns true only if a {@link #value} is present or an {@link Expression} exists and the {@link ExpressionPurityContext#isPure(ExpressionFunctionName)}
     * returns true.
     */
    public boolean isPure(final ExpressionPurityContext context) {
        Objects.requireNonNull(context, "context");

        final Expression expression = this.expression.orElse(null);
        return this.value.isPresent() ||
            (null != expression && expression.isPure(context));
    }
}


