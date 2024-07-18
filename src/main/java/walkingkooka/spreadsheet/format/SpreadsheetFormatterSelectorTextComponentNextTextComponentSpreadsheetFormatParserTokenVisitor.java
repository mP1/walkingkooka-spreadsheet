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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} returns the next text component for the given index.
 */
final class SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    // only called by SpreadsheetFormatterSelectorTextComponent#nextTextComponent
    static Optional<SpreadsheetFormatterSelectorTextComponent> nextTextComponent(final ParserToken token,
                                                                                 final int index) {
        Objects.requireNonNull(token, "token");

        final SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor(index);
        visitor.accept(token);

        final SpreadsheetFormatParserTokenKind kind = visitor.kind;
        if (null == kind) {
            throw new IndexOutOfBoundsException("Invalid index " + index);
        }

        return Optional.of(
                SpreadsheetFormatterSelectorTextComponent.with(
                        "", // label
                        "", // text
                        kind.alternatives()
                                .stream()
                                .map(t -> SpreadsheetFormatterSelectorTextComponentAlternative.with(t, t))
                                .collect(Collectors.toList())
                )
        );
    }

    // @VisibleForTesting
    SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor(final int requestedIndex) {
        this.requestedIndex = requestedIndex;
        this.kind = null;
    }

    @Override
    protected Visiting startVisit(final ParserToken token) {
        return this.index <= this.requestedIndex ?
                Visiting.CONTINUE :
                Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
        final Optional<SpreadsheetFormatParserTokenKind> maybeKind = token.kind();

        // parents dont have kinds, the small unit is a leaf parser token.
        if (maybeKind.isPresent()) {
            this.index++;

            if (this.requestedIndex == this.index) {
                this.kind = maybeKind.get();
            }
        }

        // dont want to visit children if a parent has a kind.
        return maybeKind.isPresent() ?
                Visiting.SKIP :
                Visiting.CONTINUE;
    }

    /**
     * The requested index. Visiting should stop when this is reached.
     */
    private final int requestedIndex;

    /**
     * An index that is incremented when a token is found.
     */
    private int index;

    /**
     * The {@link SpreadsheetFormatParserTokenKind} for the token at the requested index.
     */
    private SpreadsheetFormatParserTokenKind kind;

    @Override
    public String toString() {
        return this.requestedIndex + " " + this.index + " " + this.kind;
    }
}
