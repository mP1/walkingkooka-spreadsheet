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

package walkingkooka.spreadsheet.format;

import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {link SpreadsheetFormatterProvider} that supports creating {@link SpreadsheetFormatter} for each of the
 * available {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}.
 */
final class SpreadsheetFormatPatternSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    final static SpreadsheetFormatPatternSpreadsheetFormatterProvider INSTANCE = new SpreadsheetFormatPatternSpreadsheetFormatterProvider();

    private SpreadsheetFormatPatternSpreadsheetFormatterProvider() {
        super();
    }

    @Override
    public Optional<SpreadsheetFormatter> spreadsheetFormatter(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");
        return selector.spreadsheetFormatPattern()
                .map(SpreadsheetPattern::formatter);
    }

    @Override
    public Set<SpreadsheetFormatterInfo> spreadsheetFormatterInfos() {
        return INFOS;
    }

    private final static Set<SpreadsheetFormatterInfo> INFOS = Sets.of(
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE_FORMAT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.DATE_TIME_FORMAT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.NUMBER_FORMAT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TEXT_FORMAT),
            spreadsheetFormatterInfo(SpreadsheetFormatterName.TIME_FORMAT)
    );


    private static SpreadsheetFormatterInfo spreadsheetFormatterInfo(final SpreadsheetFormatterName name) {
        return SpreadsheetFormatterInfo.with(
                Url.parseAbsolute("https://github.com/mP1/walkingkooka-spreadsheet/" + name),
                name
        );
    }

    @Override
    public String toString() {
        return "SpreadsheetFormatPattern.spreadsheetFormatter";
    }
}
