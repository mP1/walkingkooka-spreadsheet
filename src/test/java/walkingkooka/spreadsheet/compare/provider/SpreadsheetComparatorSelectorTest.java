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

package walkingkooka.spreadsheet.compare.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.plugin.PluginSelectorLikeTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetComparatorSelectorTest implements PluginSelectorLikeTesting<SpreadsheetComparatorSelector, SpreadsheetComparatorName> {

    // HasContentType...................................................................................................

    @Test
    public void testContentType() {
        this.contentTypeAndCheck(
            this.createObject(),
            "application/json+walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorSelector"
        );
    }

    // PluginSelectorLike...............................................................................................

    @Override
    public SpreadsheetComparatorSelector createPluginSelectorLike(final SpreadsheetComparatorName name,
                                                                  final String text) {
        return SpreadsheetComparatorSelector.with(name, text);
    }

    @Override
    public SpreadsheetComparatorName createName(final String name) {
        return SpreadsheetComparatorName.with(name);
    }

    @Override
    public SpreadsheetComparatorSelector parseString(final String text) {
        return SpreadsheetComparatorSelector.parse(text);
    }

    @Override
    public SpreadsheetComparatorSelector unmarshall(final JsonNode jsonNode,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetComparatorSelector.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetComparatorSelector createJsonNodeMarshallingValue() {
        return SpreadsheetComparatorSelector.with(
            SpreadsheetComparatorName.with("test123"),
            "456"
        );
    }

    // class.............................................................................................................

    @Override
    public Class<SpreadsheetComparatorSelector> type() {
        return SpreadsheetComparatorSelector.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetComparator.class.getSimpleName();
    }
}
