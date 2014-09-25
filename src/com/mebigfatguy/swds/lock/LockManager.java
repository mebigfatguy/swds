/** SWDS - a sample webdav server. 
 * Copyright 2014 MeBigFatGuy.com 
 * Copyright 2014 Dave Brosius 
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
package com.mebigfatguy.swds.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LockManager {

    private static LockManager instance = new LockManager();

    private Map<String, String> locks = new ConcurrentHashMap<>();

    private LockManager() {
    }

    public static LockManager getInstance() {
        return instance;
    }

    public void addLock(String resource, String token) {
        locks.put(resource, token);
    }

    public boolean isLocked(String resource) {
        return locks.containsKey(resource);
    }

    public String getToken(String resource) {
        return locks.get(resource);
    }

    public void removeLock(String resource) {
        locks.remove(resource);
    }
}
