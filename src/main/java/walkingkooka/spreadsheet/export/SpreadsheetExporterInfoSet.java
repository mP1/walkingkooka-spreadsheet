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

import walkingkooka.collect.set.ImmutableSet;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.UrlFragment;
import walkingkooka.plugin.PluginInfoSet;
import walkingkooka.plugin.PluginInfoSetLike;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * A read only {@link Set} of {@link SpreadsheetExporterInfo} sorted by {@link SpreadsheetExporterName}.
 */
public final class SpreadsheetExporterInfoSet extends AbstractSet<SpreadsheetExporterInfo> implements PluginInfoSetLike<SpreadsheetExporterInfoSet, SpreadsheetExporterInfo, SpreadsheetExporterName> {

    public final static SpreadsheetExporterInfoSet EMPTY = new SpreadsheetExporterInfoSet(
            PluginInfoSet.with(
                    Sets.<SpreadsheetExporterInfo>empty()
            )
    );

    public static SpreadsheetExporterInfoSet parse(final String text) {
        return new SpreadsheetExporterInfoSet(
                PluginInfoSet.parse(
                        text,
                        SpreadsheetExporterInfo::parse
                )
        );
    }

    public static SpreadsheetExporterInfoSet with(final Set<SpreadsheetExporterInfo> infos) {
        Objects.requireNonNull(infos, "infos");

        final PluginInfoSet<SpreadsheetExporterName, SpreadsheetExporterInfo> pluginInfoSet = PluginInfoSet.with(infos);
        return pluginInfoSet.isEmpty() ?
                EMPTY :
                new SpreadsheetExporterInfoSet(pluginInfoSet);
    }

    private SpreadsheetExporterInfoSet(final PluginInfoSet<SpreadsheetExporterName, SpreadsheetExporterInfo> pluginInfoSet) {
        this.pluginInfoSet = pluginInfoSet;
    }

    // PluginInfoSetLike................................................................................................

    @Override
    public Set<SpreadsheetExporterName> names() {
        return this.pluginInfoSet.names();
    }

    @Override
    public Set<AbsoluteUrl> url() {
        return this.pluginInfoSet.url();
    }

    @Override
    public UrlFragment urlFragment() {
        return this.pluginInfoSet.urlFragment();
    }

    @Override
    public SpreadsheetExporterInfoSet filter(final SpreadsheetExporterInfoSet infos) {
        return this.setElements(
                this.pluginInfoSet.filter(
                        infos.pluginInfoSet
                )
        );
    }

    @Override
    public SpreadsheetExporterInfoSet renameIfPresent(SpreadsheetExporterInfoSet renameInfos) {
        return this.setElements(
                this.pluginInfoSet.renameIfPresent(
                        renameInfos.pluginInfoSet
                )
        );
    }

    @Override
    public SpreadsheetExporterInfoSet concat(final SpreadsheetExporterInfo info) {
        return this.setElements(
                this.pluginInfoSet.concat(info)
        );
    }

    @Override
    public SpreadsheetExporterInfoSet delete(final SpreadsheetExporterInfo info) {
        return this.setElements(
                this.pluginInfoSet.delete(info)
        );
    }

    @Override
    public SpreadsheetExporterInfoSet replace(final SpreadsheetExporterInfo oldInfo,
                                    final SpreadsheetExporterInfo newInfo) {
        return this.setElements(
                this.pluginInfoSet.replace(
                        oldInfo,
                        newInfo
                )
        );
    }

    @Override
    public ImmutableSet<SpreadsheetExporterInfo> setElementsFailIfDifferent(final Set<SpreadsheetExporterInfo> infos) {
        return this.setElements(
                this.pluginInfoSet.setElementsFailIfDifferent(
                        infos
                )
        );
    }

    @Override
    public SpreadsheetExporterInfoSet setElements(final Set<SpreadsheetExporterInfo> infos) {
        final SpreadsheetExporterInfoSet after = new SpreadsheetExporterInfoSet(
                this.pluginInfoSet.setElements(infos)
        );
        return this.pluginInfoSet.equals(infos) ?
                this :
                after;
    }

    @Override
    public Set<SpreadsheetExporterInfo> toSet() {
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
    public Iterator<SpreadsheetExporterInfo> iterator() {
        return this.pluginInfoSet.iterator();
    }

    @Override
    public int size() {
        return this.pluginInfoSet.size();
    }

    private final PluginInfoSet<SpreadsheetExporterName, SpreadsheetExporterInfo> pluginInfoSet;

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetExporterInfoSet unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return with(
                context.unmarshallSet(
                        node,
                        SpreadsheetExporterInfo.class
                )
        );
    }

    static {
        SpreadsheetExporterInfo.register(); // force registry of json marshaller

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetExporterInfoSet.class),
                SpreadsheetExporterInfoSet::unmarshall,
                SpreadsheetExporterInfoSet::marshall,
                SpreadsheetExporterInfoSet.class
        );
    }
}