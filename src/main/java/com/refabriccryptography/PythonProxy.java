package com.refabriccryptography;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import py4j.GatewayServer;

@SuppressWarnings("unused")
public class PythonProxy implements ClientModInitializer {
	private static PythonProxy pythonProxy;
	public static Set<String> noRenderList;
	public static Map<String, String> globalMap;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("fabric-cryptography");

	public static PythonProxy getInstance(){
		return pythonProxy;
	}

	@Override
	public void onInitializeClient() {
		pythonProxy = this;

		noRenderList = new HashSet<>();
		noRenderList.add("netherrack");
		noRenderList.add("stone");
		noRenderList.add("granite");
		noRenderList.add("diorite");
		noRenderList.add("andesite");

		globalMap = new HashMap<>();

		GatewayServer gatewayServer = new GatewayServer(this);
		gatewayServer.start();

		LOGGER.info("Hello Fabric world!");
	}
}