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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} that is used to visit tokens and produce {@link SpreadsheetParserSelectorToken} including alternatives.
 * Note currently labels in all cases are pattern text not actual labels such as DAY but pattern symbols such as D or DD.
 */
final class SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    // only called by SpreadsheetParserSelectorToken#tokens
    static List<SpreadsheetParserSelectorToken> tokens(final ParserToken token) {
        Objects.requireNonNull(token, "token");

        final SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return visitor.tokens;
    }

    SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor() {
        this.tokens = Lists.array();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
        final Optional<SpreadsheetFormatParserTokenKind> maybeKind = token.kind();

        // parents dont have kinds, the small unit is a leaf parser token.
        if (maybeKind.isPresent()) {
            final SpreadsheetFormatParserTokenKind kind = maybeKind.get();

            // maybe later use a real label like hour rather than its pattern symbol character(s).
            final String text = token.text();

            this.tokens.add(
                SpreadsheetParserSelectorToken.with(
                    text, // label
                    text, // text,
                    this.alternatives(
                        kind,
                        text
                    )
                )
            );
        }

        // dont want to visit children if a parent has a kind.
        return maybeKind.isPresent() ?
            Visiting.SKIP :
            Visiting.CONTINUE;
    }

    private List<SpreadsheetParserSelectorTokenAlternative> alternatives(final SpreadsheetFormatParserTokenKind kind,
                                                                         final String text) {
        return kind.alternatives()
            .stream()
            .filter(t -> false == t.equals(text))
            .map(t -> SpreadsheetParserSelectorTokenAlternative.with(t, t))
            .collect(Collectors.toList());
    }

    private final List<SpreadsheetParserSelectorToken> tokens;

    @Override
    public String toString() {
        return this.tokens.toString();
    }
}
