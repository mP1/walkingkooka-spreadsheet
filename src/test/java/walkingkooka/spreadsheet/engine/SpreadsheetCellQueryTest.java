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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.EndOfTextException;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.Url;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellQueryTest implements HasUrlFragmentTesting,
    HasTextTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetCellQuery>,
    ToStringTesting<SpreadsheetCellQuery>,
    ParseStringTesting<SpreadsheetCellQuery>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<SpreadsheetCellQuery> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellQuery.with(null)
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetFormulaParserToken parserToken = SpreadsheetFormulaParserToken.text(
            Lists.of(
                SpreadsheetFormulaParserToken.textLiteral("Hello", "\"Hello\"")
            ),
            "\"Hello\""
        );

        final SpreadsheetCellQuery query = SpreadsheetCellQuery.with(parserToken);
        this.checkEquals(
            parserToken,
            query.parserToken()
        );
    }

    // setParserToken...................................................................................................

    @Test
    public void testSetParserTokenNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> new SpreadsheetCellQuery(textLiteral("Hello"))
                .setParserToken(null)
        );
    }

    @Test
    public void testSetParserTokenSame() {
        final SpreadsheetCellQuery query = new SpreadsheetCellQuery(textLiteral("Hello"));
        assertSame(
            query,
            query.setParserToken(query.parserToken())
        );
    }

    @Test
    public void testSetParserTokenDifferent() {
        final SpreadsheetCellQuery query = new SpreadsheetCellQuery(textLiteral("Hello"));
        final SpreadsheetFormulaParserToken different = textLiteral("Different");

        this.checkEquals(
            new SpreadsheetCellQuery(different),
            query.setParserToken(different)
        );
    }

    // text.............................................................................................................

    @Test
    public void testText() {
        final String text = "1+2";

        this.textAndCheck(
            SpreadsheetCellQuery.parse(text),
            text
        );
    }

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseInvalidExpressionFails() {
        this.parseStringFails(
            "1+",
            new EndOfTextException("End of text at (3,1) expected LAMBDA_FUNCTION | NAMED_FUNCTION | \"true\" | \"false\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\"")
        );
    }

    @Test
    public void testParseNumberLiteral() {
        final String text = "111";

        this.parseStringAndCheck(
            text,
            new SpreadsheetCellQuery(
                numberLiteral(text)
            )
        );
    }

    @Test
    public void testParseStringLiteralFails() {
        this.parseStringFails(
            "'Hello",
            new InvalidCharacterException("'Hello", 0)
                .appendToMessage("expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"true\" | \"false\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\"")
        );
    }

    @Test
    public void testParseAddition() {
        final String text = "11+22";

        this.parseStringAndCheck(
            text,
            new SpreadsheetCellQuery(
                SpreadsheetFormulaParserToken.addition(
                    Lists.of(
                        numberLiteral("11"),
                        SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                        numberLiteral("22")
                    ),
                    text
                )
            )
        );
    }

    @Test
    public void testParseFunctionExpression() {
        final String text = "abc(111)";

        this.parseStringAndCheck(
            text,
            new SpreadsheetCellQuery(
                SpreadsheetFormulaParserToken.namedFunction(
                    Lists.of(
                        SpreadsheetFormulaParserToken.functionName(
                            SpreadsheetFunctionName.with("abc"),
                            "abc"
                        ),
                        SpreadsheetFormulaParserToken.functionParameters(
                            Lists.of(
                                SpreadsheetFormulaParserToken.parenthesisOpenSymbol("(", "("),
                                numberLiteral("111"),
                                SpreadsheetFormulaParserToken.parenthesisCloseSymbol(")", ")")
                            ),
                            "(111)"
                        )
                    ),
                    text
                )
            )
        );
    }

    @Override
    public SpreadsheetCellQuery parseString(final String text) {
        return SpreadsheetCellQuery.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> thrown) {
        return thrown;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException thrown) {
        return thrown;
    }

    // extract..........................................................................................................

    @Test
    public void testExtractWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellQuery.extract(null)
        );
    }

    @Test
    public void testExtractParameterMissing() {
        this.extractAndCheck(
            "different=1",
            Optional.empty()
        );
    }

    @Test
    public void testExtract() {
        this.extractAndCheck(
            "query=1*2",
            Optional.of(
                SpreadsheetCellQuery.parse("1*2")
            )
        );
    }

    private void extractAndCheck(final String text,
                                 final Optional<SpreadsheetCellQuery> expected) {
        this.extractAndCheck0(
            Cast.to(
                UrlQueryString.parse(text)
                    .parameters()
            ),
            expected
        );
    }

    @Test
    public void testExtractFromRequest() {
        final String queryString = "1*2";

        this.extractAndCheck0(
            HttpRequests.get(
                HttpTransport.SECURED,
                Url.parseRelative("/path123?query=" + queryString),
                HttpProtocolVersion.VERSION_1_0,
                HttpEntity.EMPTY
            ).routerParameters(),
            SpreadsheetCellQuery.parse(queryString)
        );
    }

    private void extractAndCheck0(final Map<HttpRequestAttribute<?>, ?> parameters,
                                  final SpreadsheetCellQuery expected) {
        this.extractAndCheck0(
            parameters,
            Optional.of(expected)
        );
    }

    private void extractAndCheck0(final Map<HttpRequestAttribute<?>, ?> parameters,
                                  final Optional<SpreadsheetCellQuery> expected) {
        this.checkEquals(
            expected,
            SpreadsheetCellQuery.extract(
                parameters
            )
        );
    }

    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentParserTOken() {
        this.checkNotEquals(
            new SpreadsheetCellQuery(
                textLiteral("different")
            )
        );
    }

    @Override
    public SpreadsheetCellQuery createObject() {
        return new SpreadsheetCellQuery(
            textLiteral("value123")
        );
    }

    // urlFragment......................................................................................................

    @Test
    public void testUrlFragment() {
        final String text = "1+2";

        this.urlFragmentAndCheck(
            SpreadsheetCellQuery.parse(text),
            UrlFragment.parse(text)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final String text = "1+2";

        this.toStringAndCheck(
            SpreadsheetCellQuery.parse(text),
            text
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetCellQuery unmarshall(final JsonNode json,
                                           final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellQuery.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetCellQuery createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellQuery> type() {
        return SpreadsheetCellQuery.class;
    }

    // helpers..........................................................................................................

    private static SpreadsheetFormulaParserToken numberLiteral(final String number) {
        return SpreadsheetFormulaParserToken.number(
            Lists.of(
                SpreadsheetFormulaParserToken.digits(
                    number,
                    number
                )
            ),
            number
        );
    }

    private static SpreadsheetFormulaParserToken textLiteral(final String text) {
        final String quoted = '"' + text + '"'; // not perfect but good enuff for here

        return SpreadsheetFormulaParserToken.text(
            Lists.of(
                SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\""),
                SpreadsheetFormulaParserToken.textLiteral(
                    text,
                    text
                ),
                SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\"")
            ),
            quoted
        );
    }
}
