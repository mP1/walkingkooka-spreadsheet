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

import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.plugin.PluginAliasSet;
import walkingkooka.plugin.PluginAliasSetLike;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;

/**
 * A declaration of comparator names and mapping of aliases to comparator names with parameters.
 */
public final class SpreadsheetComparatorAliasSet extends AbstractSet<SpreadsheetComparatorAlias>
    implements PluginAliasSetLike<SpreadsheetComparatorName,
    SpreadsheetComparatorInfo,
    SpreadsheetComparatorInfoSet,
    SpreadsheetComparatorSelector,
    SpreadsheetComparatorAlias,
    SpreadsheetComparatorAliasSet>,
    ImmutableSortedSetDefaults<SpreadsheetComparatorAliasSet, SpreadsheetComparatorAlias> {

    /**
     * An empty {@link SpreadsheetComparatorAliasSet}.
     */
    public final static SpreadsheetComparatorAliasSet EMPTY = new SpreadsheetComparatorAliasSet(
        PluginAliasSet.with(
            SortedSets.empty(),
            SpreadsheetComparatorPluginHelper.INSTANCE
        )
    );

    /**
     * {@see PluginAliasSet#SEPARATOR}
     */
    public final static CharacterConstant SEPARATOR = PluginAliasSet.SEPARATOR;

    /**
     * Factory that creates {@link SpreadsheetComparatorAliasSet} with the given aliases.
     */
    public static SpreadsheetComparatorAliasSet with(final SortedSet<SpreadsheetComparatorAlias> aliases) {
        return EMPTY.setElements(aliases);
    }

    public static SpreadsheetComparatorAliasSet parse(final String text) {
        return new SpreadsheetComparatorAliasSet(
            PluginAliasSet.parse(
                text,
                SpreadsheetComparatorPluginHelper.INSTANCE
            )
        );
    }

    private SpreadsheetComparatorAliasSet(final PluginAliasSet<SpreadsheetComparatorName, SpreadsheetComparatorInfo, SpreadsheetComparatorInfoSet, SpreadsheetComparatorSelector, SpreadsheetComparatorAlias, SpreadsheetComparatorAliasSet> pluginAliasSet) {
        this.pluginAliasSet = pluginAliasSet;
    }

    @Override
    public SpreadsheetComparatorSelector selector(final SpreadsheetComparatorSelector selector) {
        return this.pluginAliasSet.selector(selector);
    }

    @Override
    public Optional<SpreadsheetComparatorSelector> aliasSelector(final SpreadsheetComparatorName alias) {
        return this.pluginAliasSet.aliasSelector(alias);
    }

    @Override
    public Optional<SpreadsheetComparatorName> aliasOrName(final SpreadsheetComparatorName aliasOrName) {
        return this.pluginAliasSet.aliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetComparatorInfoSet merge(final SpreadsheetComparatorInfoSet infos) {
        return this.pluginAliasSet.merge(infos);
    }

    @Override
    public boolean containsAliasOrName(final SpreadsheetComparatorName aliasOrName) {
        return this.pluginAliasSet.containsAliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetComparatorAliasSet concatOrReplace(final SpreadsheetComparatorAlias alias) {
        return new SpreadsheetComparatorAliasSet(
            this.pluginAliasSet.concatOrReplace(alias)
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet deleteAliasOrNameAll(final Collection<SpreadsheetComparatorName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.deleteAliasOrNameAll(aliasOrNames)
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet keepAliasOrNameAll(final Collection<SpreadsheetComparatorName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.keepAliasOrNameAll(aliasOrNames)
        );
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    public Comparator<? super SpreadsheetComparatorAlias> comparator() {
        return this.pluginAliasSet.comparator();
    }

    @Override
    public Iterator<SpreadsheetComparatorAlias> iterator() {
        return this.pluginAliasSet.stream().iterator();
    }

    @Override
    public int size() {
        return this.pluginAliasSet.size();
    }

    @Override
    public SpreadsheetComparatorAliasSet concat(final SpreadsheetComparatorAlias alias) {
        return this.setElements(
            this.pluginAliasSet.concat(alias)
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet concatAll(final Collection<SpreadsheetComparatorAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.concatAll(aliases)
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet replace(final SpreadsheetComparatorAlias oldAlias,
                                                 final SpreadsheetComparatorAlias newAlias) {
        return this.setElements(
            this.pluginAliasSet.replace(
                oldAlias,
                newAlias
            )
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet delete(final SpreadsheetComparatorAlias alias) {
        return this.setElements(
            this.pluginAliasSet.delete(alias)
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet deleteAll(final Collection<SpreadsheetComparatorAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.deleteAll(aliases)
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet setElements(final SortedSet<SpreadsheetComparatorAlias> aliases) {
        final SpreadsheetComparatorAliasSet after;

        if (aliases instanceof SpreadsheetComparatorAliasSet) {
            after = (SpreadsheetComparatorAliasSet) aliases;
        } else {
            after = new SpreadsheetComparatorAliasSet(
                this.pluginAliasSet.setElements(aliases)
            );
            return this.pluginAliasSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public SpreadsheetComparatorAliasSet setElementsFailIfDifferent(SortedSet<SpreadsheetComparatorAlias> elements) {
        return ImmutableSortedSetDefaults.super.setElementsFailIfDifferent(elements);
    }

    @Override
    public SortedSet<SpreadsheetComparatorAlias> toSet() {
        return this.pluginAliasSet.toSet();
    }

    @Override
    public SpreadsheetComparatorAliasSet subSet(final SpreadsheetComparatorAlias from,
                                                final SpreadsheetComparatorAlias to) {
        return this.setElements(
            this.pluginAliasSet.subSet(
                from,
                to
            )
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet headSet(final SpreadsheetComparatorAlias alias) {
        return this.setElements(
            this.pluginAliasSet.headSet(alias)
        );
    }

    @Override
    public SpreadsheetComparatorAliasSet tailSet(final SpreadsheetComparatorAlias alias) {
        return this.setElements(
            this.pluginAliasSet.tailSet(alias)
        );
    }

    @Override
    public SpreadsheetComparatorAlias first() {
        return this.pluginAliasSet.first();
    }

    @Override
    public SpreadsheetComparatorAlias last() {
        return this.pluginAliasSet.last();
    }

    @Override
    public void elementCheck(final SpreadsheetComparatorAlias alias) {
        Objects.requireNonNull(alias, "alias");
    }

    @Override
    public String text() {
        return this.pluginAliasSet.text();
    }

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.pluginAliasSet.printTree(printer);
    }

    private final PluginAliasSet<SpreadsheetComparatorName, SpreadsheetComparatorInfo, SpreadsheetComparatorInfoSet, SpreadsheetComparatorSelector, SpreadsheetComparatorAlias, SpreadsheetComparatorAliasSet> pluginAliasSet;

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
            this.pluginAliasSet.text()
        );
    }

    static SpreadsheetComparatorAliasSet unmarshall(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetComparatorAliasSet.class),
            SpreadsheetComparatorAliasSet::unmarshall,
            SpreadsheetComparatorAliasSet::marshall,
            SpreadsheetComparatorAliasSet.class
        );
        SpreadsheetComparatorInfoSet.EMPTY.size(); // trigger static init and json marshall/unmarshall registry
    }
}
