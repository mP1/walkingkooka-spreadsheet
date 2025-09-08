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

import org.junit.jupiter.api.Test;
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.spreadsheet.reference.CanReplaceReferencesTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContexts;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;
import walkingkooka.validation.ValidationValueTypeName;

import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormulaTest implements ClassTesting2<SpreadsheetFormula>,
    CanBeEmptyTesting,
    CanReplaceReferencesTesting<SpreadsheetFormula>,
    HashCodeEqualsDefinedTesting2<SpreadsheetFormula>,
    JsonNodeMarshallingTesting<SpreadsheetFormula>,
    PatchableTesting<SpreadsheetFormula>,
    ToStringTesting<SpreadsheetFormula>,
    HasTextTesting,
    TreePrintableTesting {

    private final static String TEXT = "1+2";
    private final static String EXPRESSION = "1+2";
    private final static Double EXPRESSION_VALUE = 3.0;
    private final static ValidationValueTypeName VALUE_TYPE = ValidationValueTypeName.TEXT;
    private final static String VALUE = "\"Value444\"";
    private final static String ERROR = "Message #1";

    private final static String DIFFERENT_TEXT = "99+99";
    private final static ValidationValueTypeName DIFFERENT_VALUE_TYPE = ValidationValueTypeName.TIME;

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    // with.............................................................................................................

    @Test
    public void testWithNullExpressionFails() {
        assertThrows(
            NullPointerException.class,
            () -> formula(null)
        );
    }

    @Test
    public void testWithNullMaxTextLengthFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> formula(
                CharSequences.repeating(' ', 8192)
                    .toString()
            )
        );
    }

    @Test
    public void testNotEmpty() {
        final SpreadsheetFormula formula = this.createObject();
        this.textAndCheck(
            formula,
            TEXT
        );
        this.expressionAndCheck(formula);
        this.valueAndCheck(formula);
        this.valueTypeAndCheck(formula);
        this.errorAndCheck(formula);

        this.isEmptyAndCheck(
            formula,
            false
        );
    }

    @Test
    public void testWithEmpty() {
        final String text = "";
        final SpreadsheetFormula formula = formula(text);
        this.textAndCheck(
            formula,
            text
        );

        this.isEmptyAndCheck(
            formula,
            true
        );
    }

    // SetText..........................................................................................................

    @Test
    public void testSetTextNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject().setText(null)
        );
    }

    @Test
    public void testSetTextMaxTextLengthFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createObject()
                .setText(
                    CharSequences.repeating(' ', 8192)
                        .toString()
                )
        );
    }

    @Test
    public void testSetTextSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
            formula,
            formula.setText(TEXT)
        );
    }

    @Test
    public void testSetTextDifferent() {
        this.setTextAndCheck("different");
    }

    private void setTextAndCheck(final String differentText) {
        final SpreadsheetFormula formula = this.createObject();
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);
        this.textAndCheck(different, differentText);

        this.isEmptyAndCheck(
            different,
            differentText.isEmpty()
        );
    }

    @Test
    public void testSetTextDifferent2() {
        final SpreadsheetFormula formula = this.createObject();

        final String differentText = "different";
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);

        this.textAndCheck(different, differentText);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetTextDifferentEmpty() {
        final SpreadsheetFormula formula = this.createObject();

        final String differentText = "different";
        final SpreadsheetFormula different = formula.setText(differentText);

        assertSame(
            SpreadsheetFormula.EMPTY,
            different.setText("")
        );
    }

    @Test
    public void testSetTextAfterSetExpression() {
        final SpreadsheetFormula formula = this.createObject()
            .setExpression(this.expression());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(formula, different);

        this.textAndCheck(different, DIFFERENT_TEXT);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetTextAfterSetExpressionSetValue() {
        final SpreadsheetFormula formula = this.createObject()
            .setExpression(this.expression())
            .setValue(this.value());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(different, DIFFERENT_TEXT);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    private void textAndCheck(final SpreadsheetFormula formula) {
        this.textAndCheck(
            formula,
            ""
        );
    }

    // SetToken.........................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetTokenNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setToken(null)
        );
    }

    @Test
    public void testSetTokenSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
            formula,
            formula.setToken(
                formula.token()
            )
        );
    }

    @Test
    public void testSetTokenDifferent() {
        final SpreadsheetFormula formula = this.createObject();

        final String differentText = "different!";
        final Optional<SpreadsheetFormulaParserToken> differentToken = this.token(differentText);
        final SpreadsheetFormula different = formula.setToken(differentToken);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(different, differentText);
        this.tokenAndCheck(different, differentToken);

        this.expressionAndCheck(different); // should also clear expression, value, error
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    private Optional<SpreadsheetFormulaParserToken> token() {
        return this.token(EXPRESSION);
    }

    private Optional<SpreadsheetFormulaParserToken> token(final String text) {
        return Optional.of(
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.textLiteral(
                        text,
                        text
                    )
                ),
                text
            )
        );
    }

    private void tokenAndCheck(final SpreadsheetFormula formula) {
        this.tokenAndCheck(
            formula,
            SpreadsheetFormula.NO_TOKEN
        );
    }

    private void tokenAndCheck(final SpreadsheetFormula formula,
                               final Optional<SpreadsheetFormulaParserToken> token) {
        this.checkEquals(
            token,
            formula.token(),
            "token"
        );
    }

    // SetExpression....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetExpressionNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setExpression(null)
        );
    }

    @Test
    public void testSetExpressionSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
            formula,
            formula.setExpression(
                formula.expression()
            )
        );
    }

    @Test
    public void testSetExpressionDifferent() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token());
        final Optional<Expression> differentExpression = Optional.of(
            Expression.value("different!")
        );
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            this.token()
        );
        this.expressionAndCheck(
            different,
            differentExpression
        );
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetExpressionDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token())
            .setExpression(this.expression());
        final Optional<Expression> differentExpression = SpreadsheetFormula.NO_EXPRESSION;
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            this.token()
        );
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetExpressionDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token())
            .setExpression(this.expression())
            .setValue(this.value());

        final Optional<Expression> differentExpression = Optional.of(
            Expression.value("different!")
        );
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            this.token()
        );
        this.expressionAndCheck(
            different,
            differentExpression
        );
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    private Optional<Expression> expression() {
        return this.expression(EXPRESSION);
    }

    private Optional<Expression> expression(final String text) {
        return Optional.of(
            Expression.value(text)
        );
    }

    private void expressionAndCheck(final SpreadsheetFormula formula,
                                    final Optional<Expression> expression) {
        this.checkEquals(
            expression,
            formula.expression(),
            "expression"
        );
    }

    private void expressionAndCheck(final SpreadsheetFormula formula) {
        this.expressionAndCheck(
            formula,
            SpreadsheetFormula.NO_EXPRESSION
        );
    }

    // SetValueType................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetValueTypeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setValueType(null)
        );
    }

    @Test
    public void testSetValueTypeSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
            formula,
            formula.setValueType(
                formula.valueType()
            )
        );
    }

    @Test
    public void testSetValueTypeDifferent() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token());
        final Optional<ValidationValueTypeName> differentValueType = Optional.of(DIFFERENT_VALUE_TYPE);
        final SpreadsheetFormula different = formula.setValueType(differentValueType);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(
            different,
            differentValueType
        );
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetValueTypeDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
            .setValueType(this.valueType());
        final Optional<ValidationValueTypeName> differentValueType = Optional.of(DIFFERENT_VALUE_TYPE);
        final SpreadsheetFormula different = formula.setValueType(differentValueType);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different, DIFFERENT_VALUE_TYPE);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetValueType() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token())
            .setExpression(this.expression())
            .setValueType(this.valueType());

        final Optional<ValidationValueTypeName> differentValueType = Optional.of(DIFFERENT_VALUE_TYPE);
        final SpreadsheetFormula different = formula.setValueType(differentValueType);
        assertNotSame(formula, different);

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(
            different,
            formula.expression()
        );
        this.valueTypeAndCheck(
            different,
            differentValueType
        );
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    private Optional<ValidationValueTypeName> valueType() {
        return this.valueType(VALUE_TYPE);
    }

    private Optional<ValidationValueTypeName> valueType(final ValidationValueTypeName type) {
        return Optional.of(type);
    }

    private void valueTypeAndCheck(final SpreadsheetFormula formula,
                                   final ValidationValueTypeName type) {
        this.valueTypeAndCheck(
            formula,
            Optional.of(type)
        );
    }

    private void valueTypeAndCheck(final SpreadsheetFormula formula,
                                   final Optional<ValidationValueTypeName> type) {
        this.checkEquals(
            type,
            formula.valueType(),
            "valueType"
        );
    }

    private void valueTypeAndCheck(final SpreadsheetFormula formula) {
        this.valueTypeAndCheck(
            formula,
            SpreadsheetFormula.NO_VALUE_TYPE
        );
    }

    // SetValue.........................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetValueNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setValue(null)
        );
    }

    @Test
    public void testSetValueSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
            formula,
            formula.setValue(
                formula.value()
            )
        );
    }

    @Test
    public void testSetValueDifferent() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token());
        final Optional<Object> differentValue = Optional.of(
            "different!"
        );
        final SpreadsheetFormula different = formula.setValue(differentValue);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            formula.token()
        );
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different, differentValue);
        this.valueAndCheck(
            different,
            differentValue
        );
        this.errorAndCheck(different);
    }

    @Test
    public void testSetValueDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
            .setValue(this.value());
        final Optional<Object> differentValue = SpreadsheetFormula.NO_VALUE;
        final SpreadsheetFormula different = formula.setValue(differentValue);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetValue() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token())
            .setExpression(this.expression())
            .setValue(this.value());

        final Optional<Object> differentValue = Optional.of("different!");
        final SpreadsheetFormula different = formula.setValue(differentValue);
        assertNotSame(formula, different);

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            formula.token()
        );
        this.expressionAndCheck(
            different,
            formula.expression()
        );
        this.valueTypeAndCheck(different);
        this.valueAndCheck(
            different,
            differentValue
        );
        this.errorAndCheck(different);
    }

    private Optional<Object> value() {
        return this.value(EXPRESSION_VALUE);
    }

    private Optional<Object> value(final Object value) {
        return Optional.of(value);
    }

    private void valueAndCheck(final SpreadsheetFormula formula,
                               final Optional<Object> value) {
        this.checkEquals(
            value,
            formula.value(),
            formula::toString
        );
    }

    private void valueAndCheck(final SpreadsheetFormula formula) {
        this.valueAndCheck(
            formula,
            SpreadsheetFormula.NO_VALUE
        );
    }

    // setError.........................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetErrorNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setError(null)
        );
    }

    @Test
    public void testSetErrorSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
            formula,
            formula.setError(formula.error())
        );
    }

    @Test
    public void testSetErrorDifferent() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token());
        final Optional<SpreadsheetError> differentError = Optional.of(
            SpreadsheetError.cycle(SpreadsheetSelection.A1)
        );
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            this.token()
        );
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(
            different,
            differentError
        );
    }

    @Test
    public void testSetErrorDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token())
            .setError(this.error());
        final Optional<SpreadsheetError> differentError = SpreadsheetFormula.NO_ERROR;
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(
            formula,
            different
        );

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            this.token()
        );
        this.expressionAndCheck(different);
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetErrorDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
            .setToken(this.token())
            .setExpression(this.expression())
            .setError(this.error());

        final Optional<SpreadsheetError> differentError = this.error("Different error");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.textAndCheck(
            different,
            TEXT
        );
        this.tokenAndCheck(
            different,
            this.token()
        );
        this.expressionAndCheck(different, formula.expression());
        this.valueTypeAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different, differentError);
    }

    private Optional<SpreadsheetError> error() {
        return this.error(ERROR);
    }

    private Optional<SpreadsheetError> error(final String error) {
        return Optional.of(
            SpreadsheetErrorKind.VALUE.setMessage(error)
        );
    }

    private void errorAndCheck(final SpreadsheetFormula formula) {
        this.checkEquals(
            SpreadsheetFormula.NO_ERROR,
            formula.error(),
            () -> "formula shouldnt have error=" + formula
        );
    }

    private void errorAndCheck(final SpreadsheetFormula formula,
                               final Optional<SpreadsheetError> error) {
        this.checkEquals(
            error,
            formula.error(),
            () -> "formula: " + formula
        );
    }

    // replaceErrorWithValueIfPossible..................................................................................

    @Test
    public void testSetValueIfErrorNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.EMPTY
                .setValueIfError(null)
        );
    }

    @Test
    public void testSetValueIfErrorWhenMissingCellBecomesZero() {
        final ExpressionNumberKind kind = ExpressionNumberKind.BIG_DECIMAL;
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");


        this.setValueIfErrorAndCheck(
            formula.setValue(
                Optional.of(
                    SpreadsheetError.selectionNotFound(SpreadsheetSelection.A1)
                )
            ),
            new FakeSpreadsheetEngineContext() {
                @Override
                public SpreadsheetMetadata spreadsheetMetadata() {
                    return SpreadsheetMetadata.EMPTY.defaults()
                        .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, kind);
                }
            },
            formula.setValue(
                Optional.of(
                    kind.zero()
                )
            )
        );
    }

    @Test
    public void testSetValueIfErrorWhenNotMissingCell() {
        this.setValueIfErrorAndCheck(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(
                    "abc"
                )
            )
        );
    }

    private void setValueIfErrorAndCheck(final SpreadsheetFormula formula) {
        this.setValueIfErrorAndCheck(
            formula,
            SpreadsheetEngineContexts.fake(),
            formula
        );
    }

    private void setValueIfErrorAndCheck(final SpreadsheetFormula formula,
                                         final SpreadsheetEngineContext context,
                                         final Object expected) {
        this.checkEquals(
            expected,
            formula.setValueIfError(context),
            () -> formula + " setValueIfError"
        );
    }

    // errorOrValue.....................................................................................................

    @Test
    public void testErrorOrValueWhenEmpty() {
        this.errorOrValueAndCheck(
            SpreadsheetFormula.EMPTY,
            Optional.empty()
        );
    }

    @Test
    public void testErrorOrValueWhenError() {
        final Optional<SpreadsheetError> error = Optional.of(
            SpreadsheetErrorKind.VALUE.setMessage("error123")
        );

        this.errorOrValueAndCheck(
            SpreadsheetFormula.EMPTY.setError(error),
            error
        );
    }

    @Test
    public void testErrorOrValueWhenValue() {
        final Optional<Object> value = Optional.of(123);

        this.errorOrValueAndCheck(
            SpreadsheetFormula.EMPTY.setValue(value),
            value
        );
    }

    @Test
    public void testErrorOrValueWhenErrorAndValue() {
        final Optional<SpreadsheetError> error = Optional.of(
            SpreadsheetErrorKind.VALUE.setMessage("error123")
        );

        this.errorOrValueAndCheck(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(123)
            ).setError(error),
            error
        );
    }

    private void errorOrValueAndCheck(final SpreadsheetFormula formula,
                                      final Optional<?> expected) {
        this.checkEquals(
            expected,
            formula.errorOrValue(),
            formula::toString
        );
    }

    // clear............................................................................................................

    @Test
    public void testClearNonEmptyText() {
        final SpreadsheetFormula formula = formula("1+99");
        final SpreadsheetFormula cleared = formula.clear();
        assertSame(
            formula,
            cleared
        );
        this.clearAndCheck(cleared);
    }

    @Test
    public void testClearNonEmptyTextAndToken() {
        final SpreadsheetFormula formula = formula("1+99")
            .setToken(this.token());
        final SpreadsheetFormula cleared = formula.clear();
        assertSame(
            formula,
            cleared
        );
        this.clearAndCheck(cleared);
    }

    @Test
    public void testClearNonEmptyTextTokenExpression() {
        final SpreadsheetFormula formula = formula("1+99")
            .setToken(this.token())
            .setExpression(this.expression());
        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(
            formula,
            cleared
        );

        this.clearAndCheck(cleared);
    }

    @Test
    public void testClearNonEmptyTextTokenValue() {
        final SpreadsheetFormula formula = formula("1+99")
            .setToken(this.token())
            .setExpression(this.expression());
        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(
            formula,
            cleared
        );

        this.clearAndCheck(cleared);
    }

    private void clearAndCheck(final SpreadsheetFormula formula) {
        this.expressionAndCheck(formula);
        this.valueAndCheck(formula);
        this.errorAndCheck(formula);
    }

    @Test
    public void testClearEmptyTextAndValue() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setValue(
            Optional.of(123)
        );

        final SpreadsheetFormula cleared = formula.clear();
        assertSame(
            formula,
            cleared
        );
    }

    @Test
    public void testClearWhenValue() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setValue(this.value());

        final SpreadsheetFormula cleared = formula.clear();
        assertSame(
            formula,
            cleared
        );

        this.textAndCheck(cleared);
        this.tokenAndCheck(cleared);
        this.expressionAndCheck(cleared);
        this.valueTypeAndCheck(cleared);
        this.valueAndCheck(
            cleared,
            this.value()
        );
        this.errorAndCheck(cleared);
    }

    @Test
    public void testClearWhenValueAndError() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setValue(this.value())
            .setError(this.error());

        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(
            formula,
            cleared
        );

        this.textAndCheck(cleared);
        this.tokenAndCheck(cleared);
        this.expressionAndCheck(cleared);
        this.valueTypeAndCheck(cleared);
        this.valueAndCheck(
            cleared,
            this.value()
        );
        this.errorAndCheck(cleared);
    }

    // isPure...........................................................................................................

    @Test
    public void testIsPureWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.EMPTY.isPure(null)
        );
    }

    @Test
    public void testIsPureMissingExpressionFalse() {
        this.isPureAndCheck(
            SpreadsheetFormula.EMPTY.setText("Hello"),
            ExpressionEvaluationContexts.fake(),
            false
        );
    }

    @Test
    public void testIsPureWithPureExpression() {
        final Expression expression = Expression.value("Hello");

        this.isPureAndCheck(
            SpreadsheetFormula.EMPTY.setText("Hello")
                .setExpression(
                    Optional.of(expression)
                ),
            ExpressionEvaluationContexts.fake(),
            true
        );
    }

    @Test
    public void testIsPureWithPureFunction() {
        final ExpressionFunctionName functionName = SpreadsheetExpressionFunctions.name("Hello");

        this.isPureAndCheck(
            SpreadsheetFormula.EMPTY.setText("Hello")
                .setExpression(
                    Optional.of(
                        Expression.namedFunction(functionName)
                    )
                ),
            new FakeExpressionEvaluationContext() {
                @Override
                public boolean isPure(final ExpressionFunctionName n) {
                    return functionName.equals(n);
                }
            },
            true
        );
    }

    @Test
    public void testIsPureWithImpureFunction() {
        this.isPureAndCheck(
            SpreadsheetFormula.EMPTY.setText("Hello")
                .setExpression(
                    Optional.of(
                        Expression.namedFunction(
                            SpreadsheetExpressionFunctions.name("Hello")
                        )
                    )
                ),
            new FakeExpressionEvaluationContext() {
                @Override
                public boolean isPure(final ExpressionFunctionName n) {
                    return false;
                }
            },
            false
        );
    }

    private void isPureAndCheck(final SpreadsheetFormula formula,
                                final ExpressionPurityContext context,
                                final boolean expected) {
        this.checkEquals(
            expected,
            formula.isPure(context)
        );
    }

    // parse............................................................................................................

    @Test
    public void testParseNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.parse(
                null, // text
                Parsers.fake(), // parser
                SpreadsheetParserContexts.fake() // context
            )
        );
    }

    @Test
    public void testParseNullParserFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.parse(
                TextCursors.fake(), // text
                null, // parser
                SpreadsheetParserContexts.fake() // context
            )
        );
    }

    @Test
    public void testParseNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.parse(
                TextCursors.fake(), // text
                Parsers.fake(), // parser
                null // context
            )
        );
    }

    @Test
    public void testParseEmpty() {
        this.parseAndCheck(
            "",
            SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy")
                .parser(),
            SpreadsheetFormula.EMPTY
        );
    }

    @Test
    public void testParseOnlyWhitespaceWithDateParser() {
        final String text = "   ";

        this.parseAndCheck(
            text,
            SpreadsheetPattern.parseDateParsePattern(text)
                .parser(),
            SpreadsheetFormula.EMPTY
                .setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.date(
                            Lists.of(
                                SpreadsheetFormulaParserToken.textLiteral(
                                    text,
                                    text
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseInvalidParsePattern() {
        final String text = "=2A";

        this.parseAndCheck(
            text,
            SpreadsheetPattern.parseNumberParsePattern("#")
                .parser(),
            SpreadsheetFormula.EMPTY.setText(text)
                .setValue(
                    Optional.of(
                        SpreadsheetErrorKind.ERROR.setMessage("Invalid character \'=\' at 0 expected \"#\"")
                    )
                )
        );
    }

    @Test
    public void testParseInvalidDateParsePattern() {
        this.parseAndCheck(
            "@",
            SpreadsheetPattern.parseDateParsePattern("dd/mmm/yyyy")
                .parser(),
            SpreadsheetFormula.EMPTY.setText("@")
                .setValue(
                    Optional.of(
                        SpreadsheetErrorKind.ERROR.setMessage("Invalid character \'@\' expected \"dd/mmm/yyyy\"")
                    )
                )
        );
    }

    @Test
    public void testParseInvalidExpression() {
        final String text = "=1@Bad2+3";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetMetadataTesting.METADATA_EN_AU.spreadsheetParser(
                    SpreadsheetMetadataTesting.SPREADSHEET_PARSER_PROVIDER,
                    SpreadsheetMetadataTesting.PROVIDER_CONTEXT
                )
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setValue(
                    Optional.of(
                        SpreadsheetErrorKind.ERROR.setMessage("Invalid character \'@\' at 2 expected \"\\\'\", [STRING] | EQUALS_EXPRESSION | VALUE")
                    )
                )
        );
    }

    @Test
    public void testParseDateWithDateParsePattern() {
        final String text = "31/12/1999";

        this.parseAndCheck(
            text,
            SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy")
                .parser(),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.date(
                            Lists.of(
                                SpreadsheetFormulaParserToken.dayNumber(
                                    31,
                                    "31"
                                ),
                                SpreadsheetFormulaParserToken.textLiteral(
                                    "/",
                                    "/"
                                ),
                                SpreadsheetFormulaParserToken.monthNumber(
                                    12,
                                    "12"
                                ),
                                SpreadsheetFormulaParserToken.textLiteral(
                                    "/",
                                    "/"
                                ),
                                SpreadsheetFormulaParserToken.year(
                                    1999,
                                    "1999"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseStringWithValueParser() {
        final String text = "'Hello";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                Parsers.never()
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.text(
                            Lists.of(
                                SpreadsheetFormulaParserToken.apostropheSymbol(
                                    "'",
                                    "'"
                                ),
                                SpreadsheetFormulaParserToken.textLiteral(
                                    "Hello",
                                    "Hello"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithNumbers() {
        final String text = "=1+2";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.addition(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("1", "1")
                                            ),
                                            "1"
                                        ),
                                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("2", "2")
                                            ),
                                            "2"
                                        )
                                    ),
                                    "1+2"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithNumbersWithDecimals() {
        final String text = "=1.5+2";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.addition(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("1", "1"),
                                                SpreadsheetFormulaParserToken.decimalSeparatorSymbol(".", "."),
                                                SpreadsheetFormulaParserToken.digits("5", "5")
                                            ),
                                            "1.5"
                                        ),
                                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("2", "2")
                                            ),
                                            "2"
                                        )
                                    ),
                                    "1.5+2"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithNumberPercent() {
        final String text = "=1+200%";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.addition(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("1", "1")
                                            ),
                                            "1"
                                        ),
                                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("200", "200"),
                                                SpreadsheetFormulaParserToken.percentSymbol("%", "%")
                                            ),
                                            "200%"
                                        )
                                    ),
                                    "1+200%"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithNumberPercentPercent() {
        final String text = "=100%%+2";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.addition(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("100", "100"),
                                                SpreadsheetFormulaParserToken.percentSymbol("%%", "%%")
                                            ),
                                            "100%%"
                                        ),
                                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("2", "2")
                                            ),
                                            "2"
                                        )
                                    ),
                                    "100%%+2"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithNumbersWithDecimalsAndCustomDecimalSeparator() {
        final String text = "=1@5+2";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            this.parserContext(
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '-',
                        '+',
                        '0',
                        "$",
                        '@', // decimalSeparator
                        "E",
                        ',', // groupSeparator
                        "Infinity",
                        '.',
                        "NAN",
                        '%',
                        '^'
                    ),
                    LOCALE,
                    MathContext.DECIMAL32
                )
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.addition(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("1", "1"),
                                                SpreadsheetFormulaParserToken.decimalSeparatorSymbol("@", "@"), // custom DECIMAL SEPARATOR
                                                SpreadsheetFormulaParserToken.digits("5", "5")
                                            ),
                                            "1@5"
                                        ),
                                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("2", "2")
                                            ),
                                            "2"
                                        )
                                    ),
                                    "1@5+2"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithNumbersWithDecimalsAndCustomDecimalSeparator2() {
        final String text = "=1,5+2";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            this.parserContext(
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '-',
                        '+',
                        '0',
                        "$",
                        ',', // decimalSeparator SWAPPED
                        "E",
                        '.', // groupSeparator
                        "Infinity",
                        ',',
                        "NAN",
                        '%',
                        '^'
                    ),
                    LOCALE,
                    MathContext.DECIMAL32
                )
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.addition(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("1", "1"),
                                                SpreadsheetFormulaParserToken.decimalSeparatorSymbol(",", ","), // custom DECIMAL SEPARATOR
                                                SpreadsheetFormulaParserToken.digits("5", "5")
                                            ),
                                            "1,5"
                                        ),
                                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                                        SpreadsheetFormulaParserToken.number(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.digits("2", "2")
                                            ),
                                            "2"
                                        )
                                    ),
                                    "1,5+2"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithValueSeparator() {
        final String text = "=concat(1,2)";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            this.parserContext(),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.namedFunction(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.functionName(
                                            SpreadsheetFunctionName.with("concat"),
                                            "concat"
                                        ),
                                        SpreadsheetFormulaParserToken.functionParameters(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.parenthesisOpenSymbol("(", "("),
                                                SpreadsheetFormulaParserToken.number(
                                                    Lists.of(
                                                        SpreadsheetFormulaParserToken.digits("1", "1")
                                                    ),
                                                    "1"
                                                ),
                                                SpreadsheetFormulaParserToken.valueSeparatorSymbol(",", ","),
                                                SpreadsheetFormulaParserToken.number(
                                                    Lists.of(
                                                        SpreadsheetFormulaParserToken.digits("2", "2")
                                                    ),
                                                    "2"
                                                ),
                                                SpreadsheetFormulaParserToken.parenthesisCloseSymbol(")", ")")
                                            ),
                                            "(1,2)"
                                        )
                                    ),
                                    "concat(1,2)"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    @Test
    public void testParseExpressionWithCustomValueSeparator() {
        final String text = "=concat(1;2)";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParsers.valueOrExpression(
                SpreadsheetParsers.parser(
                    Parsers.never(),
                    Optional.empty()
                )
            ),
            this.parserContext(
                DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                        '-',
                        '+',
                        '0',
                        "$",
                        ',', // decimalSeparator SWAPPED
                        "E",
                        '.', // groupSeparator
                        "Infinity",
                        ',',
                        "NAN",
                        '%',
                        '^'
                    ),
                    LOCALE,
                    MathContext.DECIMAL32
                ),
                ';' // custom value separator
            ),
            SpreadsheetFormula.EMPTY.setText(text)
                .setToken(
                    Optional.of(
                        SpreadsheetFormulaParserToken.expression(
                            Lists.of(
                                SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                                SpreadsheetFormulaParserToken.namedFunction(
                                    Lists.of(
                                        SpreadsheetFormulaParserToken.functionName(
                                            SpreadsheetFunctionName.with("concat"),
                                            "concat"
                                        ),
                                        SpreadsheetFormulaParserToken.functionParameters(
                                            Lists.of(
                                                SpreadsheetFormulaParserToken.parenthesisOpenSymbol("(", "("),
                                                SpreadsheetFormulaParserToken.number(
                                                    Lists.of(
                                                        SpreadsheetFormulaParserToken.digits("1", "1")
                                                    ),
                                                    "1"
                                                ),
                                                SpreadsheetFormulaParserToken.valueSeparatorSymbol(";", ";"),
                                                SpreadsheetFormulaParserToken.number(
                                                    Lists.of(
                                                        SpreadsheetFormulaParserToken.digits("2", "2")
                                                    ),
                                                    "2"
                                                ),
                                                SpreadsheetFormulaParserToken.parenthesisCloseSymbol(")", ")")
                                            ),
                                            "(1;2)"
                                        )
                                    ),
                                    "concat(1;2)"
                                )
                            ),
                            text
                        )
                    )
                )
        );
    }

    private void parseAndCheck(final String text,
                               final Parser<SpreadsheetParserContext> parser,
                               final SpreadsheetFormula expected) {
        this.parseAndCheck(
            text,
            parser,
            this.parserContext(),
            expected
        );
    }

    private void parseAndCheck(final String text,
                               final Parser<SpreadsheetParserContext> parser,
                               final SpreadsheetParserContext context,
                               final SpreadsheetFormula expected) {
        this.parseAndCheck(
            TextCursors.charSequence(text),
            parser,
            context,
            expected
        );
    }

    private void parseAndCheck(final TextCursor text,
                               final Parser<SpreadsheetParserContext> parser,
                               final SpreadsheetParserContext context,
                               final SpreadsheetFormula expected) {
        final TextCursorSavePoint save = text.save();
        text.end();
        final CharSequence textCharSequence = save.textBetween();
        save.restore();

        this.checkEquals(
            expected,
            SpreadsheetFormula.parse(
                text,
                parser,
                context
            ),
            () -> "parse " + CharSequences.quoteIfChars(textCharSequence)
        );
    }

    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");

    private SpreadsheetParserContext parserContext() {
        return this.parserContext(
            DecimalNumberContexts.american(MathContext.DECIMAL32)
        );
    }

    private SpreadsheetParserContext parserContext(final DecimalNumberContext decimalNumberContext) {
        return this.parserContext(
            decimalNumberContext,
            VALUE_SEPARATOR
        );
    }

    private final static char VALUE_SEPARATOR = ',';

    private SpreadsheetParserContext parserContext(final DecimalNumberContext decimalNumberContext,
                                                   final char valueSeparator) {
        return this.parserContext(
            DateTimeContexts.basic(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(LOCALE)
                ),
                LOCALE,
                1920,
                50,
                () -> {
                    throw new UnsupportedOperationException("now");
                }
            ),
            decimalNumberContext,
            valueSeparator
        );
    }

    private SpreadsheetParserContext parserContext(final DateTimeContext dateTimeContext,
                                                   final DecimalNumberContext decimalNumberContext,
                                                   final char valueSeparator) {
        return SpreadsheetParserContexts.basic(
            InvalidCharacterExceptionFactory.POSITION_EXPECTED,
            dateTimeContext,
            ExpressionNumberContexts.basic(
                ExpressionNumberKind.BIG_DECIMAL,
                decimalNumberContext
            ),
            valueSeparator
        );
    }

    // CanBeEmpty.......................................................................................................

    @Test
    public void testIsEmpty() {
        this.parseAndIsEmptyCheck(
            "",
            true
        );
    }

    @Test
    public void testIsEmptyWithOnlyWhitespace() {
        this.parseAndIsEmptyCheck(
            "  ",
            false
        );
    }

    @Test
    public void testIsEmptyWithIncompleteExpression() {
        this.parseAndIsEmptyCheck(
            "1+",
            false
        );
    }

    @Test
    public void testIsEmptyWithExpression() {
        this.parseAndIsEmptyCheck(
            "1+2",
            false
        );
    }

    @Test
    public void testIsEmptyWithExpression2() {
        this.parseAndIsEmptyCheck(
            " 1 + 2 + hello()",
            false
        );
    }

    private void parseAndIsEmptyCheck(final String text,
                                      final boolean expected) {
        this.isEmptyAndCheck(
            this.parseFormula(text),
            expected
        );
    }

    @Test
    public void testIsEmptyWhenTextEmptyAndNonEmptyValue() {
        this.isEmptyAndCheck(
            SpreadsheetFormula.EMPTY
                .setValue(
                    Optional.of("Hello")
                ),
            false
        );
    }

    @Test
    public void testIsEmptyWithValueType() {
        this.isEmptyAndCheck(
            SpreadsheetFormula.EMPTY
                .setValueType(
                    Optional.of(ValidationValueTypeName.TEXT)
                ),
            false
        );
    }

    // consumeSpreadsheetExpressionReferences...........................................................................

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithNullConsumerFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.EMPTY.consumeSpreadsheetExpressionReferences(null)
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesAbsent() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            SpreadsheetFormula.EMPTY
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithout() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+2"
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithLabel() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+Label123",
            SpreadsheetSelection.labelName("Label123")
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithLabel2() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+Label123+Label456",
            SpreadsheetSelection.labelName("Label123"),
            SpreadsheetSelection.labelName("Label456")
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithCell() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+A1",
            SpreadsheetSelection.A1
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithCell2() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+A1+B2",
            SpreadsheetSelection.A1,
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithCellRange() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+A1:B2",
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseCell("A1"),
            SpreadsheetSelection.parseCell("B2")
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithCellRange2() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+A1:B2+C3:D4+Label123",
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseCell("A1"),
            SpreadsheetSelection.parseCell("B2"),
            SpreadsheetSelection.parseCellRange("C3:D4"),
            SpreadsheetSelection.parseCell("C3"),
            SpreadsheetSelection.parseCell("D4"),
            SpreadsheetSelection.labelName("Label123")
        );
    }

    @Test
    public void testConsumeSpreadsheetExpressionReferencesWithCellRange3() {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            "=1+A1:B2+C3",
            SpreadsheetSelection.parseCellRange("A1:B2"),
            SpreadsheetSelection.parseCell("A1"),
            SpreadsheetSelection.parseCell("B2"),
            SpreadsheetSelection.parseCell("C3")
        );
    }

    private void consumeSpreadsheetExpressionReferencesAndCheck(final String formula,
                                                                final SpreadsheetExpressionReference... expected) {
        this.consumeSpreadsheetExpressionReferencesAndCheck(
            SpreadsheetFormula.parse(
                TextCursors.charSequence(formula),
                SpreadsheetFormulaParsers.valueOrExpression(
                    SpreadsheetPattern.parseNumberParsePattern("#")
                        .parser()
                ),
                this.parserContext()
            ),
            expected
        );
    }

    private void consumeSpreadsheetExpressionReferencesAndCheck(final SpreadsheetFormula formula,
                                                                final SpreadsheetExpressionReference... expected) {
        final List<SpreadsheetExpressionReference> consumer = Lists.array();
        formula.consumeSpreadsheetExpressionReferences(consumer::add);

        this.checkEquals(
            Lists.of(
                expected
            ),
            consumer,
            formula::toString
        );
    }

    // replaceReferences................................................................................................

    @Test
    public void testReplaceReferencesNoCells() {
        this.replaceReferencesAndCheck(
            parseFormula("=1+2"),
            (r) -> {
                throw new UnsupportedOperationException();
            }
        );
    }

    @Test
    public void testReplaceReferencesOnlyLabel() {
        this.replaceReferencesAndCheck(
            parseFormula("=1+Label123"),
            (r) -> {
                throw new UnsupportedOperationException();
            }
        );
    }

    @Test
    public void testReplaceReferencesWithCellReferenceThenNone() {
        this.replaceReferencesAndCheck(
            parseFormula("=A1"),
            (t) -> Optional.empty(),
            parseFormula("=A1")
                .setExpression(
                    Optional.of(
                        Expression.value(SpreadsheetError.selectionDeleted())
                    )
                )
        );
    }

    @Test
    public void testReplaceReferencesWithCellReference() {
        this.replaceReferencesAndCheck(
            "=1+A1",
            (t) -> Optional.of(
                t.add(1, 1)
            ),
            "=1+B2"
        );
    }

    @Test
    public void testReplaceReferencesWithSeveralCellReference() {
        this.replaceReferencesAndCheck(
            "=1+A1+2+B2",
            (t) -> Optional.of(
                t.add(1, 1)
            ),
            "=1+B2+2+C3"
        );
    }

    @Test
    public void testReplaceReferencesWithCellRange() {
        this.replaceReferencesAndCheck(
            "=1+A1:B2",
            (t) -> Optional.of(
                t.add(1, 1)
            ),
            "=1+B2:C3"
        );
    }

    @Test
    public void testReplaceReferencesWithCellRangeAndCell() {
        this.replaceReferencesAndCheck(
            "=1+A1:B2+D4",
            (t) -> Optional.of(
                t.add(1, 1)
            ),
            "=1+B2:C3+E5"
        );
    }

    @Test
    public void testReplaceReferencesWithCellRangeAndCellMixedAbsolutes() {
        this.replaceReferencesAndCheck(
            "=1+A1:$B$2+$D4",
            (t) -> Optional.of(
                t.add(1, 1)
            ),
            "=1+B2:$C$3+$E5"
        );
    }

    private void replaceReferencesAndCheck(final String formula,
                                           final Function<SpreadsheetCellReference, Optional<SpreadsheetCellReference>> mapper,
                                           final String expected) {
        this.replaceReferencesAndCheck(
            parseFormula(formula),
            mapper,
            parseFormula(expected)
        );
    }

    private SpreadsheetFormula parseFormula(final String text) {
        final SpreadsheetFormula formula = SpreadsheetFormula.parse(
            TextCursors.charSequence(text),
            SpreadsheetFormulaParsers.valueOrExpression(
                Parsers.never()
            ),
            this.parserContext()
        );

        return formula.setExpression(
            formula.token()
                .map(
                    t -> t.toExpression(
                        new FakeExpressionEvaluationContext() {
                            @Override
                            public ExpressionNumberKind expressionNumberKind() {
                                return EXPRESSION_NUMBER_KIND;
                            }
                        }
                    ).get()
                )
        );
    }

    @Override
    public SpreadsheetFormula createReplaceReference() {
        return this.createObject();
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testTreePrintText() {
        this.treePrintAndCheck(
            this.formula("1+2"),
            "Formula\n" +
                "  text:\n" +
                "    \"1+2\"\n"
        );
    }

    @Test
    public void testTreePrintTextToken() {
        this.treePrintAndCheck(
            this.formula("1+2")
                .setToken(this.token()),
            "Formula\n" +
                "  token:\n" +
                "    TextSpreadsheetFormula \"1+2\"\n" +
                "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n"
        );
    }

    @Test
    public void testTreePrintTextTokenExpression() {
        this.treePrintAndCheck(
            this.formula("1+2")
                .setToken(this.token())
                .setExpression(this.expression()),
            "Formula\n" +
                "  token:\n" +
                "    TextSpreadsheetFormula \"1+2\"\n" +
                "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                "  expression:\n" +
                "    ValueExpression \"1+2\" (java.lang.String)\n"
        );
    }

    @Test
    public void testTreePrintTextTokenValue() {
        this.treePrintAndCheck(
            this.formula("1+2")
                .setToken(this.token())
                .setExpression(this.expression())
                .setValue(this.value()),
            "Formula\n" +
                "  token:\n" +
                "    TextSpreadsheetFormula \"1+2\"\n" +
                "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                "  expression:\n" +
                "    ValueExpression \"1+2\" (java.lang.String)\n" +
                "  value:\n" +
                "    3.0\n"
        );
    }

    @Test
    public void testTreePrintTextTokenValueError() {
        this.treePrintAndCheck(
            this.formula("1+2")
                .setToken(this.token())
                .setExpression(this.expression())
                .setValue(this.value())
                .setError(this.error()),
            "Formula\n" +
                "  token:\n" +
                "    TextSpreadsheetFormula \"1+2\"\n" +
                "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                "  expression:\n" +
                "    ValueExpression \"1+2\" (java.lang.String)\n" +
                "  value:\n" +
                "    3.0\n" +
                "  error:\n" +
                "    #VALUE!\n" +
                "      \"Message #1\"\n"
        );
    }

    @Test
    public void testTreePrintTreeValueString() {
        this.treePrintAndCheck(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("Hello123")
            ),
            "Formula\n" +
                "  value:\n" +
                "    \"Hello123\"\n"
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/1893

    @Test
    public void testTreePrintTreeTextTokenValueImplementsTreePrintable() {
        this.treePrintAndCheck(
            this.formula("1+2")
                .setToken(this.token())
                .setExpression(this.expression())
                .setValue(
                    Optional.of(
                        new TreePrintable() {
                            @Override
                            public void printTree(final IndentingPrinter printer) {
                                printer.println("1111");
                                printer.println("2222");
                                printer.println("3333");
                            }
                        })
                ),
            "Formula\n" +
                "  token:\n" +
                "    TextSpreadsheetFormula \"1+2\"\n" +
                "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                "  expression:\n" +
                "    ValueExpression \"1+2\" (java.lang.String)\n" +
                "  value:\n" +
                "    1111\n" +
                "    2222\n" +
                "    3333\n"
        );
    }

    @Test
    public void testTreePrintTextValueType() {
        this.treePrintAndCheck(
            SpreadsheetFormula.EMPTY.setValueType(
                Optional.of(ValidationValueTypeName.TEXT)
            ),
            "Formula\n" +
                "  valueType:\n" +
                "    text\n"
        );
    }

    @Test
    public void testTreePrintTextError() {
        this.treePrintAndCheck(
            this.formula("=123/0")
                .setValue(
                    Optional.of(
                        SpreadsheetErrorKind.DIV0.toError()
                    )
                ),
            "Formula\n" +
                "  text:\n" +
                "    \"=123/0\"\n" +
                "  value:\n" +
                "    #DIV/0!\n"
        );
    }

    @Test
    public void testTreePrintError() {
        this.treePrintAndCheck(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(
                    SpreadsheetErrorKind.DIV0.toError()
                )
            ),
            "Formula\n" +
                "  value:\n" +
                "    #DIV/0!\n"
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentText() {
        checkNotEquals(
            this.createFormula(
                "99+88",
                this.token(),
                this.expression(),
                this.value()
            )
        );
    }

    @Test
    public void testEqualsDifferentToken() {
        checkNotEquals(
            this.createFormula(
                TEXT,
                this.token("different"),
                this.expression(),
                this.value()
            )
        );
    }

    @Test
    public void testEqualsDifferentExpression() {
        checkNotEquals(
            this.createFormula(
                TEXT,
                this.token(),
                this.expression("44"),
                this.value()
            )
        );
    }

    @Test
    public void testEqualsDifferentValueKind() {
        checkNotEquals(
            SpreadsheetFormula.EMPTY.setValueType(
                this.valueType()
            ),
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(DIFFERENT_VALUE_TYPE)
            )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        checkNotEquals(
            SpreadsheetFormula.EMPTY.setValue(
                this.value()
            ),
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("DifferentValue")
            )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetFormula() {
        this.checkNotEquals(
            this.formula("different")
        );
    }

    @Override
    public SpreadsheetFormula createObject() {
        return this.formula(TEXT);
    }

    private SpreadsheetFormula createFormula(final String formula,
                                             final Optional<SpreadsheetFormulaParserToken> token,
                                             final Optional<Expression> expression,
                                             final Optional<Object> value) {
        return this.formula(formula)
            .setToken(token)
            .setExpression(expression)
            .setValue(value);
    }

    // textPatch........................................................................................................

    @Test
    public void testTextPatchWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.textPatch(null)
        );
    }

    @Test
    public void testTextPatch() {
        final String text = "=1+2*3";

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.TEXT_PROPERTY,
                    JsonNode.string(text)
                ),
            SpreadsheetFormula.textPatch(text)
        );
    }

    // valuePatch.......................................................................................................

    @Test
    public void testValuePatchWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.valuePatch(
                null,
                JsonNodeMarshallContexts.fake()
            )
        );
    }

    @Test
    public void testValuePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.valuePatch(
                Optional.empty(),
                null
            )
        );
    }

    @Test
    public void testValuePatch() {
        final ExpressionNumber number = EXPRESSION_NUMBER_KIND.create(123);
        final JsonNodeMarshallContext marshallContext = JsonNodeMarshallContexts.basic();

        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_PROPERTY,
                    marshallContext.marshallWithType(number)
                ),
            SpreadsheetFormula.valuePatch(
                Optional.of(number),
                marshallContext
            )
        );
    }

    // valueTypePatch...................................................................................................

    @Test
    public void testValueTypePatchWithNullValueTypeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.valueTypePatch(
                null,
                JsonNodeMarshallContexts.fake()
            )
        );
    }

    @Test
    public void testValueTypePatchWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormula.valueTypePatch(
                Optional.empty(),
                null
            )
        );
    }

    @Test
    public void testValueTypePatchWithNotEmpty() {
        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_TYPE_PROPERTY,
                    JsonNode.string("text123")
                ),
            SpreadsheetFormula.valueTypePatch(
                Optional.of(
                    ValidationValueTypeName.with("text123")
                ),
                JsonNodeMarshallContexts.basic()
            )
        );
    }

    @Test
    public void testValueTypePatchWithEmpty() {
        this.checkEquals(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_TYPE_PROPERTY,
                    JsonNode.nullNode()
                ),
            SpreadsheetFormula.valueTypePatch(
                Optional.empty(),
                JsonNodeMarshallContexts.basic()
            )
        );
    }

    // patch............................................................................................................

    @Test
    public void testPatchEmptyObject() {
        this.patchAndCheck(
            this.createPatchable(),
            JsonNode.object()
        );
    }

    @Test
    public void testPatchSetInvalidProperty() {
        this.patchInvalidPropertyFails(
            this.formula("=1"),
            JsonNode.object()
                .set(
                    SpreadsheetFormula.ERROR_PROPERTY,
                    JsonNode.nullNode()
                ),
            SpreadsheetFormula.ERROR_PROPERTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testPatchTextSameText() {
        final String text = "=1+2*3";

        this.patchAndCheck(
            this.formula(text),
            SpreadsheetFormula.textPatch(text)
        );
    }

    @Test
    public void testPatchTextDifferentText() {
        final String text = "=1+2*3";

        this.patchAndCheck(
            this.formula("'Old"),
            SpreadsheetFormula.textPatch(text),
            this.formula(text)
        );
    }

    @Test
    public void testPatchValueWithNull() {
        final SpreadsheetFormula formula = this.formula("=1");

        this.patchAndCheck(
            formula,
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_PROPERTY,
                    JsonNode.nullNode()
                ),
            formula.setValue(
                Optional.empty()
            )
        );
    }

    @Test
    public void testPatchValueWithNonNull() {
        final SpreadsheetFormula formula = this.formula("=1");
        final String inputValue = "Value111";

        this.patchAndCheck(
            formula,
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_PROPERTY,
                    JsonNode.string(inputValue)
                ),
            formula.setValue(
                Optional.of(inputValue)
            )
        );
    }

    @Test
    public void testPatchValueWithNonNull2() {
        final SpreadsheetFormula formula = this.formula("=1");
        final AbsoluteUrl inputValue = Url.parseAbsolute("https://example.com/123");

        this.patchAndCheck(
            formula,
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(inputValue)
                ),
            formula.setValue(
                Optional.of(inputValue)
            )
        );
    }

    @Test
    public void testPatchValueTypeWithEmpty() {
        final SpreadsheetFormula formula = this.formula("=1");
        final Optional<ValidationValueTypeName> valueType = SpreadsheetFormula.NO_VALUE_TYPE;

        this.patchAndCheck(
            formula,
            SpreadsheetFormula.valueTypePatch(
                valueType,
                JsonNodeMarshallContexts.basic()
            ),
            formula.setValueType(valueType)
        );
    }

    @Test
    public void testPatchValueTypeWithNonEmpty() {
        final SpreadsheetFormula formula = this.formula("=1");
        final Optional<ValidationValueTypeName> valueType = Optional.of(
            ValidationValueTypeName.with("text123")
        );

        this.patchAndCheck(
            formula,
            SpreadsheetFormula.valueTypePatch(
                valueType,
                JsonNodeMarshallContexts.basic()
            ),
            formula.setValueType(valueType)
        );
    }

    // PatchableTesting.................................................................................................

    @Override
    public SpreadsheetFormula createPatchable() {
        return this.createObject();
    }

    @Override
    public JsonNode createPatch() {
        return JsonNode.object();
    }

    @Override
    public JsonNodeUnmarshallContext createPatchContext() {
        return JsonNodeUnmarshallContexts.basic(
            ExpressionNumberKind.BIG_DECIMAL,
            MathContext.UNLIMITED
        );
    }

    // HasUrlFragment...................................................................................................

    @Test
    public void testUrlFragment() {
        final String formula = "1+2+345";

        this.checkEquals(
            UrlFragment.with(formula),
            SpreadsheetFormula.EMPTY.setText(formula)
                .urlFragment()
        );
    }

    // helpers..........................................................................................................

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.EMPTY
            .setText(text);
    }

    // toString.........................................................................................................

    @Test
    public void testToStringWithValueTypeAndValue() {
        this.toStringAndCheck(
            this.createObject()
                .setValueType(
                    Optional.of(VALUE_TYPE)
                ).setValue(
                    Optional.of(123)
                ),
            "text 123"
        );
    }

    @Test
    public void testToStringWithValue() {
        this.toStringAndCheck(
            this.createObject()
                .setValue(
                    Optional.of(123)
                ),
            "1+2 123"
        );
    }

    // json.............................................................................................................

    @Test
    public void testUnmarshallBooleanFails() {
        this.unmarshallFails(
            JsonNode.booleanNode(true)
        );
    }

    @Test
    public void testUnmarshallNumberFails() {
        this.unmarshallFails(
            JsonNode.number(12)
        );
    }

    @Test
    public void testUnmarshallArrayFails() {
        this.unmarshallFails(
            JsonNode.array()
        );
    }

    @Test
    public void testUnmarshallStringFails() {
        this.unmarshallFails(
            JsonNode.string("fails")
        );
    }

    @Test
    public void testUnmarshallObjectEmpty() {
        this.unmarshallAndCheck(
            JsonNode.object(),
            SpreadsheetFormula.EMPTY
        );
    }

    @Test
    public void testUnmarshallText() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.TEXT_PROPERTY,
                    JsonNode.string(TEXT)
                ),
            this.formula(TEXT)
        );
    }

    @Test
    public void testUnmarshallTextAndToken() {
        final Optional<SpreadsheetFormulaParserToken> token = this.token();

        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.TEXT_PROPERTY,
                    JsonNode.string(TEXT)
                ).set(
                    SpreadsheetFormula.TOKEN_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(
                            token.get()
                        )
                ),
            this.formula(TEXT)
                .setToken(token)
        );
    }

    @Test
    public void testUnmarshallTextAndTokenAndDifferentTextIgnored() {
        final Optional<SpreadsheetFormulaParserToken> token = this.token();

        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.TEXT_PROPERTY,
                    JsonNode.string("Different text parse token")
                ).set(
                    SpreadsheetFormula.TOKEN_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(
                            token.get()
                        )
                ),
            SpreadsheetFormula.EMPTY
                .setToken(token)
        );
    }

    @Test
    public void testUnmarshallTextAndExpression() {
        final Optional<Expression> expression = this.expression();

        this.unmarshallAndCheck(
            JsonNode.object()
                .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                .set(
                    SpreadsheetFormula.EXPRESSION_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(
                            expression.get()
                        )
                ),
            this.formula(TEXT)
                .setExpression(expression)
        );
    }

    @Test
    public void testUnmarshallTextTokenAndExpression() {
        final Optional<SpreadsheetFormulaParserToken> token = this.token();
        final Optional<Expression> expression = this.expression();

        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.TEXT_PROPERTY,
                    JsonNode.string(TEXT)
                ).set(
                    SpreadsheetFormula.TOKEN_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(
                            token.get()
                        )
                ).set(
                    SpreadsheetFormula.EXPRESSION_PROPERTY,
                    this.marshallContext()
                        .marshallWithType(
                            expression.get()
                        )
                ),
            this.formula(TEXT)
                .setToken(token)
                .setExpression(expression)
        );
    }

    @Test
    public void testUnmarshallTextAndValue() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.TEXT_PROPERTY,
                    JsonNode.string(TEXT)
                ).set(
                    SpreadsheetFormula.VALUE_PROPERTY,
                    JsonNode.number(EXPRESSION_VALUE)
                ),
            this.formula(TEXT)
                .setValue(
                    Optional.of(EXPRESSION_VALUE)
                )
        );
    }

    @Test
    public void testUnmarshallValue() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_PROPERTY,
                    JsonNode.string(VALUE)
                ),
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(VALUE)
            )
        );
    }

    @Test
    public void testUnmarshallValueTypeAndValue() {
        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.VALUE_TYPE_PROPERTY,
                    JsonNode.string(VALUE_TYPE.value())
                ).set(
                    SpreadsheetFormula.VALUE_PROPERTY,
                    JsonNode.string(VALUE)
                ),
            SpreadsheetFormula.EMPTY.setValueType(
                Optional.of(VALUE_TYPE)
            ).setValue(
                Optional.of(VALUE)
            )
        );
    }

    @Test
    public void testUnmarshallTextAndError() {
        final SpreadsheetError error = SpreadsheetErrorKind.DIV0.setMessageAndValue(
            "Hello error message",
            123
        );

        this.unmarshallAndCheck(
            JsonNode.object()
                .set(
                    SpreadsheetFormula.TEXT_PROPERTY,
                    JsonNode.string(TEXT)
                ).set(
                    SpreadsheetFormula.ERROR_PROPERTY,
                    this.marshallContext()
                        .marshall(error)
                ),
            this.formula(TEXT)
                .setError(
                    Optional.of(error)
                )
        );
    }

    // marshall.........................................................................................................

    @Test
    public void testMarshallText() {
        this.marshallAndCheck(
            this.formula(TEXT),
            "{ \"text\": \"1+2\"}"
        );
    }

    @Test
    public void testMarshallTextAndToken() {
        this.marshallAndCheck(
            this.formula(TEXT)
                .setToken(this.token()),
            "{\n" +
                "  \"text\": \"1+2\",\n" +
                "  \"token\": {\n" +
                "    \"type\": \"text-spreadsheet-formula-parser-token\",\n" +
                "    \"value\": {\n" +
                "      \"value\": [\n" +
                "        {\n" +
                "          \"type\": \"text-literal-spreadsheet-formula-parser-token\",\n" +
                "          \"value\": {\n" +
                "            \"value\": \"1+2\",\n" +
                "            \"text\": \"1+2\"\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"text\": \"1+2\"\n" +
                "    }\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallTextTokenAndExpression() {
        this.marshallAndCheck(
            this.formula(TEXT)
                .setToken(this.token())
                .setExpression(this.expression()),
            "{\n" +
                "  \"text\": \"1+2\",\n" +
                "  \"token\": {\n" +
                "    \"type\": \"text-spreadsheet-formula-parser-token\",\n" +
                "    \"value\": {\n" +
                "      \"value\": [\n" +
                "        {\n" +
                "          \"type\": \"text-literal-spreadsheet-formula-parser-token\",\n" +
                "          \"value\": {\n" +
                "            \"value\": \"1+2\",\n" +
                "            \"text\": \"1+2\"\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"text\": \"1+2\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"expression\": {\n" +
                "    \"type\": \"value-expression\",\n" +
                "    \"value\": \"1+2\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    public void testMarshallTextAndValue() {
        this.marshallAndCheck(
            this.formula(TEXT)
                .setValue(
                    Optional.of(123L)
                ),
            JsonNode.object()
                .set(
                    JsonPropertyName.with("text"),
                    JsonNode.string("1+2")
                ).set(
                    JsonPropertyName.with("value"),
                    this.marshallContext()
                        .marshallWithType(123L)
                )
        );
    }

    @Test
    public void testMarshallTextAndValue2() {
        this.marshallAndCheck(
            this.formula(TEXT)
                .setValue(Optional.of("abc123")),
            "{ \"text\": \"1+2\", \"value\": \"abc123\"}"
        );
    }

    @Test
    public void testMarshallValueTypeAndValue() {
        this.marshallAndCheck(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of(VALUE_TYPE)
            ).setValue(
                Optional.of("abc123")
            ),
            "{ \"value\": \"abc123\"}"
        );
    }

    @Test
    public void testMarshallValue() {
        this.marshallAndCheck(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("abc123")
            ),
            "{ \"value\": \"abc123\"}"
        );
    }

    @Test
    public void testMarshallValueAndError() {
        this.marshallAndCheck(
            SpreadsheetFormula.EMPTY.setValue(
                Optional.of("abc123")
            ).setError(
                this.error()
            ),
            "{\n" +
                "  \"value\": \"abc123\",\n" +
                "  \"error\": {\n" +
                "    \"kind\": \"VALUE\",\n" +
                "    \"message\": \"Message #1\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Test
    @Override
    public void testMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    @Test
    public void testMarshallRoundtripTextAndValue() {
        this.marshallRoundTripTwiceAndCheck(
            this.formula(TEXT)
                .setValue(
                    Optional.of(123L)
                )
        );
    }

    @Test
    public void testMarshallRoundtripTextValueAndExpression() {
        this.marshallRoundTripTwiceAndCheck(
            this.formula(TEXT)
                .setValue(Optional.of(123L))
                .setExpression(this.expression())
        );
    }

    @Test
    public void testMarshallRoundtripTextAndValueWithError() {
        this.marshallRoundTripTwiceAndCheck(
            this.formula(TEXT)
                .setValue(
                    Optional.of(
                        SpreadsheetErrorKind.VALUE.setMessage("error message #1")
                    )
                )
        );
    }

    @Test
    public void testMarshallRoundtripTextAndError() {
        this.marshallRoundTripTwiceAndCheck(
            this.formula(TEXT)
                .setError(
                    Optional.of(
                        SpreadsheetErrorKind.VALUE.setMessage("error message #1")
                    )
                )
        );
    }

    @Override
    public SpreadsheetFormula createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetFormula unmarshall(final JsonNode jsonNode,
                                         final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormula.unmarshall(
            jsonNode,
            context
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormula> type() {
        return SpreadsheetFormula.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
