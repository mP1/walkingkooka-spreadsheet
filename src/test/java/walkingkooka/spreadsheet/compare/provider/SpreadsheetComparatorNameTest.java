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

package walkingkooka.spreadsheet.compare.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.plugin.PluginNameTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertSame;

final public class SpreadsheetComparatorNameTest implements PluginNameTesting<SpreadsheetComparatorName> {

    @Test
    public void testConstants() {
        final Set<String> constants = Arrays.stream(
                SpreadsheetComparatorName.class.getDeclaredFields()
            ).filter(field -> String.class == field.getType() && false == field.getName().startsWith("HATEOS_") && field.getName().endsWith("_STRING"))
            .map(f -> {
                try {
                    f.setAccessible(true);
                    return (String) f.get(null);
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toCollection(SortedSets::tree));

        this.checkEquals(
            Sets.empty(),
            constants.stream()
                .filter(f -> SpreadsheetComparatorName.with(f) != SpreadsheetComparatorName.with(f))
                .collect(Collectors.toCollection(SortedSets::tree))
        );
    }

    // name.............................................................................................................

    @Override
    public SpreadsheetComparatorName createName(final String name) {
        return SpreadsheetComparatorName.with(name);
    }

    // isReversed.......................................................................................................

    @Test
    public void testIsReversedWhenNot() {
        this.isReversedAndCheck(
            "text",
            false
        );
    }

    @Test
    public void testIsReversedWhenReversed() {
        this.isReversedAndCheck(
            "text-reversed",
            true
        );
    }

    private void isReversedAndCheck(final String name,
                                    final boolean expected) {
        this.isReversedAndCheck(
            SpreadsheetComparatorName.with(name),
            expected
        );
    }

    private void isReversedAndCheck(final SpreadsheetComparatorName name,
                                    final boolean expected) {
        this.checkEquals(
            expected,
            name.isReversed(),
            name::toString
        );
    }

    // reversed.........................................................................................................

    @Test
    public void testReversed() {
        this.isReversedAndCheck(
            this.reverseAndCheck(
                "text",
                "text-reversed"
            ),
            true
        );
    }

    @Test
    public void testReversedWhenReversed() {
        this.isReversedAndCheck(
            this.reverseAndCheck(
                "text-reversed",
                "text"
            ),
            false
        );
    }

    private SpreadsheetComparatorName reverseAndCheck(final String name,
                                                      final String expected) {
        return this.reverseAndCheck(
            SpreadsheetComparatorName.with(name),
            SpreadsheetComparatorName.with(expected)
        );
    }

    private SpreadsheetComparatorName reverseAndCheck(final SpreadsheetComparatorName name,
                                                      final SpreadsheetComparatorName expected) {
        final SpreadsheetComparatorName reversed = name.reversed();
        this.checkEquals(
            expected,
            reversed,
            name::toString
        );
        return reversed;
    }

    @Test
    public void testReversedTwice() {
        final SpreadsheetComparatorName spreadsheetComparatorName = SpreadsheetComparatorName.YEAR;
        assertSame(
            spreadsheetComparatorName,
            spreadsheetComparatorName.reversed()
                .reversed()
        );
    }

    // unreversed.......................................................................................................

    @Test
    public void testUnreversed() {
        this.unreverseAndCheck(
            "text-reversed",
            "text"
        );
    }

    @Test
    public void testUnreversedWhenNotReversed() {
        this.unreverseAndCheck(
            "text",
            "text"
        );
    }

    private void unreverseAndCheck(final String name,
                                   final String expected) {
        this.unreverseAndCheck(
            SpreadsheetComparatorName.with(name),
            SpreadsheetComparatorName.with(expected)
        );
    }

    private void unreverseAndCheck(final SpreadsheetComparatorName name,
                                   final SpreadsheetComparatorName expected) {
        this.checkEquals(
            expected,
            name.unreversed(),
            name::toString
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorName> type() {
        return SpreadsheetComparatorName.class;
    }

    @Override
    public SpreadsheetComparatorName unmarshall(final JsonNode from,
                                                final JsonNodeUnmarshallContext context) {
        return SpreadsheetComparatorName.unmarshall(from, context);
    }
}
