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

import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractList;
import java.util.Collection;
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

    public final static SpreadsheetFormatterSelectorTokenList EMPTY = new SpreadsheetFormatterSelectorTokenList(Lists.empty());

    // @VisibleForTesting
    SpreadsheetFormatterSelectorTokenList(final List<SpreadsheetFormatterSelectorToken> tokens) {
        this.tokens = tokens;
    }

    @Override
    public SpreadsheetFormatterSelectorToken get(int index) {
        return this.tokens.get(index);
    }

    @Override
    public int size() {
        return this.tokens.size();
    }

    private final List<SpreadsheetFormatterSelectorToken> tokens;

    @Override
    public void elementCheck(final SpreadsheetFormatterSelectorToken token) {
        Objects.requireNonNull(token, "Includes null token");
    }

    @Override
    public SpreadsheetFormatterSelectorTokenList setElements(final Collection<SpreadsheetFormatterSelectorToken> collection) {
        final SpreadsheetFormatterSelectorTokenList spreadsheetFormatterSelectorTokenList;

        if (collection instanceof SpreadsheetFormatterSelectorTokenList) {
            spreadsheetFormatterSelectorTokenList = (SpreadsheetFormatterSelectorTokenList) collection;
        } else {
            Objects.requireNonNull(collection, "spreadsheetFormatterSelectorTokens");

            final List<SpreadsheetFormatterSelectorToken> copy = Lists.array();
            for (final SpreadsheetFormatterSelectorToken each : collection) {
                this.elementCheck(each);
                copy.add(each);
            }

            spreadsheetFormatterSelectorTokenList = this.equals(copy) ?
                this :
                this.replaceSpreadsheetFormatterSelectorTokens(copy);
        }

        return spreadsheetFormatterSelectorTokenList;
    }

    @Override
    public SpreadsheetFormatterSelectorTokenList subList(final int from,
                                                         final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceSpreadsheetFormatterSelectorTokens(
                this.tokens.subList(
                    from,
                    to
                )
            );
    }

    private SpreadsheetFormatterSelectorTokenList replaceSpreadsheetFormatterSelectorTokens(final List<SpreadsheetFormatterSelectorToken> spreadsheetFormatterSelectorTokens) {
        return spreadsheetFormatterSelectorTokens.isEmpty() ?
            EMPTY :
            new SpreadsheetFormatterSelectorTokenList(spreadsheetFormatterSelectorTokens);
    }

    // json.............................................................................................................

    static SpreadsheetFormatterSelectorTokenList unmarshall(final JsonNode node,
                                                            final JsonNodeUnmarshallContext context) {
        return EMPTY.setElements(
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
