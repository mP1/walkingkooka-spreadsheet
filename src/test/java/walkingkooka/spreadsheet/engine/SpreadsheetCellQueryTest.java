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
 * Copyright 2023 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.HashCodeEqualsDefinedTesting2;
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
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
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
        final Expression expression = Expression.value(123);

        final SpreadsheetCellQuery query = SpreadsheetCellQuery.with(expression);
        this.checkEquals(
                expression,
                query.expression()
        );
    }

    // setPath..........................................................................................................

    @Test
    public void testSetExpressionNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> new SpreadsheetCellQuery(Expression.value(123))
                        .setExpression(null)
        );
    }

    @Test
    public void testSetExpressionSame() {
        final SpreadsheetCellQuery query = new SpreadsheetCellQuery(Expression.value(true));
        assertSame(
                query,
                query.setExpression(query.expression())
        );
    }

    @Test
    public void testSetExpressionDifferent() {
        final SpreadsheetCellQuery query = new SpreadsheetCellQuery(Expression.value(true));
        final Expression different = Expression.value("different");

        this.checkEquals(
                new SpreadsheetCellQuery(different),
                query.setExpression(different)
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
                new IllegalArgumentException("Invalid character '+' at 1 in \"1+\"")
        );
    }

    @Test
    public void testParseBooleanLiteral() {
        this.parseStringAndCheck(
                "true",
                new SpreadsheetCellQuery(
                        Expression.reference(
                                SpreadsheetSelection.labelName("true")
                        )
                )
        );
    }

    @Test
    public void testParseNumberLiteral() {
        this.parseStringAndCheck(
                "111",
                new SpreadsheetCellQuery(
                        Expression.value(
                                SpreadsheetCellQuery.EXPRESSION_NUMBER_KIND.create(111)
                        )
                )
        );
    }

    @Test
    public void testParseStringLiteralFails() {
        this.parseStringFails(
                "'Hello",
                new IllegalArgumentException("Invalid character '\\'' at 0 in \"'Hello\"")
        );
    }

    @Test
    public void testParseAddition() {
        this.parseStringAndCheck(
                "11+22",
                new SpreadsheetCellQuery(
                        Expression.add(
                                Expression.value(
                                        SpreadsheetCellQuery.EXPRESSION_NUMBER_KIND.create(11)
                                ),
                                Expression.value(
                                        SpreadsheetCellQuery.EXPRESSION_NUMBER_KIND.create(22)
                                )
                        )
                )
        );
    }

    @Test
    public void testParseFunctionExpression() {
        this.parseStringAndCheck(
                "abc(111)",
                new SpreadsheetCellQuery(
                        Expression.call(
                                Expression.namedFunction(
                                        ExpressionFunctionName.with("abc")
                                ),
                                Lists.of(
                                        Expression.value(
                                                SpreadsheetCellQuery.EXPRESSION_NUMBER_KIND.create(111)
                                        )
                                )
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
    public void testEqualsDifferentExpression() {
        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        Expression.value("different")
                )
        );
    }

    @Override
    public SpreadsheetCellQuery createObject() {
        return new SpreadsheetCellQuery(
                Expression.value("value123")
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
}
