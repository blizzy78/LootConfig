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

package de.blizzy.rust.lootconfig.model;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class LootConfig {
	@Expose(deserialize=true, serialize=true)
	public int Version;
	@Expose(deserialize=true, serialize=true)
	public int VersionConfig;
	@Expose(deserialize=true, serialize=true)
	public int WorldSize;
	@Expose(deserialize=true, serialize=true)
	public int WorldSeed;
	@Expose(deserialize=true, serialize=true)
	public Map<String, LootContainer> LootContainers;
	@Expose(deserialize=true, serialize=true)
	public Map<String, Map<String, SpawnGroup>> SpawnGroups;
	@Expose(deserialize=true, serialize=true)
	public Map<String, ItemModReveal> ItemModReveals;
	@Expose(deserialize=true, serialize=true)
	public Map<String, ItemModUnwrap> ItemModUnwraps;
	@Expose(deserialize=true, serialize=true)
	public Map<String, Category> Categories;

	public Map<String, Item> allItems = new HashMap<>();

	public void initAfterDeserialize() {
		Categories.values().stream()
			.flatMap(category -> category.Items.stream())
			.map(itemSpawn -> itemSpawn._Shortname)
			.map(Item::new)
			.forEach(item -> allItems.put(item.Shortname, item));
		Categories.values().stream()
			.flatMap(category -> category.Blueprints.stream())
			.map(blueprintSpawn -> blueprintSpawn._Shortname)
			.map(Item::new)
			.forEach(item -> allItems.put(item.Shortname, item));

		Categories.entrySet().forEach(entry -> entry.getValue().initAfterDeserialize(entry.getKey(), allItems, Categories));

		LootContainers.entrySet().forEach(entry -> entry.getValue().initAfterDeserialize(entry.getKey(), Categories));
		SpawnGroups.values().stream()
			.flatMap(group -> group.values().stream())
			.forEach(spawnGroup -> spawnGroup.initAfterDeserialize(Categories));
		ItemModReveals.values().forEach(reveal -> reveal.initAfterDeserialize(Categories));
		ItemModUnwraps.values().forEach(reveal -> reveal.initAfterDeserialize(Categories));
	}
}
