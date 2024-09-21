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

package walkingkooka.spreadsheet.compare;

import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.plugin.PluginNameLike;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable list of {@link SpreadsheetComparatorName}. This can be used to hold a list of names for sorting.
 * This list will be used to build the items of possible sort comparators within the SORT menu when the user selects a range of cells.
 * It will also be used to validate active comparators for sorting operations.
 */
public final class SpreadsheetComparatorNameList extends AbstractList<SpreadsheetComparatorName>
        implements ImmutableListDefaults<SpreadsheetComparatorNameList, SpreadsheetComparatorName>,
        HasText,
        HasUrlFragment {

    /**
     * An empty {@link SpreadsheetComparatorNameList}.
     */
    public final static SpreadsheetComparatorNameList EMPTY = new SpreadsheetComparatorNameList(
            Lists.empty()
    );

    /**
     * Parses a CSV string of {@link SpreadsheetComparatorName names} into a {@link SpreadsheetComparatorNameList}.
     */
    public static SpreadsheetComparatorNameList parse(final String text) {
        return PluginNameLike.parse(
                text,
                SpreadsheetComparatorName::with,
                SpreadsheetComparatorNameList::with
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetComparatorNameList} from the list of {@link SpreadsheetComparatorName names}.
     */
    public static SpreadsheetComparatorNameList with(final List<SpreadsheetComparatorName> names) {
        Objects.requireNonNull(names, "names");

        return names instanceof SpreadsheetComparatorNameList ?
                (SpreadsheetComparatorNameList) names :
                with0(
                        Lists.immutable(names)
                );
    }

    private static SpreadsheetComparatorNameList with0(final List<SpreadsheetComparatorName> names) {
        final SpreadsheetComparatorNameList list;

        switch(names.size()) {
            case 0:
              list = EMPTY;
              break;
            default:
                list = new SpreadsheetComparatorNameList(names);
                break;
        }

        return list;
    }

    private SpreadsheetComparatorNameList(final List<SpreadsheetComparatorName> names) {
        this.names = names;
    }

    @Override
    public SpreadsheetComparatorName get(int index) {
        return this.names.get(index);
    }

    @Override
    public int size() {
        return this.names.size();
    }

    private final List<SpreadsheetComparatorName> names;

    @Override
    public SpreadsheetComparatorNameList setElements(final List<SpreadsheetComparatorName> names) {
        final SpreadsheetComparatorNameList copy = with(names);
        return this.equals(copy) ?
                this :
                copy;
    }

    // HasUrlFragment...................................................................................................

    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(
                this.text()
        );
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return CharacterConstant.COMMA.toSeparatedString(
                this,
                SpreadsheetComparatorName::value
        );
    }

    // json.............................................................................................................

    static SpreadsheetComparatorNameList unmarshall(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
                this.text()
        );
    }

    static {
        SpreadsheetComparatorName.DATE.toString();

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetComparatorNameList.class),
                SpreadsheetComparatorNameList::unmarshall,
                SpreadsheetComparatorNameList::marshall,
                SpreadsheetComparatorNameList.class
        );
    }
}
