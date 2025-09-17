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

    @Override
    public SpreadsheetComparatorName createName(final String name) {
        return SpreadsheetComparatorName.with(name);
    }

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
