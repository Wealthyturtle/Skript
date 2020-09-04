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

import java.text.SimpleDateFormat;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Getter;
import ch.njol.util.Kleenean;

@Name("Formatted Time")
@Description("Converts date to human-readable text format. By default, 'yyyy-MM-dd HH:mm:ss z' (e.g. '2018-03-30 16:03:12 +01') will be used. For reference, see this "
		+ "<a href=\"https://en.wikipedia.org/wiki/ISO_8601\">Wikipedia article</a>.")
@Examples("now formatted human-readable")
@Since("2.2-dev31")
public class ExprFormatTime extends PropertyExpression<Date, String> {
	
	private static final String defaultFormat = "yyyy-MM-dd HH:mm:ss z";
	
	static {
		Skript.registerExpression(ExprFormatTime.class, String.class, ExpressionType.PROPERTY, "%dates% formatted [human-readable] [(with|as) %-string%]");
	}

	@SuppressWarnings("null")
	private SimpleDateFormat format;

	@Override
	@SuppressWarnings({"null", "unchecked"})
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<? extends Date>) exprs[0]);
		if (exprs[1] != null) {
			if (!(exprs[1] instanceof Literal)) {
				VariableString str = (VariableString) exprs[1];
				if (!str.isSimple()) {
					Skript.error("Date format must not contain variables!");
					return false;
				}
			}
			format = new SimpleDateFormat((String) exprs[1].getSingle(null));
		} else {
			format = new SimpleDateFormat(defaultFormat);
		}
		
		return true;
	}


	@Override
	protected String[] get(Event e, Date[] source) {
		return get(source, new Getter<String, Date>() {
			@Override
			public String get(Date date) {
				return format.format(new java.util.Date(date.getTimestamp()));
			}
		});
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return getExpr().toString(e, debug) + " formatted as " + format.toPattern();
	}

}
