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

package walkingkooka.spreadsheet.format;

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

public final class SpreadsheetFormatterAliasSetTest implements PluginAliasSetLikeTesting<SpreadsheetFormatterName,
    SpreadsheetFormatterInfo,
    SpreadsheetFormatterInfoSet,
    SpreadsheetFormatterSelector,
    SpreadsheetFormatterAlias,
    SpreadsheetFormatterAliasSet>,
    HashCodeEqualsDefinedTesting2<SpreadsheetFormatterAliasSet>,
    ToStringTesting<SpreadsheetFormatterAliasSet>,
    JsonNodeMarshallingTesting<SpreadsheetFormatterAliasSet> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterAliasSet.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            SpreadsheetFormatterAliasSet.EMPTY,
            SpreadsheetFormatterAliasSet.with(SortedSets.empty())
        );
    }

    // name.............................................................................................................

    @Test
    public void testAliasOrNameWithName() {
        final SpreadsheetFormatterName abc = SpreadsheetFormatterName.with("abc");

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
            SpreadsheetFormatterName.with("sunshine-alias"),
            SpreadsheetFormatterName.with("sunshine")
        );
    }

    @Test
    public void testAliasSelectorWithName() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetFormatterName.with("abc")
        );
    }

    @Test
    public void testAliasSelectorWithAlias() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetFormatterName.with("custom-alias"),
            SpreadsheetFormatterSelector.parse("custom(1)")
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet createSet() {
        return SpreadsheetFormatterAliasSet.parse("abc, moo, mars, custom-alias custom(1) https://example.com/custom , sunshine-alias sunshine");
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetFormatterAliasSet parseString(final String text) {
        return SpreadsheetFormatterAliasSet.parse(text);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            SpreadsheetFormatterAliasSet.parse("different")
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet createObject() {
        return SpreadsheetFormatterAliasSet.parse("abc, custom-alias custom(1) https://example.com/custom");
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetFormatterAliasSet unmarshall(final JsonNode json,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterAliasSet.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet createJsonNodeMarshallingValue() {
        return SpreadsheetFormatterAliasSet.parse("alias1 name1, name2, alias3 name3(\"999\") https://example.com/name3");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterAliasSet> type() {
        return SpreadsheetFormatterAliasSet.class;
    }
}
