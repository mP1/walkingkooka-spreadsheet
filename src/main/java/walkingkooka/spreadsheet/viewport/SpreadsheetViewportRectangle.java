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
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.DoubleParserToken;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.util.Objects;

/**
 * Represents a rectangle selection of cells, with the top left being the home and pixels measuring the width and height.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
public final class SpreadsheetViewportRectangle implements Comparable<SpreadsheetViewportRectangle>,
    TreePrintable,
    HasUrlFragment {

    final static char SEPARATOR_CHAR = ':';

    final static CharacterConstant SEPARATOR = CharacterConstant.with(SEPARATOR_CHAR);

    /**
     * Parses the width and height parse text in the following format.
     * <pre>
     * cell/label SEPARATOR width SEPARATOR height
     * </pre>
     * Where width and height are decimal numbers.
     */
    public static SpreadsheetViewportRectangle parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        final String[] tokens = text.split(SEPARATOR.string());
        switch (tokens.length) {
            case 3:
                break;
            default:
                throw new IllegalArgumentException("Expected 3 tokens in " + CharSequences.quoteAndEscape(text));
        }

        final SpreadsheetCellReference home;
        try {
            home = SpreadsheetSelection.parseCell(tokens[0]);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid home in " + CharSequences.quoteAndEscape(text));
        }

        if (!(home.isCell() || home.isLabelName())) {
            throw new IllegalArgumentException("home must be cell or label got " + home);
        }

        return with(
            home,
            parseDouble(tokens[1], "width", text),
            parseDouble(tokens[2], "height", text)
        );
    }

    private static double parseDouble(final String token,
                                      final String label,
                                      final String text) {
        try {
            return Double.parseDouble(token);
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException("Invalid " + label + " in " + CharSequences.quoteAndEscape(text));
        }
    }

    /**
     * Parses the width and height parse text in the following format.
     * <pre>
     * /home/A1/width/200/height/300
     * </pre>
     * Where width and height are decimal numbers.
     */
    public static SpreadsheetViewportRectangle fromUrlFragment(final UrlFragment urlFragment) {
        Objects.requireNonNull(urlFragment, "urlFragment");

        final String text = urlFragment.value();
        final TextCursor cursor = TextCursors.charSequence(text);
        
        SpreadsheetCellReference home = null;
        double width = 0;
        double height = 0;

        final int MODE_SLASH_BEFORE_HOME_TOKEN = 1;
        final int MODE_HOME_TOKEN = 2;
        final int MODE_SLASH_BEFORE_CELL_REFERENCE = 3;
        final int MODE_HOME_TOKEN_CELL_REFERENCE = 4;

        final int MODE_SLASH_BEFORE_WIDTH_TOKEN = 5;
        final int MODE_WIDTH_TOKEN = 6;
        final int MODE_SLASH_BEFORE_WIDTH_VALUE = 7;
        final int MODE_WIDTH_VALUE = 8;

        final int MODE_SLASH_BEFORE_HEIGHT_TOKEN = 9;
        final int MODE_HEIGHT_TOKEN = 10;
        final int MODE_SLASH_BEFORE_HEIGHT_VALUE = 11;
        final int MODE_HEIGHT_VALUE = 12;

        final int MODE_FINISHED = 13;
        
        int mode = MODE_SLASH_BEFORE_HOME_TOKEN;
        while (cursor.isNotEmpty()) {
            switch (mode) {
                case MODE_SLASH_BEFORE_HOME_TOKEN:
                    SLASH_PARSER.parse(cursor, PARSER_CONTEXT);
                    mode = MODE_HOME_TOKEN;
                    break;
                case MODE_HOME_TOKEN:
                    parseTokenOrFail(cursor, HOME_TOKEN_PARSER, HOME_TOKEN);
                    mode = MODE_SLASH_BEFORE_CELL_REFERENCE;
                    break;
                case MODE_SLASH_BEFORE_CELL_REFERENCE:
                    SLASH_PARSER.parse(cursor, PARSER_CONTEXT);
                    mode = MODE_HOME_TOKEN_CELL_REFERENCE;
                    break;
                case MODE_HOME_TOKEN_CELL_REFERENCE:
                    home = SpreadsheetFormulaParsers.cell()
                        .parse(
                            cursor,
                            PARSER_CONTEXT
                        ).orElseThrow(() -> new IllegalArgumentException("Missing home"))
                        .cast(CellSpreadsheetFormulaParserToken.class)
                        .cell();
                    mode = MODE_SLASH_BEFORE_WIDTH_TOKEN;
                    break;

                case MODE_SLASH_BEFORE_WIDTH_TOKEN:
                    SLASH_PARSER.parse(cursor, PARSER_CONTEXT);
                    mode = MODE_WIDTH_TOKEN;
                    break;
                case MODE_WIDTH_TOKEN:
                    parseTokenOrFail(cursor, WIDTH_TOKEN_PARSER, WIDTH_TOKEN);
                    mode = MODE_SLASH_BEFORE_WIDTH_VALUE;
                    break;
                case MODE_SLASH_BEFORE_WIDTH_VALUE:
                    SLASH_PARSER.parse(cursor, PARSER_CONTEXT);
                    mode = MODE_WIDTH_VALUE;
                    break;
                case MODE_WIDTH_VALUE:
                    width = parseDoubleOrFail(
                        cursor,
                        WIDTH_TOKEN
                    );
                    mode = MODE_SLASH_BEFORE_HEIGHT_TOKEN;
                    break;

                case MODE_SLASH_BEFORE_HEIGHT_TOKEN:
                    SLASH_PARSER.parse(cursor, PARSER_CONTEXT);
                    mode = MODE_HEIGHT_TOKEN;
                    break;
                case MODE_HEIGHT_TOKEN:
                    parseTokenOrFail(cursor, HEIGHT_TOKEN_PARSER, HEIGHT_TOKEN);
                    mode = MODE_SLASH_BEFORE_HEIGHT_VALUE;
                    break;
                case MODE_SLASH_BEFORE_HEIGHT_VALUE:
                    SLASH_PARSER.parse(cursor, PARSER_CONTEXT);
                    mode = MODE_HEIGHT_VALUE;
                    break;
                case MODE_HEIGHT_VALUE:
                    height = parseDoubleOrFail(
                        cursor,
                        HEIGHT_TOKEN
                    );
                    mode = MODE_FINISHED;
                    break;

                default:
                    throw new IllegalArgumentException("Invalid mode: " + mode);
            }
        }

        switch (mode) {
            case MODE_SLASH_BEFORE_HOME_TOKEN:
            case MODE_HOME_TOKEN:
            case MODE_SLASH_BEFORE_CELL_REFERENCE:
                throw new IllegalArgumentException("Missing home");
            case MODE_HOME_TOKEN_CELL_REFERENCE:
            case MODE_SLASH_BEFORE_WIDTH_TOKEN:
            case MODE_WIDTH_TOKEN:
            case MODE_SLASH_BEFORE_WIDTH_VALUE:
                throw new IllegalArgumentException("Missing width");
            case MODE_WIDTH_VALUE:
            case MODE_SLASH_BEFORE_HEIGHT_TOKEN:
            case MODE_HEIGHT_TOKEN:
            case MODE_SLASH_BEFORE_HEIGHT_VALUE:
            case MODE_HEIGHT_VALUE:
                throw new IllegalArgumentException("Missing height");
            case MODE_FINISHED:
                return with(
                    home,
                    width,
                    height
                );
            default:
                throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }

    private final static Parser<SpreadsheetParserContext> SLASH_PARSER = Parsers.string("/", CaseSensitivity.SENSITIVE)
        .orReport(ParserReporters.basic())
        .cast();

    private final static String HOME_TOKEN = "home";
    private final static Parser<SpreadsheetParserContext> HOME_TOKEN_PARSER = Parsers.string(HOME_TOKEN, CaseSensitivity.SENSITIVE);

    private final static String WIDTH_TOKEN = "width";
    private final static Parser<SpreadsheetParserContext> WIDTH_TOKEN_PARSER = Parsers.string(WIDTH_TOKEN, CaseSensitivity.SENSITIVE);

    private final static String HEIGHT_TOKEN = "height";
    private final static Parser<SpreadsheetParserContext> HEIGHT_TOKEN_PARSER = Parsers.string(HEIGHT_TOKEN, CaseSensitivity.SENSITIVE);

    private static void parseTokenOrFail(final TextCursor cursor,
                                         final Parser<SpreadsheetParserContext> parser,
                                         final String label) {
        if(false == parser.parse(cursor, PARSER_CONTEXT).isPresent()) {
            throw new IllegalArgumentException("Missing " + label);
        }
    }

    /**
     * Used to parse the width or height values within a {@link UrlFragment}.
     */
    private static double parseDoubleOrFail(final TextCursor cursor,
                                            final String label) {
        return Parsers.doubleParser()
            .parse(
                cursor,
                PARSER_CONTEXT
            ).orElseThrow(() -> new IllegalArgumentException("Missing " + label))
            .cast(DoubleParserToken.class)
            .value();
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

    /**
     * Factory that creates a new {@link SpreadsheetViewportRectangle}.
     */
    public static SpreadsheetViewportRectangle with(final SpreadsheetCellReference home,
                                                    final double width,
                                                    final double height) {
        return new SpreadsheetViewportRectangle(
            checkHome(home),
            checkWidth(width),
            checkHeight(height)
        );
    }

    private static double checkWidth(final double width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width " + width + " <= 0");
        }
        return width;
    }

    private static double checkHeight(final double height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height " + height + " <= 0");
        }
        return height;
    }

    private SpreadsheetViewportRectangle(final SpreadsheetCellReference home,
                                         final double width,
                                         final double height) {
        super();
        this.home = home.toRelative();
        this.width = width;
        this.height = height;
    }

    // SpreadsheetViewport..............................................................................................

    /**
     * Creates a {@link SpreadsheetViewport} using this cell as the home.
     */
    public SpreadsheetViewport viewport() {
        return SpreadsheetViewport.with(this);
    }

    /**
     * Tests if the offset (assumed to be relative to {@link #home} is within this rectangle.
     * This will be used to test or load cells to fill a rectangular region or window of the spreadsheet being displayed.
     */
    public boolean test(final double x,
                        final double y) {
        return x >= 0 && x <= this.width &&
            y >= 0 && y <= this.height;
    }

    // properties.......................................................................................................

    public SpreadsheetCellReference home() {
        return this.home;
    }

    public SpreadsheetViewportRectangle setHome(final SpreadsheetCellReference home) {
        checkHome(home);

        return this.home.equals(home) ?
            this :
            new SpreadsheetViewportRectangle(
                home,
                this.width,
                this.height
            );
    }

    private final SpreadsheetCellReference home;

    private static SpreadsheetCellReference checkHome(final SpreadsheetCellReference home) {
        return Objects.requireNonNull(home, "home");
    }

    public double width() {
        return this.width;
    }

    public SpreadsheetViewportRectangle setWidth(final double width) {
        checkWidth(width);

        return this.width == width ?
            this :
            new SpreadsheetViewportRectangle(
                this.home,
                width,
                this.height
            );
    }

    private final double width;

    public double height() {
        return this.height;
    }

    public SpreadsheetViewportRectangle setHeight(final double height) {
        checkHeight(height);

        return this.height == height ?
            this :
            new SpreadsheetViewportRectangle(
                this.home,
                this.width,
                height
            );
    }

    private final double height;

    // Object............................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.home,
            this.width,
            this.height
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetViewportRectangle &&
                this.equals0((SpreadsheetViewportRectangle) other);
    }

    private boolean equals0(final SpreadsheetViewportRectangle other) {
        return this.home.equals(other.home) &&
            this.width == other.width &&
            this.height == other.height;
    }

    @Override
    public String toString() {
        return "home: " + this.home +
            " width: " + this.width +
            " height: " + this.height;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetViewportRectangle other) {
        throw new UnsupportedOperationException(); // required by HateosResourceHandler
    }

    // json.............................................................................................................

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetViewportRectangle.class),
            SpreadsheetViewportRectangle::unmarshall,
            SpreadsheetViewportRectangle::marshall,
            SpreadsheetViewportRectangle.class
        );
    }

    public JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonObject.string(
            this.home.toString() +
                SEPARATOR +
                this.width +
                SEPARATOR +
                this.height
        );
    }

    static SpreadsheetViewportRectangle unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }


    // TreePrintable....................................................................................................
    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.print("home: ");
        printer.println(this.home.toString());

        printer.print("width: ");
        printer.println(String.valueOf(this.width));

        printer.print("height: ");
        printer.println(String.valueOf(this.height));
    }

    // UrlFragment......................................................................................................

    // /home/A1/width/200/height/300
    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.SLASH.append(HOME)
            .appendSlashThen(this.home.urlFragment())
            .appendSlashThen(WIDTH)
            .appendSlashThen(
                UrlFragment.with(
                    toStringWithoutExtraTrailingZero(this.width)
                )
            ).appendSlashThen(HEIGHT)
            .appendSlashThen(
                UrlFragment.with(
                    toStringWithoutExtraTrailingZero(this.height)
                )
            );
    }

    private static String toStringWithoutExtraTrailingZero(final double value) {
        final String toString = String.valueOf(value);
        return toString.endsWith(".0") ?
            toString.substring(
                0,
                toString.length() - 2
            ) :
            toString;
    }

    private final static UrlFragment HOME = UrlFragment.with("home");
    private final static UrlFragment WIDTH = UrlFragment.with("width");
    private final static UrlFragment HEIGHT = UrlFragment.with("height");
}
