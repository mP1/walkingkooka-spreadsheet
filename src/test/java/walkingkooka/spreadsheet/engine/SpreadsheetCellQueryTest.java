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
import walkingkooka.CanBeEmptyTesting;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.Url;
import walkingkooka.net.UrlFragment;
import walkingkooka.net.UrlQueryString;
import walkingkooka.net.http.HttpEntity;
import walkingkooka.net.http.HttpProtocolVersion;
import walkingkooka.net.http.HttpTransport;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.HttpRequests;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellQueryTest implements HasUrlFragmentTesting,
        CanBeEmptyTesting,
        HasTextTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellQuery>,
        ToStringTesting<SpreadsheetCellQuery>,
        ParseStringTesting<SpreadsheetCellQuery>,
        JsonNodeMarshallingTesting<SpreadsheetCellQuery> {

    private final static Optional<SpreadsheetCellRangeReferencePath> PATH = Optional.of(
            SpreadsheetCellRangeReferencePath.LRTD
    );

    private final static OptionalInt OFFSET = OptionalInt.of(123);
    private final static OptionalInt MAX = OptionalInt.of(456);

    private final static Optional<String> VALUE_TYPE = Optional.of(SpreadsheetValueType.ANY);

    private final static Optional<String> QUERY = Optional.of("=789+blah()");

    // tests............................................................................................................

    @Test
    public void testEmpty() {
        this.checkEquals(
                SpreadsheetCellQuery.empty(),
                new SpreadsheetCellQuery(
                        Optional.empty(),
                        OptionalInt.empty(),
                        OptionalInt.empty(),
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    // setPath..........................................................................................................

    @Test
    public void testSetPathNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellQuery.empty()
                        .setPath(null)
        );
    }

    @Test
    public void testSetPathSame() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        assertSame(
                find,
                find.setPath(find.path())
        );
    }

    @Test
    public void testSetPathDifferent() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        final Optional<SpreadsheetCellRangeReferencePath> path = Optional.of(
                SpreadsheetCellRangeReferencePath.RLTD
        );

        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        path,
                        OFFSET,
                        MAX,
                        VALUE_TYPE,
                        QUERY
                ),
                find.setPath(path)
        );
    }

    // setOffset..........................................................................................................

    @Test
    public void testSetOffsetNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellQuery.empty()
                        .setOffset(null)
        );
    }

    @Test
    public void testSetOffsetSame() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        assertSame(
                find,
                find.setOffset(find.max())
        );
    }

    @Test
    public void testSetOffsetDifferent() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        final OptionalInt offset = OptionalInt.of(
                999
        );

        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        offset,
                        MAX,
                        VALUE_TYPE,
                        QUERY
                ),
                find.setOffset(offset)
        );
    }

    // setMax..........................................................................................................

    @Test
    public void testSetMaxNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellQuery.empty()
                        .setMax(null)
        );
    }

    @Test
    public void testSetMaxSame() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        assertSame(
                find,
                find.setMax(find.max())
        );
    }

    @Test
    public void testSetMaxDifferent() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        final OptionalInt max = OptionalInt.of(
                999
        );

        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        OFFSET,
                        max,
                        VALUE_TYPE,
                        QUERY
                ),
                find.setMax(max)
        );
    }

    // setValueType..........................................................................................................

    @Test
    public void testSetValueTypeNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellQuery.empty()
                        .setValueType(null)
        );
    }

    @Test
    public void testSetValueTypeSame() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        assertSame(
                find,
                find.setValueType(find.valueType())
        );
    }

    @Test
    public void testSetValueTypeDifferent() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        final Optional<String> valueType = Optional.of(
                SpreadsheetValueType.TEXT
        );

        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        OFFSET,
                        MAX,
                        valueType,
                        QUERY
                ),
                find.setValueType(valueType)
        );
    }

    // setQuery..........................................................................................................

    @Test
    public void testSetQueryNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetCellQuery.empty()
                        .setQuery(null)
        );
    }

    @Test
    public void testSetQuerySame() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        assertSame(
                find,
                find.setQuery(find.query())
        );
    }

    @Test
    public void testSetQueryDifferent() {
        final SpreadsheetCellQuery find = SpreadsheetCellQuery.empty();
        final Optional<String> query = Optional.of(
                "different123"
        );

        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        OFFSET,
                        MAX,
                        VALUE_TYPE,
                        query
                ),
                find.setQuery(query)
        );
    }

    // set then empty...................................................................................................

    @Test
    public void testSetUntilEmpty() {
        assertSame(
                SpreadsheetCellQuery.empty(),
                this.createObject()
                        .setPath(Optional.empty())
                        .setOffset(OptionalInt.empty())
                        .setMax(OptionalInt.empty())
                        .setValueType(Optional.empty())
                        .setQuery(Optional.empty())
        );
    }

    // isEmpty.........................................................................................................

    @Test
    public void testIsEmptyWhenEmpty() {
        this.checkEquals(
                true,
                SpreadsheetCellQuery.empty().isEmpty()
        );
    }

    @Test
    public void testIsEmptyWhenNotEmpty() {
        this.checkEquals(
                false,
                this.createObject()
                        .isEmpty()
        );
    }

    // toUrlQueryString.................................................................................................

    @Test
    public void testToUrlQueryStringPath() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellQuery.empty()
                        .setPath(PATH),
                "cell-range-path=lrtd"
        );
    }

    @Test
    public void testToUrlQueryStringOffset() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellQuery.empty()
                        .setOffset(OFFSET),
                "offset=123"
        );
    }

    @Test
    public void testToUrlQueryStringMax() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellQuery.empty()
                        .setMax(MAX),
                "max=456"
        );
    }

    @Test
    public void testToUrlQueryStringValueType() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellQuery.empty()
                        .setValueType(
                                Optional.of(
                                        SpreadsheetValueType.NUMBER)
                        ),
                "value-type=number"
        );
    }

    @Test
    public void testToUrlQueryStringQuery() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellQuery.empty()
                        .setQuery(QUERY),
                "query=%3D789%2Bblah%28%29"
        );
    }

    @Test
    public void testToUrlQueryStringAllParameters() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellQuery.empty()
                        .setPath(PATH)
                        .setOffset(OFFSET)
                        .setMax(MAX)
                        .setValueType(VALUE_TYPE)
                        .setQuery(QUERY),
                "cell-range-path=lrtd&max=456&offset=123&query=%3D789%2Bblah%28%29&value-type=*"
        );
    }

    private void toUrlQueryStringAndCheck(final SpreadsheetCellQuery find,
                                          final String expected) {
        this.toUrlQueryStringAndCheck(
                find,
                UrlQueryString.parse(expected)
        );
    }

    private void toUrlQueryStringAndCheck(final SpreadsheetCellQuery find,
                                          final UrlQueryString expected) {
        this.checkEquals(
                expected,
                find.toUrlQueryString(),
                find::toString
        );
    }

    // text.............................................................................................................

    @Test
    public void testText() {
        this.textAndCheck(
                this.createObject(),
                "cell-range-path=lrtd&max=456&offset=123&query=%3D789%2Bblah%28%29&value-type=*"
        );
    }

    // parseString......................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testParseInvalidCellRangePathFails() {
        this.parseStringFails(
                "cell-range-path=XYZ",
                new IllegalArgumentException("Got \"XYZ\" expected one of lrtd, rltd, lrbu, rlbu, tdlr, tdrl, bulr, burl")
        );
    }

    @Test
    public void testParseInvalidMaxFails() {
        this.parseStringFails(
                "max=XYZ",
                new IllegalArgumentException("Invalid max=\"XYZ\"")
        );
    }

    @Test
    public void testParseInvalidMaxFails2() {
        this.parseStringFails(
                "max=-123",
                new IllegalArgumentException("Invalid max -123 < 0")
        );
    }

    @Test
    public void testParseInvalidOffsetFails() {
        this.parseStringFails(
                "offset=XYZ",
                new IllegalArgumentException("Invalid offset=\"XYZ\"")
        );
    }

    @Test
    public void testParseInvalidOffsetFails2() {
        this.parseStringFails(
                "offset=-456",
                new IllegalArgumentException("Invalid offset -456 < 0")
        );
    }

    @Test
    public void testParseInvalidOffsetFails3() {
        this.parseStringFails(
                "offset=-00456",
                new IllegalArgumentException("Invalid offset -456 < 0")
        );
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
                "",
                SpreadsheetCellQuery.empty()
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
                "cell-range-path=lrtd&max=456&offset=123&query=%3D789%2Bblah%28%29&value-type=*",
                this.createObject()
        );
    }

    @Test
    public void testParseCellRangePath() {
        this.parseStringAndCheck(
                "cell-range-path=lrtd",
                SpreadsheetCellQuery.empty()
                        .setPath(PATH)
        );
    }

    @Test
    public void testParseIncludesInvalidQuery() {
        this.parseStringAndCheck(
                "cell-range-path=lrtd&query=XYZ",
                SpreadsheetCellQuery.empty()
                        .setPath(PATH)
                        .setQuery(
                                Optional.of("XYZ")
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
    public void testExtractCellRangePath() {
        this.extractAndCheck(
                "cell-range-path=lrtd",
                SpreadsheetCellQuery.empty()
                        .setPath(PATH)
        );
    }

    @Test
    public void testExtractIncludesInvalidQuery() {
        this.extractAndCheck(
                "cell-range-path=lrtd&query=XYZ",
                SpreadsheetCellQuery.empty()
                        .setPath(PATH)
                        .setQuery(
                                Optional.of("XYZ")
                        )
        );
    }

    @Test
    public void testExtract() {
        this.extractAndCheck(
                "cell-range-path=lrtd&max=456&offset=123&query=%3D789%2Bblah%28%29&value-type=*",
                this.createObject()
        );
    }

    private void extractAndCheck(final String text,
                                 final SpreadsheetCellQuery expected) {
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
        final String queryString = "cell-range-path=lrtd&max=456&offset=123&query=%3D789%2Bblah%28%29&value-type=*";

        this.extractAndCheck0(
                HttpRequests.get(
                        HttpTransport.SECURED,
                        Url.parseRelative("/path123?" + queryString),
                        HttpProtocolVersion.VERSION_1_0,
                        HttpEntity.EMPTY
                ).routerParameters(),
                this.createObject()
        );
    }

    private void extractAndCheck0(final Map<HttpRequestAttribute<?>, ?> parameters,
                                  final SpreadsheetCellQuery expected) {
        this.checkEquals(
                expected,
                SpreadsheetCellQuery.extract(
                        parameters
                )
        );
    }

    // queryToExpression................................................................................................

    @Test
    public void testQueryToExpressionNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createObject().queryToExpression(null)
        );
    }

    @Test
    public void testQueryToExpressionWhenEmpty() {
        this.queryToExpressionAndCheck(
                SpreadsheetCellQuery.empty(),
                SpreadsheetEngineContexts.fake(),
                Optional.empty()
        );
    }

    @Test
    public void testQueryToExpressionWhenPresent() {
        final String query = "true()";
        final SpreadsheetParserToken token = SpreadsheetParserToken.functionName(
                SpreadsheetFunctionName.with("true"),
                "true"
        );
        final Optional<Expression> expression = Optional.of(
                Expression.value(true)
        );

        this.queryToExpressionAndCheck(
                SpreadsheetCellQuery.parse("query=" + query),
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public SpreadsheetParserToken parseFormula(final TextCursor formula) {
                        final TextCursorSavePoint begin = formula.save();
                        formula.end();

                        checkEquals(
                                query,
                                begin.textBetween()
                                        .toString()
                        );

                        return token;
                    }

                    @Override
                    public Optional<Expression> toExpression(final SpreadsheetParserToken t) {
                        checkEquals(
                                token,
                                t
                        );
                        return expression;
                    }
                },
                expression
        );
    }

    private void queryToExpressionAndCheck(final SpreadsheetCellQuery find,
                                           final SpreadsheetEngineContext context,
                                           final Optional<Expression> expected) {
        this.checkEquals(
                expected,
                find.queryToExpression(context),
                find::toString
        );
    }
    
    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        Optional.of(
                                SpreadsheetCellRangeReferencePath.BULR
                        ),
                        OFFSET,
                        MAX,
                        VALUE_TYPE,
                        QUERY
                )
        );
    }

    @Test
    public void testEqualsDifferentOffset() {
        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        OptionalInt.of(999),
                        MAX,
                        VALUE_TYPE,
                        QUERY
                )
        );
    }

    @Test
    public void testEqualsDifferentMax() {
        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        OFFSET,
                        OptionalInt.of(9999),
                        VALUE_TYPE,
                        QUERY
                )
        );
    }

    @Test
    public void testEqualsDifferentValueType() {
        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        OFFSET,
                        MAX,
                        Optional.of(SpreadsheetValueType.DATE),
                        QUERY
                )
        );
    }

    @Test
    public void testEqualsDifferentQuery() {
        this.checkNotEquals(
                new SpreadsheetCellQuery(
                        PATH,
                        OFFSET,
                        MAX,
                        VALUE_TYPE,
                        Optional.of("different")
                )
        );
    }

    // urlFragment......................................................................................................

    @Test
    public void testUrlFragmentPath() {
        this.urlFragmentAndCheck(
                SpreadsheetCellQuery.empty()
                        .setPath(
                                Optional.of(SpreadsheetCellRangeReferencePath.BULR)
                        ),
                UrlFragment.parse("path/BULR")
        );
    }

    @Test
    public void testUrlFragmentOffset() {
        this.urlFragmentAndCheck(
                SpreadsheetCellQuery.empty()
                        .setOffset(
                                OptionalInt.of(123)
                        ),
                UrlFragment.parse("offset/123")
        );
    }

    @Test
    public void testUrlFragmentMax() {
        this.urlFragmentAndCheck(
                SpreadsheetCellQuery.empty()
                        .setMax(
                                OptionalInt.of(456)
                        ),
                UrlFragment.parse("max/456")
        );
    }

    @Test
    public void testUrlFragmentValueType() {
        this.urlFragmentAndCheck(
                SpreadsheetCellQuery.empty()
                        .setValueType(
                                Optional.of(SpreadsheetValueType.NUMBER)
                        ),
                UrlFragment.parse("value-type/number")
        );
    }

    @Test
    public void testUrlFragmentQuery() {
        this.urlFragmentAndCheck(
                SpreadsheetCellQuery.empty()
                        .setQuery(
                                Optional.of("query123")
                        ),
                UrlFragment.parse("query/query123")
        );
    }

    @Test
    public void testUrlFragmentAll() {
        this.urlFragmentAndCheck(
                this.createObject(),
                UrlFragment.parse("path/LRTD/offset/123/max/456/value-type/*/query/%3D789%2Bblah%28%29")
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringEmpty() {
        this.toStringAndCheck(
                SpreadsheetCellQuery.empty(),
                ""
        );
    }

    @Test
    public void testToStringNonEmpty() {
        this.toStringAndCheck(
                new SpreadsheetCellQuery(
                        Optional.of(SpreadsheetCellRangeReferencePath.BULR),
                        OptionalInt.of(123), // offset
                        OptionalInt.of(456), // max
                        Optional.of(SpreadsheetValueType.NUMBER),
                        Optional.of("query789")
                ),
                "path/BULR/offset/123/max/456/value-type/number/query/query789"
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

    // Object...........................................................................................................

    public SpreadsheetCellQuery createObject() {
        return new SpreadsheetCellQuery(
                PATH,
                OFFSET,
                MAX,
                VALUE_TYPE,
                QUERY
        );
    }
}
