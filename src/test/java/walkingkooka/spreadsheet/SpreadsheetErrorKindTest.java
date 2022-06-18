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

import org.junit.jupiter.api.Test;
import walkingkooka.convert.ConversionException;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.store.SpreadsheetExpressionReferenceLoadStoreException;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionEvaluationReferenceException;
import walkingkooka.tree.expression.ExpressionNumber;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorKindTest implements ClassTesting<SpreadsheetErrorKind> {

    // translate.......................................................................................................

    @Test
    public void testTranslateNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetErrorKind.translate(null)
        );
    }

    @Test
    public void testTranslateSpreadsheetError() {
        final SpreadsheetError error = SpreadsheetErrorKind.VALUE.setMessage("Custom message 123");

        this.checkEquals(
                error,
                SpreadsheetErrorKind.translate(new SpreadsheetErrorConversionException(error))
        );
    }

    private final static String MESSAGE = "Hello 123";

    @Test
    public void testTranslateExpressionEvaluateExceptionWithoutCause() {
        this.translateAndCheck(
                new ExpressionEvaluationException(MESSAGE),
                SpreadsheetErrorKind.VALUE
        );
    }

    @Test
    public void testTranslateExpressionEvaluateExceptionWithArithmeticExceptionCause() {
        this.translateAndCheck(
                new ExpressionEvaluationException(
                        "ignored!",
                        new ArithmeticException(MESSAGE)
                ),
                SpreadsheetErrorKind.DIV0
        );
    }

    @Test
    public void testTranslateHasSpreadsheetErrorKindException() {
        for (final SpreadsheetErrorKind kind : SpreadsheetErrorKind.values()) {
            this.translateAndCheck(
                    new TestHasSpreadsheetErrorKindException(
                            MESSAGE,
                            kind
                    ),
                    kind
            );
        }
    }

    static class TestHasSpreadsheetErrorKindException extends Exception implements HasSpreadsheetErrorKind {

        private static final long serialVersionUID = 1L;

        TestHasSpreadsheetErrorKindException(final String message,
                                             final SpreadsheetErrorKind errorKind) {
            super(message);
            this.errorKind = errorKind;
        }

        @Override
        public SpreadsheetErrorKind spreadsheetErrorKind() {
            return this.errorKind;
        }

        private final SpreadsheetErrorKind errorKind;
    }

    @Test
    public void testTranslateHasExpressionReferenceException() {
        this.translateAndCheck(
                new ExpressionEvaluationReferenceException(
                        MESSAGE,
                        SpreadsheetSelection.parseCell("A1")
                ),
                SpreadsheetErrorKind.REF
        );
    }

    @Test
    public void testTranslateHasExpressionReferenceException2() {
        this.translateAndCheck(
                new SpreadsheetExpressionReferenceLoadStoreException(
                        MESSAGE,
                        SpreadsheetSelection.parseCell("B2")
                ),
                SpreadsheetErrorKind.REF
        );
    }

    @Test
    public void testTranslateArithmeticException() {
        this.translateAndCheck(
                new ArithmeticException(MESSAGE),
                SpreadsheetErrorKind.DIV0
        );
    }

    @Test
    public void testTranslateClassCastException() {
        ClassCastException thrown = null;
        try {
            final Object object = this;
            final Void voidVoid = (Void) object; // intended, want to capture the ClassCastException message.
        } catch (final ClassCastException expected) {
            thrown = expected;
        }

        this.translateAndCheck(
                new ClassCastException(thrown.getMessage()),
                SpreadsheetErrorKind.VALUE,
                "Failed to convert " + this.getClass().getSimpleName() + " to " + Void.class.getSimpleName()
        );
    }

    @Test
    public void testTranslateConversionException() {
        this.translateAndCheck(
                new ConversionException(
                        MESSAGE,
                        "abc",
                        ExpressionNumber.class
                ),
                SpreadsheetErrorKind.VALUE,
                "Cannot convert \"abc\" to ExpressionNumber"
        );
    }

    @Test
    public void testTranslateParserException() {
        this.translateAndCheck(
                new ParserException(MESSAGE),
                SpreadsheetErrorKind.NAME
        );
    }

    @Test
    public void testTranslateNullPointerException() {
        this.translateAndCheck(
                new NullPointerException(MESSAGE),
                SpreadsheetErrorKind.VALUE
        );
    }

    @Test
    public void testTranslateIllegalArgumentException() {
        this.translateAndCheck(
                new IllegalArgumentException(MESSAGE),
                SpreadsheetErrorKind.VALUE
        );
    }

    @Test
    public void testTranslateException() {
        this.translateAndCheck(
                new Exception(MESSAGE),
                SpreadsheetErrorKind.VALUE
        );
    }

    @Test
    public void testTranslateExceptionNullMessage() {
        this.translateAndCheck(
                new Exception(),
                SpreadsheetErrorKind.VALUE,
                ""
        );
    }

    @Test
    public void testTranslateExceptionEmptyMessage() {
        this.translateAndCheck(
                new Exception(""),
                SpreadsheetErrorKind.VALUE,
                ""
        );
    }

    private void translateAndCheck(final Throwable cause,
                                   final SpreadsheetErrorKind kind) {
        this.translateAndCheck(cause, kind, MESSAGE);
    }

    private void translateAndCheck(final Throwable cause,
                                   final SpreadsheetErrorKind kind,
                                   final String message) {
        this.checkEquals(
                kind.setMessage(message),
                SpreadsheetErrorKind.translate(cause),
                () -> "translate " + cause
        );
    }

    // setMessage......................................................................................................

    @Test
    public void testSetMessage() {
        final String message = "123";

        for (final SpreadsheetErrorKind kind : SpreadsheetErrorKind.values()) {
            final SpreadsheetError error = kind.setMessage(message);

            this.checkEquals(kind, error.kind(), "kind");
            this.checkEquals(message, error.value(), "message");
        }
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<SpreadsheetErrorKind> type() {
        return SpreadsheetErrorKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
