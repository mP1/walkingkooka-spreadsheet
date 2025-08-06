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

package walkingkooka.spreadsheet.viewport;

import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.DoubleParserToken;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;

/**
 * A {@link Parser} that includes helpers to expect and parse individual tokens.
 */
final class SpreadsheetViewportParser {

    static SpreadsheetViewportParser with(final TextCursor cursor) {
        return new SpreadsheetViewportParser(cursor);
    }

    private SpreadsheetViewportParser(final TextCursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Parses a {@link SpreadsheetViewportRectangle} but does not complain about extra components.
     */
    // /home/A1/width/200/height/300
    SpreadsheetViewportRectangle parseSpreadsheetViewportRectangle() {
        this.slash();
        this.homeToken();
        this.slash();
        final SpreadsheetCellReference home = this.homeCellReference();
        
        this.slash();
        this.widthToken();
        this.slash();
        final double width = this.width();

        this.slash();
        this.heightToken();
        this.slash();
        final double height = this.height();

        return SpreadsheetViewportRectangle.with(
            home,
            width,
            height
        );
    }
    
    // helpers...........................................................................................................

    void slash() {
        SLASH_PARSER.parse(cursor, PARSER_CONTEXT);
    }

    private final static Parser<SpreadsheetParserContext> SLASH_PARSER = Parsers.string("/", CaseSensitivity.SENSITIVE)
        .orReport(ParserReporters.basic())
        .cast();

    void homeToken() {
        this.parseTokenOrFail(
            HOME_TOKEN_PARSER,
            SpreadsheetViewportRectangle.HOME_STRING
        );
    }

    void widthToken() {
        this.parseTokenOrFail(
            WIDTH_TOKEN_PARSER,
            SpreadsheetViewportRectangle.WIDTH_STRING
        );
    }

    void heightToken() {
        this.parseTokenOrFail(
            HEIGHT_TOKEN_PARSER,
            SpreadsheetViewportRectangle.HEIGHT_STRING
        );
    }

    private final static Parser<SpreadsheetParserContext> HOME_TOKEN_PARSER = parserStringToken(SpreadsheetViewportRectangle.HOME_STRING);
    private final static Parser<SpreadsheetParserContext> WIDTH_TOKEN_PARSER = parserStringToken(SpreadsheetViewportRectangle.WIDTH_STRING);
    private final static Parser<SpreadsheetParserContext> HEIGHT_TOKEN_PARSER = parserStringToken(SpreadsheetViewportRectangle.HEIGHT_STRING);

    private void parseTokenOrFail(final Parser<SpreadsheetParserContext> parser,
                                  final String label) {
        if(false == parser.parse(this.cursor, PARSER_CONTEXT).isPresent()) {
            throw new IllegalArgumentException("Missing " + label);
        }
    }

    SpreadsheetCellReference homeCellReference() {
        return SpreadsheetFormulaParsers.cell()
            .parse(
                cursor,
                PARSER_CONTEXT
            ).orElseThrow(() -> new IllegalArgumentException("Missing home"))
            .cast(CellSpreadsheetFormulaParserToken.class)
            .cell();
    }

    double width() {
        return this.parseDoubleOrFail(SpreadsheetViewportRectangle.WIDTH_STRING);
    }

    double height() {
        return this.parseDoubleOrFail(SpreadsheetViewportRectangle.HEIGHT_STRING);
    }

    /**
     * Used to parse the width or height values within a {@link UrlFragment}.
     */
    private double parseDoubleOrFail(final String label) {
        return Parsers.doubleParser()
            .parse(
                this.cursor,
                PARSER_CONTEXT
            ).orElseThrow(() -> new IllegalArgumentException("Missing " + label))
            .cast(DoubleParserToken.class)
            .value();
    }

    private static Parser<SpreadsheetParserContext> parserStringToken(final String token) {
        return Parsers.string(
            token,
            CaseSensitivity.SENSITIVE
        );
    }

    private final static SpreadsheetParserContext PARSER_CONTEXT = SpreadsheetParserContexts.basic(
        InvalidCharacterExceptionFactory.POSITION_EXPECTED,
        DateTimeContexts.fake(),
        ExpressionNumberContexts.basic(
            ExpressionNumberKind.BIG_DECIMAL,
            DecimalNumberContexts.american(MathContext.DECIMAL32)
        ),
        ';' // not actually used/
    );

    final TextCursor cursor;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cursor.toString();
    }
}
