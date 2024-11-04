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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.text.HasTextTesting;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellFindTest implements HasUrlFragmentTesting,
        CanBeEmptyTesting,
        HasTextTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellFind>,
        ToStringTesting<SpreadsheetCellFind>,
        ParseStringTesting<SpreadsheetCellFind> {

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
                SpreadsheetCellFind.empty(),
                new SpreadsheetCellFind(
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
                () -> SpreadsheetCellFind.empty()
                        .setPath(null)
        );
    }

    @Test
    public void testSetPathSame() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        assertSame(
                find,
                find.setPath(find.path())
        );
    }

    @Test
    public void testSetPathDifferent() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        final Optional<SpreadsheetCellRangeReferencePath> path = Optional.of(
                SpreadsheetCellRangeReferencePath.RLTD
        );

        this.checkNotEquals(
                new SpreadsheetCellFind(
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
                () -> SpreadsheetCellFind.empty()
                        .setOffset(null)
        );
    }

    @Test
    public void testSetOffsetSame() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        assertSame(
                find,
                find.setOffset(find.max())
        );
    }

    @Test
    public void testSetOffsetDifferent() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        final OptionalInt offset = OptionalInt.of(
                999
        );

        this.checkNotEquals(
                new SpreadsheetCellFind(
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
                () -> SpreadsheetCellFind.empty()
                        .setMax(null)
        );
    }

    @Test
    public void testSetMaxSame() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        assertSame(
                find,
                find.setMax(find.max())
        );
    }

    @Test
    public void testSetMaxDifferent() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        final OptionalInt max = OptionalInt.of(
                999
        );

        this.checkNotEquals(
                new SpreadsheetCellFind(
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
                () -> SpreadsheetCellFind.empty()
                        .setValueType(null)
        );
    }

    @Test
    public void testSetValueTypeSame() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        assertSame(
                find,
                find.setValueType(find.valueType())
        );
    }

    @Test
    public void testSetValueTypeDifferent() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        final Optional<String> valueType = Optional.of(
                SpreadsheetValueType.TEXT
        );

        this.checkNotEquals(
                new SpreadsheetCellFind(
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
                () -> SpreadsheetCellFind.empty()
                        .setQuery(null)
        );
    }

    @Test
    public void testSetQuerySame() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        assertSame(
                find,
                find.setQuery(find.query())
        );
    }

    @Test
    public void testSetQueryDifferent() {
        final SpreadsheetCellFind find = SpreadsheetCellFind.empty();
        final Optional<String> query = Optional.of(
                "different123"
        );

        this.checkNotEquals(
                new SpreadsheetCellFind(
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
                SpreadsheetCellFind.empty(),
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
                SpreadsheetCellFind.empty().isEmpty()
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
                SpreadsheetCellFind.empty()
                        .setPath(PATH),
                "cell-range-path=lrtd"
        );
    }

    @Test
    public void testToUrlQueryStringOffset() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellFind.empty()
                        .setOffset(OFFSET),
                "offset=123"
        );
    }

    @Test
    public void testToUrlQueryStringMax() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellFind.empty()
                        .setMax(MAX),
                "max=456"
        );
    }

    @Test
    public void testToUrlQueryStringValueType() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellFind.empty()
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
                SpreadsheetCellFind.empty()
                        .setQuery(QUERY),
                "query=%3D789%2Bblah%28%29"
        );
    }

    @Test
    public void testToUrlQueryStringAllParameters() {
        this.toUrlQueryStringAndCheck(
                SpreadsheetCellFind.empty()
                        .setPath(PATH)
                        .setOffset(OFFSET)
                        .setMax(MAX)
                        .setValueType(VALUE_TYPE)
                        .setQuery(QUERY),
                "cell-range-path=lrtd&max=456&offset=123&query=%3D789%2Bblah%28%29&value-type=*"
        );
    }

    private void toUrlQueryStringAndCheck(final SpreadsheetCellFind find,
                                          final String expected) {
        this.toUrlQueryStringAndCheck(
                find,
                UrlQueryString.parse(expected)
        );
    }

    private void toUrlQueryStringAndCheck(final SpreadsheetCellFind find,
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
    public void testParseEmpty() {
        this.parseStringAndCheck(
                "",
                SpreadsheetCellFind.empty()
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
                SpreadsheetCellFind.empty()
                        .setPath(PATH)
        );
    }

    @Test
    public void testParseIncludesInvalidQuery() {
        this.parseStringAndCheck(
                "cell-range-path=lrtd&query=XYZ",
                SpreadsheetCellFind.empty()
                        .setPath(PATH)
                        .setQuery(
                                Optional.of("XYZ")
                        )
        );
    }

    @Override
    public SpreadsheetCellFind parseString(final String text) {
        return SpreadsheetCellFind.parse(text);
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
                () -> SpreadsheetCellFind.extract(null)
        );
    }

    @Test
    public void testExtractCellRangePath() {
        this.extractAndCheck(
                "cell-range-path=lrtd",
                SpreadsheetCellFind.empty()
                        .setPath(PATH)
        );
    }

    @Test
    public void testExtractIncludesInvalidQuery() {
        this.extractAndCheck(
                "cell-range-path=lrtd&query=XYZ",
                SpreadsheetCellFind.empty()
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
                                 final SpreadsheetCellFind expected) {
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
                                  final SpreadsheetCellFind expected) {
        this.checkEquals(
                expected,
                SpreadsheetCellFind.extract(
                        parameters
                )
        );
    }

    // Object...........................................................................................................

    @Test
    public void testEqualsDifferentPath() {
        this.checkNotEquals(
                new SpreadsheetCellFind(
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
                new SpreadsheetCellFind(
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
                new SpreadsheetCellFind(
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
                new SpreadsheetCellFind(
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
                new SpreadsheetCellFind(
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
                SpreadsheetCellFind.empty()
                        .setPath(
                                Optional.of(SpreadsheetCellRangeReferencePath.BULR)
                        ),
                UrlFragment.parse("path/BULR")
        );
    }

    @Test
    public void testUrlFragmentOffset() {
        this.urlFragmentAndCheck(
                SpreadsheetCellFind.empty()
                        .setOffset(
                                OptionalInt.of(123)
                        ),
                UrlFragment.parse("offset/123")
        );
    }

    @Test
    public void testUrlFragmentMax() {
        this.urlFragmentAndCheck(
                SpreadsheetCellFind.empty()
                        .setMax(
                                OptionalInt.of(456)
                        ),
                UrlFragment.parse("max/456")
        );
    }

    @Test
    public void testUrlFragmentValueType() {
        this.urlFragmentAndCheck(
                SpreadsheetCellFind.empty()
                        .setValueType(
                                Optional.of(SpreadsheetValueType.NUMBER)
                        ),
                UrlFragment.parse("value-type/number")
        );
    }

    @Test
    public void testUrlFragmentQuery() {
        this.urlFragmentAndCheck(
                SpreadsheetCellFind.empty()
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
                SpreadsheetCellFind.empty(),
                ""
        );
    }

    @Test
    public void testToStringNonEmpty() {
        this.toStringAndCheck(
                new SpreadsheetCellFind(
                        Optional.of(SpreadsheetCellRangeReferencePath.BULR),
                        OptionalInt.of(123), // offset
                        OptionalInt.of(456), // max
                        Optional.of(SpreadsheetValueType.NUMBER),
                        Optional.of("query789")
                ),
                "path/BULR/offset/123/max/456/value-type/number/query/query789"
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellFind> type() {
        return SpreadsheetCellFind.class;
    }

    // Object...........................................................................................................

    public SpreadsheetCellFind createObject() {
        return new SpreadsheetCellFind(
                PATH,
                OFFSET,
                MAX,
                VALUE_TYPE,
                QUERY
        );
    }
}
