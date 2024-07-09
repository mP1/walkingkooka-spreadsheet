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

import walkingkooka.plugin.PluginSelectorTextComponentLike;
import walkingkooka.plugin.PluginSelectorTextComponentLikeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

public final class SpreadsheetParserSelectorTextComponentTest implements PluginSelectorTextComponentLikeTesting<SpreadsheetParserSelectorTextComponent, SpreadsheetParserSelectorTextComponentAlternative> {
    @Override
    public SpreadsheetParserSelectorTextComponent createPluginSelectorTextComponentLike(final String label,
                                                                                        final String text,
                                                                                        final List<SpreadsheetParserSelectorTextComponentAlternative> alternatives) {
        return SpreadsheetParserSelectorTextComponent.with(
                label,
                text,
                alternatives
        );
    }

    @Override
    public SpreadsheetParserSelectorTextComponentAlternative createPluginSelectorTextComponentAlternativesLike(final String label,
                                                                                                               final String text) {
        return SpreadsheetParserSelectorTextComponentAlternative.with(
                label,
                text
        );
    }

    @Override
    public SpreadsheetParserSelectorTextComponent unmarshall(final JsonNode json,
                                                             final JsonNodeUnmarshallContext context) {
        return PluginSelectorTextComponentLike.unmarshall(
                json,
                context,
                SpreadsheetParserSelectorTextComponent::with,
                SpreadsheetParserSelectorTextComponentAlternative.class
        );
    }

    @Override
    public Class<SpreadsheetParserSelectorTextComponent> type() {
        return SpreadsheetParserSelectorTextComponent.class;
    }
}
