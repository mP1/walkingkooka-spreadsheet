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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoSetLikeTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetCellImporterInfoSetTest implements PluginInfoSetLikeTesting<SpreadsheetCellImporterInfoSet, SpreadsheetCellImporterInfo, SpreadsheetCellImporterName>,
        ClassTesting<SpreadsheetCellImporterInfoSet> {

    // immutable set....................................................................................................

    @Test
    public void testImmutableSet() {
        final SpreadsheetCellImporterInfoSet set = this.createSet();

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
    public SpreadsheetCellImporterInfoSet parseString(final String text) {
        return SpreadsheetCellImporterInfoSet.parse(text);
    }

    // Set..............................................................................................................

    @Override
    public SpreadsheetCellImporterInfoSet createSet() {
        return SpreadsheetCellImporterInfoSet.with(
                Sets.of(
                        SpreadsheetCellImporterInfo.with(
                                Url.parseAbsolute("https://example.com/SpreadsheetCellImporter1"),
                                SpreadsheetCellImporterName.with("test123")
                        )
                )
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshallEmpty() {
        this.marshallAndCheck(
                SpreadsheetCellImporterInfoSet.with(Sets.empty()),
                JsonNode.array()
        );
    }

    @Test
    public void testMarshallNotEmpty2() {
        final SpreadsheetCellImporterInfoSet set = SpreadsheetCellImporterInfoSet.with(
                Sets.of(
                        SpreadsheetCellImporterInfo.with(
                                Url.parseAbsolute("https://example.com/test123"),
                                SpreadsheetCellImporterName.with("test123")
                        )
                )
        );

        this.marshallAndCheck(
                set,
                "[\n" +
                        "  {\n" +
                        "    \"url\": \"https://example.com/test123\",\n" +
                        "    \"name\": \"test123\"\n" +
                        "  }\n" +
                        "]"
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetCellImporterInfoSet unmarshall(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellImporterInfoSet.unmarshall(
                node,
                context
        );
    }

    @Override
    public SpreadsheetCellImporterInfoSet createJsonNodeMarshallingValue() {
        return SpreadsheetCellImporterInfoSet.with(
                Sets.of(
                        SpreadsheetCellImporterInfo.with(
                                Url.parseAbsolute("https://example.com/test111"),
                                SpreadsheetCellImporterName.with("test111")
                        ),
                        SpreadsheetCellImporterInfo.with(
                                Url.parseAbsolute("https://example.com/test222"),
                                SpreadsheetCellImporterName.with("test222")
                        )
                )
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetCellImporterInfoSet> type() {
        return SpreadsheetCellImporterInfoSet.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
