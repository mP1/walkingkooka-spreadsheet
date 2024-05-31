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

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.spreadsheet.component.SpreadsheetComponentName;
import walkingkooka.spreadsheet.component.SpreadsheetComponentNameLike;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * The {@link Name} of a {@link SpreadsheetFormatter}. Note formatter names are case-sensitive.
 */
final public class SpreadsheetFormatterName implements SpreadsheetComponentNameLike<SpreadsheetFormatterName> {

    public static boolean isChar(final int pos,
                                 final char c) {
        return SpreadsheetComponentNameLike.isChar(pos, c);
    }

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = SpreadsheetComponentName.MAX_LENGTH;

    // SpreadsheetFormatterName instances...............................................................................

    private final static String DATE_FORMAT_STRING = "date-format";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName DATE_FORMAT = new SpreadsheetFormatterName(
            DATE_FORMAT_STRING,
            SpreadsheetPatternKind.DATE_FORMAT_PATTERN
    );

    private final static String DATE_TIME_FORMAT_STRING = "date-time-format";

    /**
     * The name of the date-time {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName DATE_TIME_FORMAT = new SpreadsheetFormatterName(
            DATE_TIME_FORMAT_STRING,
            SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN
    );

    private final static String NUMBER_FORMAT_STRING = "number-format";

    /**
     * The name of the number {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName NUMBER_FORMAT = new SpreadsheetFormatterName(
            NUMBER_FORMAT_STRING,
            SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN
    );
    private final static String TEXT_FORMAT_STRING = "text-format";

    /**
     * The name of the text {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName TEXT_FORMAT = new SpreadsheetFormatterName(
            TEXT_FORMAT_STRING,
            SpreadsheetPatternKind.TEXT_FORMAT_PATTERN
    );

    private final static String TIME_FORMAT_STRING = "time-format";

    /**
     * The name of the time {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName TIME_FORMAT = new SpreadsheetFormatterName(
            TIME_FORMAT_STRING,
            SpreadsheetPatternKind.TIME_FORMAT_PATTERN
    );

    /**
     * Factory that creates a {@link SpreadsheetFormatterName}
     */
    public static SpreadsheetFormatterName with(final String name) {
        Objects.requireNonNull(name, "name");

        final SpreadsheetFormatterName spreadsheetFormatterName;

        switch (name) {
            case DATE_FORMAT_STRING:
                spreadsheetFormatterName = DATE_FORMAT;
                break;
            case DATE_TIME_FORMAT_STRING:
                spreadsheetFormatterName = DATE_TIME_FORMAT;
                break;
            case NUMBER_FORMAT_STRING:
                spreadsheetFormatterName = NUMBER_FORMAT;
                break;
            case TEXT_FORMAT_STRING:
                spreadsheetFormatterName = TEXT_FORMAT;
                break;
            case TIME_FORMAT_STRING:
                spreadsheetFormatterName = TIME_FORMAT;
                break;
            default:
                spreadsheetFormatterName = new SpreadsheetFormatterName(
                        name,
                        null // other SpreadsheetFormatterNames dont have a SpreadsheetPatternKind
                );
                break;
        }

        return spreadsheetFormatterName;
    }

    /**
     * Private constructor
     */
    private SpreadsheetFormatterName(final String name,
                                     final SpreadsheetPatternKind patternKind) {
        super();
        this.name = SpreadsheetComponentName.with(name);
        this.patternKind = patternKind;
    }

    @Override
    public String value() {
        return this.name.value();
    }

    private final SpreadsheetComponentName name;


    // used by SpreadsheetFormatterSelector.formatter
    final SpreadsheetPatternKind patternKind;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetFormatterName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetFormatterName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    // Json.............................................................................................................

    static SpreadsheetFormatterName unmarshall(final JsonNode node,
                                               final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetFormatterName.class),
                SpreadsheetFormatterName::unmarshall,
                SpreadsheetFormatterName::marshall,
                SpreadsheetFormatterName.class
        );
    }
}
