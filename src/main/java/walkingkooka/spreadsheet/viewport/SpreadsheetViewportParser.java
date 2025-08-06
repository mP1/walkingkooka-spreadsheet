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

import walkingkooka.InvalidCharacterException;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursorSavePoint;
import walkingkooka.text.cursor.parser.DoubleParserToken;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Optional;

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

    boolean optionalSlash() {
        return OPTIONAL_SLASH_PARSER.parse(
            this.cursor,
            PARSER_CONTEXT
        ).isPresent();
    }

    private final static Parser<SpreadsheetParserContext> OPTIONAL_SLASH_PARSER = Parsers.string("/", CaseSensitivity.SENSITIVE)
        .cast();

    void slash() {
        SLASH_PARSER.parse(
            this.cursor,
            PARSER_CONTEXT
        );
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

    boolean includeFrozenColumnsRowsToken() {
        return this.parseToken(INCLUDE_FROZEN_COLUMNS_ROW_TOKEN_PARSER);
    }

    boolean includeFrozenColumnsRowsValue() {
        boolean includeFrozenColumnsRows = false;

        final TextCursor cursor = this.cursor;
        final TextCursorSavePoint save = cursor.save();
        final String token = this.readToken();

        switch (token) {
            case "true":
                includeFrozenColumnsRows = true;
                break;
            case "false":
                includeFrozenColumnsRows = false;
                break;
            default:
                save.restore();

                throw new InvalidCharacterException(
                    cursor.text(),
                    cursor.lineInfo().textOffset()
                );
        }

        return includeFrozenColumnsRows;
    }

    private final static Parser<SpreadsheetParserContext> INCLUDE_FROZEN_COLUMNS_ROW_TOKEN_PARSER = parserStringToken(SpreadsheetViewport.INCLUDE_FROZEN_COLUMNS_ROWS_STRING);

    boolean selectionToken() {
        return this.parseToken(SELECTION_TOKEN_PARSER);
    }

    private final static Parser<SpreadsheetParserContext> SELECTION_TOKEN_PARSER = parserStringToken(SpreadsheetViewport.SELECTION_STRING);

    boolean navigationToken() {
        return this.parseToken(NAVIGATION_TOKEN_PARSER);
    }

    private final static Parser<SpreadsheetParserContext> NAVIGATION_TOKEN_PARSER = parserStringToken(SpreadsheetViewport.NAVIGATIONS_STRING);

    private void parseTokenOrFail(final Parser<SpreadsheetParserContext> parser,
                                  final String label) {
        if (false == parser.parse(this.cursor, PARSER_CONTEXT).isPresent()) {
            throw new IllegalArgumentException("Missing " + label);
        }
    }

    SpreadsheetSelection spreadsheetSelection() {
        SpreadsheetSelection spreadsheetSelection = null;

        final SpreadsheetFormulaParserToken token = SpreadsheetFormulaParsers.cellOrCellRangeOrLabel()
            .parse(
                this.cursor,
                PARSER_CONTEXT
            ).orElseThrow(() -> new IllegalArgumentException("Missing selection"))
            .cast(SpreadsheetFormulaParserToken.class);
        return ((HasSpreadsheetReference<?>) token).toSpreadsheetSelection();
    }

    Optional<SpreadsheetViewportAnchor> anchor() {
        SpreadsheetViewportAnchor anchor = null;

        final TextCursorSavePoint save = this.cursor.save();
        final String token = this.readToken();
        if (false == token.isEmpty()) {
            for (final SpreadsheetViewportAnchor possible : SpreadsheetViewportAnchor.values()) {
                if (possible == SpreadsheetViewportAnchor.NONE) {
                    continue;
                }

                if (token.equals(possible.kebabText())) {
                    anchor = possible;
                    break;
                }
            }
        }

        if (null == anchor) {
            save.restore();
        }

        return Optional.ofNullable(anchor);
    }

    private String readToken() {
        final StringBuilder b = new StringBuilder();

        final TextCursor cursor = this.cursor;

        while (cursor.isNotEmpty()) {
            final char c = cursor.at();
            if ('-' == c || Character.isLetter(c)) {
                b.append(c);
                cursor.next();
                continue;
            }
            break;
        }

        return b.toString();
    }

    private boolean parseToken(final Parser<SpreadsheetParserContext> parser) {
        return parser.parse(this.cursor, PARSER_CONTEXT)
            .isPresent();
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

    SpreadsheetViewportNavigationList navigations() {
        final TextCursor cursor = this.cursor;

        final TextCursorSavePoint save = cursor.save();
        cursor.end();

        return SpreadsheetViewportNavigationList.parse(
            save.textBetween()
                .toString()
        );
    }

    final TextCursor cursor;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cursor.toString();
    }
}
