
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

package walkingkooka.spreadsheet.value;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Optional;

public abstract class SpreadsheetColumnOrRowTestCase<T extends SpreadsheetColumnOrRow<R>,
    R extends SpreadsheetSelection & Comparable<R>>
    implements ClassTesting2<T>,
    HashCodeEqualsDefinedTesting2<T>,
    JsonNodeMarshallingTesting<T>,
    ToStringTesting<T>,
    TreePrintableTesting {

    SpreadsheetColumnOrRowTestCase() {
        super();
    }

    static boolean differentHidden() {
        return true;
    }

    final void checkReference(final T columnOrRow,
                              final R reference) {
        this.checkEquals(reference, columnOrRow.reference(), "reference");
        this.checkEquals(Optional.of(reference), columnOrRow.id(), "id");
    }

    final void checkHidden(final T columnOrRow,
                           final boolean hidden) {
        this.checkEquals(hidden, columnOrRow.hidden(), "hidden");
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public final void testUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"));
    }

    // TreePrintable....................................................................................................

    @Test
    public final void testTreePrintable() {
        final T columnOrRow = this.createObject();

        this.treePrintAndCheck(
            columnOrRow,
            columnOrRow.reference().toString() + EOL
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public final JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Override
    public final T createJsonNodeMarshallingValue() {
        return this.createObject();
    }
}
