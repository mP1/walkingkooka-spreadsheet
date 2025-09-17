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

package walkingkooka.spreadsheet.parser.provider;

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

final class SpreadsheetParserPluginHelper implements PluginHelper<SpreadsheetParserName,
    SpreadsheetParserInfo,
    SpreadsheetParserInfoSet,
    SpreadsheetParserSelector,
    SpreadsheetParserAlias,
    SpreadsheetParserAliasSet> {

    final static SpreadsheetParserPluginHelper INSTANCE = new SpreadsheetParserPluginHelper();

    private SpreadsheetParserPluginHelper() {
    }

    @Override
    public SpreadsheetParserName name(final String text) {
        return SpreadsheetParserName.with(text);
    }

    @Override
    public Optional<SpreadsheetParserName> parseName(final TextCursor cursor,
                                                     final ParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        return Parsers.initialAndPartCharPredicateString(
            c -> SpreadsheetParserName.isChar(0, c),
            c -> SpreadsheetParserName.isChar(1, c),
            SpreadsheetParserName.MIN_LENGTH, // minLength
            SpreadsheetParserName.MAX_LENGTH // maxLength
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
    public Set<SpreadsheetParserName> names(final Set<SpreadsheetParserName> names) {
        return Sets.immutable(
            Objects.requireNonNull(names, "names")
        );
    }

    @Override
    public Function<SpreadsheetParserName, RuntimeException> unknownName() {
        return n -> new IllegalArgumentException("Unknown Parser " + n);
    }

    @Override
    public Comparator<SpreadsheetParserName> nameComparator() {
        return Name.comparator(SpreadsheetParserName.CASE_SENSITIVITY);
    }

    @Override
    public SpreadsheetParserInfo info(final AbsoluteUrl url,
                                      final SpreadsheetParserName name) {
        return SpreadsheetParserInfo.with(url, name);
    }

    @Override
    public SpreadsheetParserInfo parseInfo(final String text) {
        return SpreadsheetParserInfo.parse(text);
    }

    @Override
    public SpreadsheetParserInfoSet infoSet(final Set<SpreadsheetParserInfo> infos) {
        return SpreadsheetParserInfoSet.with(infos);
    }

    @Override
    public SpreadsheetParserSelector parseSelector(final String text) {
        return SpreadsheetParserSelector.parse(text);
    }

    @Override
    public SpreadsheetParserAlias alias(final SpreadsheetParserName name,
                                        final Optional<SpreadsheetParserSelector> selector,
                                        final Optional<AbsoluteUrl> url) {
        return SpreadsheetParserAlias.with(
            name,
            selector,
            url
        );
    }

    @Override
    public SpreadsheetParserAlias alias(final PluginAlias<SpreadsheetParserName, SpreadsheetParserSelector> pluginAlias) {
        return SpreadsheetParserAlias.with(pluginAlias);
    }

    @Override
    public SpreadsheetParserAliasSet aliasSet(final SortedSet<SpreadsheetParserAlias> aliases) {
        return SpreadsheetParserAliasSet.with(aliases);
    }

    @Override
    public String label() {
        return "Parser";
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
