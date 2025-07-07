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

package walkingkooka.spreadsheet.meta;

import walkingkooka.locale.LocaleContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;

import java.util.Optional;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds the default formatter for {@link java.time.LocalDate} values.
 */
final class SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate extends SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelector {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate() {
        super(
            "dateFormatter",
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN
        );
    }

    @Override
    void accept(final SpreadsheetFormatterSelector value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDateFormatter(value);
    }

    @Override
    Optional<SpreadsheetFormatPattern> extractLocaleAwareValueSpreadsheetFormatPattern(final LocaleContext context) {
        return Optional.of(
            SpreadsheetPattern.dateFormatPatternLocale(
                context.locale()
            )
        );
    }
}
