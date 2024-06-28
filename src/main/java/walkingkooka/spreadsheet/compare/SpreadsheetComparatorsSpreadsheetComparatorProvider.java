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

package walkingkooka.spreadsheet.compare;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.UrlPath;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetComparatorProvider} that uses the {@link SpreadsheetComparatorProvider} defined in {@link SpreadsheetComparators}.
 */
final class SpreadsheetComparatorsSpreadsheetComparatorProvider implements SpreadsheetComparatorProvider {

    /**
     * Singleton
     */
    final static SpreadsheetComparatorsSpreadsheetComparatorProvider INSTANCE = new SpreadsheetComparatorsSpreadsheetComparatorProvider();

    private SpreadsheetComparatorsSpreadsheetComparatorProvider() {
        super();
    }

    @Override
    public Optional<SpreadsheetComparator<?>> spreadsheetComparator(final SpreadsheetComparatorName name) {
        Objects.requireNonNull(name, "name");

        return Optional.ofNullable(
                NAME_TO_COMPARATOR.get(name)
        );
    }

    @Override
    public Set<SpreadsheetComparatorInfo> spreadsheetComparatorInfos() {
        return NAME_TO_COMPARATOR.keySet()
                .stream()
                .map(n -> SpreadsheetComparatorInfo.with(
                        SpreadsheetComparatorProviders.BASE_URL.appendPath(
                                UrlPath.parse(
                                        n.value()
                                )
                        ),
                                n
                        )
                ).collect(Collectors.toCollection(Sets::sorted));
    }

    private final static Map<SpreadsheetComparatorName, SpreadsheetComparator<?>> NAME_TO_COMPARATOR = Maps.readOnly(
            Lists.of(
                    SpreadsheetComparators.date(),
                    SpreadsheetComparators.dateTime(),
                    SpreadsheetComparators.dayOfMonth(),
                    SpreadsheetComparators.dayOfWeek(),
                    SpreadsheetComparators.hourOfAmPm(),
                    SpreadsheetComparators.hourOfDay(),
                    SpreadsheetComparators.minuteOfHour(),
                    SpreadsheetComparators.monthOfYear(),
                    SpreadsheetComparators.nanoOfSecond(),
                    SpreadsheetComparators.number(),
                    SpreadsheetComparators.secondsOfMinute(),
                    SpreadsheetComparators.text(),
                    SpreadsheetComparators.textCaseInsensitive(),
                    SpreadsheetComparators.time(),
                    SpreadsheetComparators.year()
            ).stream()
                    .collect(Collectors.toMap(n -> n.name(), n -> n))
    );

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
