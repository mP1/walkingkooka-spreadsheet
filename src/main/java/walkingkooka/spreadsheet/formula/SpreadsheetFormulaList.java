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

package walkingkooka.spreadsheet.formula;

import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.HasCsvStringListTsvStringList;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.CharSequences;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An {@link walkingkooka.collect.list.ImmutableList} that may contain nullable {@link SpreadsheetFormula}.
 * The returned {@link SpreadsheetFormula} will only have text all other properties will be absent.
 */
public final class SpreadsheetFormulaList extends AbstractList<SpreadsheetFormula>
    implements ImmutableListDefaults<SpreadsheetFormulaList, SpreadsheetFormula>,
    HasCsvStringListTsvStringList {

    /**
     * Empty singleton
     */
    public final static SpreadsheetFormulaList EMPTY = new SpreadsheetFormulaList(Lists.empty());

    // @VisibleForTesting
    SpreadsheetFormulaList(final List<String> formulas) {
        super();
        this.formulas = formulas;
    }

    @Override
    public SpreadsheetFormula get(final int index) {
        final String text = this.formulas.get(index);
        return null != text ?
            SpreadsheetFormula.EMPTY.setText(text) :
            null;
    }

    @Override
    public int size() {
        return this.formulas.size();
    }

    private final List<String> formulas;

    @Override
    public SpreadsheetFormulaList setElements(final Collection<SpreadsheetFormula> elements) {
        final SpreadsheetFormulaList spreadsheetFormulaList;

        if (elements instanceof SpreadsheetFormulaList) {
            spreadsheetFormulaList = (SpreadsheetFormulaList) elements;
        } else {
            Objects.requireNonNull(elements, "elements");

            final List<String> copy = Lists.array();
            for (final SpreadsheetFormula each : elements) {
                this.elementCheck(each);
                copy.add(
                    null != each ?
                        each.text() :
                        null
                );
            }

            spreadsheetFormulaList = this.formulas.equals(copy) ?
                this :
                new SpreadsheetFormulaList(copy);
        }

        return spreadsheetFormulaList;
    }

    @Override
    public SpreadsheetFormulaList subList(final int from,
                                          final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceSpreadsheetFormulas(
                this.formulas.subList(
                    from,
                    to
                )
            );
    }

    private SpreadsheetFormulaList replaceSpreadsheetFormulas(final List<String> spreadsheetFormulas) {
        return spreadsheetFormulas.isEmpty() ?
            EMPTY :
            new SpreadsheetFormulaList(spreadsheetFormulas);
    }

    @Override
    public void elementCheck(final SpreadsheetFormula spreadsheetFormula) {
        // allow nulls
    }

    // HasCsvStringListTsvStringList....................................................................................

    @Override
    public CsvStringList csvStringList() {
        return CsvStringList.makeCsvStringList(
            this.formulas,
            (final String string) -> CharSequences.nullToEmpty(string).toString()
        );
    }
}
