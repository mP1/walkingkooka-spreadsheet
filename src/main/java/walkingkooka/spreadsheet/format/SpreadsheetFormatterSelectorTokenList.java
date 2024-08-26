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

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable list of {@link SpreadsheetFormatterSelectorToken}. This exists primarily to support marshalling/unmarshalling JSON which does
 * not support generic types.
 * <pre>
 * [
 *   {
 *     "type": "spreadsheet-formatter-selector-text-component",
 *     "value": {
 *       "label": "label1",
 *       "text": "text1",
 *       "alternatives": [
 *         {
 *           "label": "alternative-label-1",
 *           "text": "alternative-text-1"
 *         }
 *       ]
 *     }
 *   },
 *   {
 *     "type": "spreadsheet-formatter-selector-text-component",
 *     "value": {
 *       "label": "label1",
 *       "text": "text1",
 *       "alternatives": []
 *     }
 *   }
 * ]
 * </pre>
 */
public final class SpreadsheetFormatterSelectorTokenList extends AbstractList<SpreadsheetFormatterSelectorToken>
        implements ImmutableListDefaults<SpreadsheetFormatterSelectorTokenList, SpreadsheetFormatterSelectorToken> {

    public static SpreadsheetFormatterSelectorTokenList with(final List<SpreadsheetFormatterSelectorToken> components) {
        Objects.requireNonNull(components, "components");

        return components instanceof SpreadsheetFormatterSelectorTokenList ?
                (SpreadsheetFormatterSelectorTokenList) components :
                new SpreadsheetFormatterSelectorTokenList(
                        Lists.immutable(components)
                );
    }

    private SpreadsheetFormatterSelectorTokenList(final List<SpreadsheetFormatterSelectorToken> components) {
        this.components = components;
    }

    @Override
    public SpreadsheetFormatterSelectorToken get(int index) {
        return this.components.get(index);
    }

    @Override
    public int size() {
        return this.components.size();
    }

    private final List<SpreadsheetFormatterSelectorToken> components;

    @Override
    public SpreadsheetFormatterSelectorTokenList setElements(final List<SpreadsheetFormatterSelectorToken> components) {
        final SpreadsheetFormatterSelectorTokenList copy = with(components);
        return this.equals(copy) ?
                this :
                copy;
    }

    // json.............................................................................................................

    static SpreadsheetFormatterSelectorTokenList unmarshall(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return with(
                Cast.to(
                        context.unmarshallList(
                                node,
                                SpreadsheetFormatterSelectorToken.class
                        )
                )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    static {
        SpreadsheetFormatterSelectorToken.with(
                "", // label
                "", // text
                SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
        );

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormatterSelectorTokenList.class),
                SpreadsheetFormatterSelectorTokenList::unmarshall,
                SpreadsheetFormatterSelectorTokenList::marshall,
                SpreadsheetFormatterSelectorTokenList.class
        );
    }
}
