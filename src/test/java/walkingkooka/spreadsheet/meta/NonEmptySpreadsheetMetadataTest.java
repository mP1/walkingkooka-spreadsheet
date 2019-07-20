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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.CharSequences;

import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class NonEmptySpreadsheetMetadataTest extends SpreadsheetMetadataTestCase<NonEmptySpreadsheetMetadata> {

    @Test
    public void testWithSpreadsheetMetadataMap() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());
        final SpreadsheetMetadataMap metadataMap = SpreadsheetMetadataMap.with(map);

        final NonEmptySpreadsheetMetadata metadata = this.createSpreadsheetMetadata(metadataMap);
        assertSame(metadataMap, metadata.value(), "value");
    }

    @Test
    public void testWithMapCopied() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(map);

        final NonEmptySpreadsheetMetadata metadata = this.createSpreadsheetMetadata(map);

        map.clear();
        assertEquals(copy, metadata.value(), "value");
    }

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadataMap.EMPTY, SpreadsheetMetadataMap.with(Maps.empty()));
    }

    @Test
    public void testId() {
        final SpreadsheetId id = SpreadsheetId.with(123);
        final SpreadsheetMetadata metadata = this.createSpreadsheetMetadata(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id));
        assertEquals(Optional.of(id), metadata.id(), "id");
    }

    @Test
    public void testValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final NonEmptySpreadsheetMetadata metadata = this.createSpreadsheetMetadata(map);
        assertEquals(SpreadsheetMetadataMap.class, metadata.value().getClass(), () -> "" + metadata.value);
    }

    // get..............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(this.createSpreadsheetMetadata(),
                this.property1(),
                this.value1());
    }

    @Test
    public void testGet2() {
        this.getAndCheck(this.createSpreadsheetMetadata(),
                this.property2(),
                this.value2());
    }

    // set..............................................................................................................

    @Test
    public void testSetExistingPropertyAndValue() {
        this.setAndCheck(this.createSpreadsheetMetadata(),
                this.property1(),
                this.value1());
    }

    @Test
    public void testSetExistingPropertyAndValue2() {
        this.setAndCheck(this.createSpreadsheetMetadata(),
                this.property2(),
                this.value2());
    }

    @Test
    public void testSetReplacePropertyAndValue() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final LocalDateTime different = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        assertNotSame(different, value1);

        this.setAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, value2),
                property1,
                different,
                this.createSpreadsheetMetadata(property1, different, property2, value2));
    }

    @Test
    public void testSetReplacePropertyAndValue2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final EmailAddress different = EmailAddress.parse("different@example.com");
        assertNotSame(different, value2);

        this.setAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, value2),
                property2,
                different,
                this.createSpreadsheetMetadata(property1, value1, property2, different));
    }

    @Test
    public void testSetNewPropertyAndValue() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final SpreadsheetMetadataPropertyName<EmailAddress> property3 = this.property3();
        final EmailAddress value3 = this.value3();

        this.setAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, value2),
                property3,
                value3,
                this.createSpreadsheetMetadata(property1, value1, property2, value2, property3, value3));
    }

    @Test
    public void testSetNewPropertyAndValue2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        final SpreadsheetMetadataPropertyName<EmailAddress> property3 = this.property3();
        final EmailAddress value3 = this.value3();

        this.setAndCheck(this.createSpreadsheetMetadata(property2, value2, property3, value3),
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1, property2, value2, property3, value3));
    }

    private <T> void setAndCheck(final SpreadsheetMetadata metadata,
                                 final SpreadsheetMetadataPropertyName<T> propertyName,
                                 final T value) {
        assertSame(metadata,
                metadata.set(propertyName, value),
                () -> metadata + " set " + propertyName + " and " + CharSequences.quoteIfChars(value));
    }

    // remove...........................................................................................................

    @Test
    public void testRemove() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, this.value1(), property2, value2),
                property1,
                this.createSpreadsheetMetadata(property2, value2));
    }

    @Test
    public void testRemove2() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, value1, property2, this.value2()),
                property2,
                this.createSpreadsheetMetadata(property1, value1));
    }

    @Test
    public void testRemoveBecomesEmpty() {
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();

        this.removeAndCheck(this.createSpreadsheetMetadata(property1, value1),
                property1,
                SpreadsheetMetadata.EMPTY);
    }

    // set & remove ...................................................................................................

    @Test
    public void testSetSetRemoveRemove() {
        //set
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1));

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(metadata1,
                property2,
                value2,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));

        // remove1
        final SpreadsheetMetadata metadata3 = this.removeAndCheck(metadata2,
                property1,
                this.createSpreadsheetMetadata(property2, value2));

        this.removeAndCheck(metadata3,
                property2,
                SpreadsheetMetadata.EMPTY);
    }

    @Test
    public void testSetSetRemoveSet() {
        //set
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();
        final SpreadsheetMetadata metadata1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1));

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata metadata2 = this.setAndCheck(metadata1,
                property2,
                value2,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));

        // remove1
        final SpreadsheetMetadata metadata3 = this.removeAndCheck(metadata2,
                property1,
                this.createSpreadsheetMetadata(property2, value2));


        //set property1 again
        this.setAndCheck(metadata3,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));
    }

    // HateosResource...................................................................................................

    @Test
    public void testHateosLinkId() {
        this.hateosLinkIdAndCheck(NonEmptySpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(0x12347f))),
                "12347f");
    }

    @Test
    public void testHateosLinkIdMissingIdFails() {
        assertThrows(IllegalStateException.class, () -> {
            this.createSpreadsheetMetadata().hateosLinkId();
        });
    }

    // HasMathContext...................................................................................................

    @Test
    public void testHasMathContextRequiredPropertiesAbsentFails2() {
        assertThrows(IllegalStateException.class, () -> {
            NonEmptySpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.PRECISION, 1))
                    .mathContext();
        });
    }

    @Test
    public void testMathContext() {
        final int precision = 11;

        Arrays.stream(RoundingMode.values()).forEach(r -> {
            final MathContext mathContext = NonEmptySpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.PRECISION, precision,
                    SpreadsheetMetadataPropertyName.ROUNDING_MODE, r))
                    .mathContext();
            assertEquals(precision, mathContext.getPrecision(), "precision");
            assertEquals(r, mathContext.getRoundingMode(), "roundingMode");
        });
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        this.toStringAndCheck(NonEmptySpreadsheetMetadata.with(map), map.toString());
    }

    // HasJsonNode......................................................................................................

    @Test
    public void testHasJsonNodeRoundtrip() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> properties = Maps.ordered();

        properties.put(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, LocalDateTime.of(2000, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("creator@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, "$AUD");
        properties.put(SpreadsheetMetadataPropertyName.DECIMAL_POINT, 'D');
        properties.put(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, 'E');
        properties.put(SpreadsheetMetadataPropertyName.GENERAL_DECIMAL_FORMAT_PATTERN, "##.##");
        properties.put(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, 'G');
        properties.put(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
        properties.put(SpreadsheetMetadataPropertyName.MINUS_SIGN, 'M');
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_BY, EmailAddress.parse("modified@example.com"));
        properties.put(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, LocalDateTime.of(1999, 12, 31, 12, 58, 59));
        properties.put(SpreadsheetMetadataPropertyName.PERCENTAGE_SYMBOL, 'P');
        properties.put(SpreadsheetMetadataPropertyName.PLUS_SIGN, 'L');
        properties.put(SpreadsheetMetadataPropertyName.PRECISION, 123);
        properties.put(SpreadsheetMetadataPropertyName.ROUNDING_MODE, RoundingMode.FLOOR);
        properties.put(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, SpreadsheetId.with(123));

        this.toJsonNodeRoundTripTwiceAndCheck(SpreadsheetMetadata.with(properties));
    }

    // helpers...........................................................................................................

    @Override
    public NonEmptySpreadsheetMetadata createObject() {
        return this.createSpreadsheetMetadata();
    }

    private NonEmptySpreadsheetMetadata createSpreadsheetMetadata() {
        return this.createSpreadsheetMetadata(this.property1(), this.value1(), this.property2(), this.value2());
    }

    private <X> NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                      final X value1) {
        return this.createSpreadsheetMetadata(Maps.of(property1, value1));
    }

    private <X, Y> NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                         final X value1,
                                                                         final SpreadsheetMetadataPropertyName<Y> property2,
                                                                         final Y value2) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(property1, value1);
        map.put(property2, value2);
        return this.createSpreadsheetMetadata(map);
    }

    private <X, Y, Z> NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final SpreadsheetMetadataPropertyName<X> property1,
                                                                            final X value1,
                                                                            final SpreadsheetMetadataPropertyName<Y> property2,
                                                                            final Y value2,
                                                                            final SpreadsheetMetadataPropertyName<Z> property3,
                                                                            final Z value3) {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(property1, value1);
        map.put(property2, value2);
        map.put(property3, value3);
        return this.createSpreadsheetMetadata(map);
    }

    private NonEmptySpreadsheetMetadata createSpreadsheetMetadata(final Map<SpreadsheetMetadataPropertyName<?>, Object> map) {
        return Cast.to(SpreadsheetMetadata.with(map));
    }

    private SpreadsheetMetadataPropertyName<LocalDateTime> property1() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    private LocalDateTime value1() {
        return LocalDateTime.of(2000, 1, 2, 12, 58, 59);
    }

    private SpreadsheetMetadataPropertyName<EmailAddress> property2() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    private EmailAddress value2() {
        return EmailAddress.parse("user@example.com");
    }

    private SpreadsheetMetadataPropertyName<EmailAddress> property3() {
        return SpreadsheetMetadataPropertyName.MODIFIED_BY;
    }

    private EmailAddress value3() {
        return EmailAddress.parse("different@example.com");
    }

    @Override
    Class<NonEmptySpreadsheetMetadata> metadataType() {
        return NonEmptySpreadsheetMetadata.class;
    }

}
