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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterator.IteratorTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class MissingConverterTest implements ClassTesting2<MissingConverter>,
        HashCodeEqualsDefinedTesting2<MissingConverter>,
        ComparableTesting2<MissingConverter>,
        IteratorTesting,
        ToStringTesting<MissingConverter> {

    private final static ConverterName NAME = ConverterName.BOOLEAN_TO_NUMBER;

    private final static Set<MissingConverterValue> VALUES = Sets.of(
            MissingConverterValue.with(
                    "Hello",
                    String.class
            )
    );

    @Test
    public void testWithNullNameFails() {
        assertThrows(
                NullPointerException.class,
                () -> MissingConverter.with(
                        null,
                        VALUES
                )
        );
    }

    @Test
    public void testWithNullValuesFails() {
        assertThrows(
                NullPointerException.class,
                () -> MissingConverter.with(
                        NAME,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final MissingConverter missing = MissingConverter.with(
                NAME,
                VALUES
        );

        this.checkEquals(
                NAME,
                missing.name(),
                "name"
        );
        this.checkEquals(
                VALUES,
                missing.values(),
                "values"
        );
    }

    // hashcode/equals..................................................................................................

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(
                MissingConverter.with(
                        ConverterName.with("different"),
                        VALUES
                )
        );
    }

    @Test
    public void testEqualsDifferentValues() {
        this.checkNotEquals(
                MissingConverter.with(
                        NAME,
                        Sets.of(
                                MissingConverterValue.with(
                                        "Different",
                                        String.class
                                )
                        )
                )
        );
    }

    @Override
    public MissingConverter createObject() {
        return MissingConverter.with(
                NAME,
                VALUES
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                "boolean-to-number \"Hello\" java.lang.String"
        );
    }

    // Comparable.......................................................................................................

    @Test
    public void testComparableSort() {
        final MissingConverter apple = MissingConverter.with(
                ConverterName.with("apple"),
                VALUES
        );

        final MissingConverter banana = MissingConverter.with(
                ConverterName.with("banana"),
                VALUES
        );

        final MissingConverter carrot = MissingConverter.with(
                ConverterName.with("carrot"),
                VALUES
        );

        final Set<MissingConverter> treeSet = SortedSets.tree();
        treeSet.add(apple);
        treeSet.add(carrot);
        treeSet.add(banana);

        this.iterateAndCheck(
                treeSet.iterator(),
                apple,
                banana,
                carrot
        );
    }

    @Override
    public MissingConverter createComparable() {
        return this.createObject();
    }

    // class............................................................................................................

    @Override
    public Class<MissingConverter> type() {
        return MissingConverter.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
