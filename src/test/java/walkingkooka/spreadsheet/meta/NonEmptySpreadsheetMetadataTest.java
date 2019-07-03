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
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class NonEmptySpreadsheetMetadataTest extends SpreadsheetMetadataTestCase<NonEmptySpreadsheetMetadata> {

    @Test
    public void testWithSpreadsheetMetadataMap() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());
        final SpreadsheetMetadataMap textStyleMap = SpreadsheetMetadataMap.with(map);

        final NonEmptySpreadsheetMetadata textStyle = this.createSpreadsheetMetadata(textStyleMap);
        assertSame(textStyleMap, textStyle.value(), "value");
    }

    @Test
    public void testWithMapCopied() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final Map<SpreadsheetMetadataPropertyName<?>, Object> copy = Maps.sorted();
        copy.putAll(map);

        final NonEmptySpreadsheetMetadata textStyle = this.createSpreadsheetMetadata(map);

        map.clear();
        assertEquals(copy, textStyle.value(), "value");
    }

    @Test
    public void testEmpty() {
        assertSame(SpreadsheetMetadataMap.EMPTY, SpreadsheetMetadataMap.with(Maps.empty()));
    }

    @Test
    public void testValue() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        final NonEmptySpreadsheetMetadata textStyle = this.createSpreadsheetMetadata(map);
        assertEquals(SpreadsheetMetadataMap.class, textStyle.value().getClass(), () -> "" + textStyle.value);
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

    private <T> void setAndCheck(final SpreadsheetMetadata textStyle,
                                 final SpreadsheetMetadataPropertyName<T> propertyName,
                                 final T value) {
        assertSame(textStyle,
                textStyle.set(propertyName, value),
                () -> textStyle + " set " + propertyName + " and " + CharSequences.quoteIfChars(value));
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
        final SpreadsheetMetadata textStyle1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1));

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata textStyle2 = this.setAndCheck(textStyle1,
                property2,
                value2,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));

        // remove1
        final SpreadsheetMetadata textStyle3 = this.removeAndCheck(textStyle2,
                property1,
                this.createSpreadsheetMetadata(property2, value2));

        this.removeAndCheck(textStyle3,
                property2,
                SpreadsheetMetadata.EMPTY);
    }

    @Test
    public void testSetSetRemoveSet() {
        //set
        final SpreadsheetMetadataPropertyName<LocalDateTime> property1 = this.property1();
        final LocalDateTime value1 = this.value1();
        final SpreadsheetMetadata textStyle1 = this.setAndCheck(SpreadsheetMetadata.EMPTY,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1));

        //set
        final SpreadsheetMetadataPropertyName<EmailAddress> property2 = this.property2();
        final EmailAddress value2 = this.value2();
        final SpreadsheetMetadata textStyle2 = this.setAndCheck(textStyle1,
                property2,
                value2,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));

        // remove1
        final SpreadsheetMetadata textStyle3 = this.removeAndCheck(textStyle2,
                property1,
                this.createSpreadsheetMetadata(property2, value2));


        //set property1 again
        this.setAndCheck(textStyle3,
                property1,
                value1,
                this.createSpreadsheetMetadata(property1, value1, property2, value2));
    }

    // ToString.........................................................................................................

    @Test
    public void testToString() {
        final Map<SpreadsheetMetadataPropertyName<?>, Object> map = Maps.sorted();
        map.put(this.property1(), this.value1());
        map.put(this.property2(), this.value2());

        this.toStringAndCheck(NonEmptySpreadsheetMetadata.with(map), map.toString());
    }

    @Test
    public void testFromEmptyJsonObject() {
        assertSame(SpreadsheetMetadata.EMPTY, SpreadsheetMetadata.fromJsonNode(JsonNode.object()));
    }

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
