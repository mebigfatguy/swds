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
package com.mebigfatguy.swds.utils;

import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream extends OutputStream {

	private OutputStream[] syncs;
	
	public TeeOutputStream(OutputStream... streamSyncs) {
		syncs = streamSyncs;
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream os : syncs) {
			os.write(b);
		}
		
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (OutputStream os : syncs) {
			os.write(b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (OutputStream os : syncs) {
			os.write(b, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		for (OutputStream os : syncs) {
			os.flush();
		}
	}

	@Override
	public void close() throws IOException {
		for (OutputStream os : syncs) {
			os.close();
		}
	}
}
