/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.plugin.PluginAliasesLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetExporterAliasesTest implements PluginAliasesLikeTesting<SpreadsheetExporterAliases, SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector>,
        HashCodeEqualsDefinedTesting2<SpreadsheetExporterAliases>,
        ToStringTesting<SpreadsheetExporterAliases>,
        JsonNodeMarshallingTesting<SpreadsheetExporterAliases> {

    @Test
    public void testNameWithName() {
        final SpreadsheetExporterName html = SpreadsheetExporterName.with("html");

        this.nameAndCheck(
                this.createPluginAliases(),
                html,
                html
        );
    }

    @Test
    public void testNameWithAlias() {
        this.nameAndCheck(
                this.createPluginAliases(),
                SpreadsheetExporterName.with("json-alias")
        );
    }

    @Test
    public void testAliasWithName() {
        this.aliasAndCheck(
                this.createPluginAliases(),
                SpreadsheetExporterName.with("html")
        );
    }

    @Test
    public void testAliasWithAlias() {
        this.aliasAndCheck(
                this.createPluginAliases(),
                SpreadsheetExporterName.with("custom-alias"),
                SpreadsheetExporterSelector.parse("custom(1)")
        );
    }

    @Override
    public SpreadsheetExporterAliases createPluginAliases() {
        return SpreadsheetExporterAliases.parse("html, this, that, custom-alias custom(1) https://example.com/custom , json-alias json");
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
                SpreadsheetExporterAliases.parse("different")
        );
    }

    @Override
    public SpreadsheetExporterAliases createObject() {
        return SpreadsheetExporterAliases.parse("html, custom-alias custom(1) https://example.com/custom");
    }

    // toString...........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetExporterAliases.parse("json, custom-alias custom(1) https://example.com/custom , html, this"),
                "custom-alias custom(1) https://example.com/custom , html, json, this"
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetExporterAliases unmarshall(final JsonNode json,
                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetExporterAliases.unmarshall(
                json,
                context
        );
    }

    @Override
    public SpreadsheetExporterAliases createJsonNodeMarshallingValue() {
        return SpreadsheetExporterAliases.parse("alias1 name1, name2, alias3 name3(\"999\") https://example.com/name3");
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExporterAliases> type() {
        return SpreadsheetExporterAliases.class;
    }
}

