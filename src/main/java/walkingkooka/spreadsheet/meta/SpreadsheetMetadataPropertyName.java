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
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeName;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The {@link Name} of metadata property, used to fetch a value given a name.
 */
public abstract class SpreadsheetMetadataPropertyName<T> implements Name, Comparable<SpreadsheetMetadataPropertyName<?>> {

    // constants

    final static CaseSensitivity CASE_SENSITIVITY = CaseSensitivity.SENSITIVE;

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
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link Long date time offset}.
     */
    private static SpreadsheetMetadataPropertyName<Long> registerDateTimeOffsetConstant(final String name,
                                                                                        final BiConsumer<Long, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.dateTimeOffset(),
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
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link Locale value}.
     */
    private static SpreadsheetMetadataPropertyName<Locale> registerLocaleConstant(final String name,
                                                                                  final BiConsumer<Locale, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.locale(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a non {@link String value}.
     */
    private static SpreadsheetMetadataPropertyName<String> registerNonEmptyStringConstant(final String name,
                                                                                          final BiConsumer<String, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.nonEmpty(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a positive {@link Integer value}.
     */
    private static SpreadsheetMetadataPropertyName<Integer> registerPositiveIntegerConstant(final String name,
                                                                                            final BiConsumer<Integer, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.positiveInteger(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link RoundingMode value}.
     */
    private static SpreadsheetMetadataPropertyName<RoundingMode> registerRoundingModeConstant(final String name,
                                                                                              final BiConsumer<RoundingMode, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.roundingMode(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link SpreadsheetId value}.
     */
    private static SpreadsheetMetadataPropertyName<SpreadsheetId> registerSpreadsheetIdConstant(final String name,
                                                                                                final BiConsumer<SpreadsheetId, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.spreadsheetId(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with a {@link String value}.
     */
    private static SpreadsheetMetadataPropertyName<String> registerStringConstant(final String name,
                                                                                  final BiConsumer<String, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name,
                SpreadsheetMetadataPropertyValueHandler.string(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName} constant with {@link SpreadsheetTextFormatter pattern}.
     */
    private static SpreadsheetMetadataPropertyName<String> registerSpreadsheetTextFormatterPatternConstant(final String name,
                                                                                                           final BiConsumer<String, SpreadsheetMetadataVisitor> visitor) {
        return registerConstant(name, SpreadsheetMetadataPropertyValueHandler.textFormatterPattern(),
                visitor);
    }

    /**
     * Registers a new {@link SpreadsheetMetadataPropertyName}.
     */
    private static <T> SpreadsheetMetadataPropertyName<T> registerConstant(final String name,
                                                                           final SpreadsheetMetadataPropertyValueHandler<T> handler,
                                                                           final BiConsumer<T, SpreadsheetMetadataVisitor> visitor) {
        final SpreadsheetMetadataPropertyName<T> spreadsheetMetadataName = SpreadsheetMetadataPropertyNameNonColor.with(name,
                handler,
                visitor);
        SpreadsheetMetadataPropertyName.CONSTANTS.put(name, spreadsheetMetadataName);
        return spreadsheetMetadataName;
    }

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>BigDecimal pattern</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> BIG_DECIMAL_PATTERN = registerSpreadsheetTextFormatterPatternConstant("big-decimal-pattern",
            (p, v) -> v.visitBigDecimalPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>BigInteger pattern</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> BIG_INTEGER_PATTERN = registerSpreadsheetTextFormatterPatternConstant("big-integer-pattern",
            (p, v) -> v.visitBigIntegerPattern(p));

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
    public final static SpreadsheetMetadataPropertyName<String> CURRENCY_SYMBOL = registerNonEmptyStringConstant("currency-symbol",
            (c, v) -> v.visitCurrencySymbol(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-pattern {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> DATE_PATTERN = registerSpreadsheetTextFormatterPatternConstant("date-pattern",
            (p, v) -> v.visitDatePattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-offset {@link Long}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Long> DATETIME_OFFSET = registerDateTimeOffsetConstant("date-time-offset",
            (c, v) -> v.visitDateTimeOffset(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>date-time-pattern {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> DATETIME_PATTERN = registerSpreadsheetTextFormatterPatternConstant("date-time-pattern",
            (p, v) -> v.visitDateTimePattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>decimal point {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> DECIMAL_POINT = registerCharacterConstant("decimal-point",
            (c, v) -> v.visitDecimalPoint(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>default-pattern {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> DEFAULT_PATTERN = registerSpreadsheetTextFormatterPatternConstant("default-pattern",
            (p, v) -> v.visitDefaultPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>Double pattern</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> DOUBLE_PATTERN = registerSpreadsheetTextFormatterPatternConstant("double-pattern",
            (p, v) -> v.visitDoublePattern(p));

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
    public final static SpreadsheetMetadataPropertyName<Locale> LOCALE = registerLocaleConstant("locale",
            (l, v) -> v.visitLocale(l));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>Long pattern</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> LONG_PATTERN = registerSpreadsheetTextFormatterPatternConstant("long-pattern",
            (p, v) -> v.visitLongPattern(p));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>minus-sign {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> MINUS_SIGN = registerCharacterConstant("minus-sign",
            (c, v) -> v.visitMinusSign(c));

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
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>percentage-symbol {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> PERCENTAGE_SYMBOL = registerCharacterConstant("percentage-symbol",
            (c, v) -> v.visitPercentageSymbol(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>plus-sign {@link Character}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Character> PLUS_SIGN = registerCharacterConstant("plus-sign",
            (c, v) -> v.visitPlusSign(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>rounding-mode {@link RoundingMode}</code>
     */
    public final static SpreadsheetMetadataPropertyName<RoundingMode> ROUNDING_MODE = registerRoundingModeConstant("rounding-mode",
            (c, v) -> v.visitRoundingMode(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>precision {@link Integer}</code>
     */
    public final static SpreadsheetMetadataPropertyName<Integer> PRECISION = registerPositiveIntegerConstant("precision",
            (c, v) -> v.visitPrecision(c));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>spreadsheet-id {@link SpreadsheetId}</code>
     */
    public final static SpreadsheetMetadataPropertyName<SpreadsheetId> SPREADSHEET_ID = registerSpreadsheetIdConstant("spreadsheet-id",
            (e, v) -> v.visitSpreadsheetId(e));

    /**
     * A {@link SpreadsheetMetadataPropertyName} holding the <code>time-pattern {@link String}</code>
     */
    public final static SpreadsheetMetadataPropertyName<String> TIME_PATTERN = registerSpreadsheetTextFormatterPatternConstant("time-pattern",
            (p, v) -> v.visitTimePattern(p));

    /**
     * Factory that assumnes a valid {@link SpreadsheetMetadataPropertyName} or fails.
     */
    public static SpreadsheetMetadataPropertyName with(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        SpreadsheetMetadataPropertyName propertyName = CONSTANTS.get(name);
        if (null == propertyName) {
            if (!name.startsWith(COLOR_PREFIX)) {
                throw new IllegalArgumentException("Unknown metadata property name " + CharSequences.quoteAndEscape(name));
            }
            try {
                propertyName = color(Integer.parseInt(name.substring(COLOR_PREFIX.length())));
            } catch (final NumberFormatException cause) {
                throw new IllegalArgumentException("Unknown metadata property name " + CharSequences.quoteAndEscape(name));
            }
        }
        return propertyName;
    }

    final static String COLOR_PREFIX = "color-";

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyName} for a numbered {@link Color}.
     */
    public static SpreadsheetMetadataPropertyName<Color> color(final int number) {
        return SpreadsheetMetadataPropertyNameNumberedColor.color0(number);
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

    // NonEmptySpreadsheetMetadata......................................................................................

    /**
     * Used by {@link NonEmptySpreadsheetMetadata#numberToColor()}
     */
    abstract void addNumberedColor(final Object value, final Map<Integer, Color> numberToColor);

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
    public final CaseSensitivity caseSensitivity() {
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
    static SpreadsheetMetadataPropertyName<?> fromJsonNodeName(final JsonNode node) {
        return with(node.name().value());
    }
}
