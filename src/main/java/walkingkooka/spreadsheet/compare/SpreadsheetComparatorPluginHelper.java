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

package walkingkooka.spreadsheet.compare;

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
import java.util.function.Function;

final class SpreadsheetComparatorPluginHelper implements PluginHelper<SpreadsheetComparatorName,
        SpreadsheetComparatorInfo,
        SpreadsheetComparatorInfoSet,
        SpreadsheetComparatorSelector,
        PluginAlias<SpreadsheetComparatorName, SpreadsheetComparatorSelector>> {

    final static SpreadsheetComparatorPluginHelper INSTANCE = new SpreadsheetComparatorPluginHelper();

    private SpreadsheetComparatorPluginHelper() {
    }

    @Override
    public SpreadsheetComparatorName name(final String text) {
        return SpreadsheetComparatorName.with(text);
    }

    @Override
    public Optional<SpreadsheetComparatorName> parseName(final TextCursor cursor,
                                                       final ParserContext context) {
        Objects.requireNonNull(cursor, "cursor");
        Objects.requireNonNull(context, "context");

        return Parsers.stringInitialAndPartCharPredicate(
                c -> SpreadsheetComparatorName.isChar(0, c),
                c -> SpreadsheetComparatorName.isChar(1, c),
                SpreadsheetComparatorName.MIN_LENGTH, // minLength
                SpreadsheetComparatorName.MAX_LENGTH // maxLength
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
    public Set<SpreadsheetComparatorName> names(final Set<SpreadsheetComparatorName> names) {
        return Sets.immutable(
                Objects.requireNonNull(names, "names")
        );
    }

    @Override
    public Function<SpreadsheetComparatorName, RuntimeException> unknownName() {
        return n -> new IllegalArgumentException("Unknown Comparator " + n);
    }

    @Override
    public Comparator<SpreadsheetComparatorName> nameComparator() {
        return Name.comparator(SpreadsheetComparatorName.CASE_SENSITIVITY);
    }

    @Override
    public SpreadsheetComparatorInfo info(final AbsoluteUrl url,
                                          final SpreadsheetComparatorName name) {
        return SpreadsheetComparatorInfo.with(url, name);
    }

    @Override
    public SpreadsheetComparatorInfo parseInfo(final String text) {
        return SpreadsheetComparatorInfo.parse(text);
    }

    @Override
    public SpreadsheetComparatorInfoSet infoSet(final Set<SpreadsheetComparatorInfo> infos) {
        return SpreadsheetComparatorInfoSet.with(infos);
    }

    @Override
    public SpreadsheetComparatorSelector parseSelector(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginAlias<SpreadsheetComparatorName, SpreadsheetComparatorSelector> alias(final SpreadsheetComparatorName name,
                                                                                       final Optional<SpreadsheetComparatorSelector> selector,
                                                                                       final Optional<AbsoluteUrl> url) {
        PluginAlias.with(
                name,
                selector,
                url
        );
        throw new UnsupportedOperationException();
    }

    @Override
    public String label() {
        return "Comparator";
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
