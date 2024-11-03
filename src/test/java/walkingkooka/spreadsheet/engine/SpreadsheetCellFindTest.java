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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReferencePath;

import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellFindTest implements HasUrlFragmentTesting,
        CanBeEmptyTesting,
        HashCodeEqualsDefinedTesting2<SpreadsheetCellFind>,
        ToStringTesting<SpreadsheetCellFind> {

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
