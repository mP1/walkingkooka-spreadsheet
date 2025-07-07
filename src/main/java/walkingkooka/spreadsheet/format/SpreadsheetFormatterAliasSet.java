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

public final class SpreadsheetFormatterAliasSet extends AbstractSet<SpreadsheetFormatterAlias>
    implements PluginAliasSetLike<SpreadsheetFormatterName,
    SpreadsheetFormatterInfo,
    SpreadsheetFormatterInfoSet,
    SpreadsheetFormatterSelector,
    SpreadsheetFormatterAlias,
    SpreadsheetFormatterAliasSet>,
    ImmutableSortedSetDefaults<SpreadsheetFormatterAliasSet, SpreadsheetFormatterAlias> {

    /**
     * An empty {@link SpreadsheetFormatterAliasSet}.
     */
    public final static SpreadsheetFormatterAliasSet EMPTY = new SpreadsheetFormatterAliasSet(
        PluginAliasSet.with(
            SortedSets.empty(),
            SpreadsheetFormatterPluginHelper.INSTANCE
        )
    );

    /**
     * {@see PluginAliasSet#SEPARATOR}
     */
    public final static CharacterConstant SEPARATOR = PluginAliasSet.SEPARATOR;

    /**
     * Factory that creates {@link SpreadsheetFormatterAliasSet} with the given aliases.
     */
    public static SpreadsheetFormatterAliasSet with(final SortedSet<SpreadsheetFormatterAlias> aliases) {
        return EMPTY.setElements(aliases);
    }

    public static SpreadsheetFormatterAliasSet parse(final String text) {
        return new SpreadsheetFormatterAliasSet(
            PluginAliasSet.parse(
                text,
                SpreadsheetFormatterPluginHelper.INSTANCE
            )
        );
    }

    private SpreadsheetFormatterAliasSet(final PluginAliasSet<SpreadsheetFormatterName, SpreadsheetFormatterInfo, SpreadsheetFormatterInfoSet, SpreadsheetFormatterSelector, SpreadsheetFormatterAlias, SpreadsheetFormatterAliasSet> pluginAliasSet) {
        this.pluginAliasSet = pluginAliasSet;
    }

    @Override
    public SpreadsheetFormatterSelector selector(final SpreadsheetFormatterSelector selector) {
        return this.pluginAliasSet.selector(selector);
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> aliasSelector(final SpreadsheetFormatterName alias) {
        return this.pluginAliasSet.aliasSelector(alias);
    }

    @Override
    public Optional<SpreadsheetFormatterName> aliasOrName(final SpreadsheetFormatterName aliasOrName) {
        return this.pluginAliasSet.aliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetFormatterInfoSet merge(final SpreadsheetFormatterInfoSet infos) {
        return this.pluginAliasSet.merge(infos);
    }

    @Override
    public boolean containsAliasOrName(final SpreadsheetFormatterName aliasOrName) {
        return this.pluginAliasSet.containsAliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetFormatterAliasSet concatOrReplace(final SpreadsheetFormatterAlias alias) {
        return new SpreadsheetFormatterAliasSet(
            this.pluginAliasSet.concatOrReplace(alias)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet deleteAliasOrNameAll(final Collection<SpreadsheetFormatterName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.deleteAliasOrNameAll(aliasOrNames)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet keepAliasOrNameAll(final Collection<SpreadsheetFormatterName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.keepAliasOrNameAll(aliasOrNames)
        );
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    public Comparator<? super SpreadsheetFormatterAlias> comparator() {
        return this.pluginAliasSet.comparator();
    }

    @Override
    public Iterator<SpreadsheetFormatterAlias> iterator() {
        return this.pluginAliasSet.stream().iterator();
    }

    @Override
    public int size() {
        return this.pluginAliasSet.size();
    }

    @Override
    public SpreadsheetFormatterAliasSet setElements(final SortedSet<SpreadsheetFormatterAlias> aliases) {
        final SpreadsheetFormatterAliasSet after;

        if (aliases instanceof SpreadsheetFormatterAliasSet) {
            after = (SpreadsheetFormatterAliasSet) aliases;
        } else {
            after = new SpreadsheetFormatterAliasSet(
                this.pluginAliasSet.setElements(aliases)
            );
            return this.pluginAliasSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public SpreadsheetFormatterAliasSet setElementsFailIfDifferent(SortedSet<SpreadsheetFormatterAlias> sortedSet) {
        return null;
    }

    @Override
    public SortedSet<SpreadsheetFormatterAlias> toSet() {
        return this.pluginAliasSet.toSet();
    }

    @Override
    public SpreadsheetFormatterAliasSet subSet(final SpreadsheetFormatterAlias from,
                                               final SpreadsheetFormatterAlias to) {
        return this.setElements(
            this.pluginAliasSet.subSet(
                from,
                to
            )
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet headSet(final SpreadsheetFormatterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.headSet(alias)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet tailSet(final SpreadsheetFormatterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.tailSet(alias)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet concat(final SpreadsheetFormatterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.concat(alias)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet concatAll(final Collection<SpreadsheetFormatterAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.concatAll(aliases)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet delete(final SpreadsheetFormatterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.delete(alias)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet deleteAll(final Collection<SpreadsheetFormatterAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.deleteAll(aliases)
        );
    }

    @Override
    public SpreadsheetFormatterAliasSet replace(final SpreadsheetFormatterAlias oldAlias,
                                                final SpreadsheetFormatterAlias newAlias) {
        return this.setElements(
            this.pluginAliasSet.replace(
                oldAlias,
                newAlias
            )
        );
    }

    @Override
    public SpreadsheetFormatterAlias first() {
        return this.pluginAliasSet.first();
    }

    @Override
    public SpreadsheetFormatterAlias last() {
        return this.pluginAliasSet.last();
    }

    @Override
    public void elementCheck(final SpreadsheetFormatterAlias alias) {
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

    private final PluginAliasSet<SpreadsheetFormatterName, SpreadsheetFormatterInfo, SpreadsheetFormatterInfoSet, SpreadsheetFormatterSelector, SpreadsheetFormatterAlias, SpreadsheetFormatterAliasSet> pluginAliasSet;

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
            this.pluginAliasSet.text()
        );
    }

    static SpreadsheetFormatterAliasSet unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetFormatterAliasSet.class),
            SpreadsheetFormatterAliasSet::unmarshall,
            SpreadsheetFormatterAliasSet::marshall,
            SpreadsheetFormatterAliasSet.class
        );
        SpreadsheetFormatterInfoSet.EMPTY.size(); // trigger static init and json marshall/unmarshall registry
    }
}