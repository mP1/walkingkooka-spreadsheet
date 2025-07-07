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

import walkingkooka.collect.set.ImmutableSet;
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
 * A read only {@link Set} of {@link SpreadsheetComparatorInfo} sorted by {@link SpreadsheetComparatorName}.
 */
public final class SpreadsheetComparatorInfoSet extends AbstractSet<SpreadsheetComparatorInfo> implements PluginInfoSetLike<SpreadsheetComparatorName, SpreadsheetComparatorInfo, SpreadsheetComparatorInfoSet, SpreadsheetComparatorSelector, SpreadsheetComparatorAlias, SpreadsheetComparatorAliasSet> {

    public final static SpreadsheetComparatorInfoSet EMPTY = new SpreadsheetComparatorInfoSet(
            PluginInfoSet.with(
                    Sets.<SpreadsheetComparatorInfo>empty()
            )
    );

    public static SpreadsheetComparatorInfoSet parse(final String text) {
        return new SpreadsheetComparatorInfoSet(
                PluginInfoSet.parse(
                        text,
                        SpreadsheetComparatorInfo::parse
                )
        );
    }

    public static SpreadsheetComparatorInfoSet with(final Set<SpreadsheetComparatorInfo> infos) {
        SpreadsheetComparatorInfoSet with;

        if (infos instanceof SpreadsheetComparatorInfoSet) {
            with = (SpreadsheetComparatorInfoSet) infos;
        } else {
            final PluginInfoSet<SpreadsheetComparatorName, SpreadsheetComparatorInfo> pluginInfoSet = PluginInfoSet.with(
                Objects.requireNonNull(infos, "infos")
            );
            with = pluginInfoSet.isEmpty() ?
                EMPTY :
                new SpreadsheetComparatorInfoSet(pluginInfoSet);
        }

        return with;
    }

    private SpreadsheetComparatorInfoSet(final PluginInfoSet<SpreadsheetComparatorName, SpreadsheetComparatorInfo> pluginInfoSet) {
        this.pluginInfoSet = pluginInfoSet;
    }

    // PluginInfoSetLike................................................................................................

    @Override
    public Set<SpreadsheetComparatorName> names() {
        return this.pluginInfoSet.names();
    }

    @Override
    public Set<AbsoluteUrl> url() {
        return this.pluginInfoSet.url();
    }

    @Override
    public SpreadsheetComparatorAliasSet aliasSet() {
        return SpreadsheetComparatorPluginHelper.INSTANCE.toAliasSet(this);
    }

    @Override
    public SpreadsheetComparatorInfoSet filter(final SpreadsheetComparatorInfoSet infos) {
        return this.setElements(
                this.pluginInfoSet.filter(
                        infos.pluginInfoSet
                )
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet renameIfPresent(SpreadsheetComparatorInfoSet renameInfos) {
        return this.setElements(
                this.pluginInfoSet.renameIfPresent(
                        renameInfos.pluginInfoSet
                )
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet concat(final SpreadsheetComparatorInfo info) {
        return this.setElements(
                this.pluginInfoSet.concat(info)
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet concatAll(final Collection<SpreadsheetComparatorInfo> infos) {
        return this.setElements(
                this.pluginInfoSet.concatAll(infos)
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet delete(final SpreadsheetComparatorInfo info) {
        return this.setElements(
                this.pluginInfoSet.delete(info)
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet deleteAll(final Collection<SpreadsheetComparatorInfo> infos) {
        return this.setElements(
                this.pluginInfoSet.deleteAll(infos)
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet deleteIf(final Predicate<? super SpreadsheetComparatorInfo> predicate) {
        return this.setElements(
                this.pluginInfoSet.deleteIf(predicate)
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet replace(final SpreadsheetComparatorInfo oldInfo,
                                                final SpreadsheetComparatorInfo newInfo) {
        return this.setElements(
                this.pluginInfoSet.replace(
                        oldInfo,
                        newInfo
                )
        );
    }

    @Override
    public ImmutableSet<SpreadsheetComparatorInfo> setElementsFailIfDifferent(final Set<SpreadsheetComparatorInfo> infos) {
        return this.setElements(
                this.pluginInfoSet.setElementsFailIfDifferent(
                        infos
                )
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet setElements(final Set<SpreadsheetComparatorInfo> aliases) {
        final SpreadsheetComparatorInfoSet after;

        if (aliases instanceof SpreadsheetComparatorInfoSet) {
            after = (SpreadsheetComparatorInfoSet) aliases;
        } else {
            after = new SpreadsheetComparatorInfoSet(
                this.pluginInfoSet.setElements(aliases)
            );
            return this.pluginInfoSet.equals(aliases) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public Set<SpreadsheetComparatorInfo> toSet() {
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
    public Iterator<SpreadsheetComparatorInfo> iterator() {
        return this.pluginInfoSet.iterator();
    }

    @Override
    public int size() {
        return this.pluginInfoSet.size();
    }

    private final PluginInfoSet<SpreadsheetComparatorName, SpreadsheetComparatorInfo> pluginInfoSet;

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetComparatorInfoSet unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return with(
                context.unmarshallSet(
                        node,
                        SpreadsheetComparatorInfo.class
                )
        );
    }

    static {
        SpreadsheetComparatorInfo.register(); // force registry of json marshaller

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetComparatorInfoSet.class),
                SpreadsheetComparatorInfoSet::unmarshall,
                SpreadsheetComparatorInfoSet::marshall,
                SpreadsheetComparatorInfoSet.class
        );
    }
}
