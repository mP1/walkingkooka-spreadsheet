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
package walkingkooka.spreadsheet.parser;

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The parameters including the open/close parenthesis for a function.
 * <br>
 * (A10:A20)
 */
public final class SpreadsheetFunctionParametersParserToken extends SpreadsheetParentParserToken {

    static SpreadsheetFunctionParametersParserToken with(final List<ParserToken> value,
                                                         final String text) {
        return new SpreadsheetFunctionParametersParserToken(
                copyAndCheckTokens(value),
                checkText(text)
        );
    }

    private SpreadsheetFunctionParametersParserToken(final List<ParserToken> value,
                                                     final String text) {
        super(value, text);

        this.parameters = ParserToken.filterWithoutNoise(value);
    }

    public List<ParserToken> parameters() {
        return this.parameters;
    }

    private final List<ParserToken> parameters;

    // children.........................................................................................................

    @Override
    public SpreadsheetFunctionParametersParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetFunctionParametersParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetFunctionParametersParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                SpreadsheetFunctionParametersParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetFunctionParametersParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfParent(
                this,
                predicate,
                SpreadsheetFunctionParametersParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFunctionParametersParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                   final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetFunctionParametersParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFunctionParametersParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                              final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetFunctionParametersParserToken.class
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFunctionParametersParserToken;
    }
}
