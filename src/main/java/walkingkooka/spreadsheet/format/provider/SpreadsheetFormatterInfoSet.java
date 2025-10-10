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

package walkingkooka.spreadsheet.format.provider;

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
 * A read only {@link Set} of {@link SpreadsheetFormatterInfo} sorted by {@link SpreadsheetFormatterName}.
 */
public final class SpreadsheetFormatterInfoSet extends AbstractSet<SpreadsheetFormatterInfo> implements PluginInfoSetLike<SpreadsheetFormatterName, SpreadsheetFormatterInfo, SpreadsheetFormatterInfoSet, SpreadsheetFormatterSelector, SpreadsheetFormatterAlias, SpreadsheetFormatterAliasSet> {

    public final static SpreadsheetFormatterInfoSet EMPTY = new SpreadsheetFormatterInfoSet(
        PluginInfoSet.with(
            Sets.<SpreadsheetFormatterInfo>empty()
        )
    );

    public static SpreadsheetFormatterInfoSet parse(final String text) {
        return new SpreadsheetFormatterInfoSet(
            PluginInfoSet.parse(
                text,
                SpreadsheetFormatterInfo::parse
            )
        );
    }

    public static SpreadsheetFormatterInfoSet with(final Collection<SpreadsheetFormatterInfo> infos) {
        SpreadsheetFormatterInfoSet with;

        if (infos instanceof SpreadsheetFormatterInfoSet) {
            with = (SpreadsheetFormatterInfoSet) infos;
        } else {
            final PluginInfoSet<SpreadsheetFormatterName, SpreadsheetFormatterInfo> pluginInfoSet = PluginInfoSet.with(
                Objects.requireNonNull(infos, "infos")
            );
            with = pluginInfoSet.isEmpty() ?
                EMPTY :
                new SpreadsheetFormatterInfoSet(pluginInfoSet);
        }

        return with;
    }

    private SpreadsheetFormatterInfoSet(final PluginInfoSet<SpreadsheetFormatterName, SpreadsheetFormatterInfo> pluginInfoSet) {
        this.pluginInfoSet = pluginInfoSet;
    }

    // PluginInfoSetLike................................................................................................

    @Override
    public Set<SpreadsheetFormatterName> names() {
        return this.pluginInfoSet.names();
    }

    @Override
    public Set<AbsoluteUrl> url() {
        return this.pluginInfoSet.url();
    }

    @Override
    public SpreadsheetFormatterAliasSet aliasSet() {
        return SpreadsheetFormatterPluginHelper.INSTANCE.toAliasSet(this);
    }

    @Override
    public SpreadsheetFormatterInfoSet filter(final SpreadsheetFormatterInfoSet infos) {
        return this.setElements(
            this.pluginInfoSet.filter(
                infos.pluginInfoSet
            )
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet renameIfPresent(SpreadsheetFormatterInfoSet renameInfos) {
        return this.setElements(
            this.pluginInfoSet.renameIfPresent(
                renameInfos.pluginInfoSet
            )
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet concat(final SpreadsheetFormatterInfo info) {
        return this.setElements(
            this.pluginInfoSet.concat(info)
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet concatAll(final Collection<SpreadsheetFormatterInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.concatAll(infos)
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet delete(final SpreadsheetFormatterInfo info) {
        return this.setElements(
            this.pluginInfoSet.delete(info)
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet deleteAll(final Collection<SpreadsheetFormatterInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.deleteAll(infos)
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet deleteIf(final Predicate<? super SpreadsheetFormatterInfo> predicate) {
        return this.setElements(
            this.pluginInfoSet.deleteIf(predicate)
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet replace(final SpreadsheetFormatterInfo oldInfo,
                                               final SpreadsheetFormatterInfo newInfo) {
        return this.setElements(
            this.pluginInfoSet.replace(
                oldInfo,
                newInfo
            )
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet setElementsFailIfDifferent(final Collection<SpreadsheetFormatterInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.setElementsFailIfDifferent(
                infos
            )
        );
    }

    @Override
    public SpreadsheetFormatterInfoSet setElements(final Collection<SpreadsheetFormatterInfo> aliases) {
        final SpreadsheetFormatterInfoSet after;

        if (aliases instanceof SpreadsheetFormatterInfoSet) {
            after = (SpreadsheetFormatterInfoSet) aliases;
        } else {
            after = new SpreadsheetFormatterInfoSet(
                this.pluginInfoSet.setElements(aliases)
            );
            return this.pluginInfoSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public Set<SpreadsheetFormatterInfo> toSet() {
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
    public Iterator<SpreadsheetFormatterInfo> iterator() {
        return this.pluginInfoSet.iterator();
    }

    @Override
    public int size() {
        return this.pluginInfoSet.size();
    }

    private final PluginInfoSet<SpreadsheetFormatterName, SpreadsheetFormatterInfo> pluginInfoSet;

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetFormatterInfoSet unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallSet(
                node,
                SpreadsheetFormatterInfo.class
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetFormatterInfoSet.class),
            SpreadsheetFormatterInfoSet::unmarshall,
            SpreadsheetFormatterInfoSet::marshall,
            SpreadsheetFormatterInfoSet.class
        );
    }
}
