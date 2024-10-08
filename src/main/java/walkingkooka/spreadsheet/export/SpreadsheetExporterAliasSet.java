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
import java.util.Comparator;
import java.util.Iterator;
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
        SpreadsheetExporterAlias>,
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
    public static SpreadsheetExporterAliasSet with(final SortedSet<SpreadsheetExporterAlias> aliases) {
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

    private SpreadsheetExporterAliasSet(final PluginAliasSet<SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector, SpreadsheetExporterAlias> pluginAliasSet) {
        this.pluginAliasSet = pluginAliasSet;
    }

    @Override
    public Optional<SpreadsheetExporterSelector> alias(final SpreadsheetExporterName name) {
        return this.pluginAliasSet.alias(name);
    }

    @Override
    public Optional<SpreadsheetExporterName> name(final SpreadsheetExporterName name) {
        return this.pluginAliasSet.name(name);
    }

    @Override
    public SpreadsheetExporterInfoSet merge(final SpreadsheetExporterInfoSet infos) {
        return this.pluginAliasSet.merge(infos);
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
    public SpreadsheetExporterAliasSet setElements(final SortedSet<SpreadsheetExporterAlias> aliases) {
        final SpreadsheetExporterAliasSet after = new SpreadsheetExporterAliasSet(
                this.pluginAliasSet.setElements(aliases)
        );
        return this.pluginAliasSet.equals(aliases) ?
                this :
                after;
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
    public SortedSet<SpreadsheetExporterAlias> tailSet(final SpreadsheetExporterAlias alias) {
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
    public String text() {
        return this.pluginAliasSet.text();
    }

    @Override
    public void printTree(final IndentingPrinter printer) {
        this.pluginAliasSet.printTree(printer);
    }

    private final PluginAliasSet<SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterInfoSet, SpreadsheetExporterSelector, SpreadsheetExporterAlias> pluginAliasSet;

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
