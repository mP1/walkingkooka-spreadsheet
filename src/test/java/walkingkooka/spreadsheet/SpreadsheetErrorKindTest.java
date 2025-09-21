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
import walkingkooka.InvalidCharacterException;
import walkingkooka.convert.ConversionException;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.store.MissingStoreException;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.cursor.parser.ParserException;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionEvaluationReferenceException;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionNumberContext;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;

import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorKindTest implements ParseStringTesting<SpreadsheetErrorKind>,
    ClassTesting<SpreadsheetErrorKind> {

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
            error,
            () -> "spreadsheetError: " + error
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
            SpreadsheetErrorKind.DIV0,
            "Division by zero"
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
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;

        this.translateAndCheck(
            new ExpressionEvaluationReferenceException(
                MESSAGE,
                cell
            ),
            SpreadsheetErrorKind.NAME,
            MESSAGE,
            cell
        );
    }

    @Test
    public void testTranslateMissingStoreException() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("B2");

        this.translateAndCheck(
            new MissingStoreException(cell),
            SpreadsheetErrorKind.NAME,
            "Cell not found: \"B2\"",
            cell
        );
    }

    @Test
    public void testTranslateArithmeticExceptionDivideByZeroBigDecimal() {
        final ExpressionEvaluationException thrown = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionNumberKind.BIG_DECIMAL.one()
                .divide(
                    ExpressionNumberKind.BIG_DECIMAL.zero(),
                    new FakeExpressionNumberContext() {
                        @Override
                        public MathContext mathContext() {
                            return MathContext.DECIMAL32;
                        }
                    }
                )
        );

        this.translateAndCheck(
            thrown,
            SpreadsheetErrorKind.DIV0,
            thrown.getMessage()
        );
    }

    @Test
    public void testTranslateArithmeticExceptionDivideByZeroDouble() {
        final ExpressionEvaluationException thrown = assertThrows(
            ExpressionEvaluationException.class,
            () -> ExpressionNumberKind.DOUBLE.one()
                .divide(
                    ExpressionNumberKind.DOUBLE.zero(),
                    new FakeExpressionNumberContext() {
                        @Override
                        public MathContext mathContext() {
                            return MathContext.DECIMAL32;
                        }
                    }
                )
        );

        this.translateAndCheck(
            thrown,
            SpreadsheetErrorKind.DIV0,
            thrown.getMessage()
        );
    }

    @Test
    public void testTranslateClassCastExceptionCustomMessage() {
        this.translateAndCheck(
            new ClassCastException(MESSAGE),
            SpreadsheetErrorKind.VALUE,
            MESSAGE
        );
    }

    // "walkingkooka.spreadsheet.SpreadsheetErrorKindTest cannot be cast to java.base/java.lang.Void"
    @Test
    public void testTranslateClassCastExceptionClassNameCannotBeCastoClassName() {
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
            "Failed to convert " + this.getClass().getSimpleName() + " to Void"
        );
    }

    // class walkingkooka.spreadsheet.SpreadsheetError cannot be cast to class walkingkooka.tree.expression.ExpressionNumber
    // (walkingkooka.spreadsheet.SpreadsheetError and walkingkooka.tree.expression.ExpressionNumber are in unnamed module of loader 'app')

    @Test
    public void testTranslateClassCastExceptionClassClassNameCannotBeCastToClasClassName() {
        this.translateAndCheck(
            new ClassCastException("class walkingkooka.spreadsheet.SpreadsheetError cannot be cast to class walkingkooka.tree.expression.ExpressionNumber(walkingkooka.spreadsheet.SpreadsheetError and walkingkooka.tree.expression.ExpressionNumber are in unnamed module of loader 'app')"),
            SpreadsheetErrorKind.VALUE,
            "Failed to convert SpreadsheetError to ExpressionNumber"
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
            "Cannot convert \"abc\" to ExpressionNumber",
            "abc"
        );
    }

    @Test
    public void testTranslateInvalidCharacterException() {
        final InvalidCharacterException ice = new InvalidCharacterException(
            "abc123",
            2
        );

        this.translateAndCheck(
            ice,
            SpreadsheetErrorKind.ERROR,
            ice.getMessage()
        );
    }

    @Test
    public void testTranslateParserException() {
        this.translateAndCheck(
            new ParserException(MESSAGE),
            SpreadsheetErrorKind.ERROR
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
    public void testTranslateUnknownExpressionFunctionException() {
        final ExpressionFunctionName badFunction = SpreadsheetExpressionFunctions.name("badFunction");

        this.translateAndCheck(
            new UnknownExpressionFunctionException(badFunction),
            SpreadsheetErrorKind.NAME,
            SpreadsheetError.functionNotFound(badFunction).message(),
            badFunction
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
        this.translateAndCheck0(
            cause,
            kind,
            MESSAGE,
            Optional.empty()
        );
    }

    private void translateAndCheck(final Throwable cause,
                                   final SpreadsheetErrorKind kind,
                                   final String message) {
        this.translateAndCheck0(
            cause,
            kind,
            message,
            Optional.empty()
        );
    }

    private void translateAndCheck(final Throwable cause,
                                   final SpreadsheetErrorKind kind,
                                   final String message,
                                   final Object value) {
        this.translateAndCheck0(
            cause,
            kind,
            message,
            Optional.of(value)
        );
    }

    private void translateAndCheck0(final Throwable cause,
                                    final SpreadsheetErrorKind kind,
                                    final String message,
                                    final Optional<?> value) {
        this.checkEquals(
            kind.setMessageAndValue(message, value.orElse(null)),
            SpreadsheetErrorKind.translate(cause),
            () -> "translate " + cause
        );
    }

    // toError............................................................................................................

    @Test
    public void testToError() {
        for (final SpreadsheetErrorKind kind : SpreadsheetErrorKind.values()) {
            final SpreadsheetError error = kind.toError();

            this.checkEquals(
                SpreadsheetError.with(
                    kind,
                    "",
                    Optional.empty()
                ),
                error
            );
        }
    }

    @Test
    public void testToErrorCached() {
        for (final SpreadsheetErrorKind kind : SpreadsheetErrorKind.values()) {
            final SpreadsheetError error = kind.toError();
            assertSame(
                error,
                kind.toError(),
                () -> kind + ".toError not cached"
            );
        }
    }

    // setMessage......................................................................................................

    @Test
    public void testSetMessage() {
        final String message = "123";

        for (final SpreadsheetErrorKind kind : SpreadsheetErrorKind.values()) {
            final SpreadsheetError error = kind.setMessage(message);

            this.checkEquals(kind, error.kind(), "kind");
            this.checkEquals(message, error.message(), "message");
            this.checkEquals(Optional.empty(), error.value(), "value");
        }
    }

    // withValue........................................................................................................

    @Test
    public void testWithValueUnknownValueFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetErrorKind.withValue(9999)
        );

        this.checkEquals(
            "Unknown value=9999",
            thrown.getMessage(),
            "message"
        );
    }

    @Test
    public void testWithValue() {
        for (final SpreadsheetErrorKind kind : SpreadsheetErrorKind.values()) {
            if (SpreadsheetErrorKind.NAME_STRING == kind) {
                continue;
            }

            this.checkEquals(
                kind,
                SpreadsheetErrorKind.withValue(kind.value())
            );
        }
    }

    // ParseString......................................................................................................

    @Test
    public void testParseHashDivSlashZeroExclamationMark() {
        this.parseStringAndCheck(
            "#DIV/0!",
            SpreadsheetErrorKind.DIV0
        );
    }

    @Test
    public void testParseEachValue() {
        for (final SpreadsheetErrorKind kind : SpreadsheetErrorKind.values()) {
            switch (kind) {
                case NAME_STRING:
                    break;
                case VALIDATION:
                    break;
                default:
                    this.parseStringAndCheck(
                        kind.text(),
                        kind
                    );
            }
        }
    }

    @Override
    public SpreadsheetErrorKind parseString(final String text) {
        return SpreadsheetErrorKind.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
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
