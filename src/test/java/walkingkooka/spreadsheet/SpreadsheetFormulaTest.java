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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;

import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormulaTest implements ClassTesting2<SpreadsheetFormula>,
        HashCodeEqualsDefinedTesting2<SpreadsheetFormula>,
        JsonNodeMarshallingTesting<SpreadsheetFormula>,
        PatchableTesting<SpreadsheetFormula>,
        ToStringTesting<SpreadsheetFormula>,
        TreePrintableTesting {

    private final static String TEXT = "a+2";
    private final static String EXPRESSION = "1+2";
    private final static Double VALUE = 3.0;
    private final static String ERROR = "Message #1";

    private final static String DIFFERENT_TEXT = "99+99";

    @Test
    public void testWithNullExpressionFails() {
        assertThrows(NullPointerException.class, () -> formula(null));
    }

    @Test
    public void testWithNullMaxTextLengthFails() {
        assertThrows(IllegalArgumentException.class, () -> formula(CharSequences.repeating(' ', 8192).toString()));
    }

    @Test
    public void testEmpty() {
        final SpreadsheetFormula formula = this.createObject();
        this.checkText(formula);
        this.checkExpressionAbsent(formula);
        this.checkValueAbsent(formula);
        this.checkErrorAbsent(formula);
    }

    @Test
    public void testWithEmpty() {
        final String text = "";
        final SpreadsheetFormula formula = formula(text);
        this.checkText(formula, text);
    }

    // SetText..........................................................................................................

    @Test
    public void testSetTextNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setText(null));
    }

    @Test
    public void testSetTextMaxTextLengthFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createObject().setText(CharSequences.repeating(' ', 8192).toString()));
    }

    @Test
    public void testSetTextSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setText(TEXT));
    }

    @Test
    public void testSetTextDifferent() {
        this.setTextAndCheck("different");
    }

    private void setTextAndCheck(final String differentText) {
        final SpreadsheetFormula formula = this.createObject();
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);
        this.checkText(different, differentText);
    }

    @Test
    public void testSetTextDifferent2() {
        final SpreadsheetFormula formula = this.createObject();

        final String differentText = "different";
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);

        this.checkText(different, differentText);
        this.checkTokenAbsent(different);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetTextDifferentEmpty() {
        final SpreadsheetFormula formula = this.createObject();

        final String differentText = "different";
        final SpreadsheetFormula different = formula.setText(differentText);

        assertSame(SpreadsheetFormula.EMPTY, different.setText(""));
    }

    @Test
    public void testSetTextAfterSetExpression() {
        final SpreadsheetFormula formula = this.createObject()
                .setExpression(this.expression());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(formula, different);

        this.checkText(different, DIFFERENT_TEXT);
        this.checkTokenAbsent(different);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetTextAfterSetExpressionSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setExpression(this.expression())
                .setValue(this.value());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(formula, different);

        this.checkText(different, DIFFERENT_TEXT);
        this.checkTokenAbsent(different);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetToken.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetTokenNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setToken(null));
    }

    @Test
    public void testSetTokenSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setToken(formula.token()));
    }

    @Test
    public void testSetTokenDifferent() {
        final SpreadsheetFormula formula = this.createObject();
        final Optional<SpreadsheetParserToken> differentToken = this.token("different!");
        final SpreadsheetFormula different = formula.setToken(differentToken);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkToken(different, differentToken);

        this.checkExpressionAbsent(different); // should also clear expression, value, error
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetExpression.....................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetExpressionNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setExpression(null));
    }

    @Test
    public void testSetExpressionSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setExpression(formula.expression()));
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

        this.checkText(different, TEXT);
        this.checkToken(different);
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetExpressionDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setExpression(this.expression());
        final Optional<Expression> differentExpression = SpreadsheetFormula.NO_EXPRESSION;
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkToken(different);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
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

        this.checkText(different, TEXT);
        this.checkToken(different);
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetError..........................................................................................................

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    public void testSetErrorNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setError(null));
    }

    @Test
    public void testSetErrorSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setError(formula.error()));
    }

    @Test
    public void testSetErrorDifferent() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkToken(different);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferent2() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setError(this.error());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkToken(different);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setToken(this.token())
                .setValue(this.value());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkToken(different);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    // clear.......................................................................................................

    @Test
    public void testClearText() {
        final SpreadsheetFormula formula = formula("1+99");
        final SpreadsheetFormula cleared = formula.clear();
        assertSame(formula, cleared);
        this.checkClear(cleared);
    }

    @Test
    public void testClearTextAndToken() {
        final SpreadsheetFormula formula = formula("1+99")
                .setToken(this.token());
        final SpreadsheetFormula cleared = formula.clear();
        assertSame(formula, cleared);
        this.checkClear(cleared);
    }

    @Test
    public void testClearTextTokenExpression() {
        final SpreadsheetFormula formula = formula("1+99")
                .setToken(this.token())
                .setExpression(this.expression());
        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(formula, cleared);

        this.checkClear(cleared);
    }

    @Test
    public void testClearTextTokenExpressionValue() {
        final SpreadsheetFormula formula = formula("1+99")
                .setToken(this.token())
                .setExpression(this.expression())
                .setValue(this.value());
        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(formula, cleared);

        this.checkClear(cleared);
    }

    @Test
    public void testClearTextTokenExpressionError() {
        final SpreadsheetFormula formula = formula("1+99")
                .setToken(this.token())
                .setExpression(this.expression())
                .setError(this.error());
        final SpreadsheetFormula cleared = formula.clear();
        assertNotSame(formula, cleared);

        this.checkClear(cleared);
    }

    private void checkClear(final SpreadsheetFormula formula) {
        this.checkExpressionAbsent(formula);
        this.checkValueAbsent(formula);
        this.checkErrorAbsent(formula);
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testTreePrintText() {
        this.treePrintAndCheck(
                formula("1+2"),
                "Formula\n" +
                        "  text: \"1+2\"\n"
        );
    }

    @Test
    public void testTreePrintTextToken() {
        this.treePrintAndCheck(
                formula("1+2")
                        .setToken(this.token()),
                "Formula\n" +
                        "  text: \"1+2\"\n" +
                        "  token:\n" +
                        "    SpreadsheetText\n" +
                        "      SpreadsheetTextLiteral \"1+2\" \"1+2\" (java.lang.String)\n"
        );
    }

    @Test
    public void testTreePrintTextTokenExpression() {
        this.treePrintAndCheck(
                formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression()),
                "Formula\n" +
                        "  text: \"1+2\"\n" +
                        "  token:\n" +
                        "    SpreadsheetText\n" +
                        "      SpreadsheetTextLiteral \"1+2\" \"1+2\" (java.lang.String)\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\"\n"
        );
    }

    @Test
    public void testTreePrintTextTokenExpressionValue() {
        this.treePrintAndCheck(
                formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression())
                        .setValue(this.value()),
                "Formula\n" +
                        "  text: \"1+2\"\n" +
                        "  token:\n" +
                        "    SpreadsheetText\n" +
                        "      SpreadsheetTextLiteral \"1+2\" \"1+2\" (java.lang.String)\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\"\n" +
                        "  value: 3.0 (java.lang.Double)\n"
        );
    }

    @Test
    public void testTreePrintTextTokenExpressionError() {
        this.treePrintAndCheck(
                formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression())
                        .setError(this.error()),
                "Formula\n" +
                        "  text: \"1+2\"\n" +
                        "  token:\n" +
                        "    SpreadsheetText\n" +
                        "      SpreadsheetTextLiteral \"1+2\" \"1+2\" (java.lang.String)\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\"\n" +
                        "  error: \"Message #1\"\n"
        );
    }

    // https://github.com/mP1/walkingkooka-spreadsheet/issues/1893

    @Test
    public void testTreePrintTreeValueImplementsTreePrintable() {
        this.treePrintAndCheck(
                formula("1+2")
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
                        "  text: \"1+2\"\n" +
                        "  token:\n" +
                        "    SpreadsheetText\n" +
                        "      SpreadsheetTextLiteral \"1+2\" \"1+2\" (java.lang.String)\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\"\n" +
                        "  value: 1111\n" +
                        "    2222\n" +
                        "    3333\n"
        );
    }

    // equals.......................................................................................................

    @Test
    public void testEqualsDifferentText() {
        checkNotEquals(
                this.createFormula(
                        "99+88",
                        this.token(),
                        this.expression(),
                        this.value(),
                        this.error()
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
                        this.value(),
                        this.error()
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
                        this.value(),
                        this.error()
                )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        checkNotEquals(
                this.createFormula(
                        TEXT,
                        this.token(),
                        this.expression(),
                        this.value(),
                        SpreadsheetFormula.NO_ERROR
                ),
                this.createFormula(
                        TEXT,
                        this.token(),
                        this.expression(),
                        this.value("different-value"),
                        SpreadsheetFormula.NO_ERROR
                )
        );
    }

    @Test
    public void testEqualsDifferentError() {
        checkNotEquals(
                this.createFormula(TEXT,
                        this.token(),
                        this.expression(),
                        this.value(),
                        this.error("different error message"))
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetFormula() {
        this.checkNotEquals(formula("different"));
    }

    private SpreadsheetFormula createFormula(final String formula,
                                             final Optional<SpreadsheetParserToken> token,
                                             final Optional<Expression> expression,
                                             final Optional<Object> value,
                                             final Optional<SpreadsheetError> error) {
        return formula(formula)
                .setToken(token)
                .setExpression(expression)
                .setValue(value)
                .setError(error);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testJsonNodeUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testJsonNodeUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12));
    }

    @Test
    public void testJsonNodeUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array());
    }

    @Test
    public void testJsonNodeUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"));
    }

    @Test
    public void testJsonNodeUnmarshallObjectEmptyFails() {
        this.unmarshallFails(JsonNode.object());
    }

    @Test
    public void testJsonNodeUnmarshallText() {
        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT)),
                formula(TEXT));
    }

    @Test
    public void testJsonNodeUnmarshallTextAndToken() {
        final Optional<SpreadsheetParserToken> token = this.token();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.TOKEN_PROPERTY, this.marshallContext().marshallWithType(token.get())),
                formula(TEXT)
                        .setToken(token)
        );
    }

    @Test
    public void testJsonNodeUnmarshallTextAndExpression() {
        final Optional<Expression> expression = this.expression();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.EXPRESSION_PROPERTY, this.marshallContext().marshallWithType(expression.get())),
                formula(TEXT)
                        .setExpression(expression)
        );
    }

    @Test
    public void testJsonNodeUnmarshallTextTokenAndExpression() {
        final Optional<SpreadsheetParserToken> token = this.token();
        final Optional<Expression> expression = this.expression();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.TOKEN_PROPERTY, this.marshallContext().marshallWithType(token.get()))
                        .set(SpreadsheetFormula.EXPRESSION_PROPERTY, this.marshallContext().marshallWithType(expression.get())),
                formula(TEXT)
                        .setToken(token)
                        .setExpression(expression)
        );
    }

    @Test
    public void testJsonNodeUnmarshallTextAndError() {
        final SpreadsheetError error = SpreadsheetError.with(ERROR);

        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.ERROR_PROPERTY, this.marshallContext().marshall(error)),
                formula(TEXT).setError(Optional.of(error)));
    }

    @Test
    public void testJsonNodeUnmarshallTextAndValue() {
        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.VALUE_PROPERTY, JsonNode.number(VALUE)),
                formula(TEXT).setValue(Optional.of(VALUE)));
    }

    @Test
    public void testJsonNodeUnmarshallTextAndErrorAndValueFails() {
        this.unmarshallFails(JsonNode.object()
                .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                .set(SpreadsheetFormula.VALUE_PROPERTY, JsonNode.string("1"))
                .set(SpreadsheetFormula.ERROR_PROPERTY, this.marshallContext().marshall(SpreadsheetError.with(ERROR))));
    }

    // marshall.......................................................................................................

    @Test
    public void testJsonNodeMarshallText() {
        this.marshallAndCheck(formula(TEXT),
                "{ \"text\": \"a+2\"}");
    }

    @Test
    public void testJsonNodeMarshallTextAndToken() {
        this.marshallAndCheck(formula(TEXT)
                        .setToken(this.token()),
                "{\n" +
                        "  \"text\": \"a+2\",\n" +
                        "  \"token\": {\n" +
                        "    \"type\": \"spreadsheet-text-parser-token\",\n" +
                        "    \"value\": {\n" +
                        "      \"value\": [{\n" +
                        "        \"type\": \"spreadsheet-text-literal-parser-token\",\n" +
                        "        \"value\": {\n" +
                        "          \"value\": \"1+2\",\n" +
                        "          \"text\": \"1+2\"\n" +
                        "        }\n" +
                        "      }],\n" +
                        "      \"text\": \"1+2\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}");
    }

    @Test
    public void testJsonNodeMarshallTextAndExpression() {
        this.marshallAndCheck(formula(TEXT)
                        .setExpression(this.expression()),
                "{\n" +
                        "  \"text\": \"a+2\",\n" +
                        "  \"expression\": {\n" +
                        "    \"type\": \"value-expression\",\n" +
                        "    \"value\": \"1+2\"\n" +
                        "  }\n" +
                        "}");
    }

    @Test
    public void testJsonNodeMarshallTextTokenAndExpression() {
        this.marshallAndCheck(formula(TEXT)
                        .setToken(this.token())
                        .setExpression(this.expression()),
                "{\n" +
                        "  \"text\": \"a+2\",\n" +
                        "  \"token\": {\n" +
                        "    \"type\": \"spreadsheet-text-parser-token\",\n" +
                        "    \"value\": {\n" +
                        "      \"value\": [{\n" +
                        "        \"type\": \"spreadsheet-text-literal-parser-token\",\n" +
                        "        \"value\": {\n" +
                        "          \"value\": \"1+2\",\n" +
                        "          \"text\": \"1+2\"\n" +
                        "        }\n" +
                        "      }],\n" +
                        "      \"text\": \"1+2\"\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"expression\": {\n" +
                        "    \"type\": \"value-expression\",\n" +
                        "    \"value\": \"1+2\"\n" +
                        "  }\n" +
                        "}");
    }

    @Test
    public void testJsonNodeMarshallTextAndValue() {
        this.marshallAndCheck(formula(TEXT)
                        .setValue(Optional.of(123L)),
                JsonNode.object()
                        .set(JsonPropertyName.with("text"), JsonNode.string("a+2"))
                        .set(JsonPropertyName.with("value"), this.marshallContext().marshallWithType(123L)));
    }

    @Test
    public void testJsonNodeMarshallTextAndValue2() {
        this.marshallAndCheck(formula(TEXT)
                        .setValue(Optional.of("abc123")),
                "{ \"text\": \"a+2\", \"value\": \"abc123\"}");
    }

    @Test
    public void testJsonNodeMarshallTextAndError() {
        this.marshallAndCheck(formula(TEXT)
                        .setError(Optional.of(SpreadsheetError.with("error123"))),
                "{ \"text\": \"a+2\", \"error\": \"error123\"}");
    }

    @Test
    public void testJsonNodeMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    @Test
    public void testJsonNodeMarshallRoundtripTextAndValue() {
        this.marshallRoundTripTwiceAndCheck(formula(TEXT)
                .setValue(Optional.of(123L)));
    }

    @Test
    public void testJsonNodeMarshallRoundtripTextValueAndExpression() {
        this.marshallRoundTripTwiceAndCheck(formula(TEXT)
                .setValue(Optional.of(123L))
                .setExpression(this.expression()));
    }

    @Test
    public void testJsonNodeMarshallRoundtripTextAndError() {
        this.marshallRoundTripTwiceAndCheck(formula(TEXT)
                .setError(Optional.of(SpreadsheetError.with("error message #1"))));
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
        this.patchAndCheck(
                formula("=1"),
                JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string("=1"))
        );
    }

    @Test
    public void testPatchDifferentText() {
        this.patchAndCheck(
                formula("=1"),
                JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string("=2")),
                formula("=2")
        );
    }

    @Test
    public void testPatchSetInvalidProperty() {
        this.patchInvalidPropertyFails(
                formula("=1"),
                JsonNode.object()
                        .set(SpreadsheetFormula.VALUE_PROPERTY, JsonNode.nullNode()),
                SpreadsheetFormula.VALUE_PROPERTY,
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
                ExpressionNumberContexts.basic(ExpressionNumberKind.BIG_DECIMAL, MathContext.UNLIMITED)
        );
    }

    // toString...............................................................................................

    @Test
    public void testToStringWithValue() {
        this.toStringAndCheck(this.createObject().setValue(this.value(VALUE)),
                TEXT + " (=" + VALUE + ")");
    }

    @Test
    public void testToStringWithError() {
        this.toStringAndCheck(this.createObject().setError(this.error(ERROR)),
                TEXT + " (" + ERROR + ")");
    }

    @Test
    public void testToStringWithoutError() {
        this.toStringAndCheck(this.createObject(), TEXT);
    }

    @Override
    public SpreadsheetFormula createObject() {
        return formula(TEXT);
    }

    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.EMPTY
                .setText(text);
    }

    private void checkText(final SpreadsheetFormula formula) {
        checkText(formula, TEXT);
    }

    private void checkText(final SpreadsheetFormula formula,
                           final String text) {
        this.checkEquals(text, formula.text(), "text(Expression)");
    }

    private Optional<SpreadsheetParserToken> token() {
        return this.token(EXPRESSION);
    }

    private Optional<SpreadsheetParserToken> token(final String text) {
        return Optional.of(
                SpreadsheetParserToken.text(
                        Lists.of(
                                SpreadsheetParserToken.textLiteral(text, text)
                        ),
                        text
                )
        );
    }

    private void checkToken(final SpreadsheetFormula formula) {
        this.checkToken(formula, this.token());
    }

    private void checkTokenAbsent(final SpreadsheetFormula formula) {
        this.checkToken(formula, SpreadsheetFormula.NO_TOKEN);
    }

    private void checkToken(final SpreadsheetFormula formula, final Optional<SpreadsheetParserToken> token) {
        this.checkEquals(token, formula.token(), "token");
    }

    private Optional<Expression> expression() {
        return this.expression(EXPRESSION);
    }

    private Optional<Expression> expression(final String text) {
        return Optional.of(
                Expression.value(text)
        );
    }

    private void checkExpression(final SpreadsheetFormula formula, final Optional<Expression> expression) {
        this.checkEquals(expression, formula.expression(), "expression");
    }

    private void checkExpressionAbsent(final SpreadsheetFormula formula) {
        this.checkExpression(formula, SpreadsheetFormula.NO_EXPRESSION);
    }

    private Optional<Object> value() {
        return this.value(VALUE);
    }

    private Optional<Object> value(final Object value) {
        return Optional.of(value);
    }

    private void checkValue(final SpreadsheetFormula formula, final Optional<Object> value) {
        this.checkEquals(value, formula.value(), "value");
    }

    private void checkValueAbsent(final SpreadsheetFormula formula) {
        this.checkValue(formula, SpreadsheetFormula.NO_VALUE);
    }

    private Optional<SpreadsheetError> error() {
        return this.error(ERROR);
    }

    private Optional<SpreadsheetError> error(final String error) {
        return Optional.of(SpreadsheetError.with(error));
    }

    private void checkErrorAbsent(final SpreadsheetFormula formula) {
        this.checkError(formula, SpreadsheetFormula.NO_ERROR);
    }

    private void checkError(final SpreadsheetFormula formula, final Optional<SpreadsheetError> error) {
        this.checkEquals(error, formula.error(), "formula");
    }

    @Override
    public Class<SpreadsheetFormula> type() {
        return SpreadsheetFormula.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetFormula createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetFormula unmarshall(final JsonNode jsonNode,
                                         final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormula.unmarshall(jsonNode, context);
    }
}
