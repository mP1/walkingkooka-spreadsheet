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
import walkingkooka.UsesToStringBuilder;
import walkingkooka.Value;
import walkingkooka.convert.HasConvertError;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.validation.form.SpreadsheetForms;
import walkingkooka.store.HasNotFoundText;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.validation.HasValidationChoiceList;
import walkingkooka.validation.ValidationChoiceList;
import walkingkooka.validation.ValidationError;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An error for an individual cell or formula which may be a parsing or execution error.
 * Note the message may be empty but the {@link SpreadsheetErrorKind} is always required.
 */
public final class SpreadsheetError implements Value<Optional<Object>>,
    HasText,
    TreePrintable,
    HasSpreadsheetErrorKind,
    UsesToStringBuilder,
    HasConvertError,
    HasValidationChoiceList {

    /**
     * The message component is optional.
     */
    public static final String NO_MESSAGE = "";

    public static final Optional<Object> NO_VALUE = Optional.empty();

    /**
     * Creates a {@link SpreadsheetError} indicating a cycle involving the provided {@link SpreadsheetExpressionReference}.
     */
    public static SpreadsheetError cycle(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return SpreadsheetErrorKind.REF.setMessageAndValue(
            "Cycle involving " + CharSequences.quoteAndEscape(reference.text()),
            reference
        );
    }

    /**
     * Creates a {@link SpreadsheetError} reporting that no {@link walkingkooka.spreadsheet.format.SpreadsheetFormatter}
     * matched a given value during a format.
     */
    public static SpreadsheetError formatterNotFound(final SpreadsheetFormatterName name) {
        return SpreadsheetErrorKind.ERROR.setMessageAndValue(
            "Formatter not found",
            name
        );
    }

    /**
     * Creates a {@link SpreadsheetError} reporting that a {@link ExpressionFunctionName function} was not found.
     */
    public static SpreadsheetError functionNotFound(final ExpressionFunctionName function) {
        Objects.requireNonNull(function, "function");

        return SpreadsheetErrorKind.NAME.setMessageAndValue(
            function.notFoundText(),
            function
        );
    }

    /**
     * Creates a {@link SpreadsheetError} reporting that a {@link ExpressionReference} was not found.
     * If the {@link ExpressionReference} is a {@link SpreadsheetExpressionReference} then {@link #selectionNotFound(SpreadsheetExpressionReference)}
     * is returned.
     */
    public static SpreadsheetError referenceNotFound(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return reference instanceof SpreadsheetExpressionReference ?
            selectionNotFound((SpreadsheetExpressionReference) reference) :
            referenceNotSpreadsheetExpressionReferenceNotFound(reference);
    }

    private static SpreadsheetError referenceNotSpreadsheetExpressionReferenceNotFound(final ExpressionReference reference) {
        String text;

        if (reference instanceof HasNotFoundText) {
            final HasNotFoundText hasNotFoundText = (HasNotFoundText) reference;
            text = hasNotFoundText.notFoundText();
        } else {
            text = "Missing " + CharSequences.quoteAndEscape(reference.toString());
        }

        return SpreadsheetErrorKind.NAME.setMessageAndValue(
            text,
            reference
        );
    }

    /**
     * Creates a {@link SpreadsheetError} reporting that a cell was deleted.
     */
    public static SpreadsheetError selectionDeleted() {
        return SpreadsheetErrorKind.REF.toError();
    }

    /**
     * Creates a {@link SpreadsheetError} reporting that a cell or label was not found.
     */
    public static SpreadsheetError selectionNotFound(final SpreadsheetExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        return SpreadsheetErrorKind.NAME.setMessageAndValue(
            reference.notFoundText(),
            reference
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetError} without any message only holding {@link ValidationChoiceList}.
     */
    public static SpreadsheetError validationChoiceList(final ValidationChoiceList choices) {
        Objects.requireNonNull(choices, "choices");

        return SpreadsheetErrorKind.VALIDATION.toError()
            .setValue(
                Optional.of(choices)
            );
    }

    /**
     * Accepts potentially a list of {@link ValidationError errors} and returns a {@link SpreadsheetError}.
     */
    public static Optional<SpreadsheetError> validationErrors(final List<ValidationError<SpreadsheetExpressionReference>> errors) {
        Objects.requireNonNull(errors, "errors");

        SpreadsheetError spreadsheetError = null;

        if (false == errors.isEmpty()) {
            final ValidationError<SpreadsheetExpressionReference> firstValidationError = errors.get(0);
            final Optional<Object> value = firstValidationError.value();
            final Object valueOrNull = value.orElse(null);

            spreadsheetError = firstValidationError.text()
                .isEmpty() &&
                valueOrNull instanceof ValidationChoiceList ?
                validationChoiceList((ValidationChoiceList) valueOrNull) :
                SpreadsheetError.parse(
                    firstValidationError.text()
                ).setValue(value);
        }

        return Optional.ofNullable(spreadsheetError);
    }

    /**
     * Supports parsing error messages, with the first token containing the {@link SpreadsheetErrorKind} and the remaining
     * text the message.
     * <pre>
     * #DIV Error123
     * </pre>
     * becomes {@link SpreadsheetErrorKind#DIV0} and a {@link String} of <code>Error123</code>.
     * <br>
     * Parsing empty text returns an {@link SpreadsheetError} with {@link SpreadsheetErrorKind#MISSING_PREFIX}.
     */
    public static SpreadsheetError parse(final String text) {
        Objects.requireNonNull(text, "text");

        final SpreadsheetErrorKind kind;
        final String message;

        // missing prefix text is the message
        if (text.length() > 1 && text.charAt(0) == SpreadsheetErrorKind.PREFIX) {
            final int nextToken = text.indexOf(' ');
            final String kindText = -1 == nextToken ?
                text :
                text.substring(
                    0,
                    nextToken
                );
            if (kindText.isEmpty()) {
                throw new IllegalArgumentException("Missing error kind");
            }

            try {
                kind = SpreadsheetErrorKind.parse(kindText);
            } catch (final IllegalArgumentException cause) {
                throw new IllegalArgumentException("Invalid error kind", cause);
            }

            message = -1 == nextToken ?
                "" :
                text.substring(nextToken + 1);
        } else {
            kind = SpreadsheetErrorKind.MISSING_PREFIX;
            message = text;
        }

        return kind.setMessage(message);
    }

    /**
     * Generic factory that creates a new {@link SpreadsheetError} with the provided details.
     */
    public static SpreadsheetError with(final SpreadsheetErrorKind kind,
                                        final String message,
                                        final Optional<Object> value) {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(message, "message");
        Objects.requireNonNull(value, "value");

        return new SpreadsheetError(
            kind,
            message,
            value
        );
    }

    private SpreadsheetError(final SpreadsheetErrorKind kind,
                             final String message,
                             final Optional<Object> value) {
        this.kind = kind;
        this.message = message;
        this.value = value;
    }

    /**
     * Wraps this {@link SpreadsheetError} inside an exception ready to be thrown.
     */
    public SpreadsheetErrorException exception() {
        return new SpreadsheetErrorException(this);
    }

    // kind.............................................................................................................

    public SpreadsheetErrorKind kind() {
        return this.kind;
    }

    private final SpreadsheetErrorKind kind;

    // message..........................................................................................................

    /**
     * The error message text.
     */
    public String message() {
        return this.message;
    }

    public SpreadsheetError setMessage(final String message) {
        SpreadsheetError error = this;

        if (false == this.message.equals(message)) {
            // if message is not empty and kind is SpreadsheetErrorKind#VALIDATION
            SpreadsheetErrorKind kind = this.kind;
            if (false == message.isEmpty() && SpreadsheetErrorKind.VALIDATION == kind) {
                kind = SpreadsheetErrorKind.ERROR;
            }

            error = new SpreadsheetError(
                kind,
                Objects.requireNonNull(message, "message"),
                this.value
            );
        }

        return error;
    }

    private final String message;

    /**
     * Returns a value if this error should be replaced with a value.
     * Applies some cell formula value transformations such as turning formulas to missing cells should give a value of zero.
     */
    public Optional<Object> replaceWithValueIfPossible(final SpreadsheetEngineContext context) {
        Objects.requireNonNull(context, "context");

        return Optional.ofNullable(
            this.isMissingCell() ?
                context.missingCellNumberValue() :
                null
        );
    }

    // Value............................................................................................................

    @Override
    public Optional<Object> value() {
        return this.value;
    }

    /**
     * Would be setter that returns a {@link SpreadsheetError} with the given value, creating a new instance if necessary.
     */
    public SpreadsheetError setValue(final Optional<Object> value) {
        return this.value.equals(value) ?
            this :
            new SpreadsheetError(
                this.kind,
                this.message,
                Objects.requireNonNull(value, "value")
            );
    }

    /**
     * Returns a {@link SpreadsheetError} with no value, returning a new instance if this has a value.
     */
    public SpreadsheetError clearValue() {
        return this.setValue(NO_VALUE);
    }

    private final Optional<Object> value;

    /**
     * Only returns true if the {@link #kind} == {@link SpreadsheetErrorKind#NAME} and {@link #value()} is a {@link SpreadsheetCellReference}.
     * <br>
     * This is necessary to support formulas with references to empty/missing cells which will be given a value of zero.
     */
    public boolean isMissingCell() {
        return this.kind() == SpreadsheetErrorKind.NAME &&
            this.value().orElse(null) instanceof SpreadsheetCellReference;
    }

    // HasText..........................................................................................................

    /**
     * Returns the text form of this error, note the message and any value will be ignored.
     */
    @Override
    public String text() {
        // #DIV0
        return this.kind.text();
    }

    // textIncludingMessage.............................................................................................

    /**
     * Unlike {@link #text()} this also includes the {@link #message()}.
     * Note this may be passed to {@link #parse(String)} returning an equivalent {@link SpreadsheetError}
     */
    public String textIncludingMessage() {
        final String kindText = this.kind.text();
        final String message = this.message;

        // #DIV0 Message
        return message.isEmpty() ?
            kindText :
            kindText +
                ' ' +
                CharSequences.trimLeft(
                    this.message
                );
    }

    // setNameString...................................................................................................

    /**
     * Returns a {@link SpreadsheetError} with {@link SpreadsheetErrorKind#NAME_STRING} signifying it was not possible
     * to convert a #NAME to a {@link String} value.
     */
    public SpreadsheetError setNameString() {
        final SpreadsheetErrorKind kind = this.kind;
        return kind == SpreadsheetErrorKind.NAME_STRING ?
            this :
            this.setNameStringDifferent();
    }

    private SpreadsheetError setNameStringDifferent() {
        final SpreadsheetErrorKind kind = this.kind;
        if (kind != SpreadsheetErrorKind.NAME) {
            throw new IllegalStateException(
                "SpreadsheetError.kind is not " +
                    SpreadsheetErrorKind.NAME +
                    " but is " +
                    kind
            );
        }

        return new SpreadsheetError(
            SpreadsheetErrorKind.NAME_STRING,
            this.message,
            this.value
        );
    }

    // toValidationError................................................................................................

    public ValidationError<SpreadsheetExpressionReference> toValidationError(final SpreadsheetExpressionReference cellOrLabel) {
        Objects.requireNonNull(cellOrLabel, "cellOrLabel");

        if (cellOrLabel.isCellRange()) {
            throw new IllegalArgumentException("ValidationErrors only accept cell or label but got cell-range");
        }

        return SpreadsheetForms.error(cellOrLabel)
            .setMessage(this.message)
            .setValue(this.value);
    }

    // HasSpreadsheetErrorKind .........................................................................................

    @Override
    public SpreadsheetErrorKind spreadsheetErrorKind() {
        return this.kind();
    }

    // HasConvertError..................................................................................................

    @Override
    public Optional<String> convertErrorMessage() {
        return Optional.ofNullable(
            SpreadsheetErrorKind.VALUE == this.kind && false == CharSequences.isNullOrEmpty(this.message) ?
                this.message :
                null
        );
    }

    // HasValidationChoiceList..........................................................................................

    @Override
    public Optional<ValidationChoiceList> validationChoiceList() {
        final Object value = this.value()
            .orElse(null);

        return Optional.ofNullable(
            value instanceof ValidationChoiceList ?
                (ValidationChoiceList) value :
                null
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.kind().text());

        printer.indent();

        final String message = this.message();
        if (false == message.isEmpty()) {
            printer.println(
                CharSequences.quoteAndEscape(this.message())
            );
        }

        final Object value = this.value()
            .orElse(null);

        if (null != value) {
            TreePrintable.printTreeOrToString(
                value,
                printer
            );
        }

        printer.outdent();
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.kind,
            this.message,
            this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetError &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetError error) {
        return this.kind == error.kind &&
            this.message.equals(error.message) &&
            this.value.equals(error.value);
    }

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.kind)
            .value(this.message)
            .value(this.value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.buildFrom(this);
    }

    // json.............................................................................................................

    static SpreadsheetError unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        SpreadsheetErrorKind kind = null;
        String message = NO_MESSAGE;
        Object value = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case KIND_PROPERTY_STRING:
                    kind = SpreadsheetErrorKind.valueOf(child.stringOrFail());
                    break;
                case MESSAGE_PROPERTY_STRING:
                    message = child.stringOrFail();
                    break;
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithType(child);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == kind) {
            JsonNodeUnmarshallContext.missingProperty(KIND_PROPERTY, node);
        }

        return new SpreadsheetError(
            kind,
            message,
            Optional.ofNullable(value)
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject json = JsonNode.object()
            .set(KIND_PROPERTY, JsonNode.string(this.kind.name()));

        final String message = this.message();
        if (false == message.isEmpty()) {
            json = json.set(
                MESSAGE_PROPERTY,
                JsonNode.string(message)
            );
        }

        final Object value = this.value()
            .orElse(null);
        if (null != value) {
            json = json.set(
                VALUE_PROPERTY,
                context.marshallWithType(value)
            );
        }

        return json;
    }

    private final static String KIND_PROPERTY_STRING = "kind";
    private final static String MESSAGE_PROPERTY_STRING = "message";
    private final static String VALUE_PROPERTY_STRING = "value";

    // @VisibleForTesting

    final static JsonPropertyName KIND_PROPERTY = JsonPropertyName.with(KIND_PROPERTY_STRING);
    final static JsonPropertyName MESSAGE_PROPERTY = JsonPropertyName.with(MESSAGE_PROPERTY_STRING);
    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetError.class),
            SpreadsheetError::unmarshall,
            SpreadsheetError::marshall,
            SpreadsheetError.class
        );
    }
}
