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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LootContainer {
	@Expose(deserialize=true, serialize=true)
	public boolean DestroyOnEmpty;
	@Expose(deserialize=true, serialize=true)
	@SerializedName("LootDefinition")
	public String _LootDefinition;
	@Expose(deserialize=true, serialize=true)
	public int MaxDefinitionsToSpawn;
	@Expose(deserialize=true, serialize=true)
	public float MinSecondsBetweenRefresh;
	@Expose(deserialize=true, serialize=true)
	public float MaxSecondsBetweenRefresh;
	@Expose(deserialize=true, serialize=true)
	public boolean DistributeFragments;

	public String name;
	public Category LootDefinition;

	public void initAfterDeserialize(String name, Map<String, Category> allCategories) {
		this.name = name;

		LootDefinition = allCategories.get(_LootDefinition);
	}

	@Override
	public String toString() {
		return name;
	}
}
