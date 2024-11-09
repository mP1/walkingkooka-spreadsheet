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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellQueryTest implements HasUrlFragmentTesting,
        HasTextTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellQuery>,
        ToStringTesting<SpreadsheetCellQuery>,
        ParseStringTesting<SpreadsheetCellQuery>,
        JsonNodeMarshallingTesting<SpreadsheetCellQuery> {

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
