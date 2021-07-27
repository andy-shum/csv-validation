package com.clsa.util;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is utility for handling the number of errors 
 */
public final class MapUtil {
	
	private static final Logger logger = LogManager.getLogger(MapUtil.class);
	
	private static final int MAX_CAPACITY = 100;
	
	private static final int MAX_ERROR = 50;
	
	private static ConcurrentHashMap<UUID, Integer> map = new ConcurrentHashMap<>();
	
	public static void remove(UUID uuid) {
		map.remove(uuid);
	}
	
	public static void add(UUID uuid) {
		Integer value = map.get(uuid);
		if (value == null) {
			map.putIfAbsent(uuid, new Integer(1));
		} else {
			map.computeIfPresent(uuid, (key, oldValue) -> oldValue + 1);
		}
	}
	
	// is exceed is true if the number of validation errors found is
	// larger than or equal to the maximum number
	public static boolean isExceed(UUID uuid) {
		return (map.get(uuid) != null) ? map.get(uuid).intValue() >= MAX_ERROR : false;
	}
	
	public static boolean isThrottling() {
		logger.info("map.size: {}", map.size());
		return map.size() >= MAX_CAPACITY;
	}

}
