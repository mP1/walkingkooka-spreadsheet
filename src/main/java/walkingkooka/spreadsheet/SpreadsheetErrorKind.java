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

// #NAME
// #DIV/0
// #REF!
// #VALUE!
// #NA
// #NULL
// #NUM

import walkingkooka.convert.ConversionException;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.HasExpressionReference;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * The type of {@link SpreadsheetError}.
 * <br>
 * https://exceljet.net/excel-functions/excel-errortype-function
 */
public enum SpreadsheetErrorKind implements HasText {

    /**
     * https://support.microsoft.com/en-us/office/correct-a-null-error-11a15515-5df3-4a82-899e-e4c0070ea9c4
     * <br>
     * This error indicates an incorrect range operator within a formula.
     */
    NULL("#NULL!", 1),

    /**
     * https://support.microsoft.com/en-us/office/how-to-correct-a-div-0-error-3a5a18a9-8d80-4ebb-a908-39e759a009a5
     * <br>
     * This error indicates a divide by zero within a formula.
     */
    DIV0("#DIV/0!", 2),

    /**
     * https://support.microsoft.com/en-us/office/how-to-correct-a-value-error-15e1b616-fbf2-4147-9c0b-0a11a20e409e
     * <br>
     * General purpose error with a value.
     */
    VALUE("#VALUE!", 3),

    /**
     * https://support.microsoft.com/en-us/office/how-to-correct-a-ref-error-822c8e46-e610-4d02-bf29-ec4b8c5ff4be
     * <br>
     * This error indicates a reference to a cell that was deleted.
     */
    REF("#REF!", 4),

    /**
     * A <code>!NAME</code> representing a missing cell or label reference that may be converted to a ZERO.
     */
    NAME("#NAME?", 5),

    /**
     * A <code>!NAME</code> representing a missing cell or label reference that was converted to a {@link String}.
     * During formatting as text it will not be converted to a value of zero.
     */
    NAME_STRING("#NAME?", 5),

    /**
     * https://support.microsoft.com/en-us/office/how-to-correct-a-num-error-f5193bfc-4400-43f4-88c4-8e1dcca0428b
     * <br>
     * An incorrect number literal within a formula.
     */
    NUM("#NUM!", 6),

    /**
     * https://support.microsoft.com/en-us/office/na-function-5469c2d1-a90c-4fb5-9bbc-64bd9bb6b47c
     * <br>
     * An error that indicates a value is NOT AVAILABLE
     */
    NA("#N/A", 7),

    ERROR("#ERROR", 8),

    SPILL("#SPILL!", 9),

    CALC("#CALC!", 14);

    SpreadsheetErrorKind(final String text,
                         final int value) {
        this.text = text;
        this.value = value;
    }

    @Override
    public String text() {
        return this.text;
    }

    private final String text;

    /**
     * This value is return by ERROR.TYPE expression.
     */
    public int value() {
        return this.value;
    }

    private final int value;

    /**
     * Returns a {@link SpreadsheetError} with this {@link SpreadsheetErrorKind} but no message or value.
     */
    public SpreadsheetError toError() {
        return this.setMessage("");
    }

    public SpreadsheetError setMessage(final String message) {
        return this.setMessageAndValue(
                message,
                null
        );
    }

    public SpreadsheetError setMessageAndValue(final String message,
                                               final Object value) {
        return SpreadsheetError.with(
                this,
                message,
                Optional.ofNullable(value)
        );
    }

    @Override
    public String toString() {
        return this.text();
    }

    /**
     * Attempts to translate the given @link Throwable} into the a {@link SpreadsheetError}.
     */
    public static SpreadsheetError translate(final Throwable cause) {
        Objects.requireNonNull(cause, "cause");

        final SpreadsheetError error;
        if (cause instanceof HasSpreadsheetError) {
            final HasSpreadsheetError has = (HasSpreadsheetError) cause;
            error = has.spreadsheetError();
        } else {
            error = translate0(cause);
        }

        return error;
    }

    private static SpreadsheetError translate0(final Throwable cause) {
        Throwable translate = cause;

        if (cause instanceof ExpressionEvaluationException) {
            translate = cause.getCause();
            if (null == translate) {
                translate = cause;
            }
        }

        return translate1(translate);
    }

    private static SpreadsheetError translate1(final Throwable cause) {
        final SpreadsheetErrorKind kind;
        String message = cause.getMessage();
        Object value = null;

        SpreadsheetError error = null;

        do {
            if (cause instanceof HasSpreadsheetErrorKind) {
                final HasSpreadsheetErrorKind has = (HasSpreadsheetErrorKind) cause;
                kind = has.spreadsheetErrorKind();

                if (cause instanceof HasExpressionReference) {
                    final HasExpressionReference hasExpressionReference = (HasExpressionReference) cause;
                    value = hasExpressionReference.expressionReference();
                }
                break;
            }

            // REF!
            if (cause instanceof HasExpressionReference) {
                kind = NAME;

                final HasExpressionReference has = (HasExpressionReference) cause;
                value = has.expressionReference();
                break;
            }

            // Trying to divide by 0
            if (cause instanceof ArithmeticException) {
                kind = DIV0;
                break;
            }

            // #VALUE! 	The wrong type of operand or expression argument is used
            if (cause instanceof ClassCastException) {
                kind = VALUE;
                message = SpreadsheetErrorKindClassCastExceptionMessage.extractClassCastExceptionMessage(message);
                break;
            }

            // #VALUE! 	The wrong type of operand or expression argument is used
            if (cause instanceof ConversionException) {
                kind = VALUE;

                final ConversionException conversionException = (ConversionException) cause;
                message = "Cannot convert " + CharSequences.quoteIfChars(conversionException.value()) + " to " + conversionException.type().getSimpleName();
                value = conversionException.value();
                break;
            }

            // #NUM! 	A formula has invalid numeric data for the type of operation
            if (cause instanceof NullPointerException ||
                    cause instanceof IllegalArgumentException) {
                kind = VALUE;
                break;
            }

            // #ERROR! 	Text in the formula is not recognized
            if (cause instanceof ParserException) {
                kind = ERROR;
                break;
            }

            // unknown function name
            if (cause instanceof UnknownExpressionFunctionException) {
                kind = NAME;

                final UnknownExpressionFunctionException unknown = (UnknownExpressionFunctionException) cause;
                error = SpreadsheetError.functionNotFound(
                        unknown.name()
                );
                break;
            }

            kind = VALUE;
        } while (false);

        return null != error ?
                error :
                kind.setMessageAndValue(
                        CharSequences.nullToEmpty(message).toString(),
                        value
                );
    }

    public static SpreadsheetErrorKind withValue(final int value) {
        return Arrays.stream(values())
                .filter(k -> k.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value=" + value));
    }
}
