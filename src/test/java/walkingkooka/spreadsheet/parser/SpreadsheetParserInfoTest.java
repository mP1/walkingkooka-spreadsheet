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

package walkingkooka.spreadsheet.parser;

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginInfoLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetParserInfoTest implements PluginInfoLikeTesting<SpreadsheetParserInfo, SpreadsheetParserName> {

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetParserInfo> type() {
        return SpreadsheetParserInfo.class;
    }

    // PluginInfoLikeTesting............................................................................................

    @Override
    public SpreadsheetParserName createName(final String value) {
        return SpreadsheetParserName.with(value);
    }

    @Override
    public SpreadsheetParserInfo createPluginInfoLike(final AbsoluteUrl url,
                                                      final SpreadsheetParserName name) {
        return SpreadsheetParserInfo.with(
                url,
                name
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetParserInfo unmarshall(final JsonNode json,
                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserInfo.unmarshall(
                json,
                context
        );
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetParserInfo parseString(final String text) {
        return SpreadsheetParserInfo.parse(text);
    }
}
