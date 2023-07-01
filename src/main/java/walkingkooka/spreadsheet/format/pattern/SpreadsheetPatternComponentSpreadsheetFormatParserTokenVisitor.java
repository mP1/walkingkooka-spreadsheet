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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

final class SpreadsheetPatternComponentSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static void traverse(final ParserToken token,
                         final BiConsumer<SpreadsheetFormatParserTokenKind, String> consumer) {
        Objects.requireNonNull(consumer, "consumer");

        new SpreadsheetPatternComponentSpreadsheetFormatParserTokenVisitor(consumer)
                .accept(token);
    }

    SpreadsheetPatternComponentSpreadsheetFormatParserTokenVisitor(final BiConsumer<SpreadsheetFormatParserTokenKind, String> consumer) {
        super();
        this.consumer = consumer;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
        final Optional<SpreadsheetFormatParserTokenKind> kind = token.kind();

        if (kind.isPresent()) {
            this.consumer.accept(
                    kind.get(),
                    token.text()
            );
        }

        return Visiting.CONTINUE;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatColorParserToken token) {
        // dont want to collect kinds for the square brackets and other contents of a color eg: [RED] or [Color 123]
        return Visiting.SKIP;
    }

    /**
     * Collects all the {@link SpreadsheetFormatParserTokenKind} as they are encountered.
     */
    private final BiConsumer<SpreadsheetFormatParserTokenKind, String> consumer;

    @Override
    public String toString() {
        return this.consumer.toString();
    }
}
