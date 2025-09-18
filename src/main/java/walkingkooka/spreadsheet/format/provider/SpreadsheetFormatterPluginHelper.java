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

final class SpreadsheetFormatterPluginHelper implements PluginHelper<
    SpreadsheetFormatterName,
    SpreadsheetFormatterInfo,
    SpreadsheetFormatterInfoSet,
    SpreadsheetFormatterSelector,
    SpreadsheetFormatterAlias,
    SpreadsheetFormatterAliasSet> {

    final static SpreadsheetFormatterPluginHelper INSTANCE = new SpreadsheetFormatterPluginHelper();

    private SpreadsheetFormatterPluginHelper() {
    }

    @Override
    public SpreadsheetFormatterName name(final String text) {
        return SpreadsheetFormatterName.with(text);
    }

    @Override
    public Optional<SpreadsheetFormatterName> parseName(final TextCursor cursor,
                                                        final ParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        return Parsers.initialAndPartCharPredicateString(
            c -> SpreadsheetFormatterName.isChar(0, c),
            c -> SpreadsheetFormatterName.isChar(1, c),
            SpreadsheetFormatterName.MIN_LENGTH, // minLength
            SpreadsheetFormatterName.MAX_LENGTH // maxLength
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
    public Set<SpreadsheetFormatterName> names(final Set<SpreadsheetFormatterName> names) {
        return Sets.immutable(
            Objects.requireNonNull(names, "names")
        );
    }

    @Override
    public Function<SpreadsheetFormatterName, RuntimeException> unknownName() {
        return n -> new IllegalArgumentException("Unknown SpreadsheetFormatter " + n);
    }

    @Override
    public Comparator<SpreadsheetFormatterName> nameComparator() {
        return Name.comparator(SpreadsheetFormatterName.CASE_SENSITIVITY);
    }

    @Override
    public SpreadsheetFormatterInfo info(final AbsoluteUrl url,
                                         final SpreadsheetFormatterName name) {
        return SpreadsheetFormatterInfo.with(url, name);
    }

    @Override
    public SpreadsheetFormatterInfo parseInfo(final String text) {
        return SpreadsheetFormatterInfo.parse(text);
    }

    @Override
    public SpreadsheetFormatterInfoSet infoSet(final Set<SpreadsheetFormatterInfo> infos) {
        return SpreadsheetFormatterInfoSet.with(infos);
    }

    @Override
    public SpreadsheetFormatterSelector parseSelector(final String text) {
        return SpreadsheetFormatterSelector.parse(text);
    }

    @Override
    public SpreadsheetFormatterAlias alias(final SpreadsheetFormatterName name,
                                           final Optional<SpreadsheetFormatterSelector> selector,
                                           final Optional<AbsoluteUrl> url) {
        return SpreadsheetFormatterAlias.with(
            name,
            selector,
            url
        );
    }

    @Override
    public SpreadsheetFormatterAlias alias(final PluginAlias<SpreadsheetFormatterName, SpreadsheetFormatterSelector> pluginAlias) {
        return SpreadsheetFormatterAlias.with(pluginAlias);
    }

    @Override
    public SpreadsheetFormatterAliasSet aliasSet(final SortedSet<SpreadsheetFormatterAlias> aliases) {
        return SpreadsheetFormatterAliasSet.with(aliases);
    }

    @Override
    public String label() {
        return "Formatter";
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
