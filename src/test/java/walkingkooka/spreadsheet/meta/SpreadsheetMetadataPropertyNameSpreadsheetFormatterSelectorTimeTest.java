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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;

import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTimeTest extends SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTestCase<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime>
    implements SpreadsheetMetadataTesting {

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
            LocaleContexts.jre(Locale.ENGLISH),
            SpreadsheetTimeParsePattern.parseTimeFormatPattern("h:mm:ss AM/PM")
                .spreadsheetFormatterSelector()
        );
    }

    @Test
    public void testExtractLocaleAwareValueAndFormat() {
        final Locale locale = Locale.ENGLISH;
        final SpreadsheetFormatPattern pattern = SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime.instance()
            .extractLocaleAwareValue(
                LocaleContexts.jre(locale)
            ).get()
            .spreadsheetFormatPattern()
            .get();

        final LocalTime time = LocalTime.of(12, 58, 59);
        final String formatted = pattern.formatter()
            .format(
                Optional.of(time),
                SPREADSHEET_FORMATTER_CONTEXT
            ).get()
            .text();

        this.checkEquals(
            "12:58:59 PM",
            formatted,
            pattern::toString
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime.instance(),
            "timeFormatter"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime.instance();
    }

    @Override
    SpreadsheetFormatterSelector propertyValue() {
        return SpreadsheetTimeFormatPattern.parseTimeFormatPattern("hh mm ss\"custom\"")
            .spreadsheetFormatterSelector();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetFormatterSelectorTime.class;
    }
}
