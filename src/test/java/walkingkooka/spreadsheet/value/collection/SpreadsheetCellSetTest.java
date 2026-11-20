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

package walkingkooka.spreadsheet.value.collection;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.HasCsvStringListTesting;
import walkingkooka.collect.set.ImmutableSortedSetTesting;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.HasUrlFragmentTesting;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.HasSpreadsheetCellTesting;
import walkingkooka.spreadsheet.value.HasSpreadsheetCellValueKindTesting;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Arrays;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetCellSetTest implements ImmutableSortedSetTesting<SpreadsheetCellSet, SpreadsheetCell>,
    TreePrintableTesting,
    JsonNodeMarshallerTesting<SpreadsheetCellSet>,
    HasCsvStringListTesting,
    HasSpreadsheetCellTesting,
    HasSpreadsheetCellValueKindTesting,
    HasUrlFragmentTesting {

    @Test
    public void testDeleteBecomesEmpty() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );

        final SortedSet<SpreadsheetCell> set = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        set.add(cell);

        assertSame(
            SpreadsheetCellSet.EMPTY,
            SpreadsheetCellSet.EMPTY.setElements(set)
                .delete(cell)
        );
    }

    @Test
    public void testSetElementsWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSet()
                .setElements(
                    Arrays.asList(null)
                )
        );
    }

    @Test
    public void testSetElementsWithInvalidElementTypeFails() {
        assertThrows(
            ClassCastException.class,
            () -> this.createSet()
                .setElements(
                    Cast.to(
                        Arrays.asList("Invalid")
                    )
                )
        );
    }

    @Override
    public SpreadsheetCellSet createSet() {
        final SortedSet<SpreadsheetCell> set = SortedSets.tree(SpreadsheetCell.REFERENCE_COMPARATOR);
        set.add(SPREADSHEET_CELL);
        set.add(DIFFERENT_SPREADSHEET_CELL);

        return SpreadsheetCellSet.EMPTY.setElements(set);
    }

// TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createSet(),
            "Cell A1\n" +
                "  Formula\n" +
                "    valueType:\n" +
                "      text/Text\n" +
                "  currency:\n" +
                "    AUD (java.util.Currency)\n" +
                "  currencyExchangeRater:\n" +
                "    test-currency-exchange-rater-111\n" +
                "  dateTimeSymbols:\n" +
                "    DateTimeSymbols\n" +
                "      ampms\n" +
                "        am\n" +
                "        pm\n" +
                "      monthNames\n" +
                "        January\n" +
                "        February\n" +
                "        March\n" +
                "        April\n" +
                "        May\n" +
                "        June\n" +
                "        July\n" +
                "        August\n" +
                "        September\n" +
                "        October\n" +
                "        November\n" +
                "        December\n" +
                "      monthNameAbbreviations\n" +
                "        Jan.\n" +
                "        Feb.\n" +
                "        Mar.\n" +
                "        Apr.\n" +
                "        May\n" +
                "        Jun.\n" +
                "        Jul.\n" +
                "        Aug.\n" +
                "        Sep.\n" +
                "        Oct.\n" +
                "        Nov.\n" +
                "        Dec.\n" +
                "      weekDayNames\n" +
                "        Sunday\n" +
                "        Monday\n" +
                "        Tuesday\n" +
                "        Wednesday\n" +
                "        Thursday\n" +
                "        Friday\n" +
                "        Saturday\n" +
                "      weekDayNameAbbreviations\n" +
                "        Sun.\n" +
                "        Mon.\n" +
                "        Tue.\n" +
                "        Wed.\n" +
                "        Thu.\n" +
                "        Fri.\n" +
                "        Sat.\n" +
                "  decimalNumberSymbols:\n" +
                "    DecimalNumberSymbols\n" +
                "      negativeSign\n" +
                "        '-'\n" +
                "      positiveSign\n" +
                "        '+'\n" +
                "      zeroDigit\n" +
                "        '0'\n" +
                "      currencySymbol\n" +
                "        \"$\"\n" +
                "      decimalSeparator\n" +
                "        '.'\n" +
                "      exponentSymbol\n" +
                "        \"e\"\n" +
                "      groupSeparator\n" +
                "        ','\n" +
                "      infinitySymbol\n" +
                "        \"∞\"\n" +
                "      monetaryDecimalSeparator\n" +
                "        '.'\n" +
                "      nanSymbol\n" +
                "        \"NaN\"\n" +
                "      percentSymbol\n" +
                "        '%'\n" +
                "      permillSymbol\n" +
                "        '‰'\n" +
                "  formatter:\n" +
                "    formatter-selector-111\n" +
                "  locale:\n" +
                "    en_AU (java.util.Locale)\n" +
                "  output:\n" +
                "    Text \"*output*\"\n" +
                "  parser:\n" +
                "    parser-selector-111\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=black\n" +
                "  validator:\n" +
                "    absolute-url\n" +
                "Cell B2\n" +
                "  Formula\n" +
                "    valueType:\n" +
                "      number/Number\n" +
                "  currency:\n" +
                "    NZD (java.util.Currency)\n" +
                "  currencyExchangeRater:\n" +
                "    test-different-currency-exchange-rater-222\n" +
                "  dateTimeSymbols:\n" +
                "    DateTimeSymbols\n" +
                "      ampms\n" +
                "        AM\n" +
                "        PM\n" +
                "      monthNames\n" +
                "        January\n" +
                "        February\n" +
                "        March\n" +
                "        April\n" +
                "        May\n" +
                "        June\n" +
                "        July\n" +
                "        August\n" +
                "        September\n" +
                "        October\n" +
                "        November\n" +
                "        December\n" +
                "      monthNameAbbreviations\n" +
                "        Jan\n" +
                "        Feb\n" +
                "        Mar\n" +
                "        Apr\n" +
                "        May\n" +
                "        Jun\n" +
                "        Jul\n" +
                "        Aug\n" +
                "        Sep\n" +
                "        Oct\n" +
                "        Nov\n" +
                "        Dec\n" +
                "      weekDayNames\n" +
                "        Sunday\n" +
                "        Monday\n" +
                "        Tuesday\n" +
                "        Wednesday\n" +
                "        Thursday\n" +
                "        Friday\n" +
                "        Saturday\n" +
                "      weekDayNameAbbreviations\n" +
                "        Sun\n" +
                "        Mon\n" +
                "        Tue\n" +
                "        Wed\n" +
                "        Thu\n" +
                "        Fri\n" +
                "        Sat\n" +
                "  decimalNumberSymbols:\n" +
                "    DecimalNumberSymbols\n" +
                "      negativeSign\n" +
                "        '-'\n" +
                "      positiveSign\n" +
                "        '+'\n" +
                "      zeroDigit\n" +
                "        '0'\n" +
                "      currencySymbol\n" +
                "        \"$\"\n" +
                "      decimalSeparator\n" +
                "        '.'\n" +
                "      exponentSymbol\n" +
                "        \"E\"\n" +
                "      groupSeparator\n" +
                "        ','\n" +
                "      infinitySymbol\n" +
                "        \"∞\"\n" +
                "      monetaryDecimalSeparator\n" +
                "        '.'\n" +
                "      nanSymbol\n" +
                "        \"NaN\"\n" +
                "      percentSymbol\n" +
                "        '%'\n" +
                "      permillSymbol\n" +
                "        '‰'\n" +
                "  formatter:\n" +
                "    different-formatter-selector-222\n" +
                "  locale:\n" +
                "    en_NZ (java.util.Locale)\n" +
                "  output:\n" +
                "    Text \"*different-output*\"\n" +
                "  parser:\n" +
                "    different-parser-selector-222\n" +
                "  style:\n" +
                "    TextStyle\n" +
                "      color=white\n" +
                "  validator:\n" +
                "    email-address\n"
        );
    }

// json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            "{\n" +
                "  \"A1\": {\n" +
                "    \"formula\": {\n" +
                "      \"valueType\": \"Text\"\n" +
                "    },\n" +
                "    \"currency\": \"AUD\",\n" +
                "    \"currencyExchangeRater\": \"test-currency-exchange-rater-111\",\n" +
                "    \"dateTimeSymbols\": {\n" +
                "      \"ampms\": [\n" +
                "        \"am\",\n" +
                "        \"pm\"\n" +
                "      ],\n" +
                "      \"monthNames\": [\n" +
                "        \"January\",\n" +
                "        \"February\",\n" +
                "        \"March\",\n" +
                "        \"April\",\n" +
                "        \"May\",\n" +
                "        \"June\",\n" +
                "        \"July\",\n" +
                "        \"August\",\n" +
                "        \"September\",\n" +
                "        \"October\",\n" +
                "        \"November\",\n" +
                "        \"December\"\n" +
                "      ],\n" +
                "      \"monthNameAbbreviations\": [\n" +
                "        \"Jan.\",\n" +
                "        \"Feb.\",\n" +
                "        \"Mar.\",\n" +
                "        \"Apr.\",\n" +
                "        \"May\",\n" +
                "        \"Jun.\",\n" +
                "        \"Jul.\",\n" +
                "        \"Aug.\",\n" +
                "        \"Sep.\",\n" +
                "        \"Oct.\",\n" +
                "        \"Nov.\",\n" +
                "        \"Dec.\"\n" +
                "      ],\n" +
                "      \"weekDayNames\": [\n" +
                "        \"Sunday\",\n" +
                "        \"Monday\",\n" +
                "        \"Tuesday\",\n" +
                "        \"Wednesday\",\n" +
                "        \"Thursday\",\n" +
                "        \"Friday\",\n" +
                "        \"Saturday\"\n" +
                "      ],\n" +
                "      \"weekDayNameAbbreviations\": [\n" +
                "        \"Sun.\",\n" +
                "        \"Mon.\",\n" +
                "        \"Tue.\",\n" +
                "        \"Wed.\",\n" +
                "        \"Thu.\",\n" +
                "        \"Fri.\",\n" +
                "        \"Sat.\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"decimalNumberSymbols\": {\n" +
                "      \"negativeSign\": \"-\",\n" +
                "      \"positiveSign\": \"+\",\n" +
                "      \"zeroDigit\": \"0\",\n" +
                "      \"currencySymbol\": \"$\",\n" +
                "      \"decimalSeparator\": \".\",\n" +
                "      \"exponentSymbol\": \"e\",\n" +
                "      \"groupSeparator\": \",\",\n" +
                "      \"infinitySymbol\": \"∞\",\n" +
                "      \"monetaryDecimalSeparator\": \".\",\n" +
                "      \"nanSymbol\": \"NaN\",\n" +
                "      \"percentSymbol\": \"%\",\n" +
                "      \"permillSymbol\": \"‰\"\n" +
                "    },\n" +
                "    \"formatter\": \"formatter-selector-111\",\n" +
                "    \"locale\": \"en-AU\",\n" +
                "    \"output\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"*output*\"\n" +
                "    },\n" +
                "    \"parser\": \"parser-selector-111\",\n" +
                "    \"style\": {\n" +
                "      \"color\": \"black\"\n" +
                "    },\n" +
                "    \"validator\": \"absolute-url\"\n" +
                "  },\n" +
                "  \"B2\": {\n" +
                "    \"formula\": {\n" +
                "      \"valueType\": \"Number\"\n" +
                "    },\n" +
                "    \"currency\": \"NZD\",\n" +
                "    \"currencyExchangeRater\": \"test-different-currency-exchange-rater-222\",\n" +
                "    \"dateTimeSymbols\": {\n" +
                "      \"ampms\": [\n" +
                "        \"AM\",\n" +
                "        \"PM\"\n" +
                "      ],\n" +
                "      \"monthNames\": [\n" +
                "        \"January\",\n" +
                "        \"February\",\n" +
                "        \"March\",\n" +
                "        \"April\",\n" +
                "        \"May\",\n" +
                "        \"June\",\n" +
                "        \"July\",\n" +
                "        \"August\",\n" +
                "        \"September\",\n" +
                "        \"October\",\n" +
                "        \"November\",\n" +
                "        \"December\"\n" +
                "      ],\n" +
                "      \"monthNameAbbreviations\": [\n" +
                "        \"Jan\",\n" +
                "        \"Feb\",\n" +
                "        \"Mar\",\n" +
                "        \"Apr\",\n" +
                "        \"May\",\n" +
                "        \"Jun\",\n" +
                "        \"Jul\",\n" +
                "        \"Aug\",\n" +
                "        \"Sep\",\n" +
                "        \"Oct\",\n" +
                "        \"Nov\",\n" +
                "        \"Dec\"\n" +
                "      ],\n" +
                "      \"weekDayNames\": [\n" +
                "        \"Sunday\",\n" +
                "        \"Monday\",\n" +
                "        \"Tuesday\",\n" +
                "        \"Wednesday\",\n" +
                "        \"Thursday\",\n" +
                "        \"Friday\",\n" +
                "        \"Saturday\"\n" +
                "      ],\n" +
                "      \"weekDayNameAbbreviations\": [\n" +
                "        \"Sun\",\n" +
                "        \"Mon\",\n" +
                "        \"Tue\",\n" +
                "        \"Wed\",\n" +
                "        \"Thu\",\n" +
                "        \"Fri\",\n" +
                "        \"Sat\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"decimalNumberSymbols\": {\n" +
                "      \"negativeSign\": \"-\",\n" +
                "      \"positiveSign\": \"+\",\n" +
                "      \"zeroDigit\": \"0\",\n" +
                "      \"currencySymbol\": \"$\",\n" +
                "      \"decimalSeparator\": \".\",\n" +
                "      \"exponentSymbol\": \"E\",\n" +
                "      \"groupSeparator\": \",\",\n" +
                "      \"infinitySymbol\": \"∞\",\n" +
                "      \"monetaryDecimalSeparator\": \".\",\n" +
                "      \"nanSymbol\": \"NaN\",\n" +
                "      \"percentSymbol\": \"%\",\n" +
                "      \"permillSymbol\": \"‰\"\n" +
                "    },\n" +
                "    \"formatter\": \"different-formatter-selector-222\",\n" +
                "    \"locale\": \"en-NZ\",\n" +
                "    \"output\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"*different-output*\"\n" +
                "    },\n" +
                "    \"parser\": \"different-parser-selector-222\",\n" +
                "    \"style\": {\n" +
                "      \"color\": \"white\"\n" +
                "    },\n" +
                "    \"validator\": \"email-address\"\n" +
                "  }\n" +
                "}"
        );
    }

    @Override
    public SpreadsheetCellSet unmarshall(final JsonNode jsonNode,
                                         final JsonNodeUnmarshallContext context) {
        return SpreadsheetCellSet.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetCellSet createJsonNodeMarshallingValue() {
        return this.createSet();
    }

    // UrlFragment......................................................................................................

    @Test
    public void testUrlFragment() {
        this.urlFragmentAndCheck(
            this.createSet(),
            "%7B%0A%20%20%22A1%22:%20%7B%0A%20%20%20%20%22formula%22:%20%7B%0A%20%20%20%20%20%20%22valueType%22:%20%22Text%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22currency%22:%20%22AUD%22,%0A%20%20%20%20%22currencyExchangeRater%22:%20%22test-currency-exchange-rater-111%22,%0A%20%20%20%20%22dateTimeSymbols%22:%20%7B%0A%20%20%20%20%20%20%22ampms%22:%20[%0A%20%20%20%20%20%20%20%20%22am%22,%0A%20%20%20%20%20%20%20%20%22pm%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22monthNames%22:%20[%0A%20%20%20%20%20%20%20%20%22January%22,%0A%20%20%20%20%20%20%20%20%22February%22,%0A%20%20%20%20%20%20%20%20%22March%22,%0A%20%20%20%20%20%20%20%20%22April%22,%0A%20%20%20%20%20%20%20%20%22May%22,%0A%20%20%20%20%20%20%20%20%22June%22,%0A%20%20%20%20%20%20%20%20%22July%22,%0A%20%20%20%20%20%20%20%20%22August%22,%0A%20%20%20%20%20%20%20%20%22September%22,%0A%20%20%20%20%20%20%20%20%22October%22,%0A%20%20%20%20%20%20%20%20%22November%22,%0A%20%20%20%20%20%20%20%20%22December%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22monthNameAbbreviations%22:%20[%0A%20%20%20%20%20%20%20%20%22Jan.%22,%0A%20%20%20%20%20%20%20%20%22Feb.%22,%0A%20%20%20%20%20%20%20%20%22Mar.%22,%0A%20%20%20%20%20%20%20%20%22Apr.%22,%0A%20%20%20%20%20%20%20%20%22May%22,%0A%20%20%20%20%20%20%20%20%22Jun.%22,%0A%20%20%20%20%20%20%20%20%22Jul.%22,%0A%20%20%20%20%20%20%20%20%22Aug.%22,%0A%20%20%20%20%20%20%20%20%22Sep.%22,%0A%20%20%20%20%20%20%20%20%22Oct.%22,%0A%20%20%20%20%20%20%20%20%22Nov.%22,%0A%20%20%20%20%20%20%20%20%22Dec.%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22weekDayNames%22:%20[%0A%20%20%20%20%20%20%20%20%22Sunday%22,%0A%20%20%20%20%20%20%20%20%22Monday%22,%0A%20%20%20%20%20%20%20%20%22Tuesday%22,%0A%20%20%20%20%20%20%20%20%22Wednesday%22,%0A%20%20%20%20%20%20%20%20%22Thursday%22,%0A%20%20%20%20%20%20%20%20%22Friday%22,%0A%20%20%20%20%20%20%20%20%22Saturday%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22weekDayNameAbbreviations%22:%20[%0A%20%20%20%20%20%20%20%20%22Sun.%22,%0A%20%20%20%20%20%20%20%20%22Mon.%22,%0A%20%20%20%20%20%20%20%20%22Tue.%22,%0A%20%20%20%20%20%20%20%20%22Wed.%22,%0A%20%20%20%20%20%20%20%20%22Thu.%22,%0A%20%20%20%20%20%20%20%20%22Fri.%22,%0A%20%20%20%20%20%20%20%20%22Sat.%22%0A%20%20%20%20%20%20]%0A%20%20%20%20%7D,%0A%20%20%20%20%22decimalNumberSymbols%22:%20%7B%0A%20%20%20%20%20%20%22negativeSign%22:%20%22-%22,%0A%20%20%20%20%20%20%22positiveSign%22:%20%22+%22,%0A%20%20%20%20%20%20%22zeroDigit%22:%20%220%22,%0A%20%20%20%20%20%20%22currencySymbol%22:%20%22$%22,%0A%20%20%20%20%20%20%22decimalSeparator%22:%20%22.%22,%0A%20%20%20%20%20%20%22exponentSymbol%22:%20%22e%22,%0A%20%20%20%20%20%20%22groupSeparator%22:%20%22,%22,%0A%20%20%20%20%20%20%22infinitySymbol%22:%20%22%E2%88%9E%22,%0A%20%20%20%20%20%20%22monetaryDecimalSeparator%22:%20%22.%22,%0A%20%20%20%20%20%20%22nanSymbol%22:%20%22NaN%22,%0A%20%20%20%20%20%20%22percentSymbol%22:%20%22%25%22,%0A%20%20%20%20%20%20%22permillSymbol%22:%20%22%E2%80%B0%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22formatter%22:%20%22formatter-selector-111%22,%0A%20%20%20%20%22locale%22:%20%22en-AU%22,%0A%20%20%20%20%22output%22:%20%7B%0A%20%20%20%20%20%20%22type%22:%20%22text%22,%0A%20%20%20%20%20%20%22value%22:%20%22*output*%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22parser%22:%20%22parser-selector-111%22,%0A%20%20%20%20%22style%22:%20%7B%0A%20%20%20%20%20%20%22color%22:%20%22black%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22validator%22:%20%22absolute-url%22%0A%20%20%7D,%0A%20%20%22B2%22:%20%7B%0A%20%20%20%20%22formula%22:%20%7B%0A%20%20%20%20%20%20%22valueType%22:%20%22Number%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22currency%22:%20%22NZD%22,%0A%20%20%20%20%22currencyExchangeRater%22:%20%22test-different-currency-exchange-rater-222%22,%0A%20%20%20%20%22dateTimeSymbols%22:%20%7B%0A%20%20%20%20%20%20%22ampms%22:%20[%0A%20%20%20%20%20%20%20%20%22AM%22,%0A%20%20%20%20%20%20%20%20%22PM%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22monthNames%22:%20[%0A%20%20%20%20%20%20%20%20%22January%22,%0A%20%20%20%20%20%20%20%20%22February%22,%0A%20%20%20%20%20%20%20%20%22March%22,%0A%20%20%20%20%20%20%20%20%22April%22,%0A%20%20%20%20%20%20%20%20%22May%22,%0A%20%20%20%20%20%20%20%20%22June%22,%0A%20%20%20%20%20%20%20%20%22July%22,%0A%20%20%20%20%20%20%20%20%22August%22,%0A%20%20%20%20%20%20%20%20%22September%22,%0A%20%20%20%20%20%20%20%20%22October%22,%0A%20%20%20%20%20%20%20%20%22November%22,%0A%20%20%20%20%20%20%20%20%22December%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22monthNameAbbreviations%22:%20[%0A%20%20%20%20%20%20%20%20%22Jan%22,%0A%20%20%20%20%20%20%20%20%22Feb%22,%0A%20%20%20%20%20%20%20%20%22Mar%22,%0A%20%20%20%20%20%20%20%20%22Apr%22,%0A%20%20%20%20%20%20%20%20%22May%22,%0A%20%20%20%20%20%20%20%20%22Jun%22,%0A%20%20%20%20%20%20%20%20%22Jul%22,%0A%20%20%20%20%20%20%20%20%22Aug%22,%0A%20%20%20%20%20%20%20%20%22Sep%22,%0A%20%20%20%20%20%20%20%20%22Oct%22,%0A%20%20%20%20%20%20%20%20%22Nov%22,%0A%20%20%20%20%20%20%20%20%22Dec%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22weekDayNames%22:%20[%0A%20%20%20%20%20%20%20%20%22Sunday%22,%0A%20%20%20%20%20%20%20%20%22Monday%22,%0A%20%20%20%20%20%20%20%20%22Tuesday%22,%0A%20%20%20%20%20%20%20%20%22Wednesday%22,%0A%20%20%20%20%20%20%20%20%22Thursday%22,%0A%20%20%20%20%20%20%20%20%22Friday%22,%0A%20%20%20%20%20%20%20%20%22Saturday%22%0A%20%20%20%20%20%20],%0A%20%20%20%20%20%20%22weekDayNameAbbreviations%22:%20[%0A%20%20%20%20%20%20%20%20%22Sun%22,%0A%20%20%20%20%20%20%20%20%22Mon%22,%0A%20%20%20%20%20%20%20%20%22Tue%22,%0A%20%20%20%20%20%20%20%20%22Wed%22,%0A%20%20%20%20%20%20%20%20%22Thu%22,%0A%20%20%20%20%20%20%20%20%22Fri%22,%0A%20%20%20%20%20%20%20%20%22Sat%22%0A%20%20%20%20%20%20]%0A%20%20%20%20%7D,%0A%20%20%20%20%22decimalNumberSymbols%22:%20%7B%0A%20%20%20%20%20%20%22negativeSign%22:%20%22-%22,%0A%20%20%20%20%20%20%22positiveSign%22:%20%22+%22,%0A%20%20%20%20%20%20%22zeroDigit%22:%20%220%22,%0A%20%20%20%20%20%20%22currencySymbol%22:%20%22$%22,%0A%20%20%20%20%20%20%22decimalSeparator%22:%20%22.%22,%0A%20%20%20%20%20%20%22exponentSymbol%22:%20%22E%22,%0A%20%20%20%20%20%20%22groupSeparator%22:%20%22,%22,%0A%20%20%20%20%20%20%22infinitySymbol%22:%20%22%E2%88%9E%22,%0A%20%20%20%20%20%20%22monetaryDecimalSeparator%22:%20%22.%22,%0A%20%20%20%20%20%20%22nanSymbol%22:%20%22NaN%22,%0A%20%20%20%20%20%20%22percentSymbol%22:%20%22%25%22,%0A%20%20%20%20%20%20%22permillSymbol%22:%20%22%E2%80%B0%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22formatter%22:%20%22different-formatter-selector-222%22,%0A%20%20%20%20%22locale%22:%20%22en-NZ%22,%0A%20%20%20%20%22output%22:%20%7B%0A%20%20%20%20%20%20%22type%22:%20%22text%22,%0A%20%20%20%20%20%20%22value%22:%20%22*different-output*%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22parser%22:%20%22different-parser-selector-222%22,%0A%20%20%20%20%22style%22:%20%7B%0A%20%20%20%20%20%20%22color%22:%20%22white%22%0A%20%20%20%20%7D,%0A%20%20%20%20%22validator%22:%20%22email-address%22%0A%20%20%7D%0A%7D"
        );
    }

    // HasSpreadsheetCellValueKind......................................................................................

    @Test
    public void testSpreadsheetCellValueKind() {
        this.spreadsheetCellValueKindAndCheck(
            this.createSet(),
            SpreadsheetCellValueKind.CELL
        );
    }

    // HasCsvStringList.................................................................................................

    @Test
    public void testCsvStringList() {
        this.csvStringListAndCheck(
            this.createSet(),
            CsvStringList.parse(
                "\"A1,,AUD,test-currency-exchange-rater-111,\"\"\"\"\"\"am,pm\"\"\"\",\"\"\"\"January,February,March,April,May,June,July,August,September,October,November,December\"\"\"\",\"\"\"\"Jan.,Feb.,Mar.,Apr.,May,Jun.,Jul.,Aug.,Sep.,Oct.,Nov.,Dec.\"\"\"\",\"\"\"\"Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday\"\"\"\",\"\"\"\"Sun.,Mon.,Tue.,Wed.,Thu.,Fri.,Sat.\"\"\"\"\"\",\"\"-,+,0,$,.,e,\"\"\"\",\"\"\"\",∞,.,NaN,%,‰\"\",formatter-selector-111,en-AU,\"\"{\"\"\"\"type\"\"\"\": \"\"\"\"text\"\"\"\",\"\"\"\"value\"\"\"\": \"\"\"\"*output*\"\"\"\"}\"\",parser-selector-111,color: black;,absolute-url,,Text\"\n" +
                    "\"B2,,NZD,test-different-currency-exchange-rater-222,\"\"\"\"\"\"AM,PM\"\"\"\",\"\"\"\"January,February,March,April,May,June,July,August,September,October,November,December\"\"\"\",\"\"\"\"Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec\"\"\"\",\"\"\"\"Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday\"\"\"\",\"\"\"\"Sun,Mon,Tue,Wed,Thu,Fri,Sat\"\"\"\"\"\",\"\"-,+,0,$,.,E,\"\"\"\",\"\"\"\",∞,.,NaN,%,‰\"\",different-formatter-selector-222,en-NZ,\"\"{\"\"\"\"type\"\"\"\": \"\"\"\"text\"\"\"\",\"\"\"\"value\"\"\"\": \"\"\"\"*different-output*\"\"\"\"}\"\",different-parser-selector-222,color: white;,email-address,,Number\""
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetCellSet> type() {
        return SpreadsheetCellSet.class;
    }
}
