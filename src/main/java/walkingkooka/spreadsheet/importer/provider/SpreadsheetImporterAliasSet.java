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

package walkingkooka.spreadsheet.importer.provider;

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
 * A declaration of importer names and mapping of aliases to importer names with parameters.
 */
public final class SpreadsheetImporterAliasSet extends AbstractSet<SpreadsheetImporterAlias>
    implements PluginAliasSetLike<SpreadsheetImporterName,
    SpreadsheetImporterInfo,
    SpreadsheetImporterInfoSet,
    SpreadsheetImporterSelector,
    SpreadsheetImporterAlias,
    SpreadsheetImporterAliasSet>,
    ImmutableSortedSetDefaults<SpreadsheetImporterAliasSet, SpreadsheetImporterAlias> {

    /**
     * An empty {@link SpreadsheetImporterAliasSet}.
     */
    public final static SpreadsheetImporterAliasSet EMPTY = new SpreadsheetImporterAliasSet(
        PluginAliasSet.with(
            SortedSets.empty(),
            SpreadsheetImporterPluginHelper.INSTANCE
        )
    );

    /**
     * {@see PluginAliasSet#SEPARATOR}
     */
    public final static CharacterConstant SEPARATOR = PluginAliasSet.SEPARATOR;

    /**
     * Factory that creates {@link SpreadsheetImporterAliasSet} with the given aliases.
     */
    public static SpreadsheetImporterAliasSet with(final Collection<SpreadsheetImporterAlias> aliases) {
        return EMPTY.setElements(aliases);
    }

    public static SpreadsheetImporterAliasSet parse(final String text) {
        return new SpreadsheetImporterAliasSet(
            PluginAliasSet.parse(
                text,
                SpreadsheetImporterPluginHelper.INSTANCE
            )
        );
    }

    private SpreadsheetImporterAliasSet(final PluginAliasSet<SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet, SpreadsheetImporterSelector, SpreadsheetImporterAlias, SpreadsheetImporterAliasSet> pluginAliasSet) {
        this.pluginAliasSet = pluginAliasSet;
    }

    @Override
    public SpreadsheetImporterSelector selector(final SpreadsheetImporterSelector selector) {
        return this.pluginAliasSet.selector(selector);
    }

    @Override
    public Optional<SpreadsheetImporterSelector> aliasSelector(final SpreadsheetImporterName alias) {
        return this.pluginAliasSet.aliasSelector(alias);
    }

    @Override
    public Optional<SpreadsheetImporterName> aliasOrName(final SpreadsheetImporterName aliasOrName) {
        return this.pluginAliasSet.aliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetImporterInfoSet merge(final SpreadsheetImporterInfoSet infos) {
        return this.pluginAliasSet.merge(infos);
    }

    @Override
    public boolean containsAliasOrName(final SpreadsheetImporterName aliasOrName) {
        return this.pluginAliasSet.containsAliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetImporterAliasSet concatOrReplace(final SpreadsheetImporterAlias alias) {
        return new SpreadsheetImporterAliasSet(
            this.pluginAliasSet.concatOrReplace(alias)
        );
    }

    @Override
    public SpreadsheetImporterAliasSet deleteAliasOrNameAll(final Collection<SpreadsheetImporterName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.deleteAliasOrNameAll(aliasOrNames)
        );
    }

    @Override
    public SpreadsheetImporterAliasSet keepAliasOrNameAll(final Collection<SpreadsheetImporterName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.keepAliasOrNameAll(aliasOrNames)
        );
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    public Comparator<? super SpreadsheetImporterAlias> comparator() {
        return this.pluginAliasSet.comparator();
    }

    @Override
    public Iterator<SpreadsheetImporterAlias> iterator() {
        return this.pluginAliasSet.stream().iterator();
    }

    @Override
    public int size() {
        return this.pluginAliasSet.size();
    }

    @Override
    public SpreadsheetImporterAliasSet concat(final SpreadsheetImporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.concat(alias)
        );
    }

    @Override
    public SpreadsheetImporterAliasSet concatAll(final Collection<SpreadsheetImporterAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.concatAll(aliases)
        );
    }

    @Override
    public SpreadsheetImporterAliasSet replace(final SpreadsheetImporterAlias oldAlias,
                                               final SpreadsheetImporterAlias newAlias) {
        return this.setElements(
            this.pluginAliasSet.replace(
                oldAlias,
                newAlias
            )
        );
    }

    @Override
    public SpreadsheetImporterAliasSet delete(final SpreadsheetImporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.delete(alias)
        );
    }

    @Override
    public SpreadsheetImporterAliasSet deleteAll(final Collection<SpreadsheetImporterAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.deleteAll(aliases)
        );
    }

    @Override
    public SpreadsheetImporterAliasSet setElementsFailIfDifferent(final Collection<SpreadsheetImporterAlias> aliases) {
        final SpreadsheetImporterAliasSet after = new SpreadsheetImporterAliasSet(
            this.pluginAliasSet.setElementsFailIfDifferent(aliases)
        );
        return this.pluginAliasSet.equals(aliases) ?
            this :
            after;
    }

    @Override
    public SpreadsheetImporterAliasSet setElements(final Collection<SpreadsheetImporterAlias> aliases) {
        final SpreadsheetImporterAliasSet after;

        if (aliases instanceof SpreadsheetImporterAliasSet) {
            after = (SpreadsheetImporterAliasSet) aliases;
        } else {
            after = new SpreadsheetImporterAliasSet(
                this.pluginAliasSet.setElements(aliases)
            );
            return this.pluginAliasSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public SortedSet<SpreadsheetImporterAlias> toSet() {
        return this.pluginAliasSet.toSet();
    }

    @Override
    public SpreadsheetImporterAliasSet subSet(final SpreadsheetImporterAlias from,
                                              final SpreadsheetImporterAlias to) {
        return this.setElements(
            this.pluginAliasSet.subSet(
                from,
                to
            )
        );
    }

    @Override
    public SpreadsheetImporterAliasSet headSet(final SpreadsheetImporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.headSet(alias)
        );
    }

    @Override
    public SpreadsheetImporterAliasSet tailSet(final SpreadsheetImporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.tailSet(alias)
        );
    }

    @Override
    public SpreadsheetImporterAlias first() {
        return this.pluginAliasSet.first();
    }

    @Override
    public SpreadsheetImporterAlias last() {
        return this.pluginAliasSet.last();
    }

    @Override
    public void elementCheck(final SpreadsheetImporterAlias alias) {
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

    private final PluginAliasSet<SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet, SpreadsheetImporterSelector, SpreadsheetImporterAlias, SpreadsheetImporterAliasSet> pluginAliasSet;

    // Json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
            this.pluginAliasSet.text()
        );
    }

    static SpreadsheetImporterAliasSet unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetImporterAliasSet.class),
            SpreadsheetImporterAliasSet::unmarshall,
            SpreadsheetImporterAliasSet::marshall,
            SpreadsheetImporterAliasSet.class
        );
    }
}
