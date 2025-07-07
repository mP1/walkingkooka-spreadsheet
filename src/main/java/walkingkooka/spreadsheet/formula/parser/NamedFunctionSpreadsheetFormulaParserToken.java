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

import walkingkooka.spreadsheet.expression.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * An expression which includes its name and any parameters if any. Each parameter will be separated by a comma.
 * <br>
 * SUM(A10:A20)
 */
public final class NamedFunctionSpreadsheetFormulaParserToken extends FunctionSpreadsheetFormulaParserToken {

    static NamedFunctionSpreadsheetFormulaParserToken with(final List<ParserToken> value,
                                                           final String text) {
        return new NamedFunctionSpreadsheetFormulaParserToken(
            copyAndCheckTokens(value),
            checkText(text)
        );
    }

    private NamedFunctionSpreadsheetFormulaParserToken(final List<ParserToken> value,
                                                       final String text) {
        super(value, text);

        final List<ParserToken> without = ParserToken.filterWithoutNoise(value);
        final int count = without.size();
        if (2 != count) {
            throw new IllegalArgumentException("Expected 2 tokens but got " + count + "=" + without);
        }
        final SpreadsheetFormulaParserToken name = without.get(0)
            .cast(SpreadsheetFormulaParserToken.class);
        if (!name.isFunctionName()) {
            throw new IllegalArgumentException("Function name missing parse " + value);
        }

        final SpreadsheetFormulaParserToken parameters = without.get(1)
            .cast(SpreadsheetFormulaParserToken.class);
        if (!parameters.isFunctionParameters()) {
            throw new IllegalArgumentException("Function parameters missing parse " + value);
        }

        this.name = name.cast(FunctionNameSpreadsheetFormulaParserToken.class)
            .value();
        this.parameters = parameters.cast(FunctionParametersSpreadsheetFormulaParserToken.class);
    }

    /**
     * The name of the function
     */
    @Override
    public SpreadsheetFunctionName functionName() {
        return this.name;
    }

    private final SpreadsheetFunctionName name;

    @Override
    public FunctionParametersSpreadsheetFormulaParserToken parameters() {
        return this.parameters;
    }

    private final FunctionParametersSpreadsheetFormulaParserToken parameters;

    // children.........................................................................................................

    @Override
    public NamedFunctionSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            NamedFunctionSpreadsheetFormulaParserToken::with
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
