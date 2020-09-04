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

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;

@Name("Is Solid")
@Description("Checks whether an item is solid.")
@Examples({"grass block is solid", "player's tool isn't solid"})
@Since("2.2-dev36")
public class CondIsSolid extends PropertyCondition<ItemType> {
	
	static {
		register(CondIsSolid.class, "solid", "itemtypes");
	}
	
	@Override
	public boolean check(ItemType i) {
		return i.getMaterial().isSolid();
	}
	
	@Override
	protected String getPropertyName() {
		return "solid";
	}
	
}
