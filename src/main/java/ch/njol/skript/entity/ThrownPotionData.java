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
package ch.njol.skript.entity;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.Aliases;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.localization.Adjective;
import ch.njol.skript.localization.Language;
import ch.njol.skript.localization.Noun;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.Converters;
import ch.njol.util.coll.CollectionUtils;

/**
 * @author Peter Güttinger
 */
public class ThrownPotionData extends EntityData<ThrownPotion> {
	static {
		EntityData.register(ThrownPotionData.class, "thrown potion", ThrownPotion.class, "thrown potion");
	}
	
	private static final Adjective m_adjective = new Adjective("entities.thrown potion.adjective");
	private static final boolean RUNNING_LEGACY = !Skript.isRunningMinecraft(1, 13);
	private static final ItemType POTION = Aliases.javaItemType("potion");
	private static final ItemType SPLASH_POTION = Aliases.javaItemType("splash potion");
	private static final ItemType LINGER_POTION = Aliases.javaItemType("lingering potion");
	
	@Nullable
	private ItemType[] types;
	
	@Override
	protected boolean init(final Literal<?>[] exprs, final int matchedPattern, final ParseResult parseResult) {
		if (exprs.length > 0 && exprs[0] != null) {
			return (types = Converters.convert((ItemType[]) exprs[0].getAll(), ItemType.class, t -> {
				// If the itemtype is a potion, lets make it a splash potion (required by Bukkit)
				// Due to an issue with 1.12.2 and below, we have to force a lingering potion to be a splash potion
				if (t.isSupertypeOf(POTION) || (t.isSupertypeOf(LINGER_POTION) && RUNNING_LEGACY)) {
					ItemMeta meta = t.getItemMeta();
					ItemType itemType = SPLASH_POTION.clone();
					itemType.setItemMeta(meta);
					return itemType;
				} else if (!t.isSupertypeOf(SPLASH_POTION ) && !t.isSupertypeOf(LINGER_POTION)) {
					return null;
				}
				return t;
			})).length != 0; // no error message - other things can be thrown as well
		} else {
			types = new ItemType[]{SPLASH_POTION.clone()};
		}
		return true;
	}
	
	@Override
	protected boolean init(final @Nullable Class<? extends ThrownPotion> c, final @Nullable ThrownPotion e) {
		if (e != null) {
			final ItemStack i = e.getItem();
			types = new ItemType[] {new ItemType(i)};
		}
		return true;
	}
	
	@Override
	protected boolean match(final ThrownPotion entity) {
		if (types != null) {
			for (final ItemType t : types) {
				if (t.isOfType(entity.getItem()))
					return true;
			}
			return false;
		}
		return true;
	}
	
	@Nullable
	@Override
	public ThrownPotion spawn(Location loc) {
		ItemType t = CollectionUtils.getRandom(types);
		assert t != null;
		final ItemStack i = t.getRandom();
		if (i == null) {
			return null;
		}
		ThrownPotion potion = loc.getWorld().spawn(loc, ThrownPotion.class);
		potion.setItem(i);
		return potion;
	}
	
	@Override
	public void set(final ThrownPotion entity) {
		if (types != null) {
			final ItemType t = CollectionUtils.getRandom(types);
			assert t != null;
			ItemStack i = t.getRandom();
			if (i == null)
				return; // Missing item, can't make thrown potion of it
			entity.setItem(i);
		}
		assert false;
	}
	
	@Override
	public Class<? extends ThrownPotion> getType() {
		return ThrownPotion.class;
	}
	
	@Override
	public EntityData getSuperType() {
		return new ThrownPotionData();
	}
	
	@Override
	public boolean isSupertypeOf(final EntityData<?> e) {
		if (!(e instanceof ThrownPotionData))
			return false;
		final ThrownPotionData d = (ThrownPotionData) e;
		if (types != null) {
			return d.types != null && ItemType.isSubset(types, d.types);
		}
		return true;
	}
	
	@Override
	public String toString(final int flags) {
		final ItemType[] types = this.types;
		if (types == null)
			return super.toString(flags);
		final StringBuilder b = new StringBuilder();
		b.append(Noun.getArticleWithSpace(types[0].getTypes().get(0).getGender(), flags));
		b.append(m_adjective.toString(types[0].getTypes().get(0).getGender(), flags));
		b.append(" ");
		b.append(Classes.toString(types, flags & Language.NO_ARTICLE_MASK, false));
		return "" + b.toString();
	}
	
	//		return ItemType.serialize(types);
	@Override
	@Deprecated
	protected boolean deserialize(final String s) {
		throw new UnsupportedOperationException("old serialization is no longer supported");
//		if (s.isEmpty())
//			return true;
//		types = ItemType.deserialize(s);
//		return types != null;
	}
	
	@Override
	protected boolean equals_i(final EntityData<?> obj) {
		if (!(obj instanceof ThrownPotionData))
			return false;
		return Arrays.equals(types, ((ThrownPotionData) obj).types);
	}
	
	@Override
	protected int hashCode_i() {
		return Arrays.hashCode(types);
	}
	
}
