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

import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that wraps another {@link SpreadsheetFormatter} attempting to convert any text into
 * {@link walkingkooka.net.Url} using {@link walkingkooka.net.TextUrlVisitor}.
 */
final class SpreadsheetFormatterSharedHyperlinking extends SpreadsheetFormatterShared
    implements TreePrintable {

    static SpreadsheetFormatterSharedHyperlinking with(final SpreadsheetFormatter formatter) {
        return new SpreadsheetFormatterSharedHyperlinking(
            Objects.requireNonNull(formatter, "formatter")
        );
    }

    private SpreadsheetFormatterSharedHyperlinking(final SpreadsheetFormatter formatter) {
        super();
        this.formatter = formatter;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        final Optional<TextNode> formatted = this.formatter.format(
            value,
            context
        );

        // try and turn any Text from the formatted result of the wrapped formatter into Hyperlinks
        return formatted.map(
            t -> t.replaceIf(
                TextNode::isText,
                (TextNode textNode) -> SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor.toTextNode(
                    textNode.text()
                )
            )
        );
    }

    private final SpreadsheetFormatter formatter;

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return NO_TOKENS;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.formatter.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpreadsheetFormatterSharedHyperlinking && this.equals0((SpreadsheetFormatterSharedHyperlinking) other);
    }

    private boolean equals0(final SpreadsheetFormatterSharedHyperlinking other) {
        return this.formatter.equals(other.formatter);
    }

    @Override
    public String toString() {
        return "linking " + this.formatter;
    }

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
