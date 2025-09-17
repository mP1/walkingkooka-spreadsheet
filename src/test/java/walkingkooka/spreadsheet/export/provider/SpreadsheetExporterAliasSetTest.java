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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.plugin.PluginAliasSetLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExporterAliasSetTest implements PluginAliasSetLikeTesting<SpreadsheetExporterName,
    SpreadsheetExporterInfo,
    SpreadsheetExporterInfoSet,
    SpreadsheetExporterSelector,
    SpreadsheetExporterAlias,
    SpreadsheetExporterAliasSet>,
    HashCodeEqualsDefinedTesting2<SpreadsheetExporterAliasSet>,
    ToStringTesting<SpreadsheetExporterAliasSet>,
    JsonNodeMarshallingTesting<SpreadsheetExporterAliasSet> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExporterAliasSet.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            SpreadsheetExporterAliasSet.EMPTY,
            SpreadsheetExporterAliasSet.with(SortedSets.empty())
        );
    }

    // name.............................................................................................................

    @Test
    public void testAliasOrNameWithName() {
        final SpreadsheetExporterName abs = SpreadsheetExporterName.with("abs");

        this.aliasOrNameAndCheck(
            this.createSet(),
            abs,
            abs
        );
    }

    @Test
    public void testAliasOrNameWithAlias() {
        this.aliasOrNameAndCheck(
            this.createSet(),
            SpreadsheetExporterName.with("sum-alias"),
            SpreadsheetExporterName.with("sum")
        );
    }

    @Test
    public void testAliasSelectorWithName() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetExporterName.with("abs")
        );
    }

    @Test
    public void testAliasSelectorWithAlias() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetExporterName.with("custom-alias"),
            SpreadsheetExporterSelector.parse("custom(1)")
        );
    }

    @Override
    public SpreadsheetExporterAliasSet createSet() {
        return SpreadsheetExporterAliasSet.parse("abs, min, max, custom-alias custom(1) https://example.com/custom , sum-alias sum");
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetExporterAliasSet parseString(final String text) {
        return SpreadsheetExporterAliasSet.parse(text);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            SpreadsheetExporterAliasSet.parse("different")
        );
    }

    @Override
    public SpreadsheetExporterAliasSet createObject() {
        return SpreadsheetExporterAliasSet.parse("abs, custom-alias custom(1) https://example.com/custom");
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetExporterAliasSet unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetExporterAliasSet.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetExporterAliasSet createJsonNodeMarshallingValue() {
        return SpreadsheetExporterAliasSet.parse("alias1 name1, name2, alias3 name3(\"999\") https://example.com/name3");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExporterAliasSet> type() {
        return SpreadsheetExporterAliasSet.class;
    }
}

