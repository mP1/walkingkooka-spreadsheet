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
 * A read only {@link Set} of {@link SpreadsheetParserInfo} sorted by {@link SpreadsheetParserName}.
 */
public final class SpreadsheetParserInfoSet extends AbstractSet<SpreadsheetParserInfo> implements PluginInfoSetLike<SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserInfoSet, SpreadsheetParserSelector, SpreadsheetParserAlias, SpreadsheetParserAliasSet> {

    public final static SpreadsheetParserInfoSet EMPTY = new SpreadsheetParserInfoSet(
        PluginInfoSet.with(
            Sets.<SpreadsheetParserInfo>empty()
        )
    );

    public static SpreadsheetParserInfoSet parse(final String text) {
        return new SpreadsheetParserInfoSet(
            PluginInfoSet.parse(
                text,
                SpreadsheetParserInfo::parse
            )
        );
    }

    public static SpreadsheetParserInfoSet with(final Set<SpreadsheetParserInfo> infos) {
        SpreadsheetParserInfoSet with;

        if (infos instanceof SpreadsheetParserInfoSet) {
            with = (SpreadsheetParserInfoSet) infos;
        } else {
            final PluginInfoSet<SpreadsheetParserName, SpreadsheetParserInfo> pluginInfoSet = PluginInfoSet.with(
                Objects.requireNonNull(infos, "infos")
            );
            with = pluginInfoSet.isEmpty() ?
                EMPTY :
                new SpreadsheetParserInfoSet(pluginInfoSet);
        }

        return with;
    }

    private SpreadsheetParserInfoSet(final PluginInfoSet<SpreadsheetParserName, SpreadsheetParserInfo> pluginInfoSet) {
        this.pluginInfoSet = pluginInfoSet;
    }

    // PluginInfoSetLike................................................................................................

    @Override
    public Set<SpreadsheetParserName> names() {
        return this.pluginInfoSet.names();
    }

    @Override
    public Set<AbsoluteUrl> url() {
        return this.pluginInfoSet.url();
    }

    @Override
    public SpreadsheetParserAliasSet aliasSet() {
        return SpreadsheetParserPluginHelper.INSTANCE.toAliasSet(this);
    }

    @Override
    public SpreadsheetParserInfoSet filter(final SpreadsheetParserInfoSet infos) {
        return this.setElements(
            this.pluginInfoSet.filter(
                infos.pluginInfoSet
            )
        );
    }

    @Override
    public SpreadsheetParserInfoSet renameIfPresent(SpreadsheetParserInfoSet renameInfos) {
        return this.setElements(
            this.pluginInfoSet.renameIfPresent(
                renameInfos.pluginInfoSet
            )
        );
    }

    @Override
    public SpreadsheetParserInfoSet concat(final SpreadsheetParserInfo info) {
        return this.setElements(
            this.pluginInfoSet.concat(info)
        );
    }

    @Override
    public SpreadsheetParserInfoSet concatAll(final Collection<SpreadsheetParserInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.concatAll(infos)
        );
    }

    @Override
    public SpreadsheetParserInfoSet delete(final SpreadsheetParserInfo info) {
        return this.setElements(
            this.pluginInfoSet.delete(info)
        );
    }

    @Override
    public SpreadsheetParserInfoSet deleteAll(final Collection<SpreadsheetParserInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.deleteAll(infos)
        );
    }

    @Override
    public SpreadsheetParserInfoSet deleteIf(final Predicate<? super SpreadsheetParserInfo> predicate) {
        return this.setElements(
            this.pluginInfoSet.deleteIf(predicate)
        );
    }

    @Override
    public SpreadsheetParserInfoSet replace(final SpreadsheetParserInfo oldInfo,
                                            final SpreadsheetParserInfo newInfo) {
        return this.setElements(
            this.pluginInfoSet.replace(
                oldInfo,
                newInfo
            )
        );
    }

    @Override
    public ImmutableSet<SpreadsheetParserInfo> setElementsFailIfDifferent(final Set<SpreadsheetParserInfo> infos) {
        return this.setElements(
            this.pluginInfoSet.setElementsFailIfDifferent(
                infos
            )
        );
    }

    @Override
    public SpreadsheetParserInfoSet setElements(final Set<SpreadsheetParserInfo> infos) {
        final SpreadsheetParserInfoSet after;

        if (infos instanceof SpreadsheetParserInfoSet) {
            after = (SpreadsheetParserInfoSet) infos;
        } else {
            after = new SpreadsheetParserInfoSet(
                this.pluginInfoSet.setElements(infos)
            );
            return this.pluginInfoSet.equals(infos) ?
                this :
                after;

        }

        return after;
    }

    @Override
    public Set<SpreadsheetParserInfo> toSet() {
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
    public Iterator<SpreadsheetParserInfo> iterator() {
        return this.pluginInfoSet.iterator();
    }

    @Override
    public int size() {
        return this.pluginInfoSet.size();
    }

    private final PluginInfoSet<SpreadsheetParserName, SpreadsheetParserInfo> pluginInfoSet;

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static SpreadsheetParserInfoSet unmarshall(final JsonNode node,
                                               final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallSet(
                node,
                SpreadsheetParserInfo.class
            )
        );
    }

    static {
        SpreadsheetParserInfo.register(); // force registry of json marshaller

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserInfoSet.class),
            SpreadsheetParserInfoSet::unmarshall,
            SpreadsheetParserInfoSet::marshall,
            SpreadsheetParserInfoSet.class
        );
    }
}
