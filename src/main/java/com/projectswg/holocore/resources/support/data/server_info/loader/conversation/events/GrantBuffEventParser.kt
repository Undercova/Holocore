/***********************************************************************************
 * Copyright (c) 2025 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is an emulation project for Star Wars Galaxies founded on            *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create one or more emulators which will provide servers for      *
 * players to continue playing a game similar to the one they used to play.        *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/
package com.projectswg.holocore.resources.support.data.server_info.loader.conversation.events

import com.projectswg.holocore.resources.gameplay.conversation.events.GrantBuffEvent
import com.projectswg.holocore.resources.support.data.server_info.loader.conversation.EventParser

class GrantBuffEventParser : EventParser<GrantBuffEvent> {
	@Suppress("UNCHECKED_CAST")
	override fun parse(args: Map<String, Any>): GrantBuffEvent {
		if (args.containsKey("buff")) {
			val buffName = args["buff"] as String
			return GrantBuffEvent(listOf(buffName))
		} else if (args.containsKey("buffs")) {
			val buffs = args["buffs"] as List<String>
			return GrantBuffEvent(buffs)
		} else {
			throw IllegalArgumentException("Args must contain either a buff or buffs key")
		}
	}
}
