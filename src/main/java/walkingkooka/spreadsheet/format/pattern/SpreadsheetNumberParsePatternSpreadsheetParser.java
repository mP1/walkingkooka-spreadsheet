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

import walkingkooka.spreadsheet.SpreadsheetValueType;
import walkingkooka.spreadsheet.formula.parser.NumberSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelectorToken;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RequiredParser;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The {@link SpreadsheetParser} returned by {@link SpreadsheetNumberParsePattern#converter()}.
 */
final class SpreadsheetNumberParsePatternSpreadsheetParser implements SpreadsheetParser,
    RequiredParser<SpreadsheetParserContext> {

    static SpreadsheetNumberParsePatternSpreadsheetParser with(final SpreadsheetNumberParsePattern pattern,
                                                               final SpreadsheetNumberParsePatternMode mode) {
        return new SpreadsheetNumberParsePatternSpreadsheetParser(
            pattern,
            mode
        );
    }

    private SpreadsheetNumberParsePatternSpreadsheetParser(final SpreadsheetNumberParsePattern pattern,
                                                           final SpreadsheetNumberParsePatternMode mode) {
        super();
        this.pattern = pattern;
        this.mode = mode;

        this.mode.checkCompatible(pattern);
    }

    @Override
    public Optional<ParserToken> parse(final TextCursor cursor,
                                       final SpreadsheetParserContext context) {
        NumberSpreadsheetFormulaParserToken token = null;

        final TextCursorSavePoint save = cursor.save();

        for (final List<SpreadsheetNumberParsePatternComponent> pattern : this.pattern.patternComponents) {
            final SpreadsheetNumberParsePatternRequest request = SpreadsheetNumberParsePatternRequest.with(
                pattern.iterator(),
                this.mode,
                context
            );
            if (request.nextComponent(cursor)) {
                final List<ParserToken> tokens = request.tokens;
                if (!tokens.isEmpty()) {
                    token = NumberSpreadsheetFormulaParserToken.number(
                        tokens,
                        save.textBetween().toString()
                    );
                    break;
                }
            }
            save.restore();
        }

        return Optional.ofNullable(token);
    }

    private final SpreadsheetNumberParsePatternMode mode;

    @Override
    public List<SpreadsheetParserSelectorToken> tokens(final SpreadsheetParserContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetParserSelectorToken.tokens(
            this.pattern.value()
        );
    }

    @Override
    public Optional<ValidationValueTypeName> valueType() {
        return NUMBER;
    }

    private final static Optional<ValidationValueTypeName> NUMBER = Optional.of(
        SpreadsheetValueType.NUMBER
    );

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetNumberParsePatternSpreadsheetParser && this.equals0((SpreadsheetNumberParsePatternSpreadsheetParser) other);
    }

    private boolean equals0(final SpreadsheetNumberParsePatternSpreadsheetParser other) {
        return this.pattern.equals(other.pattern);
    }

    @Override
    public String toString() {
        return this.pattern.toString();
    }

    /**
     * The enclosing {@link SpreadsheetNumberParsePattern}.
     */
    private final SpreadsheetNumberParsePattern pattern;
}
