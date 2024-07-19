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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} returns the next text component.
 */
final class SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    // only called by SpreadsheetFormatterSelectorTextComponent#nextTextComponent
    static SpreadsheetFormatterSelectorTextComponent nextTextComponent(final ParserToken token,
                                                                       final SpreadsheetPatternKind patternKind) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(patternKind, "patternKind");

        final SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);

        final SpreadsheetFormatParserTokenKind kind = visitor.kind;

        return SpreadsheetFormatterSelectorTextComponent.with(
                        "", // label
                        "", // text
                        patternKind.spreadsheetFormatParserTokenKinds()
                                .stream()
                                .filter(SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor::keep)
                                .filter(k -> null == k || false == kind.isDuplicate(k))
                                .flatMap(k -> k.alternatives().stream())
                                .distinct()
                                .sorted()
                                .map(t -> SpreadsheetFormatterSelectorTextComponentAlternative.with(t, t))
                                .collect(Collectors.toList())
                );
    }

    private static boolean keep(final SpreadsheetFormatParserTokenKind kind) {
        final boolean keep;

        switch (kind) {
            case COLOR_NAME:
            case COLOR_NUMBER:
                keep = false;
                break; // skip for now insert color pick instead
            case CONDITION:
                keep = false;
                break;
            case GENERAL:
                keep = false;
                break; // skip GENERAL for now
            case TEXT_LITERAL:
                keep = false;
                break; // skip - let the user insert the text literal into the patternTextBox
            case SEPARATOR:
                keep = false;
                break;
            default:
                keep = true;
                break;
        }

        return keep;
    }

    // @VisibleForTesting
    SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor() {
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
     * The {@link SpreadsheetFormatParserTokenKind} for the token at the requested index.
     */
    private SpreadsheetFormatParserTokenKind kind;

    @Override
    public String toString() {
        return String.valueOf(this.kind);
    }
}
