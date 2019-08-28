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
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMappingTesting;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;


public final class SpreadsheetFormulaTest implements ClassTesting2<SpreadsheetFormula>,
        HashCodeEqualsDefinedTesting<SpreadsheetFormula>,
        JsonNodeMappingTesting<SpreadsheetFormula>,
        ToStringTesting<SpreadsheetFormula> {

    private final static String TEXT = "a+2";
    private final static String EXPRESSION = "1+2";
    private final static Double VALUE = 3.0;
    private final static String ERROR = "Message #1";

    private final static String DIFFERENT_TEXT = "99+99";

    @Test
    public void testWithNullExpressionFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetFormula.with(null);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetFormula formula = this.createObject();
        this.checkText(formula, TEXT);
        this.checkExpressionAbsent(formula);
        this.checkValueAbsent(formula);
        this.checkErrorAbsent(formula);
    }

    @Test
    public void testWithEmpty() {
        final String text = "";
        final SpreadsheetFormula formula = SpreadsheetFormula.with(text);
        this.checkText(formula, text);
    }

    @Test
    public void testSetTextNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setText(null);
        });
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

    @Test
    public void testSetTextDifferentEmpty() {
        this.setTextAndCheck("");
    }

    private void setTextAndCheck(final String differentText) {
        final SpreadsheetFormula formula = this.createObject();
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);
        this.checkText(different, differentText);
    }

    // SetFormula.....................................................................................................

    @Test
    public void testSetFormulaNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setText(null);
        });
    }

    @Test
    public void testSetFormulaSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setText(formula.text()));
    }

    @Test
    public void testSetFormulaDifferent() {
        final SpreadsheetFormula formula = this.createObject();
        final String differentText = "different";
        final SpreadsheetFormula different = formula.setText(differentText);
        assertNotSame(formula, different);

        this.checkText(different, differentText);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetFormulaAfterSetExpression() {
        final SpreadsheetFormula formula = this.createObject()
                .setExpression(this.expression());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(formula, different);

        this.checkText(different, DIFFERENT_TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetFormulaAfterSetExpressionSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setExpression(this.expression())
                .setValue(this.value());
        final SpreadsheetFormula different = formula.setText(DIFFERENT_TEXT);
        assertNotSame(formula, different);

        this.checkText(different, DIFFERENT_TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetExpression.....................................................................................................

    @Test
    public void testSetExpressionNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setExpression(null);
        });
    }

    @Test
    public void testSetExpressionSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setExpression(formula.expression()));
    }

    @Test
    public void testSetExpressionDifferent() {
        final SpreadsheetFormula formula = this.createObject();
        final Optional<ExpressionNode> differentExpression = Optional.of(ExpressionNode.text("different!"));
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetExpressionDifferentAndClear() {
        final SpreadsheetFormula formula = this.createObject()
                .setExpression(this.expression());
        final Optional<ExpressionNode> differentExpression = SpreadsheetFormula.NO_EXPRESSION;
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    @Test
    public void testSetExpressionDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setExpression(this.expression())
                .setValue(this.value());

        final Optional<ExpressionNode> differentExpression = Optional.of(ExpressionNode.text("different!"));
        final SpreadsheetFormula different = formula.setExpression(differentExpression);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkExpression(different, differentExpression);
        this.checkValueAbsent(different);
        this.checkErrorAbsent(different);
    }

    // SetError.....................................................................................................

    @Test
    public void testSetErrorNullFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createObject().setError(null);
        });
    }

    @Test
    public void testSetErrorSame() {
        final SpreadsheetFormula formula = this.createObject();
        assertSame(formula, formula.setError(formula.error()));
    }

    @Test
    public void testSetErrorDifferent() {
        final SpreadsheetFormula formula = this.createObject();
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferent2() {
        final SpreadsheetFormula formula = this.createObject()
                .setError(this.error());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    @Test
    public void testSetErrorDifferentAfterSetValue() {
        final SpreadsheetFormula formula = this.createObject()
                .setValue(this.value());
        final Optional<SpreadsheetError> differentError = this.error("different");
        final SpreadsheetFormula different = formula.setError(differentError);
        assertNotSame(formula, different);

        this.checkText(different, TEXT);
        this.checkExpressionAbsent(different);
        this.checkValueAbsent(different);
        this.checkError(different, differentError);
    }

    // clear.......................................................................................................

    @Test
    public void testClearWithoutValueAndError() {
        this.checkClear(SpreadsheetFormula.with("1+99"));
    }

    @Test
    public void testClearValue() {
        this.checkClear(this.createObject().setValue(this.value()));
    }

    @Test
    public void testClearError() {
        this.checkClear(this.createObject().setError(this.error()));
    }

    private void checkClear(final SpreadsheetFormula formula) {
        final SpreadsheetFormula cleared = formula.clear();
        this.checkValueAbsent(cleared);
        this.checkErrorAbsent(cleared);
    }

    // equals.......................................................................................................

    @Test
    public void testEqualsDifferentText() {
        checkNotEquals(this.createFormula("99+88", this.expression(), this.value(), this.error()));
    }

    @Test
    public void testEqualsDifferentExpression() {
        checkNotEquals(this.createFormula(TEXT, this.expression("44"), this.value(), this.error()));
    }

    @Test
    public void testEqualsDifferentValue() {
        checkNotEquals(this.createFormula(TEXT, this.expression(), this.value(), SpreadsheetFormula.NO_ERROR),
                this.createFormula(TEXT, this.expression(), this.value("different-value"), SpreadsheetFormula.NO_ERROR));
    }

    @Test
    public void testEqualsDifferentError() {
        checkNotEquals(this.createFormula(TEXT, this.expression(), this.value(), this.error("different error message")));
    }

    @Test
    public void testEqualsDifferentSpreadsheetFormula() {
        this.checkNotEquals(SpreadsheetFormula.with("different"));
    }

    private SpreadsheetFormula createFormula(final String formula,
                                             final Optional<ExpressionNode> expression,
                                             final Optional<Object> value,
                                             final Optional<SpreadsheetError> error) {
        return SpreadsheetFormula.with(formula)
                .setExpression(expression)
                .setValue(value)
                .setError(error);
    }

    // JsonNodeMappingTesting...........................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(12), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array(), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeStringFails() {
        this.fromJsonNodeFails(JsonNode.string("fails"), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeObjectEmptyFails() {
        this.fromJsonNodeFails(JsonNode.object(), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeText() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT, JsonNode.string(TEXT)),
                SpreadsheetFormula.with(TEXT));
    }

    @Test
    public void testFromJsonNodeTextAndError() {
        final SpreadsheetError error = SpreadsheetError.with(ERROR);

        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.ERROR_PROPERTY, this.toJsonNodeContext().toJsonNode(error)),
                SpreadsheetFormula.with(TEXT).setError(Optional.of(error)));
    }

    @Test
    public void testFromJsonNodeTextAndValue() {
        this.fromJsonNodeAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.VALUE_PROPERTY, JsonNode.number(VALUE)),
                SpreadsheetFormula.with(TEXT).setValue(Optional.of(VALUE)));
    }

    @Test
    public void testFromJsonNodeTextAndErrorAndValueFails() {
        this.fromJsonNodeFails(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.VALUE_PROPERTY, JsonNode.string("1"))
                        .set(SpreadsheetFormula.ERROR_PROPERTY, this.toJsonNodeContext().toJsonNode(SpreadsheetError.with(ERROR))),
                JsonNodeException.class);
    }

    // toJsonNode.......................................................................................................

    @Test
    public void testToJsonNodeText() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT),
                "{ \"text\": \"a+2\"}");
    }

    @Test
    public void testToJsonNodeTextAndExpression() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setExpression(this.expression()),
                "{\n" +
                        "  \"text\": \"a+2\",\n" +
                        "  \"expression\": {\n" +
                        "    \"type\": \"expression-text\",\n" +
                        "    \"value\": \"1+2\"\n" +
                        "  }\n" +
                        "}");
    }

    @Test
    public void testToJsonNodeTextAndValue() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setValue(Optional.of(123L)),
                JsonNode.object()
                        .set(JsonNodeName.with("text"), JsonNode.string("a+2"))
                        .set(JsonNodeName.with("value"), this.toJsonNodeContext().toJsonNodeWithType(123L)));
    }

    @Test
    public void testToJsonNodeTextAndValue2() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setValue(Optional.of("abc123")),
                "{ \"text\": \"a+2\", \"value\": \"abc123\"}");
    }

    @Test
    public void testToJsonNodeTextAndError() {
        this.toJsonNodeAndCheck(SpreadsheetFormula.with(TEXT)
                        .setError(Optional.of(SpreadsheetError.with("error123"))),
                "{ \"text\": \"a+2\", \"error\": \"error123\"}");
    }

    @Test
    public void testToJsonNodeRoundtripTwice() {
        this.toJsonNodeRoundTripTwiceAndCheck(this.createObject());
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
        return SpreadsheetFormula.with(TEXT);
    }

    private void checkText(final SpreadsheetFormula formula, final String text) {
        assertEquals(text, formula.text(), "text(ExpressionNode)");
    }

    private Optional<ExpressionNode> expression() {
        return this.expression(EXPRESSION);
    }

    private Optional<ExpressionNode> expression(final String text) {
        return Optional.of(ExpressionNode.text(text));
    }

    private void checkExpression(final SpreadsheetFormula formula, final Optional<ExpressionNode> expression) {
        assertEquals(expression, formula.expression(), "expression");
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
        assertEquals(value, formula.value(), "value");
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
        assertEquals(error, formula.error(), "formula");
    }

    @Override
    public Class<SpreadsheetFormula> type() {
        return SpreadsheetFormula.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMappingTesting...........................................................................................

    @Override
    public SpreadsheetFormula createJsonNodeMappingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetFormula fromJsonNode(final JsonNode jsonNode,
                                           final FromJsonNodeContext context) {
        return SpreadsheetFormula.fromJsonNode(jsonNode, context);
    }
}
