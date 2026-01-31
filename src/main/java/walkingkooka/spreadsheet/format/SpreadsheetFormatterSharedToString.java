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

import walkingkooka.Cast;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Wrapos another {@link SpreadsheetFormatter} and uses the provided {@link #toString()}.
 */
final class SpreadsheetFormatterSharedToString extends SpreadsheetFormatterShared
    implements TreePrintable {

    static SpreadsheetFormatter with(final SpreadsheetFormatter formatter,
                                  final String toString) {
        Objects.requireNonNull(formatter, "formatter");
        Objects.requireNonNull(toString, "toString");

        SpreadsheetFormatter formatterWithToString;

        SpreadsheetFormatter temp = formatter;

        if (temp.toString().equals(toString)) {
            formatterWithToString = formatter; // no need to wrap
        } else {
            if (temp instanceof SpreadsheetFormatterSharedToString) {
                temp = ((SpreadsheetFormatterSharedToString) temp).formatter;
            }
            if (temp.toString().equals(toString)) {
                formatterWithToString = formatter; // no need to wrap
            } else {
                formatterWithToString = new SpreadsheetFormatterSharedToString(
                    formatter,
                    toString
                );
            }
        }

        return formatterWithToString;
    }

    private SpreadsheetFormatterSharedToString(final SpreadsheetFormatter formatter,
                                               final String toString) {
        super();

        this.formatter = formatter;
        this.toString = toString;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value, 
                                     final SpreadsheetFormatterContext context) {
        return this.formatter.format(
            value,
            context
        );
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        return this.formatter.tokens(context);
    }

    private final SpreadsheetFormatter formatter;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.formatter,
            this.toString
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetFormatterSharedToString &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormatterSharedToString other) {
        return this.formatter.equals(other.formatter) &&
            this.toString.equals(other.toString);
    }

    @Override
    public String toString() {
        return this.toString;
    }

    private final String toString;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                this.formatter,
                printer
            );
        }
        printer.outdent();
    }
}
