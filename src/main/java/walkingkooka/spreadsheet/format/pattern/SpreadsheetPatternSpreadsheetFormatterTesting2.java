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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterTesting2;
import walkingkooka.spreadsheet.format.SpreadsheetText;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetPatternSpreadsheetFormatterTesting2<F extends SpreadsheetPatternSpreadsheetFormatter> extends SpreadsheetPatternSpreadsheetFormatterTesting,
    SpreadsheetFormatterTesting2<F> {

    @Test
    default void testFormatSpreadsheetTextWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createFormatter()
                .formatSpreadsheetText(
                    null,
                    this.createContext()
                )
        );
    }

    @Test
    default void testFormatSpreadsheetTextWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createFormatter()
                .formatSpreadsheetText(
                    Optional.of("Value"),
                    null
                )
        );
    }

    default void formatSpreadsheetTextAndCheck(final Optional<Object> value,
                                               final SpreadsheetFormatterContext context) {
        this.formatSpreadsheetTextAndCheck(
            this.createFormatter(),
            value,
            context
        );
    }

    default void formatSpreadsheetTextAndCheck(final Optional<Object> value,
                                               final SpreadsheetFormatterContext context,
                                               final SpreadsheetText expected) {
        this.formatSpreadsheetTextAndCheck(
            this.createFormatter(),
            value,
            context,
            Optional.of(expected)
        );
    }

    default void formatSpreadsheetTextAndCheck(final Optional<Object> value,
                                               final SpreadsheetFormatterContext context,
                                               final Optional<SpreadsheetText> expected) {
        this.formatSpreadsheetTextAndCheck(
            this.createFormatter(),
            value,
            context,
            expected
        );
    }
}
