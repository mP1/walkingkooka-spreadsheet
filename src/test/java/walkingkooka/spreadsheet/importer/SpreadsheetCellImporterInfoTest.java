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

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginInfoLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetCellImporterInfoTest implements PluginInfoLikeTesting<SpreadsheetCellImporterInfo, SpreadsheetCellImporterName> {

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetCellImporterInfo> type() {
        return SpreadsheetCellImporterInfo.class;
    }

    // PluginInfoLikeTesting..............................................................................

    @Override
    public SpreadsheetCellImporterName createName(final String value) {
        return SpreadsheetCellImporterName.with(value);
    }

    @Override
    public SpreadsheetCellImporterInfo createPluginInfoLike(final AbsoluteUrl url,
                                                            final SpreadsheetCellImporterName name) {
        return SpreadsheetCellImporterInfo.with(
                url,
                name
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetCellImporterInfo unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellImporterInfo.unmarshall(
                json,
                context
        );
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetCellImporterInfo parseString(final String text) {
        return SpreadsheetCellImporterInfo.parse(text);
    }
}
