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

import walkingkooka.ToStringBuilder;
import walkingkooka.convert.Converter;

/**
 * Holds individual values for each spreadsheet value type. The type parameter T should be either a {@link Converter} or
 * another {@link SpreadsheetConverterGeneralMapping}.
 */
final class SpreadsheetConverterGeneralMapping<T> {

    static <T> SpreadsheetConverterGeneralMapping<T> with(final T booleanValue,
                                                          final T date,
                                                          final T dateTime,
                                                          final T number,
                                                          final T string,
                                                          final T time) {
        return new SpreadsheetConverterGeneralMapping<>(
            booleanValue,
            date,
            dateTime,
            number,
            string,
            time
        );
    }

    /**
     * Factory that creates a new {@link SpreadsheetConverterGeneralMapping} with all the handlers.
     */
    private SpreadsheetConverterGeneralMapping(final T booleanValue,
                                               final T date,
                                               final T dateTime,
                                               final T number,
                                               final T string,
                                               final T time) {
        super();

        this.booleanValue = booleanValue;
        this.date = date;
        this.dateTime = dateTime;
        this.number = number;
        this.string = string;
        this.time = time;
    }

    final T booleanValue;
    final T date;
    final T dateTime;
    final T number;

    /**
     * String also includes character.
     */
    final T string;

    final T time;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .separator(", ")
            .label("boolean").value(this.booleanValue)
            .label("date").value(this.date)
            .label("dateTime").value(this.dateTime)
            .label("number").value(this.number)
            .label("string").value(this.string)
            .label("time").value(this.time)
            .build();
    }
}
