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
import walkingkooka.type.PublicStaticHelper;

import java.time.format.DateTimeFormatter;

public final class SpreadsheetConverters implements PublicStaticHelper {

    /**
     * {@see SpreadsheetConverter}
     */
    public static Converter converter(final long dateOffset,
                                      final String bigDecimalFormat,
                                      final String bigIntegerFormat,
                                      final String doubleFormat,
                                      final DateTimeFormatter date,
                                      final DateTimeFormatter dateTime,
                                      final DateTimeFormatter time,
                                      final String longFormat) {
        return SpreadsheetConverter.with(dateOffset,
                bigDecimalFormat,
                bigIntegerFormat,
                doubleFormat,
                date,
                dateTime,
                time,
                longFormat);
    }

    /**
     * Stop creation
     */
    private SpreadsheetConverters() {
        throw new UnsupportedOperationException();
    }
}
