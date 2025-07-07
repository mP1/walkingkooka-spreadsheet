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
package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;

/**
 * Holds a text expression in both forms an apostrophe prefixed string literal and a double-quoted string.
 */
public final class TextSpreadsheetFormulaParserToken extends ValueSpreadsheetFormulaParserToken {

    static TextSpreadsheetFormulaParserToken with(final List<ParserToken> value, final String text) {
        return new TextSpreadsheetFormulaParserToken(
            copyAndCheckTokens(value),
            Objects.requireNonNull(text, "text") // empty text is allowed to support a formula with empty text
        );
    }

    private TextSpreadsheetFormulaParserToken(final List<ParserToken> value,
                                              final String text) {
        super(value, text);

        String textValue = null;

        for (final ParserToken token : this.children()) {
            final SpreadsheetFormulaParserToken spreadsheetFormulaParserToken = token.cast(SpreadsheetFormulaParserToken.class);
            if (spreadsheetFormulaParserToken.isTextLiteral()) {
                if (null != textValue) {
                    throw new IllegalArgumentException("Extra text literal in " + CharSequences.quoteAndEscape(this.text()));
                }

                textValue = spreadsheetFormulaParserToken.cast(TextLiteralSpreadsheetFormulaParserToken.class)
                    .value();
            }
        }

        this.textValue = null == textValue ?
            "" :
            textValue;
    }

    /**
     * Getter that returns the text literal value without any surrounding quotation or leading apostrophe.
     */
    public String textValue() {
        return this.textValue;
    }

    private final String textValue;

    // children.........................................................................................................

    @Override
    public TextSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            TextSpreadsheetFormulaParserToken::with
        );
    }

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetFormulaParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }
}
