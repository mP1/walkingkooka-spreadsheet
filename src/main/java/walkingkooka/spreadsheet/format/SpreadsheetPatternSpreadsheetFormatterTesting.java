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

import java.util.Optional;

public interface SpreadsheetPatternSpreadsheetFormatterTesting extends SpreadsheetFormatterTesting {

    default void formatSpreadsheetTextAndCheck(final SpreadsheetPatternSpreadsheetFormatter formatter,
                                               final Optional<Object> value,
                                               final SpreadsheetFormatterContext context) {
        this.formatSpreadsheetTextAndCheck(
            formatter,
            value,
            context,
            Optional.empty()
        );
    }

    default void formatSpreadsheetTextAndCheck(final SpreadsheetPatternSpreadsheetFormatter formatter,
                                               final Optional<Object> value,
                                               final SpreadsheetFormatterContext context,
                                               final SpreadsheetText expected) {
        this.formatSpreadsheetTextAndCheck(
            formatter,
            value,
            context,
            Optional.of(expected)
        );
    }

    default void formatSpreadsheetTextAndCheck(final SpreadsheetPatternSpreadsheetFormatter formatter,
                                               final Optional<Object> value,
                                               final SpreadsheetFormatterContext context,
                                               final Optional<SpreadsheetText> expected) {
        this.checkEquals(
            expected,
            formatter.formatSpreadsheetText(
                value,
                context
            )
        );
    }
}
