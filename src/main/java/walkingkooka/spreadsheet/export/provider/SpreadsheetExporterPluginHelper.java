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

package walkingkooka.spreadsheet.export.provider;

import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Name;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.PluginAlias;
import walkingkooka.plugin.PluginHelper;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.cursor.parser.StringParserToken;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;

final class SpreadsheetExporterPluginHelper implements PluginHelper<SpreadsheetExporterName,
    SpreadsheetExporterInfo,
    SpreadsheetExporterInfoSet,
    SpreadsheetExporterSelector,
    SpreadsheetExporterAlias,
    SpreadsheetExporterAliasSet> {

    final static SpreadsheetExporterPluginHelper INSTANCE = new SpreadsheetExporterPluginHelper();

    private SpreadsheetExporterPluginHelper() {
    }

    @Override
    public SpreadsheetExporterName name(final String text) {
        return SpreadsheetExporterName.with(text);
    }

    @Override
    public Optional<SpreadsheetExporterName> parseName(final TextCursor cursor,
                                                       final ParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        return Parsers.initialAndPartCharPredicateString(
            c -> SpreadsheetExporterName.isChar(0, c),
            c -> SpreadsheetExporterName.isChar(1, c),
            SpreadsheetExporterName.MIN_LENGTH, // minLength
            SpreadsheetExporterName.MAX_LENGTH // maxLength
        ).parse(
            cursor,
            context
        ).map(
            (final ParserToken token) -> this.name(
                token.cast(StringParserToken.class).value()
            )
        );
    }

    @Override
    public Set<SpreadsheetExporterName> names(final Set<SpreadsheetExporterName> names) {
        return Sets.immutable(
            Objects.requireNonNull(names, "names")
        );
    }

    @Override
    public Function<SpreadsheetExporterName, RuntimeException> unknownName() {
        return n -> new IllegalArgumentException("Unknown Exporter " + n);
    }

    @Override
    public Comparator<SpreadsheetExporterName> nameComparator() {
        return Name.comparator(SpreadsheetExporterName.CASE_SENSITIVITY);
    }

    @Override
    public SpreadsheetExporterInfo info(final AbsoluteUrl url,
                                        final SpreadsheetExporterName name) {
        return SpreadsheetExporterInfo.with(url, name);
    }

    @Override
    public SpreadsheetExporterInfo parseInfo(final String text) {
        return SpreadsheetExporterInfo.parse(text);
    }

    @Override
    public SpreadsheetExporterInfoSet infoSet(final Set<SpreadsheetExporterInfo> infos) {
        return SpreadsheetExporterInfoSet.with(infos);
    }

    @Override
    public SpreadsheetExporterSelector parseSelector(final String text) {
        return SpreadsheetExporterSelector.parse(text);
    }

    @Override
    public SpreadsheetExporterAlias alias(final SpreadsheetExporterName name,
                                          final Optional<SpreadsheetExporterSelector> selector,
                                          final Optional<AbsoluteUrl> url) {
        return SpreadsheetExporterAlias.with(
            name,
            selector,
            url
        );
    }

    @Override
    public SpreadsheetExporterAlias alias(final PluginAlias<SpreadsheetExporterName, SpreadsheetExporterSelector> pluginAlias) {
        return SpreadsheetExporterAlias.with(pluginAlias);
    }

    @Override
    public SpreadsheetExporterAliasSet aliasSet(final SortedSet<SpreadsheetExporterAlias> aliases) {
        return SpreadsheetExporterAliasSet.with(aliases);
    }

    @Override
    public String label() {
        return "Exporter";
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
