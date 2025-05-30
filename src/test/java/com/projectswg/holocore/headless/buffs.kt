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

import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject
import java.util.concurrent.TimeUnit

fun ZonedInCharacter.sendSelfBuffCommand(buffCommand: String) {
	sendCommand(buffCommand)
	player.waitForNextObjectDelta(player.creatureObject.objectId, 6, 19, 1, TimeUnit.SECONDS) ?: throw IllegalStateException("Failed to receive buff object delta for player")
}

fun ZonedInCharacter.sendTargetBuffCommand(buffCommand: String, target: CreatureObject) {
	sendCommand(buffCommand, target)
	player.waitForNextObjectDelta(target.objectId, 6, 19, 1, TimeUnit.SECONDS) ?: throw IllegalStateException("Failed to receive buff object delta for player")
}
