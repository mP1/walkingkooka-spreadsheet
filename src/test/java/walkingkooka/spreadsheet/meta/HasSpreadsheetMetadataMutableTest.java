
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
import walkingkooka.ToStringTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class HasSpreadsheetMetadataMutableTest implements HasSpreadsheetMetadataTesting2<HasSpreadsheetMetadataMutable>,
    ToStringTesting<HasSpreadsheetMetadataMutable> {

    @Test
    public void testWithNullHasSpreadsheetMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> HasSpreadsheetMetadataMutable.with(null)
        );
    }

    @Test
    public void testSpreadsheetMetadata() {
        this.spreadsheetMetadataAndCheck(
            this.createHasSpreadsheetMetadata(),
            SPREADSHEET_METADATA
        );
    }

    @Override
    public HasSpreadsheetMetadataMutable createHasSpreadsheetMetadata() {
        return HasSpreadsheetMetadataMutable.with(OPTIONAL_SPREADSHEET_METADATA);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createHasSpreadsheetMetadata(),
            "{\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user123@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"user123@example.com\",\n" +
                "    \"modifiedTimestamp\": \"1999-12-31T12:58:59\"\n" +
                "  },\n" +
                "  \"autoHideScrollbars\": false,\n" +
                "  \"cellCharacterWidth\": 1,\n" +
                "  \"color1\": \"black\",\n" +
                "  \"color2\": \"white\",\n" +
                "  \"colorBlack\": 1,\n" +
                "  \"colorWhite\": 2,\n" +
                "  \"comparators\": \"background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, date-time, day-of-month, day-of-week, error, formatter, hour-of-am-pm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, text-decoration-color, text-with-numbers, text-with-numbers-case-insensitive, time, validator, value-type, year\",\n" +
                "  \"converters\": \"binary, binary-to-te"
        );
    }

    // class............................................................................................................

    @Override
    public Class<HasSpreadsheetMetadataMutable> type() {
        return HasSpreadsheetMetadataMutable.class;
    }
}
