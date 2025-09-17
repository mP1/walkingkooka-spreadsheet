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

package walkingkooka.spreadsheet.parser.provider;

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

public final class SpreadsheetParserAliasSetTest implements PluginAliasSetLikeTesting<SpreadsheetParserName,
    SpreadsheetParserInfo,
    SpreadsheetParserInfoSet,
    SpreadsheetParserSelector,
    SpreadsheetParserAlias,
    SpreadsheetParserAliasSet>,
    HashCodeEqualsDefinedTesting2<SpreadsheetParserAliasSet>,
    ToStringTesting<SpreadsheetParserAliasSet>,
    JsonNodeMarshallingTesting<SpreadsheetParserAliasSet> {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserAliasSet.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            SpreadsheetParserAliasSet.EMPTY,
            SpreadsheetParserAliasSet.with(SortedSets.empty())
        );
    }

    // name.............................................................................................................

    @Test
    public void testAliasOrNameWithName() {
        final SpreadsheetParserName abc = SpreadsheetParserName.with("abc");

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
            SpreadsheetParserName.with("sunshine-alias"),
            SpreadsheetParserName.with("sunshine")
        );
    }

    @Test
    public void testAliasSelectorWithName() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetParserName.with("abc")
        );
    }

    @Test
    public void testAliasSelectorWithAlias() {
        this.aliasSelectorAndCheck(
            this.createSet(),
            SpreadsheetParserName.with("custom-alias"),
            SpreadsheetParserSelector.parse("custom(1)")
        );
    }

    @Override
    public SpreadsheetParserAliasSet createSet() {
        return SpreadsheetParserAliasSet.parse("abc, moo, mars, custom-alias custom(1) https://example.com/custom , sunshine-alias sunshine");
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetParserAliasSet parseString(final String text) {
        return SpreadsheetParserAliasSet.parse(text);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            SpreadsheetParserAliasSet.parse("different")
        );
    }

    @Override
    public SpreadsheetParserAliasSet createObject() {
        return SpreadsheetParserAliasSet.parse("abc, custom-alias custom(1) https://example.com/custom");
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetParserAliasSet unmarshall(final JsonNode json,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserAliasSet.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetParserAliasSet createJsonNodeMarshallingValue() {
        return SpreadsheetParserAliasSet.parse("alias1 name1, name2, alias3 name3(\"999\") https://example.com/name3");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserAliasSet> type() {
        return SpreadsheetParserAliasSet.class;
    }
}
