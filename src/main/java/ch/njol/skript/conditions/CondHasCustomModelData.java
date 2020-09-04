/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package ch.njol.skript.conditions;

import org.bukkit.inventory.meta.ItemMeta;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;

@Name("Has Custom Model Data")
@Description("Check if an item has a custom model data tag")
@Examples("player's tool has custom model data")
@RequiredPlugins("1.14+")
@Since("2.5")
public class CondHasCustomModelData extends PropertyCondition<ItemType> {
	
	static {
		if (Skript.methodExists(ItemMeta.class, "hasCustomModelData")) {
			register(CondHasCustomModelData.class, PropertyType.HAVE, "[custom] model data", "itemtypes");
		}
	}
	
	@Override
	public boolean check(ItemType item) {
		return item.getItemMeta().hasCustomModelData();
	}
	
	@Override
	protected String getPropertyName() {
		return "custom model data";
	}
	
}

