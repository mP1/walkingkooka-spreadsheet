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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.CharSequences;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Contains the {@link SpreadsheetFormatterName} and some text which may contain the pattern text for {@link SpreadsheetPatternSpreadsheetFormatter}.
 */
public final class SpreadsheetFormatterSelector implements HasName<SpreadsheetFormatterName>,
        HasText,
        TreePrintable {

    /**
     * Parses the given text into a {@link SpreadsheetFormatterSelector}.
     * <br>
     * Note the format is formatter-name SPACE optional-text, for {@link SpreadsheetPatternSpreadsheetFormatter} the text will hold the raw pattern, without
     * any need for encoding of any kind.
     * <pre>
     * text-format @
     * </pre>
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
    @Override
    public String text() {
        return this.text;
    }

    private final String text;

    /**
     * Returns a {@link SpreadsheetFormatPattern} if this selector is for a {@link SpreadsheetPatternSpreadsheetFormatter},
     * providing the text is not empty and a valid pattern.
     */
    public Optional<SpreadsheetFormatPattern> spreadsheetFormatPattern() {
        if (null == this.spreadsheetFormatPattern) {
            final SpreadsheetPatternKind patternKind = this.name.patternKind;

            SpreadsheetFormatPattern formatPattern;

            try {
                formatPattern = null == patternKind ?
                        null :
                        patternKind.parse(this.text).toFormat();
            } catch (final RuntimeException fail) {
                formatPattern = null;
            }


            this.spreadsheetFormatPattern = Optional.ofNullable(formatPattern);
        }

        return this.spreadsheetFormatPattern;
    }

    private Optional<SpreadsheetFormatPattern> spreadsheetFormatPattern;

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

    // JsonNodeContext..................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormatterSelector} from a {@link JsonNode}.
     */
    static SpreadsheetFormatterSelector unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        final String name = this.name.toString();
        final String text = this.text;

        return JsonNode.string(
                text.isEmpty() ?
                        name :
                        name + " " + text
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormatterSelector.class),
                SpreadsheetFormatterSelector::unmarshall,
                SpreadsheetFormatterSelector::marshall,
                SpreadsheetFormatterSelector.class
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.name.toString());

        final String text = this.text;
        if (false == text.isEmpty()) {
            printer.indent();
            {
                printer.println(text);
            }
            printer.outdent();
        }
    }
}
