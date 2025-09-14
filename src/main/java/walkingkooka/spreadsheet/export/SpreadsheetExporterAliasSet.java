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

package walkingkooka.spreadsheet.export;

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
 * A declaration of exporter names and mapping of aliases to exporter names with parameters.
 */
public final class SpreadsheetExporterAliasSet extends AbstractSet<SpreadsheetExporterAlias>
    implements PluginAliasSetLike<SpreadsheetExporterName,
    SpreadsheetExporterInfo,
    SpreadsheetExporterInfoSet,
    SpreadsheetExporterSelector,
    SpreadsheetExporterAlias,
    SpreadsheetExporterAliasSet>,
    ImmutableSortedSetDefaults<SpreadsheetExporterAliasSet, SpreadsheetExporterAlias> {

    /**
     * An empty {@link SpreadsheetExporterAliasSet}.
     */
    public final static SpreadsheetExporterAliasSet EMPTY = new SpreadsheetExporterAliasSet(
        PluginAliasSet.with(
            SortedSets.empty(),
            SpreadsheetExporterPluginHelper.INSTANCE
        )
    );

    /**
     * {@see PluginAliasSet#SEPARATOR}
     */
    public final static CharacterConstant SEPARATOR = PluginAliasSet.SEPARATOR;

    /**
     * Factory that creates {@link SpreadsheetExporterAliasSet} with the given aliases.
     */
    public static SpreadsheetExporterAliasSet with(final Collection<SpreadsheetExporterAlias> aliases) {
        return EMPTY.setElements(aliases);
    }

    public static SpreadsheetExporterAliasSet parse(final String text) {
        return new SpreadsheetExporterAliasSet(
            PluginAliasSet.parse(
                text,
                SpreadsheetExporterPluginHelper.INSTANCE
            )
        );
    }

    private SpreadsheetExporterAliasSet(final PluginAliasSet<SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector, SpreadsheetExporterAlias, SpreadsheetExporterAliasSet> pluginAliasSet) {
        this.pluginAliasSet = pluginAliasSet;
    }

    @Override
    public SpreadsheetExporterSelector selector(final SpreadsheetExporterSelector selector) {
        return this.pluginAliasSet.selector(selector);
    }

    @Override
    public Optional<SpreadsheetExporterSelector> aliasSelector(final SpreadsheetExporterName alias) {
        return this.pluginAliasSet.aliasSelector(alias);
    }

    @Override
    public Optional<SpreadsheetExporterName> aliasOrName(final SpreadsheetExporterName aliasOrName) {
        return this.pluginAliasSet.aliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetExporterInfoSet merge(final SpreadsheetExporterInfoSet infos) {
        return this.pluginAliasSet.merge(infos);
    }

    @Override
    public boolean containsAliasOrName(final SpreadsheetExporterName aliasOrName) {
        return this.pluginAliasSet.containsAliasOrName(aliasOrName);
    }

    @Override
    public SpreadsheetExporterAliasSet concatOrReplace(final SpreadsheetExporterAlias alias) {
        return new SpreadsheetExporterAliasSet(
            this.pluginAliasSet.concatOrReplace(alias)
        );
    }

    @Override
    public SpreadsheetExporterAliasSet deleteAliasOrNameAll(final Collection<SpreadsheetExporterName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.deleteAliasOrNameAll(aliasOrNames)
        );
    }

    @Override
    public SpreadsheetExporterAliasSet keepAliasOrNameAll(final Collection<SpreadsheetExporterName> aliasOrNames) {
        return this.setElements(
            this.pluginAliasSet.keepAliasOrNameAll(aliasOrNames)
        );
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    public Comparator<? super SpreadsheetExporterAlias> comparator() {
        return this.pluginAliasSet.comparator();
    }

    @Override
    public Iterator<SpreadsheetExporterAlias> iterator() {
        return this.pluginAliasSet.stream().iterator();
    }

    @Override
    public int size() {
        return this.pluginAliasSet.size();
    }

    @Override
    public SpreadsheetExporterAliasSet concat(final SpreadsheetExporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.concat(alias)
        );
    }

    @Override
    public SpreadsheetExporterAliasSet concatAll(final Collection<SpreadsheetExporterAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.concatAll(aliases)
        );
    }

    @Override
    public SpreadsheetExporterAliasSet replace(final SpreadsheetExporterAlias oldAlias,
                                               final SpreadsheetExporterAlias newAlias) {
        return this.setElements(
            this.pluginAliasSet.replace(
                oldAlias,
                newAlias
            )
        );
    }

    @Override
    public SpreadsheetExporterAliasSet delete(final SpreadsheetExporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.delete(alias)
        );
    }

    @Override
    public SpreadsheetExporterAliasSet deleteAll(final Collection<SpreadsheetExporterAlias> aliases) {
        return this.setElements(
            this.pluginAliasSet.deleteAll(aliases)
        );
    }

    @Override
    public SpreadsheetExporterAliasSet setElements(final Collection<SpreadsheetExporterAlias> aliases) {
        final SpreadsheetExporterAliasSet after;

        if (aliases instanceof SpreadsheetExporterAliasSet) {
            after = (SpreadsheetExporterAliasSet) aliases;
        } else {
            after = new SpreadsheetExporterAliasSet(
                this.pluginAliasSet.setElements(aliases)
            );
            return this.pluginAliasSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public SpreadsheetExporterAliasSet setElementsFailIfDifferent(final Collection<SpreadsheetExporterAlias> elements) {
        return ImmutableSortedSetDefaults.super.setElementsFailIfDifferent(elements);
    }

    @Override
    public SortedSet<SpreadsheetExporterAlias> toSet() {
        return this.pluginAliasSet.toSet();
    }

    @Override
    public SpreadsheetExporterAliasSet subSet(final SpreadsheetExporterAlias from,
                                              final SpreadsheetExporterAlias to) {
        return this.setElements(
            this.pluginAliasSet.subSet(
                from,
                to
            )
        );
    }

    @Override
    public SpreadsheetExporterAliasSet headSet(final SpreadsheetExporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.headSet(alias)
        );
    }

    @Override
    public SpreadsheetExporterAliasSet tailSet(final SpreadsheetExporterAlias alias) {
        return this.setElements(
            this.pluginAliasSet.tailSet(alias)
        );
    }

    @Override
    public SpreadsheetExporterAlias first() {
        return this.pluginAliasSet.first();
    }

    @Override
    public SpreadsheetExporterAlias last() {
        return this.pluginAliasSet.last();
    }

    @Override
    public void elementCheck(final SpreadsheetExporterAlias alias) {
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

    private final PluginAliasSet<SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector, SpreadsheetExporterAlias, SpreadsheetExporterAliasSet> pluginAliasSet;

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
            this.pluginAliasSet.text()
        );
    }

    static SpreadsheetExporterAliasSet unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetExporterAliasSet.class),
            SpreadsheetExporterAliasSet::unmarshall,
            SpreadsheetExporterAliasSet::marshall,
            SpreadsheetExporterAliasSet.class
        );
        SpreadsheetExporterInfoSet.EMPTY.size(); // trigger static init and json marshall/unmarshall registry
    }
}
