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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetComparatorNameAndDirectionTest implements ClassTesting2<SpreadsheetComparatorNameAndDirection>,
    HashCodeEqualsDefinedTesting2<SpreadsheetComparatorNameAndDirection>,
    JsonNodeMarshallingTesting<SpreadsheetComparatorNameAndDirection>,
    ParseStringTesting<SpreadsheetComparatorNameAndDirection> {

    private final static SpreadsheetComparatorName NAME = SpreadsheetComparatorName.with("name-1");

    private final static SpreadsheetComparatorDirection DIRECTION = SpreadsheetComparatorDirection.UP;

    @Test
    public void testWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetComparatorNameAndDirection.with(
                null,
                DIRECTION
            )
        );
    }

    @Test
    public void testWithNullDirectionFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetComparatorNameAndDirection.with(
                NAME,
                null
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetComparatorNameAndDirection nameAndDirection = SpreadsheetComparatorNameAndDirection.with(
            NAME,
            DIRECTION
        );
        this.checkEquals(
            NAME,
            nameAndDirection.name(),
            "name"
        );
        this.checkEquals(
            DIRECTION,
            nameAndDirection.direction(),
            "direction"
        );
    }

    // setDirection.....................................................................................................

    @Test
    public void testSetDirectionUp() {
        this.checkEquals(
            SpreadsheetComparatorNameAndDirection.with(NAME, DIRECTION),
            NAME.setDirection(DIRECTION)
        );
    }

    @Test
    public void testSetDirectionDown() {
        final SpreadsheetComparatorDirection direction = SpreadsheetComparatorDirection.DOWN;

        this.checkEquals(
            SpreadsheetComparatorNameAndDirection.with(NAME, direction),
            NAME.setDirection(direction)
        );
    }

    // parseString......................................................................................................

    @Test
    public void testParseMissingDirectionDefaults() {
        this.parseStringAndCheck(
            "name-1",
            SpreadsheetComparatorNameAndDirection.with(
                NAME,
                SpreadsheetComparatorDirection.DEFAULT
            )
        );
    }

    @Test
    public void testParse() {
        this.parseStringAndCheck(
            "name-1",
            SpreadsheetComparatorNameAndDirection.with(
                NAME,
                SpreadsheetComparatorDirection.DEFAULT
            )
        );
    }

    @Test
    public void testParseUp() {
        this.parseStringAndCheck(
            "name-1 UP",
            SpreadsheetComparatorNameAndDirection.with(
                NAME,
                SpreadsheetComparatorDirection.UP
            )
        );
    }

    @Test
    public void testParseDown() {
        this.parseStringAndCheck(
            "name-1 DOWN",
            SpreadsheetComparatorNameAndDirection.with(
                NAME,
                SpreadsheetComparatorDirection.DOWN
            )
        );
    }

    @Override
    public SpreadsheetComparatorNameAndDirection parseString(final String text) {
        return SpreadsheetComparatorNameAndDirection.parse(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }

    // SpreadsheetColumnOrRowSpreadsheetComparatorNames.................................................................

    @Test
    public void testSetColumnOrRow() {
        this.checkEquals(
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.parse("A=text123 DOWN"),
            SpreadsheetComparatorName.with("text123")
                .setDirection(SpreadsheetComparatorDirection.DOWN)
                .setColumnOrRow(SpreadsheetSelection.parseColumn("A"))
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
            SpreadsheetComparatorNameAndDirection.with(
                SpreadsheetComparatorName.with("different-name-2"),
                DIRECTION
            )
        );
    }

    @Test
    public void testEqualsDifferentDifferent() {
        this.checkNotEquals(
            SpreadsheetComparatorNameAndDirection.with(
                NAME,
                DIRECTION.flip()
            )
        );
    }

    @Override
    public SpreadsheetComparatorNameAndDirection createObject() {
        return SpreadsheetComparatorNameAndDirection.with(
            NAME,
            DIRECTION
        );
    }

    // json.............................................................................................................

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            JsonNode.string("name-1 UP"),
            this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public SpreadsheetComparatorNameAndDirection unmarshall(final JsonNode json,
                                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetComparatorNameAndDirection.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetComparatorNameAndDirection createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetComparatorNameAndDirection> type() {
        return SpreadsheetComparatorNameAndDirection.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
