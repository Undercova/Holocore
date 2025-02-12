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

import com.projectswg.common.data.CRC
import com.projectswg.common.network.packets.swg.zone.SceneDestroyObject
import com.projectswg.common.network.packets.swg.zone.deltas.DeltasMessage
import com.projectswg.common.network.packets.swg.zone.object_controller.CommandQueueEnqueue
import com.projectswg.common.network.packets.swg.zone.object_controller.CommandTimer
import com.projectswg.common.network.packets.swg.zone.object_controller.combat.CombatAction
import com.projectswg.holocore.resources.support.objects.swg.SWGObject
import com.projectswg.holocore.resources.support.objects.swg.creature.CreatureObject
import com.projectswg.holocore.resources.support.objects.swg.tangible.TangibleObject
import me.joshlarson.jlcommon.log.Log
import java.util.concurrent.TimeUnit

private fun ZonedInCharacter.sendAttackCommand(target: SWGObject, attackCommand: String) {
	val targetObjectId = target.objectId
	val commandQueueEnqueue = CommandQueueEnqueue(player.creatureObject.objectId, 0, CRC.getCrc(attackCommand.lowercase()), targetObjectId, "")
	sendPacket(player, commandQueueEnqueue)
	val commandTimer = player.waitForNextPacket(CommandTimer::class.java, 80, TimeUnit.MILLISECONDS)

	if (commandTimer != null) {
		if (!commandTimer.flags.contains(CommandTimer.CommandTimerFlag.EXECUTE)) {
			throw RuntimeException("CommandTimer packet was sent, but the command was not executed: ${commandTimer.flags}")
		}
	} else {
		throw RuntimeException("No CommandTimer packet was sent at all")
	}

	player.waitForNextPacket(CombatAction::class.java, 50, TimeUnit.MILLISECONDS) ?: java.lang.IllegalStateException("No known packet received")
}

/**
 * Executes the specified combat command on the target.
 * @param target The object to attack
 * @param overrideAttackCommand Override which attack to use - defaults to the default attack of the equipped weapon
 */
fun ZonedInCharacter.attack(target: TangibleObject, overrideAttackCommand: String? = null) {
	if (target is CreatureObject) {
		attack(target, overrideAttackCommand)
	}

	val attackCommand = overrideAttackCommand ?: player.creatureObject.equippedWeapon.type.defaultAttack
	sendAttackCommand(target, attackCommand)
	val packet = player.waitForNextPacket(setOf(DeltasMessage::class.java, SceneDestroyObject::class.java), 50, TimeUnit.MILLISECONDS) ?: java.lang.IllegalStateException("Packet not received")

	if (packet is SceneDestroyObject) {
		if (packet.objectId != target.objectId) {
			throw IllegalStateException("SceneDestroyObject packet received, but the object ID did not match the target")
		}
	} else if (packet is DeltasMessage) {
		if (packet.objectId != target.objectId) {
			throw IllegalStateException("DeltasMessage packet received, but the object ID did not match the target")
		}
	}

	throw IllegalStateException("Unhandled packet received: $packet")
}

/**
 * Executes the specified combat command on the target.
 * @param target The object to attack
 * @param overrideAttackCommand Override which attack to use - defaults to the default attack of the equipped weapon
 */
fun ZonedInCharacter.attack(target: CreatureObject, overrideAttackCommand: String? = null) {
	val attackCommand = overrideAttackCommand ?: player.creatureObject.equippedWeapon.type.defaultAttack
	waitUntilAwareOf(target)
	player.clearPacketList()
	sendAttackCommand(target, attackCommand)
}

fun ZonedInCharacter.deathblow(target: CreatureObject) {
	sendCommand("deathBlow", target)
}

fun ZonedInCharacter.duel(target: CreatureObject) {
	sendCommand("duel", target)
}

fun ZonedInCharacter.heal(target: CreatureObject? = null, healCommand: String) {
	sendCommand(healCommand, target)
	waitForHealthChange(target ?: player.creatureObject)
}

fun ZonedInCharacter.waitForHealthChange(target: CreatureObject = player.creatureObject) {
	val before = target.health
	player.waitForNextObjectDelta(target.objectId, 6, 14, 1, TimeUnit.SECONDS) ?: throw IllegalStateException("No object delta received")
	val after = target.health
	Log.d("Before/After: $before / $after")
}
