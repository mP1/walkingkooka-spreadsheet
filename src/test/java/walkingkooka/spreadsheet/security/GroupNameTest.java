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

package walkingkooka.spreadsheet.security;

import org.junit.jupiter.api.Test;
import walkingkooka.naming.NameTesting;
import walkingkooka.naming.PropertiesPath;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.tree.json.HasJsonNodeTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertThrows;

final public class GroupNameTest implements NameTesting<GroupName, GroupName>,
        HasJsonNodeTesting<GroupName> {

    @Test
    public void testCreateEmptyStringFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            GroupName.with("");
        });
    }

    @Test
    public void testCreateContainsSeparatorFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            GroupName.with("xyz" + PropertiesPath.SEPARATOR.string());
        });
    }

    @Test
    public void testWithInvalidInitialFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            GroupName.with("1abc");
        });
    }

    @Test
    public void testWithInvalidPartFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            GroupName.with("abc$def");
        });
    }

    @Test
    public void testWith() {
        this.createNameAndCheck("Abc-123");
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

    // Comparator ......................................................................................................

    @Test
    public void testSort() {
        final GroupName a1 = GroupName.with("a1");
        final GroupName b2 = GroupName.with("B2");
        final GroupName c3 = GroupName.with("C3");
        final GroupName d4 = GroupName.with("d4");

        this.compareToArraySortAndCheck(d4, c3, a1, b2,
                b2, c3, a1, d4);
    }

    // toJsonNode ......................................................................................................

    @Test
    public void testFromJsonNodeBooleanFails() {
        this.fromJsonNodeFails(JsonNode.booleanNode(true), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeNumberFails() {
        this.fromJsonNodeFails(JsonNode.number(123), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeArrayFails() {
        this.fromJsonNodeFails(JsonNode.array(), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeObjectFails() {
        this.fromJsonNodeFails(JsonNode.object(), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNodeInvalidEmailFails() {
        this.fromJsonNodeFails(JsonNode.string("!"), JsonNodeException.class);
    }

    @Test
    public void testFromJsonNode() {
        final String value = "group123";
        this.fromJsonNodeAndCheck(JsonNode.string(value), GroupName.with(value));
    }

    @Test
    public void testToJsonNodeRoundtrip() {
        this.toJsonNodeRoundTripTwiceAndCheck(GroupName.with("group123"));
    }

    @Override
    public GroupName createName(final String name) {
        return GroupName.with(name);
    }

    @Override
    public CaseSensitivity caseSensitivity() {
        return CaseSensitivity.SENSITIVE;
    }

    @Override
    public String nameText() {
        return "Group123";
    }

    @Override
    public String differentNameText() {
        return "Different";
    }

    @Override
    public String nameTextLess() {
        return "Abc-group";
    }

    @Override
    public Class<GroupName> type() {
        return GroupName.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // HasJsonNodeTesting......................................................................................

    @Override
    public final GroupName createHasJsonNode() {
        return this.createObject();
    }

    @Override
    public final GroupName fromJsonNode(final JsonNode from) {
        return GroupName.fromJsonNode(from);
    }
}
