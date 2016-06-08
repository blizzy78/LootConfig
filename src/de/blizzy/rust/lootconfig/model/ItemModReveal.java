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

public class ItemModReveal {
	@Expose(deserialize=true, serialize=true)
	public int NumForReveal;
	@Expose(deserialize=true, serialize=true)
	// TODO: Item??
	public String RevealedItemOverride;
	@Expose(deserialize=true, serialize=true)
	public int RevealedItemAmount;
	@Expose(deserialize=true, serialize=true)
	public boolean AsBlueprint;
	@Expose(deserialize=true, serialize=true)
	@SerializedName("RevealList")
	public String _RevealList;

	public Category RevealList;

	public void initAfterDeserialize(Map<String, Category> allCategories) {
		RevealList = allCategories.get(_RevealList);
	}
}
