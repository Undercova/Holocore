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
package com.projectswg.holocore.headless

import com.projectswg.common.data.radial.RadialItem
import com.projectswg.common.network.packets.swg.zone.ClientOpenContainerMessage
import com.projectswg.common.network.packets.swg.zone.ObjectMenuSelect
import com.projectswg.holocore.resources.support.objects.swg.SWGObject
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject

/**
 * Radials the target and selects the "Loot" option. The loot window is expected to open and an exception will be thrown if it does not.
 * @return the objects that can be looted from the target
 */
fun ZonedInCharacter.loot(target: CreatureObject): Collection<SWGObject> {
	for (i in 0 until 50) {
		val inventory = target.getSlottedObject("inventory")
		if (inventory != null) {
			waitUntilAwareOf(inventory)
			break
		}
		Thread.sleep(20)
	}
	sendPacket(player, ObjectMenuSelect(target.objectId, RadialItem.LOOT.type.toShort()))
	player.waitForNextPacket(ClientOpenContainerMessage::class.java) ?: throw IllegalStateException("NPC inventory did not open!")
	return target.inventory.childObjects
}
