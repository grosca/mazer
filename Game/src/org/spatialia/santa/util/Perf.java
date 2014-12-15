package org.spatialia.santa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to measure different aspects.
 */
public class Perf {

	private static Map<String, List<Long>> actionTimes = new HashMap<String, List<Long>>();
	private static Map<String, Long> startTimes = new HashMap<String, Long>();
	private static Map<String, Long> averageTimes = new HashMap<String, Long>();

	public static void start(String action) {
		startTimes.put(action, System.currentTimeMillis());
		if (!actionTimes.containsKey(action)) {
			actionTimes.put(action, new ArrayList<Long>());
		}
	}

	public static long end(String action) {
		long diff = System.currentTimeMillis() - startTimes.get(action);
		actionTimes.get(action).add(diff);
		return diff;
	}

	public static long average(String action, int count) {
		if (actionTimes.get(action).size() > count) {
			long sum = 0;
			for (Long l : actionTimes.get(action)) {
				sum += l;
			}
			long average = sum / actionTimes.get(action).size();
			averageTimes.put(action, average);
			actionTimes.get(action).clear();
			return average;
		} else {
			Long l = averageTimes.get(action);
			return l != null ? l : 0;
		}
	}
}
