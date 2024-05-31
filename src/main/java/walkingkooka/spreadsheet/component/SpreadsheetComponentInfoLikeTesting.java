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

package walkingkooka.spreadsheet.component;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetComponentInfoLikeTesting<I extends SpreadsheetComponentInfoLike<I, N>, N extends SpreadsheetComponentNameLike<N>> extends ClassTesting2<I>,
        HashCodeEqualsDefinedTesting2<I>,
        HateosResourceTesting<I, N>,
        JsonNodeMarshallingTesting<I>,
        ComparableTesting2<I> {

    // factory..........................................................................................................

    @Test
    default void testWithNullUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetComponentInfo(
                        null,
                        this.createName("abc-123")
                )
        );
    }

    @Test
    default void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetComponentInfo(
                        Url.parseAbsolute("https://example.com/123"),
                        null
                )
        );
    }

    N createName(final String value);

    I createSpreadsheetComponentInfo(final AbsoluteUrl url,
                                     final N name);

    // Class............................................................................................................

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Comparable.......................................................................................................

    @Test
    default void testCompareLess() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/123");
        this.compareToAndCheckLess(
                this.createSpreadsheetComponentInfo(
                        url,
                        this.createName("abc-123")
                ),
                this.createSpreadsheetComponentInfo(
                        url,
                        this.createName("xyz-456")
                )
        );
    }

    @Override
    default I createComparable() {
        return this.createSpreadsheetComponentInfo(
                Url.parseAbsolute("https://example.com/123"),
                this.createName("abc-123")
        );
    }

    // equals...........................................................................................................

    @Test
    default void testEqualsDifferentUrl() {
        final N name = this.createName("abc-123");

        this.checkNotEquals(
                this.createSpreadsheetComponentInfo(
                        Url.parseAbsolute("https://example.com"),
                        name
                ),
                this.createSpreadsheetComponentInfo(
                        Url.parseAbsolute("https://example.com/different"),
                        name
                )
        );
    }

    @Test
    default void testEqualsDifferentName() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com");

        this.checkNotEquals(
                this.createSpreadsheetComponentInfo(
                        url,
                        this.createName("abc-123")
                ),
                this.createSpreadsheetComponentInfo(
                        url,
                        this.createName("different-456")
                )
        );
    }

    // Json.............................................................................................................

    @Override
    default I createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // HateosResource...................................................................................................

    @Test
    default void testHateosId() {
        final N name = this.createName("abc-123");
        final I info = this.createSpreadsheetComponentInfo(
                Url.parseAbsolute("https://example.com/123"),
                name
        );
        this.hateosLinkIdAndCheck(
                info,
                name.value()
        );
    }

    @Test
    default void testId() {
        final N name = this.createName("abc-123");
        final I info = this.createSpreadsheetComponentInfo(
                Url.parseAbsolute("https://example.com/123"),
                name
        );

        this.idAndCheck(
                info,
                Optional.of(name)
        );
    }

    @Override
    default I createHateosResource() {
        return this.createComparable();
    }
}
