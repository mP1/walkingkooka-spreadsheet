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

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.ImmutableSortedSet;
import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A read only {@link Set} of {@link MissingConverter} sorted by {@link MissingConverter}.
 */
public final class MissingConverterSet extends AbstractSet<MissingConverter> implements ImmutableSortedSetDefaults<MissingConverterSet, MissingConverter>,
    TreePrintable {

    public final static MissingConverterSet EMPTY = new MissingConverterSet(
        SortedSets.empty()
    );

    public static MissingConverterSet with(final Collection<MissingConverter> missing) {
        MissingConverterSet with;

        if (missing instanceof MissingConverterSet) {
            with = (MissingConverterSet) missing;
        } else {
            Objects.requireNonNull(missing, "missing");

            final ImmutableSortedSet<MissingConverter> copy = SortedSets.immutable(
                new TreeSet<>(missing)
            );
            with = copy.isEmpty() ?
                EMPTY :
                new MissingConverterSet(copy);
        }

        return with;
    }

    private MissingConverterSet(final SortedSet<MissingConverter> missings) {
        this.missings = missings;
    }

    @Override
    public Iterator<MissingConverter> iterator() {
        return Iterators.readOnly(
            this.missings.iterator()
        );
    }

    @Override
    public int size() {
        return this.missings.size();
    }

    @Override
    public SortedSet<MissingConverter> toSet() {
        return new TreeSet<>(this.missings);
    }

    @Override
    public Comparator<MissingConverter> comparator() {
        return null;
    }

    @Override
    public SortedSet<MissingConverter> subSet(final MissingConverter from,
                                              final MissingConverter to) {
        return this.setElements(
            this.missings.subSet(
                from,
                to
            )
        );
    }

    @Override
    public SortedSet<MissingConverter> headSet(final MissingConverter to) {
        return this.setElements(
            this.missings.headSet(to)
        );
    }

    @Override
    public SortedSet<MissingConverter> tailSet(final MissingConverter from) {
        return this.setElements(
            this.missings.tailSet(from)
        );
    }

    @Override
    public MissingConverter first() {
        return this.missings.first();
    }

    @Override
    public MissingConverter last() {
        return this.missings.last();
    }

    @Override
    public void elementCheck(final MissingConverter missingConverter) {
        Objects.requireNonNull(missingConverter, "missingConverter");
    }

    @Override
    public MissingConverterSet setElements(final Collection<MissingConverter> missings) {
        MissingConverterSet missingConverterSet;

        if (missings instanceof MissingConverterSet) {
            missingConverterSet = (MissingConverterSet) missings;
        } else {
            SortedSet<MissingConverter> copy = SortedSets.tree();

            for (final MissingConverter missing : missings) {
                Objects.requireNonNull(missing, "includes null MissingConverter");
                copy.add(missing);
            }

            missingConverterSet = this.missings.equals(copy) ?
                this :
                (copy.isEmpty() ?
                    EMPTY :
                    new MissingConverterSet(copy)
                );
        }

        return missingConverterSet;
    }

    private final SortedSet<MissingConverter> missings;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            for (final MissingConverter missing : this.missings) {
                missing.printTree(printer);
            }
        }
        printer.outdent();
    }

    // json.............................................................................................................

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    // @VisibleForTesting
    static MissingConverterSet unmarshall(final JsonNode node,
                                          final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallSet(
                node,
                MissingConverter.class
            )
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(MissingConverterSet.class),
            MissingConverterSet::unmarshall,
            MissingConverterSet::marshall,
            MissingConverterSet.class
        );
    }
}
