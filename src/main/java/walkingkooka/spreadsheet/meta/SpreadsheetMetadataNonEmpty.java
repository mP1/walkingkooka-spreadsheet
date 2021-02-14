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
import walkingkooka.ToStringBuilder;
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
import walkingkooka.tree.json.JsonNode;
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
     * Factory that creates a {@link SpreadsheetMetadataNonEmpty} from a {@link Map<SpreadsheetMetadataPropertyName<?>, Object>}.
     */
    static SpreadsheetMetadataNonEmpty withImmutableMap(final Map<SpreadsheetMetadataPropertyName<?>, Object> properties) {
        properties.forEach((p, v) -> p.checkValue(v));
        return new SpreadsheetMetadataNonEmpty(properties, null);
    }

    private SpreadsheetMetadataNonEmpty(final Map<SpreadsheetMetadataPropertyName<?>, Object> value,
                                        final SpreadsheetMetadata defaults) {
        super(defaults);
        this.value = value;
    }

    // Value..........................................................................................................

    @Override
    public Map<SpreadsheetMetadataPropertyName<?>, Object> value() {
        return this.value;
    }

    final Map<SpreadsheetMetadataPropertyName<?>, Object> value;

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
     * Creates a new {@link Map} and if the property is a character property and the value is a duplicate the duplicated
     * property is given the old value.
     */
    @Override
    <V> SpreadsheetMetadata set0(final SpreadsheetMetadataPropertyName<V> propertyName,
                                 final V value) {
        SpreadsheetMetadata result = this;

        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = this.value();
        final Object previous = properties.get(propertyName);
        if (!value.equals(previous)) {
            // property is different or new
            final boolean swapIfDuplicateValue = propertyName.swapIfDuplicateValue();

            final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
            copy.putAll(properties);
            copy.put(propertyName, value);

            if (swapIfDuplicateValue) {
                boolean swapped = false;
                SpreadsheetMetadataPropertyName<?> duplicate = null;

                for (final Entry<SpreadsheetMetadataPropertyName<?>, Object> propertyAndValue : properties.entrySet()) {
                    final SpreadsheetMetadataPropertyName<?> propertyName2 = propertyAndValue.getKey();
                    if (propertyName.equals(propertyName2)) {
                        continue;
                    }
                    final Object value2 = propertyAndValue.getValue();
                    swapped = value.equals(value2) && propertyName.isDuplicateIfValuesEqual(propertyName2);
                    if (!swapped) {
                        continue;
                    }
                    if (null == previous) {
                        SpreadsheetMetadataNonEmpty.reportDuplicateProperty(propertyName, value, propertyName2);
                    }
                    copy.put(propertyName2, previous); // the other property now has the previous value of $propertyName
                    duplicate = propertyName2;
                    break;
                }

                if (null != duplicate && !swapped) {
                    SpreadsheetMetadataNonEmpty.reportDuplicateProperty(propertyName, value, duplicate);
                }
            }

            result = new SpreadsheetMetadataNonEmpty(Maps.immutable(copy), this.defaults);
        }

        return result;
    }

    private static void reportDuplicateProperty(final SpreadsheetMetadataPropertyName<?> property,
                                                final Object value,
                                                final SpreadsheetMetadataPropertyName<?> original) {
        throw new IllegalArgumentException("Cannot set " + property + "=" + CharSequences.quoteIfChars(value) + " duplicate of " + original);
    }

    // remove...........................................................................................................

    @Override
    SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(this.value());
        final Object removed = copy.remove(propertyName);
        return null == removed ?
                this :
                this.removeNonEmpty(copy);
    }

    private SpreadsheetMetadata removeNonEmpty(final Map<SpreadsheetMetadataPropertyName<?>, Object> copy) {
        final SpreadsheetMetadata defaults = this.defaults;
        return copy.isEmpty() && null == defaults ?
                EMPTY :
                new SpreadsheetMetadataNonEmpty(Maps.immutable(copy), defaults);
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
        this.value()
                .entrySet()
                .forEach(visitor::acceptPropertyAndValue);
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
    public String toString() {
        return ToStringBuilder.empty()
                .append('{')
                .value(this.value())
                .append('}')
                .build();
    }

    // JsonNodeContext..................................................................................................

    @Override
    JsonObject marshallProperties(final JsonNodeMarshallContext context) {
        final List<JsonNode> json = Lists.array();

        for (final Entry<SpreadsheetMetadataPropertyName<?>, Object> propertyAndValue : this.value().entrySet()) {
            final SpreadsheetMetadataPropertyName<?> propertyName = propertyAndValue.getKey();
            final JsonNode value = context.marshall(propertyAndValue.getValue());

            json.add(value.setName(propertyName.jsonPropertyName));
        }

        return JsonNode.object()
                .setChildren(json);
    }
}
