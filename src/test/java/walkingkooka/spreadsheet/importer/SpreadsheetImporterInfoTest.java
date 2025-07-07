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
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetImporterInfoTest implements PluginInfoLikeTesting<SpreadsheetImporterInfo, SpreadsheetImporterName> {

    @Test
    public void testSetNameWithDifferent() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example/importer123");
        final SpreadsheetImporterName different = SpreadsheetImporterName.with("different");

        this.setNameAndCheck(
            SpreadsheetImporterInfo.with(
                url,
                SpreadsheetImporterName.with("original-importer-name")
            ),
            different,
            SpreadsheetImporterInfo.with(
                url,
                different
            )
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetImporterInfo> type() {
        return SpreadsheetImporterInfo.class;
    }

    // PluginInfoLikeTesting..............................................................................

    @Override
    public SpreadsheetImporterName createName(final String value) {
        return SpreadsheetImporterName.with(value);
    }

    @Override
    public SpreadsheetImporterInfo createPluginInfoLike(final AbsoluteUrl url,
                                                        final SpreadsheetImporterName name) {
        return SpreadsheetImporterInfo.with(
            url,
            name
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetImporterInfo unmarshall(final JsonNode json,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetImporterInfo.unmarshall(
            json,
            context
        );
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetImporterInfo parseString(final String text) {
        return SpreadsheetImporterInfo.parse(text);
    }
}
