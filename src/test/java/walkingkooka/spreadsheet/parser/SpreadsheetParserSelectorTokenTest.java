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

import walkingkooka.plugin.PluginSelectorTokenLike;
import walkingkooka.plugin.PluginSelectorTokenLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetParserSelectorTokenTest implements PluginSelectorTokenLikeTesting<SpreadsheetParserSelectorToken, SpreadsheetParserSelectorTokenAlternative> {
    @Override
    public SpreadsheetParserSelectorToken createPluginSelectorTokenLike(final String label,
                                                                        final String text,
                                                                        final List<SpreadsheetParserSelectorTokenAlternative> alternatives) {
        return SpreadsheetParserSelectorToken.with(
            label,
            text,
            alternatives
        );
    }

    @Override
    public SpreadsheetParserSelectorTokenAlternative createPluginSelectorTokenAlternativesLike(final String label,
                                                                                               final String text) {
        return SpreadsheetParserSelectorTokenAlternative.with(
            label,
            text
        );
    }

    @Override
    public SpreadsheetParserSelectorToken unmarshall(final JsonNode json,
                                                     final JsonNodeUnmarshallContext context) {
        return PluginSelectorTokenLike.unmarshall(
            json,
            context,
            SpreadsheetParserSelectorToken::with,
            SpreadsheetParserSelectorTokenAlternative.class
        );
    }

    @Override
    public Class<SpreadsheetParserSelectorToken> type() {
        return SpreadsheetParserSelectorToken.class;
    }
}
