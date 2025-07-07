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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that returns the {@link SpreadsheetFormatParserTokenKind} for the given {@link ParserToken}.
 */
final class SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    // only called by SpreadsheetFormatterSelectorToken#nextToken
    static Optional<SpreadsheetFormatParserTokenKind> last(final ParserToken token) {
        Objects.requireNonNull(token, "token");

        final SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return Optional.ofNullable(visitor.kind);
    }

    // @VisibleForTesting
    SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor() {
        this.kind = null;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
        final Optional<SpreadsheetFormatParserTokenKind> maybeKind = token.kind();

        // parents dont have kinds, the small unit is a leaf parser token.
        if (maybeKind.isPresent()) {
            this.kind = maybeKind.get();
        }

        // dont want to visit children if a parent has a kind.
        return maybeKind.isPresent() ?
            Visiting.SKIP :
            Visiting.CONTINUE;
    }

    /**
     * The {@link SpreadsheetFormatParserTokenKind} for the last token with a {@link SpreadsheetFormatParserTokenKind}
     */
    private SpreadsheetFormatParserTokenKind kind;

    @Override
    public String toString() {
        return String.valueOf(this.kind);
    }
}
