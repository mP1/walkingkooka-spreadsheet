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
 * A declaration of parser names and mapping of aliases to parser names with parameters.
 */
public final class SpreadsheetParserAliasSet extends AbstractSet<SpreadsheetParserAlias>
    implements PluginAliasSetLike<SpreadsheetParserName,
    SpreadsheetParserInfo,
    SpreadsheetParserInfoSet,
    SpreadsheetParserSelector,
    SpreadsheetParserAlias,
    SpreadsheetParserAliasSet>,
    ImmutableSortedSetDefaults<SpreadsheetParserAliasSet, SpreadsheetParserAlias> {

    /**
     * An empty {@link SpreadsheetParserAliasSet}.
     */
    public final static SpreadsheetParserAliasSet EMPTY = new SpreadsheetParserAliasSet(
        PluginAliasSet.with(
            SortedSets.empty(),
            SpreadsheetParserPluginHelper.INSTANCE
        )
    );

    /**
     * {@see PluginAliasSet#SEPARATOR}
     */
    public final static CharacterConstant SEPARATOR = PluginAliasSet.SEPARATOR;

    public static SpreadsheetParserAliasSet parse(final String text) {
        return new SpreadsheetParserAliasSet(
            PluginAliasSet.parse(
                text,
                SpreadsheetParserPluginHelper.INSTANCE
            )
        );
    }

    /**
     * Factory that creates {@link SpreadsheetParserAliasSet} with the given aliases.
     */
    public static SpreadsheetParserAliasSet with(final Collection<SpreadsheetParserAlias> aliases) {
        return EMPTY.setElements(aliases);
    }

    private SpreadsheetParserAliasSet(final PluginAliasSet<SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserInfoSet, SpreadsheetParserSelector, SpreadsheetParserAlias, SpreadsheetParserAliasSet> pluginAliasSet) {
        this.pluginAliasSet = pluginAliasSet;
    }

    @Override
    public SpreadsheetParserSelector selector(final SpreadsheetParserSelector selector) {
        return this.pluginAliasSet.selector(selector);
    }

    @Override
    public Optional<SpreadsheetParserSelector> aliasSelector(final SpreadsheetParserName alias) {
        return this.pluginAliasSet.aliasSelector(alias);
    }

    @Override
    public Optional<SpreadsheetParserName> aliasOrName(final SpreadsheetParserName aliasOrName) {
        return this.pluginAliasSet.aliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetParserInfoSet merge(final SpreadsheetParserInfoSet infos) {
        return this.pluginAliasSet.merge(infos);
    }

    @Override
    public boolean containsAliasOrName(final SpreadsheetParserName aliasOrName) {
        return this.pluginAliasSet.containsAliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetParserAliasSet concatOrReplace(final SpreadsheetParserAlias alias) {
        return new SpreadsheetParserAliasSet(
            this.pluginAliasSet.concatOrReplace(alias)
        );
    }

    @Override
    public SpreadsheetParserAliasSet deleteAliasOrNameAll(final Collection<SpreadsheetParserName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.deleteAliasOrNameAll(aliasOrNames)
        );
    }

    @Override
    public SpreadsheetParserAliasSet keepAliasOrNameAll(final Collection<SpreadsheetParserName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.keepAliasOrNameAll(aliasOrNames)
        );
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    public Comparator<? super SpreadsheetParserAlias> comparator() {
        return this.pluginAliasSet.comparator();
    }

    @Override
    public Iterator<SpreadsheetParserAlias> iterator() {
        return this.pluginAliasSet.stream().iterator();
    }

    @Override
    public int size() {
        return this.pluginAliasSet.size();
    }

    @Override
    public SpreadsheetParserAliasSet concat(final SpreadsheetParserAlias alias) {
        return this.setElements(
            this.pluginAliasSet.concat(alias)
        );
    }

    @Override
    public SpreadsheetParserAliasSet concatAll(final Collection<SpreadsheetParserAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.concatAll(aliases)
        );
    }

    @Override
    public SpreadsheetParserAliasSet replace(final SpreadsheetParserAlias oldAlias,
                                             final SpreadsheetParserAlias newAlias) {
        return this.setElements(
            this.pluginAliasSet.replace(
                oldAlias,
                newAlias
            )
        );
    }

    @Override
    public SpreadsheetParserAliasSet delete(final SpreadsheetParserAlias alias) {
        return this.setElements(
            this.pluginAliasSet.delete(alias)
        );
    }

    @Override
    public SpreadsheetParserAliasSet deleteAll(final Collection<SpreadsheetParserAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.deleteAll(aliases)
        );
    }

    @Override
    public SpreadsheetParserAliasSet setElements(final Collection<SpreadsheetParserAlias> aliases) {
        final SpreadsheetParserAliasSet after;

        if (aliases instanceof SpreadsheetParserAliasSet) {
            after = (SpreadsheetParserAliasSet) aliases;
        } else {
            after = new SpreadsheetParserAliasSet(
                this.pluginAliasSet.setElements(aliases)
            );
            return this.pluginAliasSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public SpreadsheetParserAliasSet setElementsFailIfDifferent(final Collection<SpreadsheetParserAlias> aliases) {
        return ImmutableSortedSetDefaults.super.setElementsFailIfDifferent(aliases);
    }

    @Override
    public SortedSet<SpreadsheetParserAlias> toSet() {
        return this.pluginAliasSet.toSet();
    }

    @Override
    public SpreadsheetParserAliasSet subSet(final SpreadsheetParserAlias from,
                                            final SpreadsheetParserAlias to) {
        return this.setElements(
            this.pluginAliasSet.subSet(
                from,
                to
            )
        );
    }

    @Override
    public SpreadsheetParserAliasSet headSet(final SpreadsheetParserAlias alias) {
        return this.setElements(
            this.pluginAliasSet.headSet(alias)
        );
    }

    @Override
    public SpreadsheetParserAliasSet tailSet(final SpreadsheetParserAlias alias) {
        return this.setElements(
            this.pluginAliasSet.tailSet(alias)
        );
    }

    @Override
    public SpreadsheetParserAlias first() {
        return this.pluginAliasSet.first();
    }

    @Override
    public SpreadsheetParserAlias last() {
        return this.pluginAliasSet.last();
    }

    @Override
    public void elementCheck(final SpreadsheetParserAlias alias) {
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

    private final PluginAliasSet<SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserInfoSet, SpreadsheetParserSelector, SpreadsheetParserAlias, SpreadsheetParserAliasSet> pluginAliasSet;

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
            this.pluginAliasSet.text()
        );
    }

    static SpreadsheetParserAliasSet unmarshall(final JsonNode node,
                                                final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserAliasSet.class),
            SpreadsheetParserAliasSet::unmarshall,
            SpreadsheetParserAliasSet::marshall,
            SpreadsheetParserAliasSet.class
        );
        SpreadsheetParserInfoSet.EMPTY.size(); // trigger static init and json marshall/unmarshall registry
    }
}
