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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDateTest extends SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTestCase<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate>
    implements SpreadsheetMetadataTesting {

    @Test
    public void testExtractLocaleAwareValue() {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetFormatPattern pattern = SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate.instance()
            .extractLocaleAwareValue(
                LocaleContexts.jre(locale)
            ).get()
            .spreadsheetFormatPattern()
            .get();

        final LocalDate date = LocalDate.of(1999, 12, 31);
        final String formatted = pattern.formatter()
            .format(
                Optional.of(date),
                SPREADSHEET_FORMATTER_CONTEXT
            ).get()
            .text();

        final SimpleDateFormat simpleDateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL, locale);
        final String expected = simpleDateFormat.format(Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC)));

        this.checkEquals(expected, formatted, () -> pattern + "\nSimpleDateFormat: " + simpleDateFormat.toPattern());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate.instance(),
            "dateFormatter"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate.instance();
    }

    @Override
    SpreadsheetFormatterSelector propertyValue() {
        return SpreadsheetDateFormatPattern.parseDateFormatPattern("dd mm yyyy \"custom\"")
            .spreadsheetFormatterSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorDate.class;
    }
}
