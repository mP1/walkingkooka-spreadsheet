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

package walkingkooka.spreadsheet.color;

import walkingkooka.collect.map.Maps;
import walkingkooka.color.Color;
import walkingkooka.color.ColorContext;
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;
import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataDefaultTextResourceProvider;
import walkingkooka.text.CharSequences;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link ColorContext} that validates {@link IndexedColor} and only contains the default {@link SpreadsheetColors}.
 */
final class ColorContextSpreadsheetMetadataDefault implements ColorContext,
    TreePrintable {

    /**
     * Singleton
     */
    final static ColorContextSpreadsheetMetadataDefault INSTANCE = new ColorContextSpreadsheetMetadataDefault();

    private ColorContextSpreadsheetMetadataDefault() {
        super();

        final JsonObject metadataDefaults = JsonNode.parse(
            new SpreadsheetMetadataDefaultTextResourceProvider()
                .text()
        ).objectOrFail();

        final Comparator<String> comparator = SpreadsheetStrings.CASE_SENSITIVITY.comparator();

        final Map<String, Object> colorIndexOrNameTo = Maps.sorted(
            (final String left, final String right) -> {
                int compare = 0;

                final boolean leftNumber = left.charAt(0) <= '9';
                final boolean rightNumber = right.charAt(0) <= '9';

                if (leftNumber && rightNumber) {
                    compare = left.length() - right.length();
                }

                if (0 == compare) {
                    compare = comparator.compare(
                        left,
                        right
                    );
                }

                return compare;
            }
        );

        //  "color56": "#333333",
        //  "colorBlack": 1,
        for (final JsonNode entry : metadataDefaults.children()) {
            final String key = entry.name()
                .value();
            if (false == key.startsWith("color")) {
                continue;
            }

            // colorXXX color000
            final String after = key.substring("color".length());

            // color999
            if (Character.isDigit(after.charAt(0))) {
                // color number

                colorIndexOrNameTo.put(
                    after,
                    Color.parse(
                        entry.stringOrFail()
                    )
                );
                continue;
            }

            // colorRed
            colorIndexOrNameTo.put(
                after,
                entry.numberOrFail()
                    .intValue()
            );
        }

        this.colorIndexOrNameTo = colorIndexOrNameTo;
    }

    @Override
    public IndexedColor indexedColor(final int index) {
        indexedColorCheck(index);

        return Color.indexed(index);
    }

    private static void indexedColorCheck(final int index) {
        if (index < SpreadsheetColors.MIN || index > SpreadsheetColors.MAX) {
            throw new IllegalArgumentException("Invalid index " + index + " < " + SpreadsheetColors.MIN + " or > " + SpreadsheetColors.MAX);
        }
    }

    @Override
    public Optional<Color> lookupColor(final Color color) {
        Objects.requireNonNull(color, "color");

        Color lookup = null;

        if (color.isIndexed()) {
            lookup = this.lookupIndexedColor(
                ((IndexedColor) color)
                    .index()
            );
        } else {
            if (color.isNamed()) {
                final NamedColor namedColor = ((NamedColor) color);
                final Integer index = (Integer) this.colorIndexOrNameTo.get(
                    String.valueOf(
                        namedColor.value()
                    )
                );
                if (null != index) {
                    lookup = this.lookupIndexedColor(index);
                }
                ;
            } else {
                lookup = color;
            }
        }

        return Optional.ofNullable(lookup);
    }

    private Color lookupIndexedColor(final int index) {
        indexedColorCheck(index);

        return (Color) this.colorIndexOrNameTo.get(
            String.valueOf(index)
        );
    }

    private final Map<String, Object> colorIndexOrNameTo;

    @Override
    public NamedColor namedColor(final String name) {
        CharSequences.failIfNullOrEmpty(name, "name");

        return Color.named(name);
    }

    @Override
    public Color parseColor(final String color) {
        final Color colorColor = Color.parse(color);
        return colorColor.isIndexed() ?
            this.indexedColor(
                ((IndexedColor) colorColor).index()
            ) :
            colorColor.isNamed() ?
                this.namedColor(
                    ((NamedColor) colorColor).value()
                ) :
                colorColor;
    }

    // Object...........................................................................................................

    /**
     * <pre>
     * color1: "BLACK"
     * ...
     * color56: "#333333",
     * colorBlack: 1,
     * </pre>
     */
    @Override
    public String toString() {
        return this.colorIndexOrNameTo.entrySet()
            .stream()
            .map(
                (Entry<String, Object> e) -> fixColorName(e.getKey()) + ": " + e.getValue() + LineEnding.NL
            ).collect(Collectors.joining());
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            for (final Entry<String, Object> entry : this.colorIndexOrNameTo.entrySet()) {
                printer.print(
                    fixColorName(entry.getKey())
                );
                printer.print(": ");
                printer.println(
                    entry.getValue()
                        .toString()
                );
            }
        }
        printer.outdent();
    }

    private static String fixColorName(final String name) {
        return "color" + name;
    }
}
