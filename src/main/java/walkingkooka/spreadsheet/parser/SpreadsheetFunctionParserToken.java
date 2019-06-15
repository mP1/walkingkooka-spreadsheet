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

import walkingkooka.Cast;
import walkingkooka.spreadsheet.SpreadsheetFunctionName;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.tree.visit.Visiting;

import java.util.List;

/**
 * A function which includes its name and any parameters if any. Each parameter will be separated by a comma.
 * <br>
 * SUM(A10:A20)
 */
public final class SpreadsheetFunctionParserToken extends SpreadsheetParentParserToken<SpreadsheetFunctionParserToken> {

    static SpreadsheetFunctionParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetFunctionParserToken(copyAndCheckTokens(value),
                checkText(text),
                WITHOUT_COMPUTE_REQUIRED);
    }

    private SpreadsheetFunctionParserToken(final List<ParserToken> value,
                                           final String text,
                                           final List<ParserToken> valueWithout) {
        super(value, text, valueWithout);

        final List<ParserToken> without = Cast.to(SpreadsheetParentParserToken.class.cast(this.withoutSymbols().get()).value());
        final int count = without.size();
        if (count < 1) {
            throw new IllegalArgumentException("Expected at least 1 tokens but got " + count + "=" + without);
        }
        final SpreadsheetParserToken name = without.get(0).cast();
        if (!name.isFunctionName()) {
            throw new IllegalArgumentException("Function name missing from " + value);
        }

        this.name = SpreadsheetFunctionNameParserToken.class.cast(name).value();
        this.parameters = without.subList(1, without.size());
    }

    /**
     * The name of the function
     */
    public SpreadsheetFunctionName functionName() {
        return this.name;
    }

    private final SpreadsheetFunctionName name;

    public List<ParserToken> parameters() {
        return this.parameters;
    }

    private final List<ParserToken> parameters;

    @Override
    SpreadsheetParentParserToken replace(final List<ParserToken> tokens, final List<ParserToken> without) {
        return new SpreadsheetFunctionParserToken(tokens,
                text,
                without);
    }

    // isXXX............................................................................................................

    @Override
    public boolean isAddition() {
        return false;
    }

    @Override
    public boolean isCellReference() {
        return false;
    }

    @Override
    public boolean isDivision() {
        return false;
    }

    @Override
    public boolean isEquals() {
        return false;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public boolean isGreaterThan() {
        return false;
    }

    @Override
    public boolean isGreaterThanEquals() {
        return false;
    }

    @Override
    public boolean isGroup() {
        return false;
    }

    @Override
    public boolean isLessThan() {
        return false;
    }

    @Override
    public boolean isLessThanEquals() {
        return false;
    }

    @Override
    public boolean isMultiplication() {
        return false;
    }

    @Override
    public boolean isNegative() {
        return false;
    }

    @Override
    public boolean isNotEquals() {
        return false;
    }

    @Override
    public boolean isPercentage() {
        return false;
    }

    @Override
    public boolean isPower() {
        return false;
    }

    @Override
    public boolean isRange() {
        return false;
    }

    @Override
    public boolean isSubtraction() {
        return false;
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    public void accept(final SpreadsheetParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFunctionParserToken;
    }
}
