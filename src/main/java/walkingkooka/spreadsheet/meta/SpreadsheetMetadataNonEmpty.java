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
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContext;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

/**
 * A {@link SpreadsheetMetadataNonEmpty} holds a non empty {@link Map} of {@link SpreadsheetMetadataPropertyName} and values.
 */
@SuppressWarnings("lgtm[java/inconsistent-equals-and-hashcode]")
final class SpreadsheetMetadataNonEmpty extends SpreadsheetMetadata {

    /**
     * Factory that creates a {@link SpreadsheetMetadataNonEmpty} from a {@link SpreadsheetMetadataNonEmptyMap}.
     */
    static SpreadsheetMetadataNonEmpty with(final SpreadsheetMetadataNonEmptyMap value) {
        return new SpreadsheetMetadataNonEmpty(value);
    }

    private SpreadsheetMetadataNonEmpty(final SpreadsheetMetadataNonEmptyMap value) {
        super();
        this.value = value;
    }

    // Value..........................................................................................................

    @Override
    public Map<SpreadsheetMetadataPropertyName<?>, Object> value() {
        return this.value;
    }

    final SpreadsheetMetadataNonEmptyMap value;

    // get..............................................................................................................

    @Override
    <V> Optional<V> get0(final SpreadsheetMetadataPropertyName<V> propertyName) {
        return Optional.ofNullable(Cast.to(this.value.get(propertyName)));
    }

    // set..............................................................................................................

    @Override
    <V> SpreadsheetMetadata set0(final SpreadsheetMetadataPropertyName<V> propertyName, final V value) {
        SpreadsheetMetadataNonEmptyMap map = this.value;
        final List<Entry<SpreadsheetMetadataPropertyName<?>, Object>> list = Lists.array();

        int mode = 0; // new property added.

        for (Entry<SpreadsheetMetadataPropertyName<?>, Object> propertyAndValue : map.entries) {
            final SpreadsheetMetadataPropertyName<?> property = propertyAndValue.getKey();

            if (propertyName.equals(property)) {
                if (propertyAndValue.getValue().equals(value)) {
                    mode = 1; // no change
                    break;
                } else {
                    list.add(Maps.entry(property, value));
                    mode = 2; // replaced
                }
            } else {
                list.add(propertyAndValue);
            }
        }

        // replace didnt happen
        if (0 == mode) {
            list.add(Maps.entry(propertyName, value));
            SpreadsheetMetadataNonEmptyMapEntrySet.sort(list);
        }

        return 1 == mode ?
                this :
                new SpreadsheetMetadataNonEmpty(SpreadsheetMetadataNonEmptyMap.withSpreadsheetMetadataMapEntrySet(SpreadsheetMetadataNonEmptyMapEntrySet.withList(list)));
    }

    // remove...........................................................................................................

    @Override
    SpreadsheetMetadata remove0(final SpreadsheetMetadataPropertyName<?> propertyName) {
        final List<Entry<SpreadsheetMetadataPropertyName<?>, Object>> list = Lists.array();
        boolean removed = false;

        for (Entry<SpreadsheetMetadataPropertyName<?>, Object> propertyAndValue : this.value.entries) {
            final SpreadsheetMetadataPropertyName<?> property = propertyAndValue.getKey();
            if (propertyName.equals(property)) {
                removed = true;
            } else {
                list.add(propertyAndValue);
            }
        }

        return removed ?
                this.remove1(list) :
                this;
    }

    /**
     * Accepts a list after removing a property, special casing if the list is empty.
     */
    private SpreadsheetMetadata remove1(List<Entry<SpreadsheetMetadataPropertyName<?>, Object>> list) {
        return list.isEmpty() ?
                SpreadsheetMetadata.EMPTY :
                new SpreadsheetMetadataNonEmpty(SpreadsheetMetadataNonEmptyMap.withSpreadsheetMetadataMapEntrySet(SpreadsheetMetadataNonEmptyMapEntrySet.withList(list))); // no need to sort after a delete
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
    public Converter converter() {
        if (null == this.converter) {
            this.converter = this.converter0();
        }
        return this.converter;
    }

    /**
     * Cached {@link Converter}.
     */
    private Converter converter;

    @Override
    public ConverterContext converterContext() {
        if (null == this.converterContext) {
            this.converterContext = this.converterContext0();
        }
        return this.converterContext;
    }

    /**
     * Cached {@link ConverterContext}.
     */
    private ConverterContext converterContext;
    
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
    boolean equals0(final SpreadsheetMetadata other) {
        return this.value.equals(other.value());
    }

    @Override
    public final String toString() {
        return this.value.toString();
    }

    // JsonNodeContext..................................................................................................

    @Override
    JsonNode marshall(final JsonNodeMarshallContext context) {
        return this.value.marshall(context);
    }
}
