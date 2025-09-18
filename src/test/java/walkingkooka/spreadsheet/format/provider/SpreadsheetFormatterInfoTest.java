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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.PluginInfoLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetFormatterInfoTest implements PluginInfoLikeTesting<SpreadsheetFormatterInfo, SpreadsheetFormatterName> {

    @Test
    public void testSetNameWithDifferent() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example/formatter123");
        final SpreadsheetFormatterName different = SpreadsheetFormatterName.with("different");

        this.setNameAndCheck(
            SpreadsheetFormatterInfo.with(
                url,
                SpreadsheetFormatterName.with("original-formatter-name")
            ),
            different,
            SpreadsheetFormatterInfo.with(
                url,
                different
            )
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetFormatterInfo> type() {
        return SpreadsheetFormatterInfo.class;
    }

    // PluginInfoLikeTesting..............................................................................

    @Override
    public SpreadsheetFormatterName createName(final String value) {
        return SpreadsheetFormatterName.with(value);
    }

    @Override
    public SpreadsheetFormatterInfo createPluginInfoLike(final AbsoluteUrl url,
                                                         final SpreadsheetFormatterName name) {
        return SpreadsheetFormatterInfo.with(
            url,
            name
        );
    }

    // json.............................................................................................................

    @Override
    public SpreadsheetFormatterInfo unmarshall(final JsonNode json,
                                               final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterInfo.unmarshall(
            json,
            context
        );
    }

    // parse............................................................................................................

    @Override
    public SpreadsheetFormatterInfo parseString(final String text) {
        return SpreadsheetFormatterInfo.parse(text);
    }
}
