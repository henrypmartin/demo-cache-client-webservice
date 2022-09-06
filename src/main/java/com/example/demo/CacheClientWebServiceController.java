package com.example.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheClientWebServiceController {

	private Region<String, String> simplePartitionedRegion;
	
	@PostConstruct
	private void initializeCacheClient() throws IOException {
		
		Properties props = new Properties();
		props.load(ClassLoader.getSystemResourceAsStream("application.properties"));
		
		ClientCacheFactory clientCacheFactory = new ClientCacheFactory();
		clientCacheFactory.set("cache-xml-file", props.getProperty("cache-xml-file"));
		
		String locators = props.getProperty("locators");
		
		for(String locatorPort : locators.split(",")) {
			System.out.println("Adding locator pool: " + locatorPort);
			clientCacheFactory.addPoolLocator(locatorPort.split(":")[0], 
					Integer.parseInt(locatorPort.split(":")[1]));
		}
		
		ClientCache cache = clientCacheFactory.create();
		
		simplePartitionedRegion = cache.getRegion("simplepartitionedregion");
	}
	
	@RequestMapping("/getCacheValue")  
	public String getCacheValue(@RequestParam String key) {
	 System.out.println("cache size on local is " + simplePartitionedRegion.size());
	 System.out.println("cache size on server is " + simplePartitionedRegion.sizeOnServer());
	  return simplePartitionedRegion.get(key); 
	}
	
	@RequestMapping("/saveValue")  
	public String getCacheValue(@RequestParam String key, @RequestParam String value) {
	  return simplePartitionedRegion.put(key, value); 
	}
}
