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

import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.function.Predicate;

/**
 * A expression which includes its name and any parameters if any. Each parameter will be separated by a comma.
 * <br>
 * SUM(A10:A20)
 */
public final class SpreadsheetNamedFunctionParserToken extends SpreadsheetParentParserToken {

    static SpreadsheetNamedFunctionParserToken with(final List<ParserToken> value,
                                                    final String text) {
        return new SpreadsheetNamedFunctionParserToken(
                copyAndCheckTokens(value),
                checkText(text)
        );
    }

    private SpreadsheetNamedFunctionParserToken(final List<ParserToken> value,
                                                final String text) {
        super(value, text);

        final List<ParserToken> without = ParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (2 != count) {
            throw new IllegalArgumentException("Expected 2 tokens but got " + count + "=" + without);
        }
        final SpreadsheetParserToken name = without.get(0)
                .cast(SpreadsheetParserToken.class);
        if (!name.isFunctionName()) {
            throw new IllegalArgumentException("Function name missing from " + value);
        }

        final SpreadsheetParserToken parameters = without.get(1)
                .cast(SpreadsheetParserToken.class);
        if (!parameters.isFunctionParameters()) {
            throw new IllegalArgumentException("Function parameters missing from " + value);
        }

        this.name = name.cast(SpreadsheetFunctionNameParserToken.class)
                .value();
        this.parameters = parameters.cast(SpreadsheetFunctionParametersParserToken.class);
    }

    /**
     * The name of the expression
     */
    public SpreadsheetFunctionName functionName() {
        return this.name;
    }

    private final SpreadsheetFunctionName name;

    public SpreadsheetFunctionParametersParserToken parameters() {
        return this.parameters;
    }

    private final SpreadsheetFunctionParametersParserToken parameters;

    // children.........................................................................................................

    @Override
    public SpreadsheetNamedFunctionParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetNamedFunctionParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public SpreadsheetNamedFunctionParserToken removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveFirstIf(
                this,
                predicate,
                SpreadsheetNamedFunctionParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public SpreadsheetNamedFunctionParserToken removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.parentRemoveIf(
                this,
                predicate,
                SpreadsheetNamedFunctionParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetNamedFunctionParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                              final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetNamedFunctionParserToken.class
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
        return other instanceof SpreadsheetNamedFunctionParserToken;
    }
}
