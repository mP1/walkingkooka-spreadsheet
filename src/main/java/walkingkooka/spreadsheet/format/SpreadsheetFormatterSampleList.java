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

package walkingkooka.spreadsheet.format;

import walkingkooka.Cast;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.TextNode;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * An immutable list of {@link SpreadsheetFormatterSample}. This exists primarily to support marshalling/unmarshalling JSON which does
 * not support generic types.
 * <pre>
 * [
 *   {
 *     "label": "label1",
 *     "selector": "date-format-pattern dd/mm/yyyy",
 *     "value": {
 *       "type": "text",
 *       "value": "31/12/1999"
 *     }
 *   },
 *   {
 *     "label": "label1",
 *     "selector": "time-format-pattern hh/mm",
 *     "value": {
 *       "type": "text",
 *       "value": "12/58"
 *     }
 *   }
 * ]
 * </pre>
 */
public final class SpreadsheetFormatterSampleList extends AbstractList<SpreadsheetFormatterSample>
    implements ImmutableListDefaults<SpreadsheetFormatterSampleList, SpreadsheetFormatterSample> {

    public final static SpreadsheetFormatterSampleList EMPTY = new SpreadsheetFormatterSampleList(Lists.empty());

    public static SpreadsheetFormatterSampleList with(final List<SpreadsheetFormatterSample> samples) {
        Objects.requireNonNull(samples, "samples");

        SpreadsheetFormatterSampleList spreadsheetFormatterSampleList;

        if (samples instanceof SpreadsheetFormatterSampleList) {
            spreadsheetFormatterSampleList = (SpreadsheetFormatterSampleList) samples;
        } else {
            final List<SpreadsheetFormatterSample> copy = Lists.array();
            for (final SpreadsheetFormatterSample sample : samples) {
                copy.add(
                    Objects.requireNonNull(sample, "includes null sample")
                );
            }

            switch (samples.size()) {
                case 0:
                    spreadsheetFormatterSampleList = EMPTY;
                    break;
                default:
                    spreadsheetFormatterSampleList = new SpreadsheetFormatterSampleList(copy);
                    break;
            }
        }

        return spreadsheetFormatterSampleList;
    }

    private SpreadsheetFormatterSampleList(final List<SpreadsheetFormatterSample> samples) {
        this.samples = samples;
    }

    @Override
    public SpreadsheetFormatterSample get(int index) {
        return this.samples.get(index);
    }

    @Override
    public int size() {
        return this.samples.size();
    }

    private final List<SpreadsheetFormatterSample> samples;

    @Override
    public void elementCheck(final SpreadsheetFormatterSample sample) {
        Objects.requireNonNull(sample, "sample");
    }

    @Override
    public SpreadsheetFormatterSampleList setElements(final List<SpreadsheetFormatterSample> samples) {
        final SpreadsheetFormatterSampleList copy = with(samples);
        return this.equals(copy) ?
            this :
            copy;
    }

    // json.............................................................................................................

    static SpreadsheetFormatterSampleList unmarshall(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return with(
            Cast.to(
                context.unmarshallList(
                    node,
                    SpreadsheetFormatterSample.class
                )
            )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallCollection(this);
    }

    static {
        SpreadsheetFormatterSample.with(
            "Label", // label
            SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT,
            TextNode.text("")
        );

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetFormatterSampleList.class),
            SpreadsheetFormatterSampleList::unmarshall,
            SpreadsheetFormatterSampleList::marshall,
            SpreadsheetFormatterSampleList.class
        );
    }
}
