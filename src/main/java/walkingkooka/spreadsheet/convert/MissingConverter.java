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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.set.Sets;
import walkingkooka.convert.provider.ConverterName;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.Objects;
import java.util.Set;

/**
 * Part of a report about a Converter and value and type that was not supported by a {@link walkingkooka.convert.Converter}.
 */
public final class MissingConverter implements Comparable<MissingConverter>,
        TreePrintable {

    public static MissingConverter with(final ConverterName name,
                                        final Set<MissingConverterValue> values) {
        return new MissingConverter(
                Objects.requireNonNull(name, "name"),
                Sets.immutable(
                        Objects.requireNonNull(values, "values")
                )
        );
    }

    private MissingConverter(final ConverterName name,
                             final Set<MissingConverterValue> values) {
        this.name = name;
        this.values = values;
    }

    public ConverterName name() {
        return this.name;
    }

    private final ConverterName name;

    public Set<MissingConverterValue> values() {
        return this.values;
    }

    private final Set<MissingConverterValue> values;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.name,
                this.values
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof MissingConverter && this.equals0((MissingConverter) other);
    }

    private boolean equals0(final MissingConverter other) {
        return this.name.equals(other.name) &&
                this.values.equals(other.values);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.name)
                .value(this.values)
                .build();
    }

    // Comparable.......................................................................................................

    @Override
    public int compareTo(final MissingConverter other) {
        return this.name.compareTo(other.name);
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.name.value());
        printer.indent();
        {
            for (final Object value : this.values) {
                TreePrintable.printTreeOrToString(
                        value,
                        printer
                );
            }
        }
        printer.outdent();
    }
}
