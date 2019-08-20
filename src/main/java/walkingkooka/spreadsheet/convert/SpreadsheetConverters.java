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

package walkingkooka.spreadsheet.convert;

import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;
import walkingkooka.type.PublicStaticHelper;

public final class SpreadsheetConverters implements PublicStaticHelper {

    /**
     * {@see SpreadsheetConverter}
     */
    public static Converter converter(final SpreadsheetFormatter dateFormatter,
                                      final SpreadsheetDateParsePatterns dateParser,
                                      final SpreadsheetFormatter dateTimeFormatter,
                                      final SpreadsheetDateTimeParsePatterns dateTimeParser,
                                      final SpreadsheetFormatter numberFormatter,
                                      final SpreadsheetNumberParsePatterns numberParser,
                                      final SpreadsheetFormatter timeFormatter,
                                      final SpreadsheetTimeParsePatterns timeParser,
                                      final long dateOffset) {
        return SpreadsheetConverter.with(dateFormatter,
                dateParser,
                dateTimeFormatter,
                dateTimeParser,
                numberFormatter,
                numberParser,
                timeFormatter,
                timeParser,
                dateOffset);
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverters() {
        throw new UnsupportedOperationException();
    }
}
