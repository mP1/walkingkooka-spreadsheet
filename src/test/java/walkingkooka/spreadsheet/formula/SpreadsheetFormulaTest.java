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
import walkingkooka.math.DecimalNumberContexts;
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
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FakeExpressionEvaluationContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;

import java.math.MathContext;
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
    private final static Double VALUE = 3.0;
    private final static String ERROR = "Message #1";

    private final static String DIFFERENT_TEXT = "99+99";

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.BIG_DECIMAL;

    @Test
    public void testWithNullExpressionFails() {
        assertThrows(NullPointerException.class, () -> formula(null));
    }

    @Test
    public void testWithNullMaxTextLengthFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> formula(
                        CharSequences.repeating(' ', 8192).toString()
                )
        );
    }

    @Test
    public void testNotEmpty() {
        final SpreadsheetFormula formula = this.createObject();
        this.textAndCheck(formula);
        this.expressionAndCheck(formula);
        this.valueAndCheck(formula);
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
        this.textAndCheck(formula, text);

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
        this.tokenEmptyAndCheck(different);
        this.expressionAndCheck(different);
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
        this.tokenEmptyAndCheck(different);
        this.expressionAndCheck(different);
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
        this.tokenEmptyAndCheck(different);
        this.expressionAndCheck(different);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    // SetToken.........................................................................................................

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
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    // SetExpression.....................................................................................................

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

        this.textAndCheck(different, TEXT);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different, differentExpression);
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

        this.textAndCheck(different, TEXT);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different);
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

        this.textAndCheck(different, TEXT);
        this.tokenAndCheck(different);
        this.expressionAndCheck(different, differentExpression);
        this.valueAndCheck(different);
        this.errorAndCheck(different);
    }

    // replaceErrorWithValueIfPossible..................................................................................

    @Test
    public void testReplaceErrorWithValueIfPossibleWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetFormula.EMPTY
                        .replaceErrorWithValueIfPossible(null)
        );
    }

    @Test
    public void testReplaceErrorWithValueIfPossibleWithMissingCellBecomesZero() {
        final ExpressionNumberKind kind = ExpressionNumberKind.BIG_DECIMAL;
        final SpreadsheetFormula formula = SpreadsheetFormula.EMPTY.setText("=1");


        this.replaceErrorWithValueIfPossibleAndCheck(
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
    public void testReplaceErrorWithValueIfPossibleWithNotMissingCell() {
        this.replaceErrorWithValueIfPossibleAndCheck(
                SpreadsheetFormula.EMPTY.setValue(
                        Optional.of(
                                "abc"
                        )
                )
        );
    }

    private void replaceErrorWithValueIfPossibleAndCheck(final SpreadsheetFormula formula) {
        this.replaceErrorWithValueIfPossibleAndCheck(
                formula,
                SpreadsheetEngineContexts.fake(),
                formula
        );
    }

    private void replaceErrorWithValueIfPossibleAndCheck(final SpreadsheetFormula formula,
                                                         final SpreadsheetEngineContext context,
                                                         final Object expected) {
        this.checkEquals(
                expected,
                formula.replaceErrorWithValueIfPossible(context),
                () -> formula + " replaceErrorWithValueIfPossible"
        );
    }

    // clear............................................................................................................

    @Test
    public void testClearText() {
        final SpreadsheetFormula formula = formula("1+99");
        final SpreadsheetFormula cleared = formula.clear();
        assertSame(
                formula,
                cleared
        );
        this.clearAndCheck(cleared);
    }

    @Test
    public void testClearTextAndToken() {
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
    public void testClearTextTokenExpression() {
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
    public void testClearTextTokenExpressionValue() {
        final SpreadsheetFormula formula = formula("1+99")
                .setToken(this.token())
                .setExpression(this.expression())
                .setValue(this.value());
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
                                        SpreadsheetErrorKind.ERROR.setMessage("Invalid character \'@\' at 0 expected \"dd/mmm/yyyy\"")
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
        return SpreadsheetParserContexts.basic(
                DateTimeContexts.locale(
                        Locale.forLanguageTag("EN-AU"), // locale
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
                true
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
                        "  token:\n" +
                        "    TextSpreadsheetFormula \"1+2\"\n" +
                        "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n"
        );
    }

    @Test
    public void testTreePrintTextTokenExpression() {
        this.treePrintAndCheck(
                formula("1+2")
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
                formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression())
                        .setValue(this.value()),
                "Formula\n" +
                        "  token:\n" +
                        "    TextSpreadsheetFormula \"1+2\"\n" +
                        "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\" (java.lang.String)\n" +
                        "  value: 3.0 (java.lang.Double)\n"
        );
    }

    @Test
    public void testTreePrintTextTokenExpressionError() {
        this.treePrintAndCheck(
                formula("1+2")
                        .setToken(this.token())
                        .setExpression(this.expression())
                        .setValue(this.error()),
                "Formula\n" +
                        "  token:\n" +
                        "    TextSpreadsheetFormula \"1+2\"\n" +
                        "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\" (java.lang.String)\n" +
                        "  value: #VALUE!\n" +
                        "      \"Message #1\"\n"
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
                        "  token:\n" +
                        "    TextSpreadsheetFormula \"1+2\"\n" +
                        "      TextLiteralSpreadsheetFormula \"1+2\" \"1+2\"\n" +
                        "  expression:\n" +
                        "    ValueExpression \"1+2\" (java.lang.String)\n" +
                        "  value: 1111\n" +
                        "    2222\n" +
                        "    3333\n"
        );
    }

    @Test
    public void testTreePrintTextError() {
        this.treePrintAndCheck(
                formula("=123/0")
                        .setValue(
                                Optional.of(
                                        SpreadsheetErrorKind.DIV0.toError()
                                )
                        ),
                "Formula\n" +
                        "  text: \"=123/0\"\n" +
                        "  value: #DIV/0!\n"
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
                        "  text: \"\"\n" +
                        "  value: #DIV/0!\n"
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
    public void testEqualsDifferentValue() {
        checkNotEquals(
                this.createFormula(
                        TEXT,
                        this.token(),
                        this.expression(),
                        this.value()
                ),
                this.createFormula(
                        TEXT,
                        this.token(),
                        this.expression(),
                        this.value("different-value")
                )
        );
    }

    @Test
    public void testEqualsDifferentSpreadsheetFormula() {
        this.checkNotEquals(
                formula("different")
        );
    }

    @Override
    public SpreadsheetFormula createObject() {
        return formula(TEXT);
    }

    private SpreadsheetFormula createFormula(final String formula,
                                             final Optional<SpreadsheetFormulaParserToken> token,
                                             final Optional<Expression> expression,
                                             final Optional<Object> value) {
        return formula(formula)
                .setToken(token)
                .setExpression(expression)
                .setValue(value);
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


    private SpreadsheetFormula formula(final String text) {
        return SpreadsheetFormula.EMPTY
                .setText(text);
    }

    private void textAndCheck(final SpreadsheetFormula formula) {
        textAndCheck(
                formula,
                TEXT
        );
    }

    private Optional<SpreadsheetFormulaParserToken> token() {
        return this.token(EXPRESSION);
    }

    private Optional<SpreadsheetFormulaParserToken> token(final String text) {
        return Optional.of(
                SpreadsheetFormulaParserToken.text(
                        Lists.of(
                                SpreadsheetFormulaParserToken.textLiteral(text, text)
                        ),
                        text
                )
        );
    }

    private void tokenAndCheck(final SpreadsheetFormula formula) {
        this.tokenAndCheck(formula, this.token());
    }

    private void tokenEmptyAndCheck(final SpreadsheetFormula formula) {
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

    private Optional<Object> value() {
        return this.value(VALUE);
    }

    private Optional<Object> value(final Object value) {
        return Optional.of(value);
    }

    private void valueAndCheck(final SpreadsheetFormula formula,
                               final Optional<Object> value) {
        this.checkEquals(
                value,
                formula.value(),
                "value"
        );
    }

    private void valueAndCheck(final SpreadsheetFormula formula) {
        this.valueAndCheck(
                formula,
                SpreadsheetFormula.NO_VALUE
        );
    }

    private Optional<Object> error() {
        return this.error(ERROR);
    }

    private Optional<Object> error(final String error) {
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
                formula.value(),
                () -> "formula: " + formula);
    }

    // toString.........................................................................................................

    @Test
    public void testToStringWithValue() {
        this.toStringAndCheck(
                this.createObject()
                        .setValue(this.value(VALUE)),
                TEXT + " (=" + VALUE + ")"
        );
    }

    // json.............................................................................................................

    @Test
    public void testUnmarshallBooleanFails() {
        this.unmarshallFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testUnmarshallNumberFails() {
        this.unmarshallFails(JsonNode.number(12));
    }

    @Test
    public void testUnmarshallArrayFails() {
        this.unmarshallFails(JsonNode.array());
    }

    @Test
    public void testUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"));
    }

    @Test
    public void testUnmarshallObjectEmptyFails() {
        this.unmarshallFails(JsonNode.object());
    }

    @Test
    public void testUnmarshallText() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(
                                SpreadsheetFormula.TEXT_PROPERTY,
                                JsonNode.string(TEXT)
                        ),
                formula(TEXT)
        );
    }

    @Test
    public void testUnmarshallTextAndToken() {
        final Optional<SpreadsheetFormulaParserToken> token = this.token();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                        .set(SpreadsheetFormula.TOKEN_PROPERTY, this.marshallContext().marshallWithType(token.get())),
                formula(TEXT)
                        .setToken(token)
        );
    }

    @Test
    public void testUnmarshallTextAndTokenAndDifferentTextIgnored() {
        final Optional<SpreadsheetFormulaParserToken> token = this.token();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(
                                SpreadsheetFormula.TEXT_PROPERTY,
                                JsonNode.string("Different text parse token")
                        )
                        .set(
                                SpreadsheetFormula.TOKEN_PROPERTY,
                                this.marshallContext()
                                        .marshallWithType(token.get())
                        ),
                SpreadsheetFormula.EMPTY
                        .setToken(token)
        );
    }

    @Test
    public void testUnmarshallTextAndExpression() {
        final Optional<Expression> expression = this.expression();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(SpreadsheetFormula.TEXT_PROPERTY, JsonNode.string(TEXT))
                        .set(
                                SpreadsheetFormula.EXPRESSION_PROPERTY,
                                this.marshallContext()
                                        .marshallWithType(
                                                expression.get()
                                        )
                        ),
                formula(TEXT)
                        .setExpression(expression)
        );
    }

    @Test
    public void testUnmarshallTextTokenAndExpression() {
        final Optional<SpreadsheetFormulaParserToken> token = this.token();
        final Optional<Expression> expression = this.expression();

        this.unmarshallAndCheck(JsonNode.object()
                        .set(
                                SpreadsheetFormula.TEXT_PROPERTY,
                                JsonNode.string(TEXT)
                        ).set(
                                SpreadsheetFormula.TOKEN_PROPERTY,
                                this.marshallContext()
                                        .marshallWithType(token.get())
                        ).set(
                                SpreadsheetFormula.EXPRESSION_PROPERTY,
                                this.marshallContext()
                                        .marshallWithType(expression.get())
                        ),
                formula(TEXT)
                        .setToken(token)
                        .setExpression(expression)
        );
    }

    @Test
    public void testUnmarshallTextAndValue() {
        this.unmarshallAndCheck(JsonNode.object()
                        .set(
                                SpreadsheetFormula.TEXT_PROPERTY,
                                JsonNode.string(TEXT)
                        ).set(
                                SpreadsheetFormula.VALUE_PROPERTY,
                                JsonNode.number(VALUE)
                        ),
                formula(TEXT)
                        .setValue(
                                Optional.of(VALUE)
                        )
        );
    }

    // marshall.......................................................................................................

    @Test
    public void testMarshallText() {
        this.marshallAndCheck(
                formula(TEXT),
                "{ \"text\": \"1+2\"}"
        );
    }

    @Test
    public void testMarshallTextAndToken() {
        this.marshallAndCheck(
                formula(TEXT)
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
                formula(TEXT)
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
                formula(TEXT)
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
                formula(TEXT)
                        .setValue(Optional.of("abc123")),
                "{ \"text\": \"1+2\", \"value\": \"abc123\"}"
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
                formula(TEXT)
                        .setValue(
                                Optional.of(123L)
                        )
        );
    }

    @Test
    public void testMarshallRoundtripTextValueAndExpression() {
        this.marshallRoundTripTwiceAndCheck(
                formula(TEXT)
                        .setValue(Optional.of(123L))
                        .setExpression(this.expression())
        );
    }

    @Test
    public void testMarshallRoundtripTextAndError() {
        this.marshallRoundTripTwiceAndCheck(
                formula(TEXT)
                        .setValue(
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
        return SpreadsheetFormula.unmarshall(jsonNode, context);
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
