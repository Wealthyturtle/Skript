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
package ch.njol.skript.expressions;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

/**
 * @author bi0qaw
 */
@Name("Vectors - Vector Between Locations")
@Description("Creates a vector between two locations.")
@Examples({"set {_v} to vector between {_loc1} and {_loc2}"})
@Since("2.2-dev28")
public class ExprVectorBetweenLocations extends SimpleExpression<Vector> {

	static {
		Skript.registerExpression(ExprVectorBetweenLocations.class, Vector.class, ExpressionType.SIMPLE,
				"[the] vector (from|between) %location% (to|and) %location%");
	}

	@SuppressWarnings("null")
	private Expression<Location> from, to;

	@Override
	@SuppressWarnings({"unchecked", "null"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		from = (Expression<Location>) exprs[0];
		to = (Expression<Location>) exprs[1];
		return true;
	}

	@Override
	@SuppressWarnings("null")
	protected Vector[] get(Event e) {
		Location l1 = from.getSingle(e);
		Location l2 = to.getSingle(e);
		if (l1 == null || l2 == null)
			return null;
		return CollectionUtils.array(new Vector(l2.getX() - l1.getX(), l2.getY() - l1.getY(), l2.getZ() - l1.getZ()));
	}

	@Override
	public boolean isSingle() {
		return true;
	}
	@Override
	public Class<? extends Vector> getReturnType() {
		return Vector.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "vector from " + from.toString(e, debug) + " to " + to.toString(e, debug);
	}

}
