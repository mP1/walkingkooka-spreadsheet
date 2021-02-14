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

import walkingkooka.Cast;
import walkingkooka.NeverError;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.CharSequences;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetMetadataNonEmpty} holds a non empty {@link Map} of {@link SpreadsheetMetadataPropertyName} and values.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
final class SpreadsheetMetadataNonEmpty extends SpreadsheetMetadata {

    /**
     * Factory that creates a {@link SpreadsheetMetadataNonEmpty} from a {@link SpreadsheetMetadataNonEmptyMap}.
     */
    static SpreadsheetMetadataNonEmpty with(final SpreadsheetMetadataNonEmptyMap value) {
        return new SpreadsheetMetadataNonEmpty(value, null);
    }

    private SpreadsheetMetadataNonEmpty(final SpreadsheetMetadataNonEmptyMap value,
                                        final SpreadsheetMetadata defaults) {
        super(defaults);
        this.value = value;
    }

    // Value..........................................................................................................

    @Override
    public Map<SpreadsheetMetadataPropertyName<?>, Object> value() {
        return this.value;
    }

    final SpreadsheetMetadataNonEmptyMap value;

    // setDefaults......................................................................................................

    @Override
    SpreadsheetMetadata replaceDefaults(final SpreadsheetMetadata defaults) {
        return new SpreadsheetMetadataNonEmpty(this.value, defaults);
    }

    /**
     * Checks that all property values are valid or general and not specific to a single spreadsheet.
     */
    @Override
    SpreadsheetMetadata checkDefault() {
        final SpreadsheetMetadata defaults = this.defaults();
        if (!defaults.isEmpty()) {
            throw new IllegalArgumentException("Default cannot have defaults: " + defaults);
        }

        final String invalid = this.value.keySet()
                .stream()
                .filter(SpreadsheetMetadataPropertyName::isInvalidGenericProperty)
                .map(Object::toString)
                .sorted()
                .collect(Collectors.joining(", "));
        if (false == invalid.isEmpty()) {
            throw new IllegalArgumentException("Defaults includes invalid default values: " + invalid);
        }
        return this;
    }

    // get..............................................................................................................

    @Override
    <V> Optional<V> getIgnoringDefaults(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return Optional.ofNullable(Cast.to(this.value.get(propertyName)));
    }

    // set..............................................................................................................

    /**
     * Loops over all entries, trying to find a match. While finding a match a new {@link List} is created with
     * sorted entries and this is used to create a new {@link SpreadsheetMetadataNonEmpty} if no match was found.
     */
    @Override
    <V> SpreadsheetMetadata set0(final SpreadsheetMetadataPropertyName<V> propertyName,
                                 final V value) {
        final boolean swapIfDuplicateValue = propertyName.swapIfDuplicateValue();
        int swapIndex = -1;
        SpreadsheetMetadataPropertyName<?> swapPropertyName = null;
        Object swapValue = null;

        final List<Entry<SpreadsheetMetadataPropertyName<?>, Object>> values = Lists.array();

        int mode = MODE_SET_APPENDED; // new property added.

        int i = 0;
        for (final Entry<SpreadsheetMetadataPropertyName<?>, Object> propertyAndValue : this.value.entries) {
            final SpreadsheetMetadataPropertyName<?> property = propertyAndValue.getKey();
            final Object propertyValue = propertyAndValue.getValue();

            final int compare = property.compareTo(propertyName);
            if (0 == compare) {
                if (propertyValue.equals(value)) {
                    mode = MODE_SET_UNMODIFIED; // no change
                    break;
                } else {
                    values.add(Maps.entry(property, value));
                    mode = MODE_SET_REPLACED; // replaced

                    swapValue = propertyValue; // needed if another property has $value it needs to be given $swapValue
                }
            } else {
                if (MODE_SET_APPENDED == mode && compare > 0) {
                    values.add(Maps.entry(propertyName, value));
                    mode = MODE_SET_INSERTED;
                }

                if (swapIfDuplicateValue &&
                        propertyName.isDuplicateIfValuesEqual(property) && propertyValue.equals(value)) {
                    swapIndex = i;
                    swapPropertyName = property;
                }
                values.add(propertyAndValue);
            }

            i++;
        }

        if (-1 != swapIndex) {
            // because the new property has no previous value the now duplicate cannot be swapped.
            if (null == swapValue) {
                throw new IllegalArgumentException("Cannot set " + propertyName + "=" + CharSequences.quoteIfChars(value) + " duplicate of " + swapPropertyName);
            }
            values.set(swapIndex, Maps.entry(swapPropertyName, swapValue));
        } else {
            // might be a duplicate of a default character property.
        }

        final SpreadsheetMetadata result;

        switch (mode) {
            case MODE_SET_APPENDED:
                values.add(Maps.entry(propertyName, value));
                result = this.setValues(values);
                break;
            case MODE_SET_UNMODIFIED:
                result = this;
                break;
            case MODE_SET_REPLACED:
            case MODE_SET_INSERTED:
                result = this.setValues(values);
                break;
            default:
                NeverError.unhandledCase(mode, MODE_SET_APPENDED, MODE_SET_UNMODIFIED, MODE_SET_REPLACED, MODE_SET_INSERTED);
                result = null;
        }

        return result;
    }

    private final static int MODE_SET_APPENDED = 0;
    private final static int MODE_SET_UNMODIFIED = 1;
    private final static int MODE_SET_REPLACED = 2;
    private final static int MODE_SET_INSERTED = 3;

    // remove...........................................................................................................

    @Override
    SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName) {
        final List<Entry<SpreadsheetMetadataPropertyName<?>, Object>> values = Lists.array();
        boolean removed = false;

        for (final Entry<SpreadsheetMetadataPropertyName<?>, Object> propertyAndValue : this.value.entries) {
            final SpreadsheetMetadataPropertyName<?> property = propertyAndValue.getKey();
            if (propertyName.equals(property)) {
                removed = true;
            } else {
                values.add(propertyAndValue);
            }
        }

        return removed ?
                this.remove1(values) :
                this;
    }

    /**
     * Accepts a list after removing a property, special casing if the list is empty.
     */
    private SpreadsheetMetadata remove1(final List<Entry<SpreadsheetMetadataPropertyName<?>, Object>> list) {
        return list.isEmpty() ?
                SpreadsheetMetadata.EMPTY.setDefaults(this.defaults()) :
                this.setValues(list); // no need to sort after a delete
    }

    private SpreadsheetMetadata setValues(final List<Entry<SpreadsheetMetadataPropertyName<?>, Object>> values) {
        return new SpreadsheetMetadataNonEmpty(
                SpreadsheetMetadataNonEmptyMap.withSpreadsheetMetadataMapEntrySet(
                        SpreadsheetMetadataNonEmptyMapEntrySet.withList(values)
                ),
                this.defaults
        );
    }

    // getters..........................................................................................................

    @Override
    public Function<SpreadsheetColorName, Optional<Color>> nameToColor() {
        if (null == this.nameToColor) {
            this.nameToColor = this.nameToColor0();
        }
        return this.nameToColor;
    }

    /**
     * Cache function
     */
    private Function<SpreadsheetColorName, Optional<Color>> nameToColor;

    @Override
    public Function<Integer, Optional<Color>> numberToColor() {
        if (null == this.numberToColor) {
            this.numberToColor = this.numberToColor0();
        }
        return this.numberToColor;
    }

    /**
     * Cache function
     */
    private Function<Integer, Optional<Color>> numberToColor;

    @Override
    public Converter<ExpressionNumberConverterContext> converter() {
        if (null == this.converter) {
            this.converter = this.converter0();
        }
        return this.converter;
    }

    /**
     * Cached {@link Converter}.
     */
    private Converter<ExpressionNumberConverterContext> converter;

    @Override
    public ExpressionNumberConverterContext converterContext() {
        if (null == this.converterContext) {
            this.converterContext = this.converterContext0();
        }
        return this.converterContext;
    }

    /**
     * Cached {@link ExpressionNumberConverterContext}.
     */
    private ExpressionNumberConverterContext converterContext;
    
    @Override
    public DateTimeContext dateTimeContext() {
        if (null == this.dateTimeContext) {
            this.dateTimeContext = this.dateTimeContext0();
        }
        return this.dateTimeContext;
    }

    /**
     * Cached {@link DateTimeContext}.
     */
    private DateTimeContext dateTimeContext;

    @Override
    public DecimalNumberContext decimalNumberContext() {
        if (null == this.decimalNumberContext) {
            this.decimalNumberContext = this.decimalNumberContext0();
        }
        return this.decimalNumberContext;
    }

    /**
     * Cached {@link DecimalNumberContext}.
     */
    private DecimalNumberContext decimalNumberContext;

    @Override
    public ExpressionNumberContext expressionNumberContext() {
        if (null == this.expressionNumberContext) {
            this.expressionNumberContext = this.expressionNumberContext0();
        }
        return this.expressionNumberContext;
    }

    /**
     * Cached {@link ExpressionNumberContext}.
     */
    private ExpressionNumberContext expressionNumberContext;

    @Override
    public JsonNodeUnmarshallContext jsonNodeUnmarshallContext() {
        if (null == this.jsonNodeUnmarshallContext) {
            this.jsonNodeUnmarshallContext = this.jsonNodeUnmarshallContext0();
        }
        return this.jsonNodeUnmarshallContext;
    }

    /**
     * Cached {@link JsonNodeUnmarshallContext}.
     */
    private JsonNodeUnmarshallContext jsonNodeUnmarshallContext;

    @Override
    public MathContext mathContext() {
        if (null == this.mathContext) {
            this.mathContext = this.mathContext0();
        }
        return this.mathContext;
    }

    /**
     * Cached {@link MathContext}.
     */
    private MathContext mathContext;

    @Override
    public SpreadsheetFormatter formatter() {
        if (null == this.formatter) {
            this.formatter = this.formatter0();
        }
        return this.formatter;
    }

    /**
     * Cached {@link SpreadsheetFormatter}.
     */
    private SpreadsheetFormatter formatter;

    @Override
    public synchronized SpreadsheetFormatterContext formatterContext(final SpreadsheetFormatter defaultFormatter) {
        if (false == defaultFormatter.equals(this.defaultFormatter)) {
            this.formatterContext = this.formatterContext0(defaultFormatter);
            this.defaultFormatter = defaultFormatter;
        }
        return this.formatterContext;
    }

    /**
     * The default formatter used to create the {@link SpreadsheetFormatterContext}.
     */
    private SpreadsheetFormatter defaultFormatter;

    /**
     * Cached {@link SpreadsheetFormatter} which also uses the {@link #defaultFormatter} as the default formatter.
     */
    private SpreadsheetFormatterContext formatterContext;

    // ParserContext....................................................................................................

    @Override
    public Parser<SpreadsheetParserContext> parser() {
        if (null == this.parser) {
            this.parser = this.createParser();
        }

        return this.parser;
    }

    private Parser<SpreadsheetParserContext> parser;

    // ParserContext....................................................................................................

    @Override
    public SpreadsheetParserContext parserContext() {
        if (null == this.parserContext) {
            this.parserContext = this.parserContext0();
        }

        return this.parserContext;
    }

    private SpreadsheetParserContext parserContext;
    
    // SpreadsheetMetadataVisitor.......................................................................................

    @Override
    void accept(final SpreadsheetMetadataVisitor visitor) {
        this.value.accept(visitor);
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.value().hashCode();
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetMetadataNonEmpty;
    }

    @Override
    boolean equalsValues(final SpreadsheetMetadata other) {
        return this.value.equals(other.value());
    }

    @Override
    public final String toString() {
        return this.value.toString();
    }

    // JsonNodeContext..................................................................................................

    @Override
    JsonObject marshallProperties(final JsonNodeMarshallContext context) {
        return this.value.marshall(context)
                .objectOrFail();
    }
}
