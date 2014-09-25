package com.mebigfatguy.swds.lock;

import java.util.UUID;

public class LockInfo {
	
	public enum Scope {exclusive, shared};
	public enum Type {write};
	public enum Depth {self, infinity};
	
	private Scope scope;
	private Type type;
	private Depth depth;
	private String owner;
	private String token;
	
	public LockInfo(String lockScope, String lockType, String lockDepth, String lockOwner) {
		scope = Scope.valueOf(lockScope.toLowerCase());
		type = Type.valueOf(lockType.toLowerCase());
		depth = "0".equals(lockDepth) ? Depth.self : Depth.infinity;
		owner = lockOwner;
		token = UUID.randomUUID().toString();
	}

	public Scope getScope() {
		return scope;
	}
	
	public Type getType() {
		return type;
	}

	public Depth getDepth() {
		return depth;
	}

	public String getOwner() {
		return owner;
	}
	
	public String getToken() {
		return token;
	}
}
