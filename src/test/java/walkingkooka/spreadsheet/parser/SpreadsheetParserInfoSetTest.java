
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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoSetLikeTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetParserInfoSetTest implements PluginInfoSetLikeTesting<SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserInfoSet, SpreadsheetParserSelector, SpreadsheetParserAlias, SpreadsheetParserAliasSet>,
    ClassTesting<SpreadsheetParserInfoSet> {

    // immutable set....................................................................................................

    @Test
    public void testImmutableSet() {
        final SpreadsheetParserInfoSet set = this.createSet();

        assertSame(
            set,
            Sets.immutable(set)
        );
    }

    // parse............................................................................................................

    @Override
    public void testParseStringEmptyFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetParserInfoSet parseString(final String text) {
        return SpreadsheetParserInfoSet.parse(text);
    }

    // Set..............................................................................................................

    @Override
    public SpreadsheetParserInfoSet createSet() {
        return SpreadsheetParserInfoSet.with(
            Sets.of(
                this.info(),
                SpreadsheetParserInfo.parse("https://example.com/parser-222 parser-222")
            )
        );
    }

    @Override
    public SpreadsheetParserInfo info() {
        return SpreadsheetParserInfo.parse("https://example.com/parser-111 parser-111");
    }

    // ImmutableSetTesting..............................................................................................

    @Override
    public void testSetElementsNullFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetElementsSame() {
        throw new UnsupportedOperationException();
    }

    // json.............................................................................................................

    @Test
    public void testMarshallEmpty() {
        this.marshallAndCheck(
            SpreadsheetParserInfoSet.EMPTY,
            JsonNode.array()
        );
    }

    @Test
    public void testMarshallNotEmpty2() {
        final SpreadsheetParserInfoSet set = SpreadsheetParserInfoSet.with(
            Sets.of(
                SpreadsheetParserInfo.with(
                    Url.parseAbsolute("https://example.com/test123"),
                    SpreadsheetParserName.with("test123")
                )
            )
        );

        this.marshallAndCheck(
            set,
            "[\n" +
                "  \"https://example.com/test123 test123\"\n" +
                "]"
        );
    }

    // json............................................................................................................

    @Override
    public SpreadsheetParserInfoSet unmarshall(final JsonNode node,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserInfoSet.unmarshall(
            node,
            context
        );
    }

    @Override
    public SpreadsheetParserInfoSet createJsonNodeMarshallingValue() {
        return SpreadsheetParserInfoSet.with(
            Sets.of(
                SpreadsheetParserInfo.with(
                    Url.parseAbsolute("https://example.com/test111"),
                    SpreadsheetParserName.with("test111")
                ),
                SpreadsheetParserInfo.with(
                    Url.parseAbsolute("https://example.com/test222"),
                    SpreadsheetParserName.with("test222")
                )
            )
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetParserInfoSet> type() {
        return SpreadsheetParserInfoSet.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
