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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.UnknownExpressionFunctionException;

import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEngineSpreadsheetExpressionEvaluationContextTest implements ClassTesting<SpreadsheetEngineSpreadsheetExpressionEvaluationContext>,
        SpreadsheetExpressionEvaluationContextTesting<SpreadsheetEngineSpreadsheetExpressionEvaluationContext>,
        ToStringTesting<SpreadsheetEngineSpreadsheetExpressionEvaluationContext> {

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static String CURRENCY = "CURR";

    private final static char DECIMAL = '.';

    private final static String EXPONENT = "e";

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DOUBLE;

    private final static char GROUP_SEPARATOR = ',';

    private final static char MINUS = '!';

    private final static char PERCENT = '#';

    private final static char PLUS = '@';

    private final static char VALUE_SEPARATOR = ',';

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
            .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"))
            .loadFromLocale()
            .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN, SpreadsheetFormatPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm"))
            .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetFormatPattern.parseTextFormatPattern("@"))
            .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL)
            .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT)
            .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
            .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, MINUS)
            .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, PLUS)
            .set(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, PERCENT)
            .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, 1)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
            .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);

    private final static Function<ExpressionReference, Optional<Optional<Object>>> REFERENCES = (reference) -> {
        Objects.requireNonNull(reference, "reference");
        throw new UnsupportedOperationException();
    };

    private final static Function<FunctionExpressionName, ExpressionFunction<?, ExpressionEvaluationContext>> FUNCTIONS = (name) -> {
        Objects.requireNonNull(name, "name");
        throw new UnknownExpressionFunctionException(name);
    };

    @Test
    public void testWithNullCellFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        null, // cell
                        SERVER_URL, // serverUrl
                        REFERENCES, // references
                        FUNCTIONS, // functions
                        null
                )
        );
    }

    @Test
    public void testWithNullServerUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        null, // serverUrl
                        REFERENCES, // references
                        FUNCTIONS, // functions
                        SpreadsheetEngineContexts.fake()// context
                )
        );
    }

    @Test
    public void testWithNullReferencesFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        SERVER_URL, // serverUrl
                        null, // references
                        FUNCTIONS, // functions
                        SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    public void testWithNullFunctionsFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        SERVER_URL, // serverUrl
                        REFERENCES, // references
                        null, // functions
                        SpreadsheetEngineContexts.fake() // context
                )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        SERVER_URL, // serverUrl
                        REFERENCES, // references
                        FUNCTIONS, // functions
                        null // context
                )
        );
    }

    // parseFormula.....................................................................................................

    @Test
    public void testParseFormulaNumber() {
        final String text = "123";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits(text, text)
                        ),
                        text
                )
        );
    }

    @Test
    public void testParseFormulaNumber2() {
        final String text = "1" + DECIMAL + "5";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits("1", "1"),
                                SpreadsheetParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL),
                                SpreadsheetParserToken.digits("5", "5")
                        ),
                        text
                )
        );
    }

    @Test
    public void testParseFormulaExpression() {
        final String text = "1+2";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.addition(
                        Lists.of(
                                SpreadsheetParserToken.number(
                                        Lists.of(
                                                SpreadsheetParserToken.digits("1", "1")
                                        ),
                                        "1"
                                ),
                                SpreadsheetParserToken.plusSymbol("+", "+"),
                                SpreadsheetParserToken.number(
                                        Lists.of(
                                                SpreadsheetParserToken.digits("2", "2")
                                        ),
                                        "2"
                                )
                        ),
                        text
                )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final SpreadsheetEngineContext context = SpreadsheetEngineContexts.fake();
        this.toStringAndCheck(
                SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.empty(), // cell
                        SERVER_URL, // serverUrl
                        REFERENCES, // references
                        FUNCTIONS,
                        context // context
                ),
                SERVER_URL + " " + context
        );
    }

    @Test
    public void testToStringWithCell() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("=1+2")
        );
        final SpreadsheetEngineContext context = SpreadsheetEngineContexts.fake();

        this.toStringAndCheck(
                SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                        Optional.of(cell), // cell
                        SERVER_URL, // serverUrl
                        REFERENCES, // references
                        FUNCTIONS,
                        context // context
                ),
                cell + " " + SERVER_URL + " " + context
        );
    }

    // SpreadsheetExpressionEvaluationContext...........................................................................

    @Override
    public SpreadsheetEngineSpreadsheetExpressionEvaluationContext createContext() {
        return SpreadsheetEngineSpreadsheetExpressionEvaluationContext.with(
                Optional.empty(), // cell
                SERVER_URL,
                REFERENCES, // references
                FUNCTIONS, // functions
                new FakeSpreadsheetEngineContext() {

                    @Override
                    public boolean isPure(final FunctionExpressionName name) {
                        Objects.requireNonNull(name, "name");
                        throw new UnknownExpressionFunctionException(name);
                    }

                    @Override
                    public SpreadsheetMetadata spreadsheetMetadata() {
                        return METADATA;
                    }
                }
        );
    }

    @Override
    public String currencySymbol() {
        return CURRENCY;
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL;
    }

    @Override
    public String exponentSymbol() {
        return EXPONENT;
    }

    @Override
    public char groupSeparator() {
        return GROUP_SEPARATOR;
    }

    @Override
    public MathContext mathContext() {
        return METADATA.mathContext();
    }

    @Override
    public char negativeSign() {
        return MINUS;
    }

    @Override
    public char percentageSymbol() {
        return PERCENT;
    }

    @Override
    public char positiveSign() {
        return PLUS;
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<SpreadsheetEngineSpreadsheetExpressionEvaluationContext> type() {
        return SpreadsheetEngineSpreadsheetExpressionEvaluationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
