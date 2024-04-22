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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetComparatorInfoTest implements ClassTesting2<SpreadsheetComparatorInfo>,
        HashCodeEqualsDefinedTesting2<SpreadsheetComparatorInfo>,
        JsonNodeMarshallingTesting<SpreadsheetComparatorInfo> {

    private final static AbsoluteUrl URL = Url.parseAbsolute("http://example.com");

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("comparator-123");

    @Test
    public void testWithNullUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetComparatorInfo.with(
                        null,
                        NAME
                )
        );
    }

    @Test
    public void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetComparatorInfo.with(
                        URL,
                        null
                )
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentUrl() {
        this.checkNotEquals(
                SpreadsheetComparatorInfo.with(
                        Url.parseAbsolute("http://example.com/different"),
                        NAME
                )
        );
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
                SpreadsheetComparatorInfo.with(
                        URL,
                        SpreadsheetComparatorName.with("different-123")
                )
        );
    }

    @Override
    public SpreadsheetComparatorInfo createObject() {
        return SpreadsheetComparatorInfo.with(
                URL,
                NAME
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetComparatorInfo unmarshall(final JsonNode json,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetComparatorInfo.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetComparatorInfo createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetComparatorInfo> type() {
        return SpreadsheetComparatorInfo.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
