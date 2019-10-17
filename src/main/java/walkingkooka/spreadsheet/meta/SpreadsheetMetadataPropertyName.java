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

package walkingkooka.spreadsheet.meta;

import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.naming.Name;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberParsePatterns;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * The {@link Name} of metadata property, used to fetch a value given a name.
 */
public abstract class SpreadsheetMetadataPropertyName<T> implements Name, Comparable<SpreadsheetMetadataPropertyName<?>> {

    // constants

    private final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

    /**
     * An {@link IntPredicate} that tests true for unsigned integers.
     */
    private final static IntPredicate POSITIVE_INTEGER = (v) -> v >= 0;

    /**
     * A read only cache of already prepared {@link SpreadsheetMetadataPropertyName names}..
     */
    final static Map<String, SpreadsheetMetadataPropertyName<?>> CONSTANTS = Maps.sorted(SpreadsheetMetadataPropertyName.CASE_SENSITIVITY.comparator());

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link Character value}.
     */
    private static SpreadsheetMetadataPropertyName<Character> registerCharacterConstant(final String name,
                                                                                        final BiConsumer<Character, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.character(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link LocalDateTime value}.
     */
    private static SpreadsheetMetadataPropertyName<LocalDateTime> registerDateTimeConstant(final String name,
                                                                                           final BiConsumer<LocalDateTime, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.localDateTime(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link EmailAddress value}.
     */
    private static SpreadsheetMetadataPropertyName<EmailAddress> registerEmailAddressConstant(final String name,
                                                                                              final BiConsumer<EmailAddress, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.emailAddress(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a positive {@link Integer value}.
     */
    private static SpreadsheetMetadataPropertyName<Integer> registerIntegerConstant(final String name,
                                                                                    final IntPredicate predicate,
                                                                                    final BiConsumer<Integer, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.integer(predicate),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a positive {@link Long value}.
     */
    private static SpreadsheetMetadataPropertyName<Long> registerLongConstant(final String name,
                                                                              final LongPredicate predicate,
                                                                              final BiConsumer<Long, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name,
                SpreadsheetMetadataPropertyValueHandler.longHandler(predicate),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link String value}.
     */
    private static SpreadsheetMetadataPropertyName<String> registerStringConstant(final String name,
                                                                                  final Predicate<String> predicate,
                                                                                  final BiConsumer<String, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name,
                SpreadsheetMetadataPropertyValueHandler.string(predicate),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName}.
     */
    private static <T> SpreadsheetMetadataPropertyName<T> registerConstant(final String name,
                                                                           final SpreadsheetMetadataPropertyValueHandler<T> handler,
                                                                           final BiConsumer<T, SpreadsheetMetadataVisitor> visitor) {
        final SpreadsheetMetadataPropertyName<T> spreadsheetMetadataName = SpreadsheetMetadataPropertyNameBasic.with(name,
                handler,
                visitor);
        SpreadsheetMetadataPropertyName.CONSTANTS.put(name, spreadsheetMetadataName);
        return spreadsheetMetadataName;
    }

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creator {@link EmailAddress}</code>
     */
    public final static SpreadsheetMetadataPropertyName<EmailAddress> CREATOR = registerEmailAddressConstant("creator",
            (e, v) -> v.visitCreator(e));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>creation {@link LocalDateTime}</code>
     */
    public final static SpreadsheetMetadataPropertyName<LocalDateTime> CREATE_DATE_TIME = registerDateTimeConstant("create-date-time",
            (d, v) -> v.visitCreateDateTime(d));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>currency {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> CURRENCY_SYMBOL = registerStringConstant("currency-symbol",
            (s) -> s.length() > 0,
            (c, v) -> v.visitCurrencySymbol(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-format-pattern {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetDateFormatPattern> DATE_FORMAT_PATTERN = registerConstant("date-format-pattern",
            SpreadsheetMetadataPropertyValueHandler.dateFormatPattern(),
            (p, v) -> v.visitDateFormatPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-parse-patterns {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetDateParsePatterns> DATE_PARSE_PATTERNS = registerConstant("date-parse-pattern",
            SpreadsheetMetadataPropertyValueHandler.dateParsePatterns(),
            (p, v) -> v.visitDateParsePatterns(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-offset {@link Long}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Long> DATETIME_OFFSET = registerLongConstant("date-time-offset",
            (v) -> true,
            (c, v) -> v.visitDateTimeOffset(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-format-pattern {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetDateTimeFormatPattern> DATETIME_FORMAT_PATTERN = registerConstant("date-time-format-pattern",
            SpreadsheetMetadataPropertyValueHandler.dateTimeFormatPattern(),
            (p, v) -> v.visitDateTimeFormatPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-parse-patterns</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetDateTimeParsePatterns> DATETIME_PARSE_PATTERNS = registerConstant("date-time-parse-patterns",
            SpreadsheetMetadataPropertyValueHandler.dateTimeParsePatterns(),
            (p, v) -> v.visitDateTimeParsePatterns(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>decimal-separator {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> DECIMAL_SEPARATOR = registerCharacterConstant("decimal-separator",
            (c, v) -> v.visitDecimalSeparator(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>exponent-symbol {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> EXPONENT_SYMBOL = registerCharacterConstant("exponent-symbol",
            (c, v) -> v.visitExponentSymbol(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>grouping-separator {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> GROUPING_SEPARATOR = registerCharacterConstant("grouping-separator",
            (c, v) -> v.visitGroupingSeparator(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>{@link Locale}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Locale> LOCALE = registerConstant("locale",
            SpreadsheetMetadataPropertyValueHandler.locale(),
            (l, v) -> v.visitLocale(l));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>last modified by {@link EmailAddress}</code>
     */
    public final static SpreadsheetMetadataPropertyName<EmailAddress> MODIFIED_BY = registerEmailAddressConstant("modified-by",
            (e, v) -> v.visitModifiedBy(e));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>modified {@link LocalDateTime}</code>
     */
    public final static SpreadsheetMetadataPropertyName<LocalDateTime> MODIFIED_DATE_TIME = registerDateTimeConstant("modified-date-time",
            (d, v) -> v.visitModifiedDateTime(d));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>negative-sign {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> NEGATIVE_SIGN = registerCharacterConstant("negative-sign",
            (c, v) -> v.visitNegativeSign(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>number-format-pattern</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetNumberFormatPattern> NUMBER_FORMAT_PATTERN = registerConstant("number-format-pattern",
            SpreadsheetMetadataPropertyValueHandler.numberFormatPattern(),
            (p, v) -> v.visitNumberFormatPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>number-parse-pattern</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetNumberParsePatterns> NUMBER_PARSE_PATTERNS = registerConstant("number-parse-patterns",
            SpreadsheetMetadataPropertyValueHandler.numberParsePatterns(),
            (p, v) -> v.visitNumberParsePatterns(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>percentage-symbol {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> PERCENTAGE_SYMBOL = registerCharacterConstant("percentage-symbol",
            (c, v) -> v.visitPercentageSymbol(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>positive-sign {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> POSITIVE_SIGN = registerCharacterConstant("positive-sign",
            (c, v) -> v.visitPositiveSign(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>rounding-mode {@link RoundingMode}</code>
     */
    public final static SpreadsheetMetadataPropertyName<RoundingMode> ROUNDING_MODE = registerConstant("rounding-mode",
            SpreadsheetMetadataPropertyValueHandler.roundingMode(),
            (c, v) -> v.visitRoundingMode(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>precision {@link Integer}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Integer> PRECISION = registerIntegerConstant("precision",
            POSITIVE_INTEGER,
            (c, v) -> v.visitPrecision(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheet-id {@link SpreadsheetId}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetId> SPREADSHEET_ID = registerConstant("spreadsheet-id",
            SpreadsheetMetadataPropertyValueHandler.spreadsheetId(),
            (e, v) -> v.visitSpreadsheetId(e));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>text-format-pattern {@link SpreadsheetFormatPattern}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetTextFormatPattern> TEXT_FORMAT_PATTERN = registerConstant("text-format-pattern",
            SpreadsheetMetadataPropertyValueHandler.textFormatPattern(),
            (p, v) -> v.visitTextFormatPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>time-format-pattern {@link SpreadsheetFormatPattern}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetTimeFormatPattern> TIME_FORMAT_PATTERN = registerConstant("time-format-pattern",
            SpreadsheetMetadataPropertyValueHandler.timeFormatPattern(),
            (p, v) -> v.visitTimeFormatPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>time-parse-patterns</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetTimeParsePatterns> TIME_PARSE_PATTERNS = registerConstant("time-parse-patterns",
            SpreadsheetMetadataPropertyValueHandler.timeParsePatterns(),
            (p, v) -> v.visitTimeParsePatterns(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>two-digit-year {@link SpreadsheetFormatPattern}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Integer> TWO_DIGIT_YEAR = registerIntegerConstant("two-digit-year",
            POSITIVE_INTEGER,
            (two, v) -> v.visitTwoDigitYear(two));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>width {@link Integer}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Integer> WIDTH = registerIntegerConstant("width",
            POSITIVE_INTEGER,
            (c, v) -> v.visitWidth(c));

    /**
     * Factory that assumes a valid {@link SpreadsheetMetadataPropertyName} or fails.
     */
    public static SpreadsheetMetadataPropertyName with(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        SpreadsheetMetadataPropertyName propertyName = CONSTANTS.get(name);
        if (null == propertyName) {
            if (false == name.startsWith(COLOR_PREFIX) || name.length() == COLOR_PREFIX.length()) {
                throw new IllegalArgumentException("Unknown metadata property name " + CharSequences.quoteAndEscape(name));
            }

            final String after = name.substring(COLOR_PREFIX.length());

            // name dash color is a numbered color, named dash letter is a named color
            try {
                if (Character.isLetter(after.charAt(0))) {
                    propertyName = namedColor(SpreadsheetColorName.with(after));
                } else {
                    propertyName = numberedColor(Integer.parseInt(after));
                }
            } catch (final RuntimeException cause) {
                throw new IllegalArgumentException("Invalid metadata property name " + CharSequences.quoteAndEscape(name), cause);
            }
        }
        return propertyName;
    }

    final static String COLOR_PREFIX = "color-";

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyName} for a {@link SpreadsheetColorName named} {@link Color}.
     */
    public static SpreadsheetMetadataPropertyName<Color> namedColor(final SpreadsheetColorName name) {
        return SpreadsheetMetadataPropertyNameNamedColor.withColorName(name);
    }

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyName} for a numbered {@link Color}.
     */
    public static SpreadsheetMetadataPropertyName<Color> numberedColor(final int number) {
        return SpreadsheetMetadataPropertyNameNumberedColor.withNumber(number);
    }

    /**
     * Package private constructor use factory.
     */
    SpreadsheetMetadataPropertyName(final String name) {
        super();
        this.name = name;
        this.jsonNodeName = JsonNodeName.with(name);
    }

    @Override
    public final String value() {
        return this.name;
    }

    final String name;

    final JsonNodeName jsonNodeName;

    /**
     * Validates the value.
     */
    public T checkValue(final Object value) {
        return this.handler().check(value, this);
    }

    abstract SpreadsheetMetadataPropertyValueHandler<T> handler();

    // SpreadsheetMetadataVisitor.......................................................................................

    /**
     * Dispatches to the appropriate {@link SpreadsheetMetadataVisitor} visit method.
     */
    abstract void accept(final Object value, final SpreadsheetMetadataVisitor visitor);

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.caseSensitivity().hash(this.name);
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                this.canBeEqual(other) &&
                        this.equals0((SpreadsheetMetadataPropertyName) other);
    }

    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final SpreadsheetMetadataPropertyName other) {
        return this.caseSensitivity().equals(this.name, other.name);
    }

    @Override
    public final String toString() {
        return this.value();
    }

    // HasCaseSensitivity...............................................................................................

    /**
     * Used during hashing and equality checks.
     */
    @Override
    public CaseSensitivity caseSensitivity() {
        return CASE_SENSITIVITY;
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final SpreadsheetMetadataPropertyName<?> other) {
        return this.caseSensitivity().comparator().compare(this.name, other.name);
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that retrieves a {@link SpreadsheetMetadataPropertyName} from a {@link JsonNode#name()}.
     */
    static SpreadsheetMetadataPropertyName<?> unmarshallName(final JsonNode node) {
        return with(node.name().value());
    }

    /**
     * Force class initialization of the following types which will ensure they also {@link walkingkooka.tree.json.marshall.JsonNodeContext#register(String, BiFunction, BiFunction, Class, Class[])}
     */
    static {
        Color.BLACK.alpha();
        EmailAddress.tryParse("user@example.com");
        SpreadsheetPattern.parseNumberFormatPattern(" ");
        SpreadsheetId.with(0);
    }
}
