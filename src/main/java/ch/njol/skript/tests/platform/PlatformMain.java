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
package ch.njol.skript.tests.platform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import ch.njol.skript.tests.TestResults;
import ch.njol.util.NonNullPair;

/**
 * Main entry point of test platform. It allows running this Skript on
 * multiple testing environments.
 */
public class PlatformMain {
	
	public static void main(String... args) throws IOException, InterruptedException {
		System.out.println("Initializing Skript test platform...");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		Path runnerRoot = Paths.get(args[0]);
		assert runnerRoot != null;
		Path testsRoot = Paths.get(args[1]).toAbsolutePath();
		assert testsRoot != null;
		Path dataRoot = Paths.get(args[2]);
		assert dataRoot != null;
		Path envsRoot = Paths.get(args[3]);
		assert envsRoot != null;
		boolean devMode = "true".equals(args[4]);
		
		// Load environments
		List<Environment> envs;
		if (Files.isDirectory(envsRoot)) {
			envs = Files.walk(envsRoot).filter(path -> !Files.isDirectory(path))
					.map(path -> {
						try {
							return gson.fromJson(new String(Files.readAllBytes(path), StandardCharsets.UTF_8), Environment.class);
						} catch (JsonSyntaxException | IOException e) {
							throw new RuntimeException(e);
						}
					}).collect(Collectors.toList());
		} else {
			envs = Collections.singletonList(gson.fromJson(new String(
					Files.readAllBytes(envsRoot),StandardCharsets.UTF_8), Environment.class));
		}
		System.out.println("Test environments: " + String.join(", ",
				envs.stream().map(Environment::getName).collect(Collectors.toList())));
		
		Set<String> allTests = new HashSet<>();
		Map<String, List<NonNullPair<Environment, String>>> failures = new HashMap<>();
		
		// Run tests and collect the results
		envs.sort(Comparator.comparing(Environment::getName));
		for (Environment env : envs) {
			System.out.println("Starting testing on " + env.getName());
			env.initialize(dataRoot, runnerRoot, false);
			TestResults results = env.runTests(runnerRoot, testsRoot, devMode, "-Xmx1G");
			
			// Collect results
			allTests.addAll(results.getSucceeded());
			allTests.addAll(results.getFailed().keySet());
			for (Map.Entry<String, String> fail : results.getFailed().entrySet()) {
				String error = fail.getValue();
				assert error != null;
				failures.computeIfAbsent(fail.getKey(), (k) -> new ArrayList<>())
						.add(new NonNullPair<>(env, error));
			}
		}
		
		// Sort results in alphabetical order
		List<String> succeeded = allTests.stream().filter(name -> !failures.containsKey(name)).collect(Collectors.toList());
		Collections.sort(succeeded);
		List<String> failNames = new ArrayList<>(failures.keySet());
		Collections.sort(failNames);
		
		// All succeeded tests in a single line
		System.out.println("Tested environments: " + String.join(", ",
				envs.stream().map(Environment::getName).collect(Collectors.toList())));
		System.out.println("Succeeded: " + String.join(", ", succeeded));
		if (!failNames.isEmpty()) { // More space for failed tests, they're important
			System.err.println("Failed:");
			for (String failed : failNames) {
				List<NonNullPair<Environment, String>> errors = failures.get(failed);
				System.err.println("  " + failed + " (on " + errors.size() + " environments)");
				for (NonNullPair<Environment, String> error : errors) {
					System.err.println("    " + error.getSecond() + " (on " + error.getFirst().getName() + ")");
				}
			}
			System.exit(failNames.size()); // Error code to indicate how many tests failed
		}
	}
}
