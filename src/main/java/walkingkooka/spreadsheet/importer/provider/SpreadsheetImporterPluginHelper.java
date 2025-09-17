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

final class SpreadsheetImporterPluginHelper implements PluginHelper<SpreadsheetImporterName,
    SpreadsheetImporterInfo,
    SpreadsheetImporterInfoSet,
    SpreadsheetImporterSelector,
    SpreadsheetImporterAlias,
    SpreadsheetImporterAliasSet> {

    final static SpreadsheetImporterPluginHelper INSTANCE = new SpreadsheetImporterPluginHelper();

    private SpreadsheetImporterPluginHelper() {
    }

    @Override
    public SpreadsheetImporterName name(final String text) {
        return SpreadsheetImporterName.with(text);
    }

    @Override
    public Optional<SpreadsheetImporterName> parseName(final TextCursor cursor,
                                                       final ParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        return Parsers.initialAndPartCharPredicateString(
            c -> SpreadsheetImporterName.isChar(0, c),
            c -> SpreadsheetImporterName.isChar(1, c),
            SpreadsheetImporterName.MIN_LENGTH, // minLength
            SpreadsheetImporterName.MAX_LENGTH // maxLength
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
    public Set<SpreadsheetImporterName> names(final Set<SpreadsheetImporterName> names) {
        return Sets.immutable(
            Objects.requireNonNull(names, "names")
        );
    }

    @Override
    public Function<SpreadsheetImporterName, RuntimeException> unknownName() {
        return n -> new IllegalArgumentException("Unknown Importer " + n);
    }

    @Override
    public Comparator<SpreadsheetImporterName> nameComparator() {
        return Name.comparator(SpreadsheetImporterName.CASE_SENSITIVITY);
    }

    @Override
    public SpreadsheetImporterInfo info(final AbsoluteUrl url,
                                        final SpreadsheetImporterName name) {
        return SpreadsheetImporterInfo.with(url, name);
    }

    @Override
    public SpreadsheetImporterInfo parseInfo(final String text) {
        return SpreadsheetImporterInfo.parse(text);
    }

    @Override
    public SpreadsheetImporterInfoSet infoSet(final Set<SpreadsheetImporterInfo> infos) {
        return SpreadsheetImporterInfoSet.with(infos);
    }

    @Override
    public SpreadsheetImporterSelector parseSelector(final String text) {
        return SpreadsheetImporterSelector.parse(text);
    }

    @Override
    public SpreadsheetImporterAlias alias(final SpreadsheetImporterName name,
                                          final Optional<SpreadsheetImporterSelector> selector,
                                          final Optional<AbsoluteUrl> url) {
        return SpreadsheetImporterAlias.with(
            name,
            selector,
            url
        );
    }

    @Override
    public SpreadsheetImporterAlias alias(final PluginAlias<SpreadsheetImporterName, SpreadsheetImporterSelector> pluginAlias) {
        return SpreadsheetImporterAlias.with(pluginAlias);
    }

    @Override
    public SpreadsheetImporterAliasSet aliasSet(final SortedSet<SpreadsheetImporterAlias> aliases) {
        return SpreadsheetImporterAliasSet.with(aliases);
    }

    @Override
    public String label() {
        return "Importer";
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
