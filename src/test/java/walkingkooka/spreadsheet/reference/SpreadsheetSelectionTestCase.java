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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.IsMethodTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetSelectionTestCase<S extends SpreadsheetSelection> implements ClassTesting2<S>,
        HashCodeEqualsDefinedTesting2<S>,
        JsonNodeMarshallingTesting<S>,
        IsMethodTesting<S>,
        ToStringTesting<S> {

    SpreadsheetSelectionTestCase() {
        super();
    }

    final void toRelativeAndCheck(final S selection) {
        this.toRelativeAndCheck(selection, selection);
    }

    final void toRelativeAndCheck(final S selection,
                                  final S expected) {
        if (expected.equals(selection)) {
            assertSame(
                    expected,
                    selection.toRelative(),
                    () -> selection.toString()
            );
        } else {
            assertEquals(
                    expected,
                    selection.toRelative(),
                    () -> selection.toString()
            );
        }
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json..............................................................................................................

    @Test
    public final void testJsonNodeMarshall() {
        final S selection = this.createSelection();
        this.marshallAndCheck(selection, JsonNode.string(selection.toString()));
    }

    abstract S createSelection();

    // HashCodeEqualsDefinedTesting.....................................................................................

    @Override
    public final S createObject() {
        return this.createSelection();
    }

    // IsMethodTesting...................................................................................................

    @Override
    public final S createIsMethodObject() {
        return this.createSelection();
    }

    @Override
    public final String isMethodTypeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public final String isMethodTypeNameSuffix() {
        return "";//ExpressionReference.class.getSimpleName();
    }

    @Override
    public final Predicate<String> isMethodIgnoreMethodFilter() {
        return Predicates.never();
    }

    // JsonNodeTesting..................................................................................................

    @Override
    public final S createJsonNodeMappingValue() {
        return this.createSelection();
    }
}
