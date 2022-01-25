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

import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.tree.expression.HasExpressionReference;

import java.util.Objects;

/**
 * The type of {@link SpreadsheetError}.
 * <br>
 * https://exceljet.net/excel-functions/excel-errortype-function
 */
public enum SpreadsheetErrorKind implements HasText {

    NULL("#NULL!", 1),

    DIV0("#DIV/0!", 2),

    VALUE("#VALUE!", 3),

    REF("#REF!", 4),

    NAME("#NAME?", 5),

    NUM("#NUM!", 6),

    NA("#N/A", 7),

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
     * This value is return by ERROR.TYPE function.
     */
    public int value() {
        return this.value;
    }

    private final int value;

    public SpreadsheetError setMessage(final String message) {
        return SpreadsheetError.with(this, message);
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

        SpreadsheetErrorKind kind = null;

        do {
            if (cause instanceof HasSpreadsheetErrorKind) {
                final HasSpreadsheetErrorKind has = (HasSpreadsheetErrorKind) cause;
                kind = has.spreadsheetErrorKind();
                break;
            }

            // REF!
            if (cause instanceof HasExpressionReference) {
                kind = REF;
                break;
            }

            // Trying to divide by 0
            if (cause instanceof ArithmeticException) {
                kind = DIV0;
                break;
            }

            // #VALUE! 	The wrong type of operand or function argument is used
            if (cause instanceof ClassCastException) {
                kind = VALUE;
                break;
            }

            // #NUM! 	A formula has invalid numeric data for the type of operation
            if (cause instanceof NullPointerException ||
                    cause instanceof IllegalArgumentException) {
                kind = VALUE;
                break;
            }

            // #NAME? 	Text in the formula is not recognized
            if (cause instanceof ParserException) {
                kind = NAME;
                break;
            }

            kind = VALUE;
        } while (false);

        final String message = cause.getMessage();

        return SpreadsheetError.with(
                kind,
                CharSequences.nullToEmpty(message).toString()
        );
    }
}
