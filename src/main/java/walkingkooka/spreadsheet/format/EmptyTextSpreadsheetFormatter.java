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

/**
 * A {@link SpreadsheetFormatter} that unconditionally always returns {@link SpreadsheetText#EMPTY}.
 */
final class EmptyTextSpreadsheetFormatter extends SpreadsheetFormatter2 {

    /**
     * Singleton
     */
    final static EmptyTextSpreadsheetFormatter INSTANCE = new EmptyTextSpreadsheetFormatter();

    private EmptyTextSpreadsheetFormatter() {
        super();
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return true;
    }

    @Override
    Optional<SpreadsheetText> format0(final Object value,
                                      final SpreadsheetFormatterContext context) {
        return RESULT;
    }

    private final static Optional<SpreadsheetText> RESULT = Optional.of(SpreadsheetText.EMPTY);

    @Override
    public String toString() {
        return "";
    }
}
