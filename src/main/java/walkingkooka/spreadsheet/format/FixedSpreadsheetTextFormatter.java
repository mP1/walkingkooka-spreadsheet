/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetTextFormatter} that ignores the value and always returns the same {@link Optional< SpreadsheetFormattedText >}.
 * This is useful to hold the result of an empty format expression.
 */
final class FixedSpreadsheetTextFormatter<V> extends SpreadsheetTextFormatter2<V> {

    /**
     * Creates a new {@link FixedSpreadsheetTextFormatter}.
     */
    static <V> FixedSpreadsheetTextFormatter<V> with(final Class<V> type, final Optional<SpreadsheetFormattedText> formattedText) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(formattedText, "formattedText");

        return type == Object.class && NO_TEXT.equals(formattedText) ?
                Cast.to(OBJECT_NO_TEXT) :
                new FixedSpreadsheetTextFormatter<>(type, formattedText);
    }

    /**
     * Singleton.
     */
    @SuppressWarnings("rawtypes")
    private final static FixedSpreadsheetTextFormatter OBJECT_NO_TEXT = new FixedSpreadsheetTextFormatter<>(Object.class, NO_TEXT);

    /**
     * Private ctor use factory.
     */
    private FixedSpreadsheetTextFormatter(final Class<V> type, final Optional<SpreadsheetFormattedText> formattedText) {
        super();
        this.type = type;
        this.formattedText = formattedText;
    }

    @Override
    public Class<V> type() {
        return this.type;
    }

    private final Class<V> type;

    @Override
    Optional<SpreadsheetFormattedText> format0(final V value, final SpreadsheetTextFormatContext context) {
        return this.formattedText;
    }

    @Override
    public String toString() {
        return this.formattedText.toString();
    }

    private final Optional<SpreadsheetFormattedText> formattedText;
}
