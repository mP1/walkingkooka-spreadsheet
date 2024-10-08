/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.spreadsheet.importer;

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
import java.util.Comparator;
import java.util.Iterator;
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
        SpreadsheetImporterAlias>,
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
    public static SpreadsheetImporterAliasSet with(final SortedSet<SpreadsheetImporterAlias> aliases) {
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

    private SpreadsheetImporterAliasSet(final PluginAliasSet<SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet, SpreadsheetImporterSelector, SpreadsheetImporterAlias> pluginAliasSet) {
        this.pluginAliasSet = pluginAliasSet;
    }

    @Override
    public Optional<SpreadsheetImporterSelector> alias(final SpreadsheetImporterName name) {
        return this.pluginAliasSet.alias(name);
    }

    @Override
    public Optional<SpreadsheetImporterName> name(final SpreadsheetImporterName name) {
        return this.pluginAliasSet.name(name);
    }

    @Override
    public SpreadsheetImporterInfoSet merge(final SpreadsheetImporterInfoSet infos) {
        return this.pluginAliasSet.merge(infos);
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
    public SpreadsheetImporterAliasSet setElements(final SortedSet<SpreadsheetImporterAlias> aliases) {
        final SpreadsheetImporterAliasSet after = new SpreadsheetImporterAliasSet(
                this.pluginAliasSet.setElements(aliases)
        );
        return this.pluginAliasSet.equals(aliases) ?
                this :
                after;
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
    public SortedSet<SpreadsheetImporterAlias> tailSet(final SpreadsheetImporterAlias alias) {
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
    public String text() {
        return this.pluginAliasSet.text();
    }

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.pluginAliasSet.printTree(printer);
    }

    private final PluginAliasSet<SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet, SpreadsheetImporterSelector, SpreadsheetImporterAlias> pluginAliasSet;

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

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
        SpreadsheetImporterInfoSet.EMPTY.size(); // trigger static init and json marshall/unmarshall registry
    }
}
