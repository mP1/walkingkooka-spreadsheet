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

import org.junit.jupiter.api.Test;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTimeTest extends SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTestCase<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime> {

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
            LocaleContexts.jre(Locale.ENGLISH),
            SpreadsheetDateParsePattern.parseDateTimeFormatPattern("dddd, mmmm d, yyyy \\a\\t h:mm:ss AM/PM")
                .spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testExtractLocaleAwareValueAndFormat() {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetFormatPattern pattern = SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime.instance()
            .extractLocaleAwareValue(
                CURRENCY_CONTEXT.setLocaleContext(
                    LocaleContexts.jre(locale)
                )
            ).get()
            .spreadsheetFormatPattern()
            .get();

        final LocalDateTime date = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        final String formatted = pattern.formatter()
            .format(
                Optional.of(date),
                SpreadsheetMetadataTesting.SPREADSHEET_FORMATTER_CONTEXT
            )
            .get()
            .text();
        this.checkEquals(
            "Friday, December 31, 1999 at 12:58:59 PM",
            formatted,
            pattern::toString
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime.instance(),
            "dateTimeFormatter"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime.instance();
    }

    @Override
    SpreadsheetFormatterSelector propertyValue() {
        return SpreadsheetDateTimeFormatPattern.parseDateTimeFormatPattern("dd mm yyyy hh mm ss\"custom\"")
            .spreadsheetFormatterSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTime.class;
    }
}
