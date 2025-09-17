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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.plugin.PluginName;
import walkingkooka.plugin.PluginNameLike;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * The {@link Name} of a {@link Parser}. Note parser names are case-sensitive.
 */
final public class SpreadsheetParserName implements PluginNameLike<SpreadsheetParserName> {

    public static final String HATEOS_RESOURCE_NAME_STRING = "parser";

    public static final HateosResourceName HATEOS_RESOURCE_NAME = HateosResourceName.with(HATEOS_RESOURCE_NAME_STRING);

    public static boolean isChar(final int pos,
                                 final char c) {
        return PluginNameLike.isChar(pos, c);
    }

    /**
     * The minimum valid length
     */
    public final static int MIN_LENGTH = 1;

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = PluginName.MAX_LENGTH;

    // SpreadsheetParserName instances...............................................................................

    final static String DATE_PARSER_PATTERN_STRING = "date-parse-pattern";

    /**
     * The name of the date {@link Parser}
     */
    public final static SpreadsheetParserName DATE_PARSER_PATTERN = new SpreadsheetParserName(
        DATE_PARSER_PATTERN_STRING,
        SpreadsheetPatternKind.DATE_PARSE_PATTERN
    );

    final static String DATE_TIME_PARSER_PATTERN_STRING = "date-time-parse-pattern";

    /**
     * The name of the date-time {@link Parser}
     */
    public final static SpreadsheetParserName DATE_TIME_PARSER_PATTERN = new SpreadsheetParserName(
        DATE_TIME_PARSER_PATTERN_STRING,
        SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN
    );

    final static String NUMBER_PARSER_PATTERN_STRING = "number-parse-pattern";

    /**
     * The name of the number {@link Parser}
     */
    public final static SpreadsheetParserName NUMBER_PARSER_PATTERN = new SpreadsheetParserName(
        NUMBER_PARSER_PATTERN_STRING,
        SpreadsheetPatternKind.NUMBER_PARSE_PATTERN
    );

    final static String TIME_PARSER_PATTERN_STRING = "time-parse-pattern";

    /**
     * The name of the time {@link Parser}
     */
    public final static SpreadsheetParserName TIME_PARSER_PATTERN = new SpreadsheetParserName(
        TIME_PARSER_PATTERN_STRING,
        SpreadsheetPatternKind.TIME_PARSE_PATTERN
    );

    /**
     * Factory that creates a {@link SpreadsheetParserName}
     */
    public static SpreadsheetParserName with(final String name) {
        Objects.requireNonNull(name, "name");

        final SpreadsheetParserName parserName;

        switch (name) {
            case DATE_PARSER_PATTERN_STRING:
                parserName = DATE_PARSER_PATTERN;
                break;
            case DATE_TIME_PARSER_PATTERN_STRING:
                parserName = DATE_TIME_PARSER_PATTERN;
                break;
            case NUMBER_PARSER_PATTERN_STRING:
                parserName = NUMBER_PARSER_PATTERN;
                break;
            case TIME_PARSER_PATTERN_STRING:
                parserName = TIME_PARSER_PATTERN;
                break;
            default:
                parserName = new SpreadsheetParserName(
                    name,
                    null // other SpreadsheetParserNames dont have a SpreadsheetPatternKind
                );
                break;
        }

        return parserName;
    }

    /**
     * Private constructor
     */
    private SpreadsheetParserName(final String name,
                                  final SpreadsheetPatternKind patternKind) {
        super();
        this.name = PluginName.with(name)
            .checkLength("Parser");
        this.patternKind = patternKind;
    }

    @Override
    public String value() {
        return this.name.value();
    }

    private final PluginName name;

    // used by ParserSelector.formatter
    final SpreadsheetPatternKind patternKind;

    /**
     * Returns true if the name is actually a {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern}
     */
    public boolean isSpreadsheetParsePattern() {
        return null != this.patternKind;
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetParserName &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetParserName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    // Json.............................................................................................................

    static SpreadsheetParserName unmarshall(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserName.class),
            SpreadsheetParserName::unmarshall,
            SpreadsheetParserName::marshall,
            SpreadsheetParserName.class
        );
    }

    // SpreadsheetParserSelector.....................................................................................

    /**
     * Factory that creates a {@link SpreadsheetParserSelector} with this name and the given text.
     */
    public SpreadsheetParserSelector setValueText(final String text) {
        return SpreadsheetParserSelector.with(
            this,
            text
        );
    }
}
