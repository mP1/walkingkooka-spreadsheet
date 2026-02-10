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

package walkingkooka.spreadsheet.compare.provider;

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.net.http.server.hateos.HateosResourceName;
import walkingkooka.plugin.PluginName;
import walkingkooka.plugin.PluginNameLike;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorDirection;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * The {@link Name} of a {@link SpreadsheetComparator}. Note comparator names are case-sensitive.
 */
final public class SpreadsheetComparatorName implements PluginNameLike<SpreadsheetComparatorName> {

    public static final String HATEOS_RESOURCE_NAME_STRING = "comparator";

    public static final HateosResourceName HATEOS_RESOURCE_NAME = HateosResourceName.with(HATEOS_RESOURCE_NAME_STRING);

    public static boolean isChar(final int pos,
                                 final char c) {
        return PluginName.isChar(pos, c);
    }

    /**
     * The minimum valid length
     */
    public final static int MIN_LENGTH = 1;

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = PluginName.MAX_LENGTH;

    // constants........................................................................................................

    private final static String DATE_STRING = "date";

    public final static SpreadsheetComparatorName DATE = registerConstant(DATE_STRING);

    private final static String DATE_TIME_STRING = "date-time";

    public final static SpreadsheetComparatorName DATE_TIME = registerConstant(DATE_TIME_STRING);

    private final static String DAY_OF_MONTH_STRING = "day-of-month";

    public final static SpreadsheetComparatorName DAY_OF_MONTH = registerConstant(DAY_OF_MONTH_STRING);

    private final static String DAY_OF_WEEK_STRING = "day-of-week";

    public final static SpreadsheetComparatorName DAY_OF_WEEK = registerConstant(DAY_OF_WEEK_STRING);

    private final static String HOUR_OF_AMPM_STRING = "hour-of-am-pm";

    public final static SpreadsheetComparatorName HOUR_OF_AMPM = registerConstant(HOUR_OF_AMPM_STRING);

    private final static String HOUR_OF_DAY_STRING = "hour-of-day";

    public final static SpreadsheetComparatorName HOUR_OF_DAY = registerConstant(HOUR_OF_DAY_STRING);

    private final static String MINUTE_OF_HOUR_STRING = "minute-of-hour";

    public final static SpreadsheetComparatorName MINUTE_OF_HOUR = registerConstant(MINUTE_OF_HOUR_STRING);

    private final static String MONTH_OF_YEAR_STRING = "month-of-year";

    public final static SpreadsheetComparatorName MONTH_OF_YEAR = registerConstant(MONTH_OF_YEAR_STRING);

    private final static String NANO_OF_SECOND_STRING = "nano-of-second";

    public final static SpreadsheetComparatorName NANO_OF_SECOND = registerConstant(NANO_OF_SECOND_STRING);

    private final static String NUMBER_STRING = "number";

    public final static SpreadsheetComparatorName NUMBER = registerConstant(NUMBER_STRING);

    private final static String SECONDS_OF_MINUTE_STRING = "seconds-of-minute";

    public final static SpreadsheetComparatorName SECONDS_OF_MINUTE = registerConstant(SECONDS_OF_MINUTE_STRING);

    private final static String TEXT_STRING = "text";

    public final static SpreadsheetComparatorName TEXT = registerConstant(TEXT_STRING);

    private final static String TEXT_CASE_INSENSITIVE_STRING = "text-case-insensitive";

    public final static SpreadsheetComparatorName TEXT_CASE_INSENSITIVE = registerConstant(TEXT_CASE_INSENSITIVE_STRING);

    private final static String TIME_STRING = "time";

    public final static SpreadsheetComparatorName TIME = registerConstant(TIME_STRING);

    private final static String YEAR_STRING = "year";

    public final static SpreadsheetComparatorName YEAR = registerConstant(YEAR_STRING);

    private static SpreadsheetComparatorName registerConstant(final String name) {
        return new SpreadsheetComparatorName(
            PluginName.with(name)
        );
    }

    /**
     * Factory that creates a {@link SpreadsheetComparatorName}
     */
    public static SpreadsheetComparatorName with(final String name) {
        Objects.requireNonNull(name, "name");

        final SpreadsheetComparatorName spreadsheetComparatorName;

        switch (name) {
            case DATE_STRING:
                spreadsheetComparatorName = DATE;
                break;
            case DATE_TIME_STRING:
                spreadsheetComparatorName = DATE_TIME;
                break;
            case DAY_OF_MONTH_STRING:
                spreadsheetComparatorName = DAY_OF_MONTH;
                break;
            case DAY_OF_WEEK_STRING:
                spreadsheetComparatorName = DAY_OF_WEEK;
                break;
            case HOUR_OF_AMPM_STRING:
                spreadsheetComparatorName = HOUR_OF_AMPM;
                break;
            case HOUR_OF_DAY_STRING:
                spreadsheetComparatorName = HOUR_OF_DAY;
                break;
            case MINUTE_OF_HOUR_STRING:
                spreadsheetComparatorName = MINUTE_OF_HOUR;
                break;
            case MONTH_OF_YEAR_STRING:
                spreadsheetComparatorName = MONTH_OF_YEAR;
                break;
            case NANO_OF_SECOND_STRING:
                spreadsheetComparatorName = NANO_OF_SECOND;
                break;
            case NUMBER_STRING:
                spreadsheetComparatorName = NUMBER;
                break;
            case SECONDS_OF_MINUTE_STRING:
                spreadsheetComparatorName = SECONDS_OF_MINUTE;
                break;
            case TEXT_STRING:
                spreadsheetComparatorName = TEXT;
                break;
            case TEXT_CASE_INSENSITIVE_STRING:
                spreadsheetComparatorName = TEXT_CASE_INSENSITIVE;
                break;
            case TIME_STRING:
                spreadsheetComparatorName = TIME;
                break;
            case YEAR_STRING:
                spreadsheetComparatorName = YEAR;
                break;
            default:
                spreadsheetComparatorName = new SpreadsheetComparatorName(
                    PluginName.with(name)
                        .checkLength("comparator")
                );
                break;
        }

        return spreadsheetComparatorName;
    }

    /**
     * Private constructor
     */
    private SpreadsheetComparatorName(final PluginName name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name.value();
    }

    private final PluginName name;

    public final static String REVERSED = "-reversed";

    /**
     * Appends the {@link #REVERSED} suffix or removes it.
     */
    public SpreadsheetComparatorName reversed() {
        final String name = this.name.value();
        final String reversed;

        if (name.endsWith(REVERSED)) {
            reversed = CharSequences.subSequence(
                name,
                0,
                -REVERSED.length()
            ).toString();
        } else {
            reversed = name.concat(REVERSED);
        }

        return with(reversed);
    }

    /**
     * The inverse of {@link #reversed()} removes the {@link #REVERSED} if necessary.
     */
    public SpreadsheetComparatorName unreversed() {
        final SpreadsheetComparatorName unreversed;

        final String name = this.name.value();
        if (name.endsWith(REVERSED)) {
            unreversed = with(
                CharSequences.subSequence(
                    name,
                    0,
                    -REVERSED.length()
                ).toString()
            );
        } else {
            unreversed = this;
        }

        return unreversed;
    }

    /**
     * Create a {@link SpreadsheetComparatorNameAndDirection} using this name and the given {@link SpreadsheetComparatorDirection direction}
     */
    public SpreadsheetComparatorNameAndDirection setDirection(final SpreadsheetComparatorDirection direction) {
        return SpreadsheetComparatorNameAndDirection.with(
            this,
            direction
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetComparatorName &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetComparatorName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name.toString();
    }

    // Json.............................................................................................................

    static SpreadsheetComparatorName unmarshall(final JsonNode node,
                                                final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetComparatorName.class),
            SpreadsheetComparatorName::unmarshall,
            SpreadsheetComparatorName::marshall,
            SpreadsheetComparatorName.class
        );
    }
}
