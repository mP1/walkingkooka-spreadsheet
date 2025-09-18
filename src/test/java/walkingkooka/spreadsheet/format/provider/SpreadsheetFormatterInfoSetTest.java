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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoSetLikeTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetFormatterInfoSetTest implements PluginInfoSetLikeTesting<SpreadsheetFormatterName, SpreadsheetFormatterInfo, SpreadsheetFormatterInfoSet, SpreadsheetFormatterSelector, SpreadsheetFormatterAlias, SpreadsheetFormatterAliasSet>,
    ClassTesting<SpreadsheetFormatterInfoSet> {

    // immutable set....................................................................................................

    @Test
    public void testImmutableSet() {
        final SpreadsheetFormatterInfoSet set = this.createSet();

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
    public SpreadsheetFormatterInfoSet parseString(final String text) {
        return SpreadsheetFormatterInfoSet.parse(text);
    }

    // Set..............................................................................................................

    @Override
    public SpreadsheetFormatterInfoSet createSet() {
        return SpreadsheetFormatterProviders.spreadsheetFormatters()
            .spreadsheetFormatterInfos();
    }

    @Override
    public SpreadsheetFormatterInfo info() {
        return SpreadsheetFormatterInfo.with(
            Url.parseAbsolute("https://example.com/test1"),
            SpreadsheetFormatterName.TEXT_FORMAT_PATTERN
        );
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
            SpreadsheetFormatterInfoSet.EMPTY,
            JsonNode.array()
        );
    }

    @Test
    public void testMarshallNotEmpty2() {
        final SpreadsheetFormatterInfoSet set = SpreadsheetFormatterInfoSet.with(
            Sets.of(
                SpreadsheetFormatterInfo.with(
                    Url.parseAbsolute("https://example.com/test123"),
                    SpreadsheetFormatterName.with("test123")
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
    public SpreadsheetFormatterInfoSet unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterInfoSet.unmarshall(
            node,
            context
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet createJsonNodeMarshallingValue() {
        return SpreadsheetFormatterInfoSet.with(
            Sets.of(
                SpreadsheetFormatterInfo.with(
                    Url.parseAbsolute("https://example.com/test111"),
                    SpreadsheetFormatterName.with("test111")
                ),
                SpreadsheetFormatterInfo.with(
                    Url.parseAbsolute("https://example.com/test222"),
                    SpreadsheetFormatterName.with("test222")
                )
            )
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterInfoSet> type() {
        return SpreadsheetFormatterInfoSet.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
