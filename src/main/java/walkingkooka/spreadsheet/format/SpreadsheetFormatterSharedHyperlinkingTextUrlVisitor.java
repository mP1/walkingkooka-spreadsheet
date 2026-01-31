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

import walkingkooka.collect.list.Lists;
import walkingkooka.net.TextUrlVisitor;
import walkingkooka.net.Url;
import walkingkooka.tree.text.TextNode;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * Accepts some plain text and converts urls into {@link walkingkooka.tree.text.Hyperlink} and text into {@link walkingkooka.tree.text.Text}.
 */
final class SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor extends TextUrlVisitor {

    static TextNode toTextNode(final String text) {
        final SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor visitor = new SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor();
        visitor.accept(text);
        return TextNode.style(
            visitor.nodes
        );
    }

    // @VisibleForTesting
    SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final Url url) {
        this.append(
            TextNode.hyperlink(url)
        );
        return Visiting.SKIP;
    }

    @Override
    protected void visitText(final String text) {
        this.appendText(text);
    }

    @Override
    protected void visitInvalidUrlText(final String text) {
        this.appendText(text);
    }

    private void appendText(final String text) {
        this.nodes.add(
            TextNode.text(text)
        );
    }

    private void append(final TextNode textNode) {
        this.nodes.add(textNode);
    }

    private final List<TextNode> nodes = Lists.array();

    // Object...........................................................................................................

    @Override
    public String toString() {
        return String.valueOf(this.nodes);
    }
}
