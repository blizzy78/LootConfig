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

import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class Category {
	@Expose(deserialize=true, serialize=true)
	public Set<ItemSpawn> Items;
	@Expose(deserialize=true, serialize=true)
	public Set<BlueprintSpawn> Blueprints;
	@Expose(deserialize=true, serialize=true)
	public Set<SubSpawn> SubSpawn;

	public String name;

	public void initAfterDeserialize(String name, Map<String, Item> allItems, Map<String, Category> allCategories) {
		this.name = name;

		Items.forEach(itemSpawn -> itemSpawn.initAfterDeserialize(allItems));
		Blueprints.forEach(blueprintSpawn -> blueprintSpawn.initAfterDeserialize(allItems));
		SubSpawn.forEach(subSpawn -> subSpawn.initAfterDeserialize(allCategories));
	}

	public boolean hasItemsOrBlueprints() {
		return !Items.isEmpty() || !Blueprints.isEmpty() || (Items.isEmpty() && Blueprints.isEmpty() && SubSpawn.isEmpty());
	}

	public boolean hasSubSpawns() {
		return !SubSpawn.isEmpty();
	}

	@Override
	public String toString() {
		return name;
	}
}
