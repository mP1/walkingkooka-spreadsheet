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

package walkingkooka.spreadsheet.parser.provider;

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
 * An immutable list of {@link SpreadsheetParserSelectorToken}. This exists primarily to support marshalling/unmarshalling JSON which does
 * not support generic types.
 * <pre>
 * [
 *   {
 *     "type": "spreadsheet-parser-selector-text-token",
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
 *     "type": "spreadsheet-parser-selector-text-token",
 *     "value": {
 *       "label": "label1",
 *       "text": "text1",
 *       "alternatives": []
 *     }
 *   }
 * ]
 * </pre>
 */
public final class SpreadsheetParserSelectorTokenList extends AbstractList<SpreadsheetParserSelectorToken>
    implements ImmutableListDefaults<SpreadsheetParserSelectorTokenList, SpreadsheetParserSelectorToken> {

    public final static SpreadsheetParserSelectorTokenList EMPTY = new SpreadsheetParserSelectorTokenList(Lists.empty());

    // @VisibleForTesting
    SpreadsheetParserSelectorTokenList(final List<SpreadsheetParserSelectorToken> tokens) {
        this.tokens = tokens;
    }

    @Override
    public SpreadsheetParserSelectorToken get(int index) {
        return this.tokens.get(index);
    }

    @Override
    public int size() {
        return this.tokens.size();
    }

    private final List<SpreadsheetParserSelectorToken> tokens;

    @Override
    public void elementCheck(final SpreadsheetParserSelectorToken token) {
        Objects.requireNonNull(token, "token");
    }

    @Override
    public SpreadsheetParserSelectorTokenList setElements(final Collection<SpreadsheetParserSelectorToken> elements) {
        return (SpreadsheetParserSelectorTokenList)
            this.setElementsMaybeCreate(
                elements,
                (final Collection<SpreadsheetParserSelectorToken> collection) -> collection instanceof SpreadsheetParserSelectorTokenList,
                this::elementCheck,
                this::replaceSpreadsheetParserSelectorTokens
            );
    }

    @Override
    public SpreadsheetParserSelectorTokenList subList(final int from,
                                                      final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceSpreadsheetParserSelectorTokens(
                this.tokens.subList(
                    from,
                    to
                )
            );
    }

    private SpreadsheetParserSelectorTokenList replaceSpreadsheetParserSelectorTokens(final List<SpreadsheetParserSelectorToken> spreadsheetParserSelectorTokens) {
        return spreadsheetParserSelectorTokens.isEmpty() ?
            EMPTY :
            new SpreadsheetParserSelectorTokenList(spreadsheetParserSelectorTokens);
    }

    // json.............................................................................................................

    static SpreadsheetParserSelectorTokenList unmarshall(final JsonNode node,
                                                         final JsonNodeUnmarshallContext context) {
        return EMPTY.setElements(
            context.unmarshallList(
                node,
                SpreadsheetParserSelectorToken.class
            )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserSelectorTokenList.class),
            SpreadsheetParserSelectorTokenList::unmarshall,
            SpreadsheetParserSelectorTokenList::marshall,
            SpreadsheetParserSelectorTokenList.class
        );
    }
}
