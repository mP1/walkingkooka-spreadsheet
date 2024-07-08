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

import walkingkooka.collect.set.Sets;
import walkingkooka.net.UrlPath;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {link SpreadsheetParserProvider} that supports creating {@link Parser} for each of the
 * available {@link SpreadsheetParsePattern}.
 */
final class SpreadsheetParsePatternSpreadsheetParserProvider implements SpreadsheetParserProvider {

    final static SpreadsheetParsePatternSpreadsheetParserProvider INSTANCE = new SpreadsheetParsePatternSpreadsheetParserProvider();

    private SpreadsheetParsePatternSpreadsheetParserProvider() {
        super();
    }

    @Override
    public Optional<SpreadsheetParser> spreadsheetParser(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return selector.spreadsheetParsePattern()
                .map(SpreadsheetParsePattern::parser);
    }

    @Override
    public Set<SpreadsheetParserInfo> spreadsheetParserInfos() {
        return INFOS;
    }

    private final static Set<SpreadsheetParserInfo> INFOS = Sets.of(
            spreadsheetParserInfo(SpreadsheetParserName.DATE_PARSER),
            spreadsheetParserInfo(SpreadsheetParserName.DATE_TIME_PARSER),
            spreadsheetParserInfo(SpreadsheetParserName.NUMBER_PARSER),
            spreadsheetParserInfo(SpreadsheetParserName.TIME_PARSER)
    );


    private static SpreadsheetParserInfo spreadsheetParserInfo(final SpreadsheetParserName name) {
        return SpreadsheetParserInfo.with(
                SpreadsheetParserProviders.BASE_URL.appendPath(UrlPath.parse(name.value())),
                name
        );
    }

    @Override
    public String toString() {
        return "SpreadsheetPattern.parser";
    }
}
