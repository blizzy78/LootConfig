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

public class BlueprintSpawn {
	@Expose(deserialize=true, serialize=true)
	@SerializedName("Shortname")
	public String _Shortname;
	@Expose(deserialize=true, serialize=true)
	public int Amount;

	public Item item;

	public void initAfterDeserialize(Map<String, Item> allItems) {
		item = allItems.get(_Shortname);
	}
}
