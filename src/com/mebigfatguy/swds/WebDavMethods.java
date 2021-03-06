/** SWDS - a sample webdav server. 
 * Copyright 2014-2019 MeBigFatGuy.com 
 * Copyright 2014-2019 Dave Brosius 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations 
 * under the License. 
 */
package com.mebigfatguy.swds;

import java.util.HashMap;
import java.util.Map;

public enum WebDavMethods {
	OPTIONS("OPTIONS"), 
	PROPFIND("PROPFIND"),
	MKCOL("MKCOL"),
	GET("GET"), 
	HEAD("HEAD"), 
	PUT("PUT"), 
	MOVE("MOVE"),
	LOCK("LOCK"), 
	UNLOCK("UNLOCK"), 
	VERSIONCONTROL("VERSION-CONTROL"),
	CHECKOUT("CHECKOUT"),
	CHECKIN("CHECKIN"),
	UNCHECKOUT("UNCHECKOUT");
	
	private static final Map<String, WebDavMethods> stringToMethod = new HashMap<>();
	static {
		for (WebDavMethods m : WebDavMethods.values()) {
			stringToMethod.put(m.rawMethod, m);
		}
	}
	
	private String rawMethod;
	
	WebDavMethods(String method) {
		rawMethod = method;
	}
	
	public static WebDavMethods fromString(String m) {
		return stringToMethod.get(m);
	}
}
