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

package com.projectswg.holocore.test.runners;

import com.projectswg.holocore.intents.support.objects.ObjectCreatedIntent;
import com.projectswg.holocore.resources.support.objects.swg.SWGObject;
import com.projectswg.holocore.utilities.HolocoreCoroutine;
import me.joshlarson.jlcommon.concurrency.Delay;
import me.joshlarson.jlcommon.control.Intent;
import me.joshlarson.jlcommon.control.IntentManager;
import me.joshlarson.jlcommon.control.ServiceBase;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class TestRunnerSynchronousIntents extends TestRunner {
	
	private final Collection<ServiceBase> instantiatedServices = new ArrayList<>();
	private IntentManager intentManager = null;
	private HolocoreCoroutine holocoreCoroutine = null;
	
	@BeforeEach
	public final void setupSynchronous() {
		intentManager = new IntentManager(1);
		holocoreCoroutine = new HolocoreCoroutine();
		IntentManager.setInstance(intentManager);
		HolocoreCoroutine.Companion.getINSTANCE().set(holocoreCoroutine);
	}
	
	@AfterEach
	public final void cleanupServices() {
		for (ServiceBase service : instantiatedServices) {
			service.setIntentManager(null);
			service.stop();
			service.terminate();
		}
		intentManager.close();
		holocoreCoroutine.close();
		IntentManager.setInstance(null);
		HolocoreCoroutine.Companion.getINSTANCE().set(null);
	}
	
	protected final void registerService(ServiceBase service) {
		service.setIntentManager(Objects.requireNonNull(intentManager));
		service.initialize();
		service.start();
		this.instantiatedServices.add(service);
	}
	
	protected final void registerObject(SWGObject ... objects) {
		for (SWGObject object : objects)
			broadcastAndWait(new ObjectCreatedIntent(object));
	}

	protected final <T extends Intent> void registerIntentHandler(Class<T> intentClass, Consumer<@NotNull T> handler) {
		String consumerKey = testInfo.getTestClass().orElse(getClass()).getName() + "#" + testInfo.getDisplayName();
		intentManager.registerForIntent(intentClass, consumerKey, handler);
	}
	
	protected final void broadcastAndWait(Intent i) {
		i.broadcast();
		while (!i.isComplete()) {
			boolean uninterrupted = Delay.sleepMicro(10);
			assert uninterrupted;
		}
		waitForIntents();
	}
	
	protected final void waitForIntents() {
		while (intentManager.getIntentCount() > 0) {
			boolean uninterrupted = Delay.sleepMilli(1);
			assert uninterrupted;
		}
		while (intentManager.getIntentCount() > 0) {
			boolean uninterrupted = Delay.sleepMilli(10);
			assert uninterrupted;
		}
	}
	
}
