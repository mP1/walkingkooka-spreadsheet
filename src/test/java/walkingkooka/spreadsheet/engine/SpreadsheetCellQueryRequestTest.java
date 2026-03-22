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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.spreadsheet.value.SpreadsheetValueType;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.validation.ValueType;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellQueryRequestTest implements HasUrlFragmentTesting,
    CanBeEmptyTesting,
    HasTextTesting,
    HashCodeEqualsDefinedTesting2<SpreadsheetCellQueryRequest>,
    ToStringTesting<SpreadsheetCellQueryRequest>,
    ParseStringTesting<SpreadsheetCellQueryRequest>,
    JsonNodeMarshallingTesting<SpreadsheetCellQueryRequest> {

    private final static Optional<SpreadsheetCellRangeReferencePath> PATH = Optional.of(
        SpreadsheetCellRangeReferencePath.LRTD
    );

    private final static OptionalInt OFFSET = OptionalInt.of(123);

    private final static OptionalInt COUNT = OptionalInt.of(456);

    private final static Optional<ValueType> VALUE_TYPE = Optional.of(ValueType.ANY);

    private final static Optional<SpreadsheetCellQuery> QUERY = Optional.of(
        SpreadsheetCellQuery.parse("789+blah()")
    );

    // tests............................................................................................................

    @Test
    public void testEmpty() {
        this.checkEquals(
            SpreadsheetCellQueryRequest.empty(),
            new SpreadsheetCellQueryRequest(
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
            () -> SpreadsheetCellQueryRequest.empty()
                .setPath(null)
        );
    }

    @Test
    public void testSetPathSame() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        assertSame(
            find,
            find.setPath(find.path())
        );
    }

    @Test
    public void testSetPathDifferent() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        final Optional<SpreadsheetCellRangeReferencePath> path = Optional.of(
            SpreadsheetCellRangeReferencePath.RLTD
        );

        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                path,
                OFFSET,
                COUNT,
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
            () -> SpreadsheetCellQueryRequest.empty()
                .setOffset(null)
        );
    }

    @Test
    public void testSetOffsetInvalidFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetCellQueryRequest.empty()
                .setOffset(
                    OptionalInt.of(-456)
                )
        );
        this.checkEquals(
            "Invalid offset -456 < 0",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetOffsetSame() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        assertSame(
            find,
            find.setOffset(find.count())
        );
    }

    @Test
    public void testSetOffsetDifferent() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        final OptionalInt offset = OptionalInt.of(
            999
        );

        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                PATH,
                offset,
                COUNT,
                VALUE_TYPE,
                QUERY
            ),
            find.setOffset(offset)
        );
    }

    // setCount..........................................................................................................

    @Test
    public void testSetCountNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellQueryRequest.empty()
                .setCount(null)
        );
    }

    @Test
    public void testSetCountInvalidFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetCellQueryRequest.empty()
                .setCount(
                    OptionalInt.of(-123)
                )
        );
        this.checkEquals(
            "Invalid count -123 < 0",
            thrown.getMessage()
        );
    }

    @Test
    public void testSetCountSame() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        assertSame(
            find,
            find.setCount(find.count())
        );
    }

    @Test
    public void testSetCountDifferent() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        final OptionalInt count = OptionalInt.of(
            999
        );

        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                PATH,
                OFFSET,
                count,
                VALUE_TYPE,
                QUERY
            ),
            find.setCount(count)
        );
    }

    // setValueType..........................................................................................................

    @Test
    public void testSetValueTypeNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetCellQueryRequest.empty()
                .setValueType(null)
        );
    }

    @Test
    public void testSetValueTypeSame() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        assertSame(
            find,
            find.setValueType(find.valueType())
        );
    }

    @Test
    public void testSetValueTypeDifferent() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        final Optional<ValueType> valueType = Optional.of(
            SpreadsheetValueType.TEXT
        );

        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                PATH,
                OFFSET,
                COUNT,
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
            () -> SpreadsheetCellQueryRequest.empty()
                .setQuery(null)
        );
    }

    @Test
    public void testSetQuerySame() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        assertSame(
            find,
            find.setQuery(find.query())
        );
    }

    @Test
    public void testSetQueryDifferent() {
        final SpreadsheetCellQueryRequest find = SpreadsheetCellQueryRequest.empty();
        final Optional<SpreadsheetCellQuery> query = Optional.of(
            SpreadsheetCellQuery.parse("different123()")
        );

        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                PATH,
                OFFSET,
                COUNT,
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
            SpreadsheetCellQueryRequest.empty(),
            this.createObject()
                .setPath(Optional.empty())
                .setOffset(OptionalInt.empty())
                .setCount(OptionalInt.empty())
                .setValueType(Optional.empty())
                .setQuery(Optional.empty())
        );
    }

    // isEmpty.........................................................................................................

    @Test
    public void testIsEmptyWhenEmpty() {
        this.checkEquals(
            true,
            SpreadsheetCellQueryRequest.empty().isEmpty()
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
            SpreadsheetCellQueryRequest.empty()
                .setPath(PATH),
            "cell-range-path=LRTD"
        );
    }

    @Test
    public void testToUrlQueryStringOffset() {
        this.toUrlQueryStringAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setOffset(OFFSET),
            "offset=123"
        );
    }

    @Test
    public void testToUrlQueryStringCount() {
        this.toUrlQueryStringAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setCount(COUNT),
            "count=456"
        );
    }

    @Test
    public void testToUrlQueryStringValueType() {
        this.toUrlQueryStringAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setValueType(
                    Optional.of(
                        SpreadsheetValueType.NUMBER
                    )
                ),
            "value-type=number"
        );
    }

    @Test
    public void testToUrlQueryStringQuery() {
        this.toUrlQueryStringAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setQuery(QUERY),
            "query=789%2Bblah%28%29"
        );
    }

    @Test
    public void testToUrlQueryStringAllParameters() {
        this.toUrlQueryStringAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setPath(PATH)
                .setOffset(OFFSET)
                .setCount(COUNT)
                .setValueType(VALUE_TYPE)
                .setQuery(QUERY),
            "cell-range-path=LRTD&count=456&offset=123&query=789%2Bblah%28%29&value-type=*"
        );
    }

    private void toUrlQueryStringAndCheck(final SpreadsheetCellQueryRequest find,
                                          final String expected) {
        this.toUrlQueryStringAndCheck(
            find,
            UrlQueryString.parse(expected)
        );
    }

    private void toUrlQueryStringAndCheck(final SpreadsheetCellQueryRequest find,
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
            "/path/LRTD/offset/123/count/456/value-type/*/query/789+blah()"
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
            "/path/XYZ",
            new IllegalArgumentException("Got \"XYZ\" expected one of LRTD, RLTD, LRBU, RLBU, TDLR, TDRL, BULR, BURL")
        );
    }

    @Test
    public void testParseInvalidCountFails() {
        this.parseStringFails(
            "/count/XYZ",
            new IllegalArgumentException("Invalid count got \"XYZ\"")
        );
    }

    @Test
    public void testParseInvalidCountFails2() {
        this.parseStringFails(
            "/count/-123",
            new IllegalArgumentException("Invalid count -123 < 0")
        );
    }

    @Test
    public void testParseInvalidOffsetFails() {
        this.parseStringFails(
            "/offset/XYZ",
            new IllegalArgumentException("Invalid offset got \"XYZ\"")
        );
    }

    @Test
    public void testParseInvalidOffsetFails2() {
        this.parseStringFails(
            "/offset/-456",
            new IllegalArgumentException("Invalid offset -456 < 0")
        );
    }

    @Test
    public void testParseInvalidOffsetFails3() {
        this.parseStringFails(
            "/offset/-00456",
            new IllegalArgumentException("Invalid offset -456 < 0")
        );
    }

    @Test
    public void testParseEmpty() {
        this.parseStringAndCheck(
            "",
            SpreadsheetCellQueryRequest.empty()
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "/path/LRTD/offset/123/count/456/value-type/*/query/789+blah()",
            this.createObject()
        );
    }

    @Test
    public void testParseCellRangePath() {
        this.parseStringAndCheck(
            "/path/LRTD",
            SpreadsheetCellQueryRequest.empty()
                .setPath(PATH)
        );
    }

    @Test
    public void testParseIncludesQuery() {
        this.parseStringAndCheck(
            "/path/LRTD/query/XYZ()",
            SpreadsheetCellQueryRequest.empty()
                .setPath(PATH)
                .setQuery(
                    Optional.of(
                        SpreadsheetCellQuery.parse("XYZ()")
                    )
                )
        );
    }

    @Override
    public SpreadsheetCellQueryRequest parseString(final String text) {
        return SpreadsheetCellQueryRequest.parse(text);
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
            () -> SpreadsheetCellQueryRequest.extract(null)
        );
    }

    @Test
    public void testExtractCellRangePath() {
        this.extractAndCheck(
            "cell-range-path=LRTD",
            SpreadsheetCellQueryRequest.empty()
                .setPath(PATH)
        );
    }

    @Test
    public void testExtractIncludesQuery() {
        this.extractAndCheck(
            "cell-range-path=LRTD&query=XYZ()",
            SpreadsheetCellQueryRequest.empty()
                .setPath(PATH)
                .setQuery(
                    Optional.of(
                        SpreadsheetCellQuery.parse("XYZ()")
                    )
                )
        );
    }

    @Test
    public void testExtract() {
        this.extractAndCheck(
            "cell-range-path=LRTD&count=456&offset=123&query=789%2Bblah%28%29&value-type=*",
            this.createObject()
        );
    }

    private void extractAndCheck(final String text,
                                 final SpreadsheetCellQueryRequest expected) {
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
        final String queryString = "cell-range-path=LRTD&count=456&offset=123&query=789%2Bblah%28%29&value-type=*";

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
                                  final SpreadsheetCellQueryRequest expected) {
        this.checkEquals(
            expected,
            SpreadsheetCellQueryRequest.extract(
                parameters
            )
        );
    }

    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                Optional.of(
                    SpreadsheetCellRangeReferencePath.BULR
                ),
                OFFSET,
                COUNT,
                VALUE_TYPE,
                QUERY
            )
        );
    }

    @Test
    public void testEqualsDifferentOffset() {
        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                PATH,
                OptionalInt.of(999),
                COUNT,
                VALUE_TYPE,
                QUERY
            )
        );
    }

    @Test
    public void testEqualsDifferentCount() {
        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
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
            new SpreadsheetCellQueryRequest(
                PATH,
                OFFSET,
                COUNT,
                Optional.of(SpreadsheetValueType.DATE),
                QUERY
            )
        );
    }

    @Test
    public void testEqualsDifferentQuery() {
        this.checkNotEquals(
            new SpreadsheetCellQueryRequest(
                PATH,
                OFFSET,
                COUNT,
                VALUE_TYPE,
                Optional.of(
                    SpreadsheetCellQuery.parse("different()")
                )
            )
        );
    }

    // urlFragment......................................................................................................

    @Test
    public void testUrlFragmentWhenEmpty() {
        this.urlFragmentAndCheck(
            SpreadsheetCellQueryRequest.empty(),
            UrlFragment.EMPTY
        );
    }

    @Test
    public void testUrlFragmentPath() {
        this.urlFragmentAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setPath(
                    Optional.of(SpreadsheetCellRangeReferencePath.BULR)
                ),
            UrlFragment.parse("/path/BULR")
        );
    }

    @Test
    public void testUrlFragmentOffset() {
        this.urlFragmentAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setOffset(
                    OptionalInt.of(123)
                ),
            UrlFragment.parse("/offset/123")
        );
    }

    @Test
    public void testUrlFragmentCount() {
        this.urlFragmentAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setCount(
                    OptionalInt.of(456)
                ),
            UrlFragment.parse("/count/456")
        );
    }

    @Test
    public void testUrlFragmentValueType() {
        this.urlFragmentAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setValueType(
                    Optional.of(SpreadsheetValueType.NUMBER)
                ),
            UrlFragment.parse("/value-type/number")
        );
    }

    @Test
    public void testUrlFragmentQuery() {
        this.urlFragmentAndCheck(
            SpreadsheetCellQueryRequest.empty()
                .setQuery(
                    Optional.of(
                        SpreadsheetCellQuery.parse("query123()")
                    )
                ),
            UrlFragment.parse("/query/query123()")
        );
    }

    @Test
    public void testUrlFragmentAll() {
        this.urlFragmentAndCheck(
            this.createObject(),
            UrlFragment.parse("/path/LRTD/offset/123/count/456/value-type/*/query/789%2Bblah%28%29")
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToStringEmpty() {
        this.toStringAndCheck(
            SpreadsheetCellQueryRequest.empty(),
            ""
        );
    }

    @Test
    public void testToStringNonEmpty() {
        this.toStringAndCheck(
            new SpreadsheetCellQueryRequest(
                Optional.of(SpreadsheetCellRangeReferencePath.BULR),
                OptionalInt.of(123), // offset
                OptionalInt.of(456), // count
                Optional.of(SpreadsheetValueType.NUMBER),
                Optional.of(
                    SpreadsheetCellQuery.parse("query789()")
                )
            ),
            "/path/BULR/offset/123/count/456/value-type/number/query/query789()"
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetCellQueryRequest unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellQueryRequest.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetCellQueryRequest createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellQueryRequest> type() {
        return SpreadsheetCellQueryRequest.class;
    }

    // Object...........................................................................................................

    @Override
    public SpreadsheetCellQueryRequest createObject() {
        return new SpreadsheetCellQueryRequest(
            PATH,
            OFFSET,
            COUNT,
            VALUE_TYPE,
            QUERY
        );
    }
}
