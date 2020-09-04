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
package ch.njol.skript.effects;

import java.net.InetSocketAddress;
import java.util.Date;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;

@Name("Ban")
@Description({"Bans or unbans a player or an IP address.",
	"If a reason is given, it will be shown to the player when they try to join the server while banned.",
	"A length of ban may also be given to apply a temporary ban. If it is absent for any reason, a permanent ban will be used instead.",
	"We recommend that you test your scripts so that no accidental permanent bans are applied.",
	"",
	"Note that banning people does not kick them from the server.",
	"Consider using the <a href='effects.html#EffKick'>kick effect</a> after applying a ban."})
@Examples({"unban player",
	"ban \"127.0.0.1\"",
	"IP-ban the player because \"he is an idiot\"",
	"ban player due to \"inappropriate language\" for 2 days"})
@Since("1.4, 2.1.1 (ban reason), 2.5 (timespan)")
public class EffBan extends Effect {
	
	static {
		Skript.registerEffect(EffBan.class,
			"ban %strings/offlineplayers% [(by reason of|because [of]|on account of|due to) %-string%] [for %-timespan%]", "unban %strings/offlineplayers%",
			"ban %players% by IP [(by reason of|because [of]|on account of|due to) %-string%] [for %-timespan%]", "unban %players% by IP",
			"IP(-| )ban %players% [(by reason of|because [of]|on account of|due to) %-string%] [for %-timespan%]", "(IP(-| )unban|un[-]IP[-]ban) %players%");
	}
	
	@SuppressWarnings("null")
	private Expression<?> players;
	@Nullable
	private Expression<String> reason;
	@Nullable
	private Expression<Timespan> expires;
	
	private boolean ban;
	private boolean ipBan;
	
	@SuppressWarnings({"null", "unchecked"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		players = exprs[0];
		reason = exprs.length > 1 ? (Expression<String>) exprs[1] : null;
		expires = exprs.length > 1 ? (Expression<Timespan>) exprs[2] : null;
		ban = matchedPattern % 2 == 0;
		ipBan = matchedPattern >= 2;
		return true;
	}
	
	@SuppressWarnings("null")
	@Override
	protected void execute(final Event e) {
		final String reason = this.reason != null ? this.reason.getSingle(e) : null; // don't check for null, just ignore an invalid reason
		Timespan ts = this.expires != null ? this.expires.getSingle(e) : null;
		final Date expires = ts != null ? new Date(System.currentTimeMillis() + ts.getMilliSeconds()) : null;
		final String source = "Skript ban effect";
		for (final Object o : players.getArray(e)) {
			if (o instanceof Player) {
				if (ipBan) {
					InetSocketAddress addr = ((Player) o).getAddress();
					if (addr == null)
						return; // Can't ban unknown IP
					final String ip = addr.getAddress().getHostAddress();
					if (ban)
						Bukkit.getBanList(BanList.Type.IP).addBan(ip, reason, expires, source);
					else
						Bukkit.getBanList(BanList.Type.IP).pardon(ip);
				} else {
					if (ban)
						Bukkit.getBanList(BanList.Type.NAME).addBan(((Player) o).getName(), reason, expires, source); // FIXME [UUID] ban UUID
					else
						Bukkit.getBanList(BanList.Type.NAME).pardon(((Player) o).getName());
				}
			} else if (o instanceof OfflinePlayer) {
				String name = ((OfflinePlayer) o).getName();
				if (name == null)
					return; // Can't ban, name unknown
				if (ban)
					Bukkit.getBanList(BanList.Type.NAME).addBan(name, reason, expires, source);
				else
					Bukkit.getBanList(BanList.Type.NAME).pardon(name);
			} else if (o instanceof String) {
				final String s = (String) o;
				if (ban) {
					Bukkit.getBanList(BanList.Type.IP).addBan(s, reason, expires, source);
					Bukkit.getBanList(BanList.Type.NAME).addBan(s, reason, expires, source);
				} else {
					Bukkit.getBanList(BanList.Type.IP).pardon(s);
					Bukkit.getBanList(BanList.Type.NAME).pardon(s);
				}
			} else {
				assert false;
			}
		}
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return (ipBan ? "IP-" : "") + (ban ? "" : "un") + "ban " + players.toString(e, debug) +
			(reason != null ? " on account of " + reason.toString(e, debug) : "") + (expires != null ? " for " + expires.toString(e, debug) : "");
	}
	
}
