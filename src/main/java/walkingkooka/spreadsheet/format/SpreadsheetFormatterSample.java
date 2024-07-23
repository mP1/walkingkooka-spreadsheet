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

import walkingkooka.Value;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Objects;

/**
 * A sample include a value that may be used to provide a sample of a {@link SpreadsheetFormatter}.
 * A {@link SpreadsheetFormatterProvider} will return a {@link List} providing samples of the given {@link SpreadsheetFormatter}.
 */
public final class SpreadsheetFormatterSample<T> implements TreePrintable, Value<T> {

    public static <T> SpreadsheetFormatterSample<T> with(final String label,
                                                         final SpreadsheetFormatterSelector selector,
                                                         final T value) {
        return new SpreadsheetFormatterSample<>(
                CharSequences.failIfNullOrEmpty(label, "label"),
                Objects.requireNonNull(selector, "selector"),
                value
        );
    }

    private SpreadsheetFormatterSample(final String label,
                                       final SpreadsheetFormatterSelector selector,
                                       final T value) {
        this.label = label;
        this.selector = selector;
        this.value = value;
    }

    public String label() {
        return this.label;
    }

    private final String label;

    public SpreadsheetFormatterSelector selector() {
        return this.selector;
    }

    private final SpreadsheetFormatterSelector selector;

    public T value() {
        return this.value;
    }

    private final T value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.label,
                this.selector,
                this.value
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormatterSample && this.equals0((SpreadsheetFormatterSample<?>) other);
    }

    private boolean equals0(final SpreadsheetFormatterSample<?> other) {
        return this.label.equals(other.label) &&
                this.selector.equals(other.selector) &&
                Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return this.label + " " + selector + " " + CharSequences.quoteIfChars(this.value);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.label.toString());

        printer.indent();
        {
            this.selector.printTree(printer);
            TreePrintable.printTreeOrToString(
                    this.value,
                    printer
            );
            printer.lineStart();
        }
        printer.outdent();
    }
}
