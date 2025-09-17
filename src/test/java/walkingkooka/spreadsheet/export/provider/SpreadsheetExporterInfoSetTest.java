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

package walkingkooka.spreadsheet.export.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoSetLikeTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetExporterInfoSetTest implements PluginInfoSetLikeTesting<SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector, SpreadsheetExporterAlias, SpreadsheetExporterAliasSet>,
    ClassTesting<SpreadsheetExporterInfoSet> {

    // immutable set....................................................................................................

    @Test
    public void testImmutableSet() {
        final SpreadsheetExporterInfoSet set = this.createSet();

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
    public SpreadsheetExporterInfoSet parseString(final String text) {
        return SpreadsheetExporterInfoSet.parse(text);
    }

    // Set..............................................................................................................

    @Override
    public SpreadsheetExporterInfoSet createSet() {
        return SpreadsheetExporterInfoSet.with(
            Sets.of(
                this.info()
            )
        );
    }

    @Override
    public SpreadsheetExporterInfo info() {
        return SpreadsheetExporterInfo.with(
            Url.parseAbsolute("https://example.com/SpreadsheetCellExporter1"),
            SpreadsheetExporterName.with("test123")
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshallEmpty() {
        this.marshallAndCheck(
            SpreadsheetExporterInfoSet.EMPTY,
            JsonNode.array()
        );
    }

    @Test
    public void testMarshallNotEmpty2() {
        this.marshallAndCheck(
            SpreadsheetExporterInfoSet.with(
                Sets.of(
                    SpreadsheetExporterInfo.with(
                        Url.parseAbsolute("https://example.com/test123"),
                        SpreadsheetExporterName.with("test123")
                    )
                )
            ),
            "[\n" +
                "  \"https://example.com/test123 test123\"\n" +
                "]"
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetExporterInfoSet unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetExporterInfoSet.unmarshall(
            node,
            context
        );
    }

    @Override
    public SpreadsheetExporterInfoSet createJsonNodeMarshallingValue() {
        return SpreadsheetExporterInfoSet.with(
            Sets.of(
                SpreadsheetExporterInfo.with(
                    Url.parseAbsolute("https://example.com/test111"),
                    SpreadsheetExporterName.with("test111")
                ),
                SpreadsheetExporterInfo.with(
                    Url.parseAbsolute("https://example.com/test222"),
                    SpreadsheetExporterName.with("test222")
                )
            )
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetExporterInfoSet> type() {
        return SpreadsheetExporterInfoSet.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
