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

import walkingkooka.naming.HasName;
import walkingkooka.text.CharSequences;

import java.util.Objects;

/**
 * Contains the {@link SpreadsheetFormatterName} and some text which may contain the pattern text for {@link SpreadsheetPatternSpreadsheetFormatter}.
 */
public final class SpreadsheetFormatterSelector implements HasName<SpreadsheetFormatterName> {

    /**
     * Parses the given text into a {@link SpreadsheetFormatterSelector}.
     */
    public static SpreadsheetFormatterSelector parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String textAfter;
        final String nameText;
        final int space = text.indexOf(' ');
        if (-1 == space) {
            nameText = text;
            textAfter = "";
        } else {
            nameText = text.substring(0, space);
            textAfter = text.substring(space + 1);
        }

        return new SpreadsheetFormatterSelector(
                SpreadsheetFormatterName.with(nameText),
                textAfter
        );
    }

    /**
     * Factory that creates a new {@link SpreadsheetFormatterSelector}.
     */
    public static SpreadsheetFormatterSelector with(final SpreadsheetFormatterName name,
                                                    final String text) {
        return new SpreadsheetFormatterSelector(
                Objects.requireNonNull(name, "name"),
                Objects.requireNonNull(text, "text")
        );
    }

    private SpreadsheetFormatterSelector(final SpreadsheetFormatterName name,
                                         final String text) {
        this.name = name;
        this.text = text;
    }

    @Override
    public SpreadsheetFormatterName name() {
        return this.name;
    }

    private final SpreadsheetFormatterName name;

    /**
     * If the {@link SpreadsheetFormatterName} identifies a {@link SpreadsheetPatternSpreadsheetFormatter}, this will
     * hold the pattern text itself.
     */
    public String text() {
        return this.text;
    }

    private final String text;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.text
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormatterSelector && this.equals0((SpreadsheetFormatterSelector) other);
    }

    private boolean equals0(final SpreadsheetFormatterSelector other) {
        return this.name.equals(other.name) &&
                this.text.equals(other.text);
    }

    @Override
    public String toString() {
        final String name = this.name.toString();
        final String text = this.text;

        return text.isEmpty() ?
                name :
                name + " " + CharSequences.quoteAndEscape(text);
    }
}
