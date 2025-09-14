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

package walkingkooka.spreadsheet.importer;

import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginInfoSet;
import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A read only {@link Set} of {@link SpreadsheetImporterInfo} sorted by {@link SpreadsheetImporterName}.
 */
public final class SpreadsheetImporterInfoSet extends AbstractSet<SpreadsheetImporterInfo> implements PluginInfoSetLike<SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterInfoSet, SpreadsheetImporterSelector, SpreadsheetImporterAlias, SpreadsheetImporterAliasSet> {

    public final static SpreadsheetImporterInfoSet EMPTY = new SpreadsheetImporterInfoSet(
        PluginInfoSet.with(
            Sets.<SpreadsheetImporterInfo>empty()
        )
    );

    public static SpreadsheetImporterInfoSet parse(final String text) {
        return new SpreadsheetImporterInfoSet(
            PluginInfoSet.parse(
                text,
                SpreadsheetImporterInfo::parse
            )
        );
    }

    public static SpreadsheetImporterInfoSet with(final Collection<SpreadsheetImporterInfo> infos) {
        SpreadsheetImporterInfoSet with;

        if (infos instanceof SpreadsheetImporterInfoSet) {
            with = (SpreadsheetImporterInfoSet) infos;
        } else {
            final PluginInfoSet<SpreadsheetImporterName, SpreadsheetImporterInfo> pluginInfoSet = PluginInfoSet.with(
                Objects.requireNonNull(infos, "infos")
            );
            with = pluginInfoSet.isEmpty() ?
                EMPTY :
                new SpreadsheetImporterInfoSet(pluginInfoSet);
        }

        return with;
    }

    private SpreadsheetImporterInfoSet(final PluginInfoSet<SpreadsheetImporterName, SpreadsheetImporterInfo> pluginInfoSet) {
        this.pluginInfoSet = pluginInfoSet;
    }

    // PluginInfoSetLike................................................................................................

    @Override
    public Set<SpreadsheetImporterName> names() {
        return this.pluginInfoSet.names();
    }

    @Override
    public Set<AbsoluteUrl> url() {
        return this.pluginInfoSet.url();
    }

    @Override
    public SpreadsheetImporterAliasSet aliasSet() {
        return SpreadsheetImporterPluginHelper.INSTANCE.toAliasSet(this);
    }

    @Override
    public SpreadsheetImporterInfoSet filter(final SpreadsheetImporterInfoSet infos) {
        return this.setElements(
            this.pluginInfoSet.filter(
                infos.pluginInfoSet
            )
        );
    }

    @Override
    public SpreadsheetImporterInfoSet renameIfPresent(SpreadsheetImporterInfoSet renameInfos) {
        return this.setElements(
            this.pluginInfoSet.renameIfPresent(
                renameInfos.pluginInfoSet
            )
        );
    }

    @Override
    public SpreadsheetImporterInfoSet concat(final SpreadsheetImporterInfo info) {
        return this.setElements(
            this.pluginInfoSet.concat(info)
        );
    }

    @Override
    public SpreadsheetImporterInfoSet concatAll(final Collection<SpreadsheetImporterInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.concatAll(infos)
        );
    }

    @Override
    public SpreadsheetImporterInfoSet delete(final SpreadsheetImporterInfo info) {
        return this.setElements(
            this.pluginInfoSet.delete(info)
        );
    }

    @Override
    public SpreadsheetImporterInfoSet deleteAll(final Collection<SpreadsheetImporterInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.deleteAll(infos)
        );
    }

    @Override
    public SpreadsheetImporterInfoSet deleteIf(final Predicate<? super SpreadsheetImporterInfo> predicate) {
        return this.setElements(
            this.pluginInfoSet.deleteIf(predicate)
        );
    }

    @Override
    public SpreadsheetImporterInfoSet replace(final SpreadsheetImporterInfo oldInfo,
                                              final SpreadsheetImporterInfo newInfo) {
        return this.setElements(
            this.pluginInfoSet.replace(
                oldInfo,
                newInfo
            )
        );
    }

    @Override
    public SpreadsheetImporterInfoSet setElementsFailIfDifferent(final Collection<SpreadsheetImporterInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.setElementsFailIfDifferent(
                infos
            )
        );
    }

    @Override
    public SpreadsheetImporterInfoSet setElements(final Collection<SpreadsheetImporterInfo> aliases) {
        final SpreadsheetImporterInfoSet after;

        if (aliases instanceof SpreadsheetImporterInfoSet) {
            after = (SpreadsheetImporterInfoSet) aliases;
        } else {
            after = new SpreadsheetImporterInfoSet(
                this.pluginInfoSet.setElements(aliases)
            );
            return this.pluginInfoSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public Set<SpreadsheetImporterInfo> toSet() {
        return this.pluginInfoSet.toSet();
    }

    // TreePrintable....................................................................................................

    @Override
    public String text() {
        return this.pluginInfoSet.text();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            this.pluginInfoSet.printTree(printer);
        }
        printer.outdent();
    }

    // AbstractSet......................................................................................................

    @Override
    public Iterator<SpreadsheetImporterInfo> iterator() {
        return this.pluginInfoSet.iterator();
    }

    @Override
    public int size() {
        return this.pluginInfoSet.size();
    }

    private final PluginInfoSet<SpreadsheetImporterName, SpreadsheetImporterInfo> pluginInfoSet;

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetImporterInfoSet unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallSet(
                node,
                SpreadsheetImporterInfo.class
            )
        );
    }

    static {
        SpreadsheetImporterInfo.register(); // force registry of json marshaller

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetImporterInfoSet.class),
            SpreadsheetImporterInfoSet::unmarshall,
            SpreadsheetImporterInfoSet::marshall,
            SpreadsheetImporterInfoSet.class
        );
    }
}
