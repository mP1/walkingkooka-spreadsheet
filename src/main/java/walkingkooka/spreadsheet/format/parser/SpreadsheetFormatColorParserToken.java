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

import walkingkooka.text.cursor.parser.ParentParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;

/**
 * A token that contains a color declaration which may be a name or color number.
 */
public final class SpreadsheetFormatColorParserToken extends SpreadsheetFormatParentParserToken {

    /**
     * Factory that creates a new {@link SpreadsheetFormatColorParserToken}.
     */
    static SpreadsheetFormatColorParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetFormatColorParserToken(copyAndCheckTokensFailIfEmpty(value),
                checkTextNotEmptyOrWhitespace(text));
    }

    /**
     * Private ctor use helper.
     */
    private SpreadsheetFormatColorParserToken(final List<ParserToken> value, final String text) {
        super(value, text);

        final List<ParserToken> without = ParentParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (1 != count) {
            throw new IllegalArgumentException("Expected 1 token but got " + count + "=" + without);
        }
        final Optional<SpreadsheetFormatParserToken> nameOrNumber = without.stream()
                .map(t -> t.cast(SpreadsheetFormatParserToken.class))
                .filter(t -> t.isColorName() || t.isColorNumber())
                .findFirst();
        if (!nameOrNumber.isPresent()) {
            throw new IllegalArgumentException("Color name or number missing from tokens " + value);
        }
        this.nameOrNumber = nameOrNumber.get();
    }

    public SpreadsheetFormatParserToken nameOrNumber() {
        return this.nameOrNumber;
    }

    private final SpreadsheetFormatParserToken nameOrNumber;

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatColorParserToken;
    }

}
