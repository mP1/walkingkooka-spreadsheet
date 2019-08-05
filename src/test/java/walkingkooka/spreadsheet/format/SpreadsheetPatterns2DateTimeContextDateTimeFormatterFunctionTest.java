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

import walkingkooka.datetime.DateTimeContext;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public final class SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunctionTest
        extends SpreadsheetPatterns2TestCase<SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction> {

    @Override
    public Class<SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction> type() {
        return SpreadsheetPatterns2DateTimeContextDateTimeFormatterFunction.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetPatterns2.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return DateTimeContext.class.getSimpleName() + DateTimeFormatter.class.getSimpleName() + Function.class.getSimpleName();
    }
}
