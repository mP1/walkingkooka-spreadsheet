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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.TextUrlVisitorTesting;
import walkingkooka.net.Url;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.text.TextNode;

public final class SpreadsheetFormatterSharedHyperlinkingTextUrlVisitorTest implements TextUrlVisitorTesting<SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor> {

    @Test
    public void testToTextWithPlainText() {
        final String text = "PlainText123";

        this.toTextNodeAndCheck(
            text,
            TextNode.text(text)
        );
    }

    @Test
    public void testToTextWithHyperlink() {
        final String url = "https://example.com";

        this.toTextNodeAndCheck(
            url,
            TextNode.hyperlink(
                Url.parse(url)
            )
        );
    }

    @Test
    public void testToTextWithHyperlinkAndText() {
        final String url = "https://example.com";
        final String text = " textAfter123";

        this.toTextNodeAndCheck(
            url + text,
            TextNode.style(
                Lists.of(
                    TextNode.hyperlink(
                        Url.parse(url)
                    ),
                    TextNode.text(text)
                )
            )
        );
    }

    private void toTextNodeAndCheck(final String text,
                                    final TextNode expected) {
        this.checkEquals(
            expected,
            SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor.toTextNode(text),
            text
        );
    }

    @Override
    public SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor createVisitor() {
        return new SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor> type() {
        return SpreadsheetFormatterSharedHyperlinkingTextUrlVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
