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

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetExporterInfoTest implements PluginInfoLikeTesting<SpreadsheetExporterInfo, SpreadsheetExporterName> {

    @Test
    public void testSetNameWithDifferent() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example/exporter123");
        final SpreadsheetExporterName different = SpreadsheetExporterName.with("different");

        this.setNameAndCheck(
            SpreadsheetExporterInfo.with(
                url,
                SpreadsheetExporterName.with("original-exporter-name")
            ),
            different,
            SpreadsheetExporterInfo.with(
                url,
                different
            )
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetExporterInfo> type() {
        return SpreadsheetExporterInfo.class;
    }

    // PluginInfoLikeTesting..............................................................................

    @Override
    public SpreadsheetExporterName createName(final String value) {
        return SpreadsheetExporterName.with(value);
    }

    @Override
    public SpreadsheetExporterInfo createPluginInfoLike(final AbsoluteUrl url,
                                                        final SpreadsheetExporterName name) {
        return SpreadsheetExporterInfo.with(
            url,
            name
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetExporterInfo unmarshall(final JsonNode json,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetExporterInfo.unmarshall(
            json,
            context
        );
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetExporterInfo parseString(final String text) {
        return SpreadsheetExporterInfo.parse(text);
    }
}
