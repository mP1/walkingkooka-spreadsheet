
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
package walkingkooka.spreadsheet.convert;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MissingConverterSetTest implements ImmutableSortedSetTesting<MissingConverterSet, MissingConverter>,
    TreePrintableTesting,
    JsonNodeMarshallingTesting<MissingConverterSet> {

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> MissingConverterSet.with(null)
        );
    }

    @Test
    public void testWithMissingConverterSetDoesntWrap() {
        final MissingConverterSet set = this.createSet();

        assertSame(
            set,
            MissingConverterSet.with(set)
        );
    }

    @Test
    public void testDeleteBecomesEmpty() {
        final MissingConverter missing = MissingConverter.with(
            ConverterName.BOOLEAN_TO_NUMBER,
            Sets.of(
                MissingConverterValue.with(
                    "Hello",
                    String.class.getName()
                )
            )
        );

        assertSame(
            MissingConverterSet.EMPTY,
            MissingConverterSet.with(
                SortedSets.of(missing)
            ).delete(missing)
        );
    }

    @Test
    public void testSetElementsWithMissingConverterSet() {
        final MissingConverterSet set = this.createSet();

        assertSame(
            set,
            set.setElements(
                new TreeSet<>(set)
            )
        );
    }

    @Override
    public MissingConverterSet createSet() {
        return MissingConverterSet.with(
            SortedSets.of(
                MissingConverter.with(
                    ConverterName.with("converter1"),
                    Sets.of(
                        MissingConverterValue.with(
                            "Hello1",
                            String.class.getName()
                        )
                    )
                ),
                MissingConverter.with(
                    ConverterName.with("converter2"),
                    Sets.of(
                        MissingConverterValue.with(
                            "Hello2",
                            String.class.getName()
                        )
                    )
                ),
                MissingConverter.with(
                    ConverterName.with("converter3"),
                    Sets.of(
                        MissingConverterValue.with(
                            "Hello2",
                            String.class.getName()
                        )
                    )
                )
            )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "MissingConverterSet\n" +
                "  converter1\n" +
                "    \"Hello1\"\n" +
                "      java.lang.String\n" +
                "  converter2\n" +
                "    \"Hello2\"\n" +
                "      java.lang.String\n" +
                "  converter3\n" +
                "    \"Hello2\"\n" +
                "      java.lang.String\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "[\n" +
                "  {\n" +
                "    \"name\": \"converter1\",\n" +
                "    \"values\": [\n" +
                "      {\n" +
                "        \"value\": \"Hello1\",\n" +
                "        \"type\": \"java.lang.String\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"converter2\",\n" +
                "    \"values\": [\n" +
                "      {\n" +
                "        \"value\": \"Hello2\",\n" +
                "        \"type\": \"java.lang.String\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"name\": \"converter3\",\n" +
                "    \"values\": [\n" +
                "      {\n" +
                "        \"value\": \"Hello2\",\n" +
                "        \"type\": \"java.lang.String\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]"
        );
    }

    @Override
    public MissingConverterSet unmarshall(final JsonNode jsonNode,
                                          final JsonNodeUnmarshallContext context) {
        return MissingConverterSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public MissingConverterSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // class............................................................................................................

    @Override
    public Class<MissingConverterSet> type() {
        return MissingConverterSet.class;
    }
}