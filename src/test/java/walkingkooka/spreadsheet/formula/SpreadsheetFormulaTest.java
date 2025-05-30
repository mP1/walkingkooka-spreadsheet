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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContexts;
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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
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
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionPurityContext;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
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
    private final static ValidationValueTypeName INPUT_VALUE_TYPE = ValidationValueTypeName.TEXT;
    private final static String INPUT_VALUE = "\"InputValue444\"";
    private final static String ERROR = "Message #1";

    private final static String DIFFERENT_TEXT = "99+99";
    private final static ValidationValueTypeName DIFFERENT_INPUT_VALUE_TYPE = ValidationValueTypeName.TIME;

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
        this.expressionValueAndCheck(formula);
        this.inputValueAndCheck(formula);
        this.inputValueTypeAndCheck(formula);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetTextAfterSetExpressionSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setExpression(this.expression())
                .setExpressionValue(this.expressionValue());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(
                formula,
                different
        );

        this.textAndCheck(different, DIFFERENT_TEXT);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetExpressionDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setExpression(this.expression())
                .setExpressionValue(this.expressionValue());

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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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

    // SetExpressionValue...............................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetExpressionValueNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject()
                        .setExpressionValue(null)
        );
    }

    @Test
    public void testSetExpressionValueSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
                formula,
                formula.setExpressionValue(
                        formula.expressionValue()
                )
        );
    }

    @Test
    public void testSetExpressionValueDifferent() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token());
        final Optional<Object> differentExpressionValue = Optional.of(
                "different!"
        );
        final SpreadsheetFormula different = formula.setExpressionValue(differentExpressionValue);
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
        this.expressionValueAndCheck(
                different,
                differentExpressionValue
        );
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetExpressionValueDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setExpressionValue(this.expressionValue());
        final Optional<Object> differentExpressionValue = SpreadsheetFormula.NO_EXPRESSION_VALUE;
        final SpreadsheetFormula different = formula.setExpressionValue(differentExpressionValue);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetExpressionValueDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setExpression(this.expression())
                .setExpressionValue(this.expressionValue());

        final Optional<Object> differentExpressionValue = Optional.of("different!");
        final SpreadsheetFormula different = formula.setExpressionValue(differentExpressionValue);
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
                formula.expression()
        );
        this.expressionValueAndCheck(
                different,
                differentExpressionValue
        );
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    private Optional<Object> expressionValue() {
        return this.expressionValue(EXPRESSION_VALUE);
    }

    private Optional<Object> expressionValue(final Object value) {
        return Optional.of(value);
    }

    private void expressionValueAndCheck(final SpreadsheetFormula formula,
                                         final Optional<Object> value) {
        this.checkEquals(
                value,
                formula.expressionValue(),
                "expressionValue"
        );
    }

    private void expressionValueAndCheck(final SpreadsheetFormula formula) {
        this.expressionValueAndCheck(
                formula,
                SpreadsheetFormula.NO_EXPRESSION_VALUE
        );
    }

    // SetInputValueType................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetInputValueTypeNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject()
                        .setInputValueType(null)
        );
    }

    @Test
    public void testSetInputValueTypeSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
                formula,
                formula.setInputValueType(
                        formula.inputValueType()
                )
        );
    }

    @Test
    public void testSetInputValueTypeDifferent() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token());
        final Optional<ValidationValueTypeName> differentInputValueType = Optional.of(DIFFERENT_INPUT_VALUE_TYPE);
        final SpreadsheetFormula different = formula.setInputValueType(differentInputValueType);
        assertNotSame(
                formula,
                different
        );

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(
                different,
                differentInputValueType
        );
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetInputValueTypeDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
                .setInputValueType(this.inputValueType());
        final Optional<ValidationValueTypeName> differentInputValueType = Optional.of(DIFFERENT_INPUT_VALUE_TYPE);
        final SpreadsheetFormula different = formula.setInputValueType(differentInputValueType);
        assertNotSame(
                formula,
                different
        );

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different, DIFFERENT_INPUT_VALUE_TYPE);
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetInputValueTypeDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setExpression(this.expression())
                .setExpressionValue(this.expressionValue())
                .setInputValueType(this.inputValueType());

        final Optional<ValidationValueTypeName> differentInputValueType = Optional.of(DIFFERENT_INPUT_VALUE_TYPE);
        final SpreadsheetFormula different = formula.setInputValueType(differentInputValueType);
        assertNotSame(formula, different);

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(
                different,
                formula.expression()
        );
        this.inputValueTypeAndCheck(
                different,
                differentInputValueType
        );
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    private Optional<ValidationValueTypeName> inputValueType() {
        return this.inputValueType(INPUT_VALUE_TYPE);
    }

    private Optional<ValidationValueTypeName> inputValueType(final ValidationValueTypeName type) {
        return Optional.of(type);
    }

    private void inputValueTypeAndCheck(final SpreadsheetFormula formula,
                                        final ValidationValueTypeName type) {
        this.inputValueTypeAndCheck(
                formula,
                Optional.of(type)
        );
    }

    private void inputValueTypeAndCheck(final SpreadsheetFormula formula,
                                        final Optional<ValidationValueTypeName> type) {
        this.checkEquals(
                type,
                formula.inputValueType(),
                "inputValueType"
        );
    }

    private void inputValueTypeAndCheck(final SpreadsheetFormula formula) {
        this.inputValueTypeAndCheck(
                formula,
                SpreadsheetFormula.NO_INPUT_VALUE_TYPE
        );
    }
    
    // SetInputValue....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetInputValueNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject()
                        .setInputValue(null)
        );
    }

    @Test
    public void testSetInputValueSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(
                formula,
                formula.setInputValue(
                        formula.inputValue()
                )
        );
    }

    @Test
    public void testSetInputValueDifferent() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token());
        final Optional<Object> differentInputValue = Optional.of(
                "different!"
        );
        final SpreadsheetFormula different = formula.setInputValue(differentInputValue);
        assertNotSame(
                formula,
                different
        );

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different, differentInputValue);
        this.inputValueAndCheck(
                different,
                differentInputValue
        );
        this.errorAndCheck(different);
    }

    @Test
    public void testSetInputValueDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
                .setInputValue(this.inputValue());
        final Optional<Object> differentInputValue = SpreadsheetFormula.NO_EXPRESSION_VALUE;
        final SpreadsheetFormula different = formula.setInputValue(differentInputValue);
        assertNotSame(
                formula,
                different
        );

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
        this.errorAndCheck(different);
    }

    @Test
    public void testSetInputValueDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setExpression(this.expression())
                .setExpressionValue(this.expressionValue())
                .setInputValue(this.inputValue());

        final Optional<Object> differentInputValue = Optional.of("different!");
        final SpreadsheetFormula different = formula.setInputValue(differentInputValue);
        assertNotSame(formula, different);

        this.textAndCheck(different);
        this.tokenAndCheck(different);
        this.expressionAndCheck(
                different,
                formula.expression()
        );
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(
                different,
                differentInputValue
        );
        this.errorAndCheck(different);
    }

    private Optional<Object> inputValue() {
        return this.inputValue(EXPRESSION_VALUE);
    }

    private Optional<Object> inputValue(final Object value) {
        return Optional.of(value);
    }

    private void inputValueAndCheck(final SpreadsheetFormula formula,
                                    final Optional<Object> value) {
        this.checkEquals(
                value,
                formula.inputValue(),
                "inputValue"
        );

        if(value.isPresent()) {
            this.textAndCheck(formula);
            this.tokenAndCheck(formula);
            this.expressionAndCheck(formula);
            this.expressionValueAndCheck(formula);
        }
    }

    private void inputValueAndCheck(final SpreadsheetFormula formula) {
        this.inputValueAndCheck(
                formula,
                SpreadsheetFormula.NO_EXPRESSION_VALUE
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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
        this.expressionValueAndCheck(different);
        this.inputValueTypeAndCheck(different);
        this.inputValueAndCheck(different);
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
    public void testSetExpressionValueIfErrorNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormula.EMPTY
                        .setExpressionValueIfError(null)
        );
    }

    @Test
    public void testSetExpressionValueIfErrorWhenMissingCellBecomesZero() {
        final ExpressionNumberKind kind = ExpressionNumberKind.BIG_DECIMAL;
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");


        this.setExpressionValueIfErrorAndCheck(
                formula.setExpressionValue(
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
                formula.setExpressionValue(
                        Optional.of(
                                kind.zero()
                        )
                )
        );
    }

    @Test
    public void testSetExpressionValueIfErrorWhenNotMissingCell() {
        this.setExpressionValueIfErrorAndCheck(
                SpreadsheetFormula.EMPTY.setExpressionValue(
                        Optional.of(
                                "abc"
                        )
                )
        );
    }

    private void setExpressionValueIfErrorAndCheck(final SpreadsheetFormula formula) {
        this.setExpressionValueIfErrorAndCheck(
                formula,
                SpreadsheetEngineContexts.fake(),
                formula
        );
    }

    private void setExpressionValueIfErrorAndCheck(final SpreadsheetFormula formula,
                                                   final SpreadsheetEngineContext context,
                                                   final Object expected) {
        this.checkEquals(
                expected,
                formula.setExpressionValueIfError(context),
                () -> formula + " setExpressionValueIfError"
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
    public void testErrorOrValueWhenInputValue() {
        final Optional<Object> value = Optional.of(123);

        this.errorOrValueAndCheck(
                SpreadsheetFormula.EMPTY.setInputValue(value),
                value
        );
    }

    @Test
    public void testErrorOrValueWhenErrorAndInputValue() {
        final Optional<SpreadsheetError> error = Optional.of(
                SpreadsheetErrorKind.VALUE.setMessage("error123")
        );

        this.errorOrValueAndCheck(
                SpreadsheetFormula.EMPTY.setInputValue(
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
    public void testClearNonEmptyTextTokenExpressionValue() {
        final SpreadsheetFormula formula = formula("1+99")
                .setToken(this.token())
                .setExpression(this.expression())
                .setExpressionValue(this.expressionValue());
        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(
                formula,
                cleared
        );

        this.clearAndCheck(cleared);
    }

    private void clearAndCheck(final SpreadsheetFormula formula) {
        this.expressionAndCheck(formula);
        this.expressionValueAndCheck(formula);
        this.errorAndCheck(formula);
    }

    @Test
    public void testClearEmptyTextAndExpressionValue() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setExpressionValue(
                Optional.of(123)
        );

        final SpreadsheetFormula cleared = formula.clear();
        assertSame(
                formula,
                cleared
        );
    }

    @Test
    public void testClearWhenInputValue() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setInputValue(this.inputValue());

        final SpreadsheetFormula cleared = formula.clear();
        assertSame(
                formula,
                cleared
        );

        this.textAndCheck(cleared);
        this.tokenAndCheck(cleared);
        this.expressionAndCheck(cleared);
        this.expressionValueAndCheck(cleared);
        this.inputValueTypeAndCheck(cleared);
        this.inputValueAndCheck(
                cleared,
                this.inputValue()
        );
        this.errorAndCheck(cleared);
    }

    @Test
    public void testClearWhenInputValueAndError() {
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setInputValue(this.inputValue())
                .setError(this.error());

        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(
                formula,
                cleared
        );

        this.textAndCheck(cleared);
        this.tokenAndCheck(cleared);
        this.expressionAndCheck(cleared);
        this.expressionValueAndCheck(cleared);
        this.inputValueTypeAndCheck(cleared);
        this.inputValueAndCheck(
                cleared,
                this.inputValue()
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
        final ExpressionFunctionName functionName = ExpressionFunctionName.with("Hello");

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
                                                ExpressionFunctionName.with("Hello")
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
                        .setExpressionValue(
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
                        .setExpressionValue(
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
                        .setExpressionValue(
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

    private SpreadsheetParserContext parserContext() {
        final Locale locale = Locale.forLanguageTag("EN-AU");

        return SpreadsheetParserContexts.basic(
                InvalidCharacterExceptionFactory.POSITION_EXPECTED,
                DateTimeContexts.basic(
                        DateTimeSymbols.fromDateFormatSymbols(
                                new DateFormatSymbols(locale)
                        ),
                        locale,
                        1920,
                        50,
                        () -> {
                            throw new UnsupportedOperationException("now");
                        }
                ),
                ExpressionNumberContexts.basic(
                        ExpressionNumberKind.BIG_DECIMAL,
                        DecimalNumberContexts.american(MathContext.DECIMAL32)
                ),
                ','
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
                        .setExpressionValue(
                                Optional.of("Hello")
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
    public void testTreePrintTextTokenExpressionValue() {
        this.treePrintAndCheck(
                this.formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression())
                        .setExpressionValue(this.expressionValue()),
                "Formula\n" +
                        "  token:\n" +
                        "    TextSpreadsheetFormula \"1+2\"\n" +
                        "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\" (java.lang.String)\n" +
                        "  expressionValue:\n" +
                        "    3.0\n"
        );
    }

    @Test
    public void testTreePrintTextTokenExpressionValueError() {
        this.treePrintAndCheck(
                this.formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression())
                        .setExpressionValue(this.expressionValue())
                        .setError(this.error()),
                "Formula\n" +
                        "  token:\n" +
                        "    TextSpreadsheetFormula \"1+2\"\n" +
                        "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\" (java.lang.String)\n" +
                        "  expressionValue:\n" +
                        "    3.0\n" +
                        "  error:\n" +
                        "    #VALUE!\n" +
                        "      \"Message #1\"\n"
        );
    }

    @Test
    public void testTreePrintTreeExpressionValueString() {
        this.treePrintAndCheck(
                SpreadsheetFormula.EMPTY.setExpressionValue(
                        Optional.of("Hello123")
                ),
                "Formula\n" +
                        "  expressionValue:\n" +
                        "    \"Hello123\"\n"
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/1893

    @Test
    public void testTreePrintTreeTextTokenExpressionExpressionValueImplementsTreePrintable() {
        this.treePrintAndCheck(
                this.formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression())
                        .setExpressionValue(
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
                        "  expressionValue:\n" +
                        "    1111\n" +
                        "    2222\n" +
                        "    3333\n"
        );
    }

    @Test
    public void testTreePrintTextInputValueType() {
        this.treePrintAndCheck(
                SpreadsheetFormula.EMPTY.setInputValueType(
                        Optional.of(ValidationValueTypeName.TEXT)
                ),
                "Formula\n" +
                        "  inputValueType:\n" +
                        "    text\n"
        );
    }

    @Test
    public void testTreePrintTextError() {
        this.treePrintAndCheck(
                this.formula("=123/0")
                        .setExpressionValue(
                                Optional.of(
                                        SpreadsheetErrorKind.DIV0.toError()
                                )
                        ),
                "Formula\n" +
                        "  text:\n" +
                        "    \"=123/0\"\n" +
                        "  expressionValue:\n" +
                        "    #DIV/0!\n"
        );
    }

    @Test
    public void testTreePrintError() {
        this.treePrintAndCheck(
                SpreadsheetFormula.EMPTY.setExpressionValue(
                        Optional.of(
                                SpreadsheetErrorKind.DIV0.toError()
                        )
                ),
                "Formula\n" +
                        "  expressionValue:\n" +
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
                        this.expressionValue()
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
                        this.expressionValue()
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
                        this.expressionValue()
                )
        );
    }

    @Test
    public void testEqualsDifferentExpressionValue() {
        checkNotEquals(
                this.createFormula(
                        TEXT,
                        this.token(),
                        this.expression(),
                        this.expressionValue()
                ),
                this.createFormula(
                        TEXT,
                        this.token(),
                        this.expression(),
                        this.expressionValue("different-expression-value")
                )
        );
    }

    @Test
    public void testEqualsDifferentInputValueKind() {
        checkNotEquals(
                SpreadsheetFormula.EMPTY.setInputValueType(
                        this.inputValueType()
                ),
                SpreadsheetFormula.EMPTY.setInputValue(
                        Optional.of(DIFFERENT_INPUT_VALUE_TYPE)
                )
        );
    }

    @Test
    public void testEqualsDifferentInputValue() {
        checkNotEquals(
                SpreadsheetFormula.EMPTY.setInputValue(
                        this.inputValue()
                ),
                SpreadsheetFormula.EMPTY.setInputValue(
                        Optional.of("DifferentInputValue")
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
                .setExpressionValue(value);
    }

    // inputValueTypePatch..............................................................................................

    @Test
    public void testInputValueTypePatchWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormula.inputValueTypePatch(null)
        );
    }

    @Test
    public void testInputValueTypePatchWithNotEmpty() {
        this.checkEquals(
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.INPUT_VALUE_TYPE_PROPERTY,
                                JsonNode.string("text123")
                        ),
                SpreadsheetFormula.inputValueTypePatch(
                        Optional.of(
                                ValidationValueTypeName.with("text123")
                        )
                )
        );
    }

    @Test
    public void testInputValueTypePatchWithEmpty() {
        this.checkEquals(
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.INPUT_VALUE_TYPE_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetFormula.inputValueTypePatch(
                        Optional.empty()
                )
        );
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
    
    // patch............................................................................................................

    @Test
    public void testPatchEmptyObject() {
        this.patchAndCheck(
                this.createPatchable(),
                JsonNode.object()
        );
    }

    @Test
    public void testPatchSameText() {
        final String text = "=1+2*3";

        this.patchAndCheck(
                this.formula(text),
                SpreadsheetFormula.textPatch(text)
        );
    }

    @Test
    public void testPatchDifferentText() {
        final String text = "=1+2*3";

        this.patchAndCheck(
                this.formula("'Old"),
                SpreadsheetFormula.textPatch(text),
                this.formula(text)
        );
    }

    @Test
    public void testPatchInputValueTypeWithEmpty() {
        final SpreadsheetFormula formula = this.formula("=1");
        final Optional<ValidationValueTypeName> valueType = SpreadsheetFormula.NO_INPUT_VALUE_TYPE;

        this.patchAndCheck(
                formula,
                SpreadsheetFormula.inputValueTypePatch(valueType),
                formula.setInputValueType(valueType)
        );
    }

    @Test
    public void testPatchInputValueTypeWithNonEmpty() {
        final SpreadsheetFormula formula = this.formula("=1");
        final Optional<ValidationValueTypeName> valueType = Optional.of(
                ValidationValueTypeName.with("text123")
        );

        this.patchAndCheck(
                formula,
                SpreadsheetFormula.inputValueTypePatch(valueType),
                formula.setInputValueType(valueType)
        );
    }

    @Test
    public void testPatchInputValueWithNull() {
        final SpreadsheetFormula formula = this.formula("=1");

        this.patchAndCheck(
                formula,
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.INPUT_VALUE_PROPERTY,
                                JsonNode.nullNode()
                        ),
                formula.setInputValue(
                        Optional.empty()
                )
        );
    }

    @Test
    public void testPatchInputValueWithNonNull() {
        final SpreadsheetFormula formula = this.formula("=1");
        final String inputValue = "InputValue111";

        this.patchAndCheck(
                formula,
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.INPUT_VALUE_PROPERTY,
                                JsonNode.string(inputValue)
                        ),
                formula.setInputValue(
                        Optional.of(inputValue)
                )
        );
    }

    @Test
    public void testPatchInputValueWithNonNull2() {
        final SpreadsheetFormula formula = this.formula("=1");
        final AbsoluteUrl inputValue = Url.parseAbsolute("https://example.com/123");

        this.patchAndCheck(
                formula,
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.INPUT_VALUE_PROPERTY,
                                this.marshallContext()
                                        .marshallWithType(inputValue)
                        ),
                formula.setInputValue(
                        Optional.of(inputValue)
                )
        );
    }

    @Test
    public void testPatchSetInvalidProperty() {
        this.patchInvalidPropertyFails(
                this.formula("=1"),
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.EXPRESSION_VALUE_PROPERTY,
                                JsonNode.nullNode()
                        ),
                SpreadsheetFormula.EXPRESSION_VALUE_PROPERTY,
                JsonNode.nullNode()
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
    public void testToStringWithExpressionValue() {
        this.toStringAndCheck(
                this.createObject()
                        .setExpressionValue(
                                this.expressionValue(EXPRESSION_VALUE)
                        ),
                TEXT + " (=" + EXPRESSION_VALUE + ")"
        );
    }

    @Test
    public void testToStringWithInputValueTypeAndInputValue() {
        this.toStringAndCheck(
                this.createObject()
                        .setInputValueType(
                                Optional.of(INPUT_VALUE_TYPE)
                        ).setInputValue(
                                Optional.of(123)
                        ),
                "text 123"
        );
    }

    @Test
    public void testToStringWithInputValue() {
        this.toStringAndCheck(
                this.createObject()
                        .setInputValue(
                                Optional.of(123)
                        ),
                "123"
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
    public void testUnmarshallTextAndExpressionValue() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.TEXT_PROPERTY,
                                JsonNode.string(TEXT)
                        ).set(
                                SpreadsheetFormula.EXPRESSION_VALUE_PROPERTY,
                                JsonNode.number(EXPRESSION_VALUE)
                        ),
                this.formula(TEXT)
                        .setExpressionValue(
                                Optional.of(EXPRESSION_VALUE)
                        )
        );
    }

    @Test
    public void testUnmarshallInputValue() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.INPUT_VALUE_PROPERTY,
                                JsonNode.string(INPUT_VALUE)
                        ),
                SpreadsheetFormula.EMPTY.setInputValue(
                        Optional.of(INPUT_VALUE)
                )
        );
    }

    @Test
    public void testUnmarshallInputValueTypeAndInputValue() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.INPUT_VALUE_TYPE_PROPERTY,
                                JsonNode.string(INPUT_VALUE_TYPE.value())
                        ).set(
                                SpreadsheetFormula.INPUT_VALUE_PROPERTY,
                                JsonNode.string(INPUT_VALUE)
                        ),
                SpreadsheetFormula.EMPTY.setInputValueType(
                        Optional.of(INPUT_VALUE_TYPE)
                ).setInputValue(
                        Optional.of(INPUT_VALUE)
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

    // marshall.......................................................................................................

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
    public void testMarshallTextAndExpressionValue() {
        this.marshallAndCheck(
                this.formula(TEXT)
                        .setExpressionValue(
                                Optional.of(123L)
                        ),
                JsonNode.object()
                        .set(
                                JsonPropertyName.with("text"),
                                JsonNode.string("1+2")
                        ).set(
                                JsonPropertyName.with("expressionValue"),
                                this.marshallContext()
                                        .marshallWithType(123L)
                        )
        );
    }

    @Test
    public void testMarshallTextAndExpressionValue2() {
        this.marshallAndCheck(
                this.formula(TEXT)
                        .setExpressionValue(Optional.of("abc123")),
                "{ \"text\": \"1+2\", \"expressionValue\": \"abc123\"}"
        );
    }

    @Test
    public void testMarshallInputValueTypeAndInputValue() {
        this.marshallAndCheck(
                SpreadsheetFormula.EMPTY.setInputValue(
                        Optional.of(INPUT_VALUE_TYPE)
                ).setInputValue(
                        Optional.of("abc123")
                ),
                "{ \"inputValue\": \"abc123\"}"
        );
    }

    @Test
    public void testMarshallInputValue() {
        this.marshallAndCheck(
                SpreadsheetFormula.EMPTY.setInputValue(
                        Optional.of("abc123")
                ),
                "{ \"inputValue\": \"abc123\"}"
        );
    }

    @Test
    public void testMarshallInputValueAndError() {
        this.marshallAndCheck(
                SpreadsheetFormula.EMPTY.setInputValue(
                        Optional.of("abc123")
                ).setError(
                        this.error()
                ),
                "{\n" +
                        "  \"inputValue\": \"abc123\",\n" +
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
    public void testMarshallRoundtripTextAndExpressionValue() {
        this.marshallRoundTripTwiceAndCheck(
                this.formula(TEXT)
                        .setExpressionValue(
                                Optional.of(123L)
                        )
        );
    }

    @Test
    public void testMarshallRoundtripTextValueAndExpression() {
        this.marshallRoundTripTwiceAndCheck(
                this.formula(TEXT)
                        .setExpressionValue(Optional.of(123L))
                        .setExpression(this.expression())
        );
    }

    @Test
    public void testMarshallRoundtripTextAndExpressionValueWithError() {
        this.marshallRoundTripTwiceAndCheck(
                this.formula(TEXT)
                        .setExpressionValue(
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
