/*

LootConfigDump - Dumps statistics about loot config on Rust servers.
Copyright (C) 2016 Maik Schreiber

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package de.blizzy.rust.lootconfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import de.blizzy.rust.lootconfig.model.Category;
import de.blizzy.rust.lootconfig.model.LootConfig;
import de.blizzy.rust.lootconfig.model.LootContainer;
import de.blizzy.rust.lootconfig.model.SubSpawn;

public class LootConfigDump {
	private static final boolean SHOW_DMLOOT = false;

	private File configFile;

	public static void main(String... args) throws IOException {
		File configFile = new File(args[0]);
		new LootConfigDump(configFile).run();
	}

	public LootConfigDump(File configFile) {
		this.configFile = configFile;
	}

	private void run() throws IOException {
		LootConfig config = loadConfig(configFile);

		Table<LootContainer, Category, Multiset<Float>> dropChances = HashBasedTable.create();

		Collection<LootContainer> lootContainers = config.LootContainers.values();
		config.Categories.values().stream()
			.filter(Category::hasItemsOrBlueprints)
			.forEach(category -> {
				lootContainers.forEach(lootContainer -> {
					Multiset<Float> categoryInContainerDropChances = getItemCategoryDropChances(category, lootContainer);
					if (!categoryInContainerDropChances.isEmpty()) {
						dropChances.put(lootContainer, category, categoryInContainerDropChances);
					}
				});
			});

		dropChances.rowKeySet().stream()
			.filter(lootContainer -> SHOW_DMLOOT || !lootContainer.name.contains("dmloot"))
			.sorted((lootContainer1, lootContainer2) -> Collator.getInstance().compare(lootContainer1.name, lootContainer2.name))
			.forEach(lootContainer -> {
				System.out.printf("%s (blueprint fragments: %s)",
						lootContainer,
						lootContainer.DistributeFragments ? "yes" : "no").println();
				Map<Category, Multiset<Float>> lootContainerDropChances = dropChances.row(lootContainer);
				AtomicDouble lootContainerDropChancesSum = new AtomicDouble();
				AtomicInteger categoriesCount = new AtomicInteger();
				lootContainerDropChances.entrySet().stream()
					.sorted(this::compareByChances)
					.limit(7)
					.forEach(categoryDropChancesEntry -> {
						Category category = categoryDropChancesEntry.getKey();
						Multiset<Float> categoryDropChances = categoryDropChancesEntry.getValue();
						float categoryDropChancesSum = sum(categoryDropChances);
						lootContainerDropChancesSum.addAndGet(categoryDropChancesSum);
						System.out.printf("  %s %s%s%s",
								formatPercent(categoryDropChancesSum),
								category,
								(category.Items.size() > 0) ? " (" + formatItems(category) + ")" : "",
								(category.Blueprints.size() > 0) ? " [" + formatBlueprints(category) + "]" : "").println();
						categoriesCount.incrementAndGet();
					});
				if (categoriesCount.get() < lootContainerDropChances.size()) {
					System.out.printf("  %s other (%d)",
							formatPercent(1f - (float) lootContainerDropChancesSum.get()),
							lootContainerDropChances.size() - categoriesCount.get()).println();
				}
			});
	}

	private String formatItems(Category category) {
		return category.Items.stream()
			.sorted((itemSpawn1, itemSpawn2) -> Collator.getInstance().compare(itemSpawn1.item.Shortname, itemSpawn2.item.Shortname))
			.map(itemSpawn -> {
				if (itemSpawn.Amount > 1) {
					return String.format("%s x%d", itemSpawn.item.Shortname, itemSpawn.Amount);
				} else {
					return itemSpawn.item.Shortname;
				}
			})
			.collect(Collectors.joining(", "));
	}

	private String formatBlueprints(Category category) {
		return category.Blueprints.stream()
			.sorted((blueprintSpawn1, blueprintSpawn2) -> Collator.getInstance().compare(blueprintSpawn1.item.Shortname, blueprintSpawn2.item.Shortname))
			.peek(blueprintSpawn -> {
				if (blueprintSpawn.item == null) {
					throw new IllegalStateException("blueprint spawn without item");
				}
			})
			.map(blueprintSpawn -> blueprintSpawn.item.Shortname)
			.collect(Collectors.joining(", "));
	}

	private float sum(Multiset<Float> chances) {
		return (float) (double) chances.stream().collect(Collectors.summingDouble(f -> f));
	}

	private int compareByChances(Map.Entry<Category, Multiset<Float>> entry1, Map.Entry<Category, Multiset<Float>> entry2) {
		return -Float.compare(sum(entry1.getValue()), sum(entry2.getValue()));
	}

	private String formatPercent(float chance) {
		return String.format(Locale.US, "%.3f%%", chance * 100f);
	}

	private Multiset<Float> getItemCategoryDropChances(Category itemCategory, LootContainer lootContainer) {
		Multiset<Float> itemCategoryDropChances = HashMultiset.create();

		SubSpawn rootSubSpawn = new SubSpawn();
		rootSubSpawn.Category = lootContainer.LootDefinition;
		rootSubSpawn.Weight = 1;
		fillItemCategoryDropChances(itemCategory, Collections.singleton(rootSubSpawn), 1f, itemCategoryDropChances);

		return itemCategoryDropChances;
	}

	private void fillItemCategoryDropChances(Category itemCategory, Set<SubSpawn> subSpawns, float parentChance, Multiset<Float> itemCategoryDropChances) {
		int totalWeight = subSpawns.stream().collect(Collectors.summingInt(subSpawn -> subSpawn.Weight));

		for (SubSpawn subSpawn : subSpawns) {
			float subSpawnChance = parentChance * subSpawn.Weight / totalWeight;
			if (subSpawn.Category == itemCategory) {
				itemCategoryDropChances.add(subSpawnChance);
			} else if (subSpawn.Category.hasSubSpawns()) {
				fillItemCategoryDropChances(itemCategory, subSpawn.Category.SubSpawn, subSpawnChance, itemCategoryDropChances);
			}
		}
	}

	private LootConfig loadConfig(File configFile) throws IOException {
		String json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
		Type type = new TypeToken<LootConfig>(){}.getType();
		GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls();
		Gson gson = gsonBuilder.create();
		LootConfig config = gson.fromJson(json, type);
		config.initAfterDeserialize();
		return config;
	}
}
