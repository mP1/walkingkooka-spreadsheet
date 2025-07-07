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

public final class SpreadsheetImporterInfoSetTest implements PluginInfoSetLikeTesting<SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet, SpreadsheetImporterSelector, SpreadsheetImporterAlias, SpreadsheetImporterAliasSet>,
    ClassTesting<SpreadsheetImporterInfoSet> {

    // immutable set....................................................................................................

    @Test
    public void testImmutableSet() {
        final SpreadsheetImporterInfoSet set = this.createSet();

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
    public SpreadsheetImporterInfoSet parseString(final String text) {
        return SpreadsheetImporterInfoSet.parse(text);
    }

    // Set..............................................................................................................

    @Override
    public SpreadsheetImporterInfoSet createSet() {
        return SpreadsheetImporterInfoSet.EMPTY.concat(
            this.info()
        );
    }

    @Override
    public SpreadsheetImporterInfo info() {
        return SpreadsheetImporterInfo.with(
            Url.parseAbsolute("https://example.com/SpreadsheetCellImporter1"),
            SpreadsheetImporterName.with("test123")
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
            SpreadsheetImporterInfoSet.EMPTY,
            JsonNode.array()
        );
    }

    @Test
    public void testMarshallNotEmpty2() {
        final SpreadsheetImporterInfoSet set = SpreadsheetImporterInfoSet.with(
            Sets.of(
                SpreadsheetImporterInfo.with(
                    Url.parseAbsolute("https://example.com/test123"),
                    SpreadsheetImporterName.with("test123")
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

    // json.............................................................................................................

    @Override
    public SpreadsheetImporterInfoSet unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetImporterInfoSet.unmarshall(
            node,
            context
        );
    }

    @Override
    public SpreadsheetImporterInfoSet createJsonNodeMarshallingValue() {
        return SpreadsheetImporterInfoSet.with(
            Sets.of(
                SpreadsheetImporterInfo.with(
                    Url.parseAbsolute("https://example.com/test111"),
                    SpreadsheetImporterName.with("test111")
                ),
                SpreadsheetImporterInfo.with(
                    Url.parseAbsolute("https://example.com/test222"),
                    SpreadsheetImporterName.with("test222")
                )
            )
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetImporterInfoSet> type() {
        return SpreadsheetImporterInfoSet.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
