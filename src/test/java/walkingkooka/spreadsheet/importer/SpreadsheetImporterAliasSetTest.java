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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.plugin.PluginAliasSetLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetImporterAliasSetTest implements PluginAliasSetLikeTesting<SpreadsheetImporterName,
    SpreadsheetImporterInfo,
    SpreadsheetImporterInfoSet,
    SpreadsheetImporterSelector,
    SpreadsheetImporterAlias,
    SpreadsheetImporterAliasSet>,
    HashCodeEqualsDefinedTesting2<SpreadsheetImporterAliasSet>,
    ToStringTesting<SpreadsheetImporterAliasSet>,
    JsonNodeMarshallingTesting<SpreadsheetImporterAliasSet> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetImporterAliasSet.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            SpreadsheetImporterAliasSet.EMPTY,
            SpreadsheetImporterAliasSet.with(SortedSets.empty())
        );
    }

    // name.............................................................................................................

    @Test
    public void testAliasOrNameWithName() {
        final SpreadsheetImporterName abc = SpreadsheetImporterName.with("abc");

        this.aliasOrNameAndCheck(
            this.createSet(),
            abc,
            abc
        );
    }

    @Test
    public void testAliasOrNameWithAlias() {
        this.aliasOrNameAndCheck(
            this.createSet(),
            SpreadsheetImporterName.with("hello-alias"),
            SpreadsheetImporterName.with("hello")
        );
    }

    @Test
    public void testAliasSelectorWithName() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetImporterName.with("abc")
        );
    }

    @Test
    public void testAliasSelectorWithAlias() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetImporterName.with("custom-alias"),
            SpreadsheetImporterSelector.parse("custom(1)")
        );
    }

    @Override
    public SpreadsheetImporterAliasSet createSet() {
        return SpreadsheetImporterAliasSet.parse("abc, dog, egg, custom-alias custom(1) https://example.com/custom , hello-alias hello");
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetImporterAliasSet parseString(final String text) {
        return SpreadsheetImporterAliasSet.parse(text);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            SpreadsheetImporterAliasSet.parse("different")
        );
    }

    @Override
    public SpreadsheetImporterAliasSet createObject() {
        return SpreadsheetImporterAliasSet.parse("abc, custom-alias custom(1) https://example.com/custom");
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetImporterAliasSet unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetImporterAliasSet.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetImporterAliasSet createJsonNodeMarshallingValue() {
        return SpreadsheetImporterAliasSet.parse("alias1 name1, name2, alias3 name3(\"999\") https://example.com/name3");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetImporterAliasSet> type() {
        return SpreadsheetImporterAliasSet.class;
    }
}

