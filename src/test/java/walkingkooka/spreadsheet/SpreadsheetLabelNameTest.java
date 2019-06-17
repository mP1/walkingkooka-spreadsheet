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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.InvalidCharacterException;
import walkingkooka.naming.NameTesting;
import walkingkooka.naming.PropertiesPath;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

final public class SpreadsheetLabelNameTest extends SpreadsheetExpressionReferenceTestCase<SpreadsheetLabelName>
        implements NameTesting<SpreadsheetLabelName, SpreadsheetLabelName> {

    @Test
    public void testCreateContainsSeparatorFails() {
        assertThrows(InvalidCharacterException.class, () -> {
            SpreadsheetLabelName.with("xyz" + PropertiesPath.SEPARATOR.string());
        });
    }

    @Test
    public void testWithInvalidInitialFails() {
        assertThrows(InvalidCharacterException.class, () -> {
            SpreadsheetLabelName.with("1abc");
        });
    }

    @Test
    public void testWithInvalidPartFails() {
        assertThrows(InvalidCharacterException.class, () -> {
            SpreadsheetLabelName.with("abc$def");
        });
    }

    @Test
    public void testCellReferenceFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetLabelName.with("A1");
        });
    }

    @Test
    public void testCellReferenceFails2() {
        assertThrows(IllegalArgumentException.class, () -> {
            SpreadsheetLabelName.with("AB12");
        });
    }

    @Test//(expected = IllegalArgumentException.class)
    public void testCellReferenceFails3() {
        SpreadsheetLabelName.with(SpreadsheetColumnReference.MAX_ROW_NAME + "1");
    }

    @Test
    public void testWith2() {
        this.createNameAndCheck("ZZZ1");
    }

    @Test
    public void testWith3() {
        this.createNameAndCheck("A123Hello");
    }

    @Test
    public void testWith4() {
        this.createNameAndCheck("A1B2C2");
    }

    @Test
    public void testWithMissingRow() {
        this.createNameAndCheck("A");
    }

    @Test
    public void testWithMissingRow2() {
        this.createNameAndCheck("ABC");
    }

    @Test
    public void testWithEnormousColumn() {
        this.createNameAndCheck("ABCDEF1");
    }

    @Test
    public void testWithEnormousColumn2() {
        this.createNameAndCheck("ABCDEF");
    }

    @Test
    public void testWithEnormousRow() {
        this.createNameAndCheck("A" + (SpreadsheetRowReference.MAX + 1));
    }

    // HasJsonNode..................................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true));
    }

    @Test
    public void testFromJsonNodeNullFails() {
        this.fromJsonNodeFails(JsonNode.nullNode());
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(123));
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array());
    }

    @Test
    public void testFromJsonNodeObjectFails() {
        this.fromJsonNodeFails(JsonNode.object());
    }

    @Test
    public void testFromJsonNodeString() {
        final String value = "LABEL123";
        this.fromJsonNodeAndCheck(JsonNode.string(value),
                SpreadsheetLabelName.with(value));
    }

    @Override
    SpreadsheetLabelName createReference() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetLabelName createName(final String name) {
        return SpreadsheetLabelName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.INSENSITIVE;
    }

    @Override
    public String nameText() {
        return "state";
    }

    @Override
    public String differentNameText() {
        return "different";
    }

    @Override
    public String nameTextLess() {
        return "postcode";
    }

    @Override
    public Class<SpreadsheetLabelName> type() {
        return SpreadsheetLabelName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HasJsonNodeTesting..........................................................................

    @Override
    public SpreadsheetLabelName fromJsonNode(final JsonNode from) {
        return SpreadsheetLabelName.fromJsonNodeLabelName(from);
    }
}
