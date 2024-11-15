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

import java.util.List;

/**
 * Base class for both types of function declarations, both lambda and named functions.
 */
public abstract class SpreadsheetFunctionParserToken extends SpreadsheetParentParserToken {

//    static SpreadsheetFunctionParserToken with(final List<ParserToken> value, final String text) {
//        return new SpreadsheetFunctionParserToken(copyAndCheckTokens(value),
//                checkText(text));
//    }

    SpreadsheetFunctionParserToken(final List<ParserToken> value,
                                   final String text) {
        super(value, text);
    }


    public abstract SpreadsheetFunctionName functionName();

    public abstract SpreadsheetFunctionParametersParserToken parameters();
    //
//    /**
//     * The name of the expression
//     */
//    public SpreadsheetFunctionName functionName() {
//        return this.name;
//    }
//
//    private final SpreadsheetFunctionName name;
//
//    public List<ParserToken> parameters() {
//        return this.parameters;
//    }
//
//    private final List<ParserToken> parameters;

//    // children.........................................................................................................
//
//    @Override
//    public SpreadsheetFunctionParserToken setChildren(final List<ParserToken> children) {
////        return ParserToken.parentSetChildren(
////                this,
////                children,
////                SpreadsheetFunctionParserToken::with
////        );
//        throw new UnsupportedOperationException();
//    }
//
//    // SpreadsheetParserTokenVisitor....................................................................................
//
//    @Override
//    void accept(final SpreadsheetParserTokenVisitor visitor) {
//        if (Visiting.CONTINUE == visitor.startVisit(this)) {
//            this.acceptValues(visitor);
//        }
//        visitor.endVisit(this);
//    }
//
//    // Object...........................................................................................................
//
//    @Override
//    boolean canBeEqual(final Object other) {
//        return other instanceof SpreadsheetFunctionParserToken;
//    }
}
