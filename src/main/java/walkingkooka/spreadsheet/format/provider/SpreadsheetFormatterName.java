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

package walkingkooka.spreadsheet.format.provider;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.plugin.PluginName;
import walkingkooka.plugin.PluginNameLike;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * The {@link Name} of a {@link SpreadsheetFormatter}. Note formatter names are case-sensitive.
 */
final public class SpreadsheetFormatterName implements PluginNameLike<SpreadsheetFormatterName> {

    public static final String HATEOS_RESOURCE_NAME_STRING = "formatter";

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

    // SpreadsheetFormatterName instances...............................................................................

    final static String AUTOMATIC_STRING = "automatic";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName AUTOMATIC = new SpreadsheetFormatterName(
        AUTOMATIC_STRING,
        null
    );

    final static String BADGE_ERROR_STRING = "badge-error";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName BADGE_ERROR = new SpreadsheetFormatterName(
        BADGE_ERROR_STRING,
        null
    );

    final static String COLLECTION_STRING = "collection";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName COLLECTION = new SpreadsheetFormatterName(
        COLLECTION_STRING,
        null
    );

    final static String CURRENCY_STRING = "currency";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName CURRENCY = new SpreadsheetFormatterName(
        CURRENCY_STRING,
        null
    );

    final static String DATE_STRING = "date";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName DATE = new SpreadsheetFormatterName(
        DATE_STRING,
        SpreadsheetPatternKind.DATE_FORMAT_PATTERN
    );

    final static String DATE_TIME_STRING = "date-time";

    /**
     * The name of the date-time {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName DATE_TIME = new SpreadsheetFormatterName(
        DATE_TIME_STRING,
        SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN
    );

    final static String DEFAULT_TEXT_STRING = "default-text";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName DEFAULT_TEXT = new SpreadsheetFormatterName(
        DEFAULT_TEXT_STRING,
        null
    );

    final static String EXPRESSION_STRING = "expression";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName EXPRESSION = new SpreadsheetFormatterName(
        EXPRESSION_STRING,
        null
    );

    final static String GENERAL_STRING = "general";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName GENERAL = new SpreadsheetFormatterName(
        GENERAL_STRING,
        null
    );

    final static String LONG_DATE_STRING = "long-date";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName LONG_DATE = new SpreadsheetFormatterName(
        LONG_DATE_STRING,
        null
    );
    
    final static String MEDIUM_DATE_STRING = "medium-date";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName MEDIUM_DATE = new SpreadsheetFormatterName(
        MEDIUM_DATE_STRING,
        null
    );
    
    final static String NUMBER_STRING = "number";

    /**
     * The name of the number {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName NUMBER = new SpreadsheetFormatterName(
        NUMBER_STRING,
        SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN
    );

    final static String PERCENT_STRING = "percent";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName PERCENT = new SpreadsheetFormatterName(
        PERCENT_STRING,
        null
    );

    final static String SCIENTIFIC_STRING = "scientific";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName SCIENTIFIC = new SpreadsheetFormatterName(
        SCIENTIFIC_STRING,
        null
    );

    final static String SHORT_DATE_STRING = "short-date";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName SHORT_DATE = new SpreadsheetFormatterName(
        SHORT_DATE_STRING,
        null
    );


    final static String SPREADSHEET_PATTERN_COLLECTION_STRING = "spreadsheet-pattern-collection";

    /**
     * The name of the date {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName SPREADSHEET_PATTERN_COLLECTION = new SpreadsheetFormatterName(
        SPREADSHEET_PATTERN_COLLECTION_STRING,
        null
    );

    final static String TEXT_STRING = "text";

    /**
     * The name of the text {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName TEXT = new SpreadsheetFormatterName(
        TEXT_STRING,
        SpreadsheetPatternKind.TEXT_FORMAT_PATTERN
    );

    final static String TIME_STRING = "time";

    /**
     * The name of the time {@link SpreadsheetFormatter}
     */
    public final static SpreadsheetFormatterName TIME = new SpreadsheetFormatterName(
        TIME_STRING,
        SpreadsheetPatternKind.TIME_FORMAT_PATTERN
    );

    /**
     * Factory that creates a {@link SpreadsheetFormatterName}
     */
    public static SpreadsheetFormatterName with(final String name) {
        Objects.requireNonNull(name, "name");

        final SpreadsheetFormatterName spreadsheetFormatterName;

        switch (name) {
            case AUTOMATIC_STRING:
                spreadsheetFormatterName = AUTOMATIC;
                break;
            case BADGE_ERROR_STRING:
                spreadsheetFormatterName = BADGE_ERROR;
                break;
            case COLLECTION_STRING:
                spreadsheetFormatterName = COLLECTION;
                break;
            case CURRENCY_STRING:
                spreadsheetFormatterName = CURRENCY;
                break;
            case DATE_STRING:
                spreadsheetFormatterName = DATE;
                break;
            case DATE_TIME_STRING:
                spreadsheetFormatterName = DATE_TIME;
                break;
            case DEFAULT_TEXT_STRING:
                spreadsheetFormatterName = DEFAULT_TEXT;
                break;
            case EXPRESSION_STRING:
                spreadsheetFormatterName = EXPRESSION;
                break;
            case GENERAL_STRING:
                spreadsheetFormatterName = GENERAL;
                break;
            case LONG_DATE_STRING:
                spreadsheetFormatterName = LONG_DATE;
                break;
            case MEDIUM_DATE_STRING:
                spreadsheetFormatterName = MEDIUM_DATE;
                break;
            case NUMBER_STRING:
                spreadsheetFormatterName = NUMBER;
                break;
            case PERCENT_STRING:
                spreadsheetFormatterName = PERCENT;
                break;
            case SCIENTIFIC_STRING:
                spreadsheetFormatterName = SCIENTIFIC;
                break;
            case SHORT_DATE_STRING:
                spreadsheetFormatterName = SHORT_DATE;
                break;
            case SPREADSHEET_PATTERN_COLLECTION_STRING:
                spreadsheetFormatterName = SPREADSHEET_PATTERN_COLLECTION;
                break;
            case TEXT_STRING:
                spreadsheetFormatterName = TEXT;
                break;
            case TIME_STRING:
                spreadsheetFormatterName = TIME;
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
        this.name = PluginName.with(name)
            .checkLength("Formatter");
        this.patternKind = patternKind;
    }

    @Override
    public String value() {
        return this.name.value();
    }

    private final PluginName name;


    // used by SpreadsheetFormatterSelector.formatter
    final SpreadsheetPatternKind patternKind;

    /**
     * Returns true if the name is actually a {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}
     */
    public boolean isSpreadsheetFormatPattern() {
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

    // SpreadsheetFormatterSelector.....................................................................................

    /**
     * Factory that creates a {@link SpreadsheetFormatterSelector} with this name and the given text.
     */
    public SpreadsheetFormatterSelector setValueText(final String text) {
        return SpreadsheetFormatterSelector.with(
            this,
            text
        );
    }
}
