/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.commonsware.cwac.cache;

import android.content.Intent;
import android.util.Log;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import com.commonsware.cwac.bus.AbstractBus;
import com.commonsware.cwac.task.AsyncTaskEx;

abstract public class AsyncCache<K, V, B extends AbstractBus, M> {
	protected abstract V create(K key, M message, int forceStyle);
	
	public static final int CACHE_MEMORY=1;
	public static final int CACHE_DISK=2;
	public static final int CACHE_NONE=3;
	public static final int FORCE_NONE=1;
	public static final int FORCE_SOFT=2;
	public static final int FORCE_HARD=3;
	private static String TAG="AsyncCache";
	private File cacheRoot=null;
	private B bus=null;
	private ConcurrentHashMap<K, SoftReference<V>> cache=
					new ConcurrentHashMap<K, SoftReference<V>>();
	
	public AsyncCache(File cacheRoot, B bus,
										DiskCachePolicy policy) {
		this.cacheRoot=cacheRoot;
		this.bus=bus;
		
		new CacheCleanTask().execute(policy);
	}
	
	public V get(K key, M message) {
		SoftReference<V> ref=cache.get(key);
		
		if (ref==null || ref.get()==null) {
			ref=new SoftReference<V>(create(key, message, FORCE_NONE));
			cache.put(key, ref);
		}
		
		return(ref.get());
	}
	
	public void forceLoad(K key, M message, int forceStyle) {
		cache.remove(key);
		create(key, message, forceStyle);	// force a new async entry
	}
	
	public int getStatus(K key) {
		if (cache.containsKey(key)) {
			return(CACHE_MEMORY);
		}
		
		return(CACHE_NONE);
	}
	
	protected void put(K key, V value) {
		cache.put(key, new SoftReference<V>(value));
	}
	
	public void remove(K key) {
		cache.remove(key);
	}
	
	protected B getBus() {
		return(bus);
	}
	
	protected File getCacheRoot() {
		return(cacheRoot);
	}
	
	public interface DiskCachePolicy {
		boolean eject(File cachedFile);
	}
	
	class CacheCleanTask extends AsyncTaskEx<DiskCachePolicy, Void, Void> {
		@Override
		protected Void doInBackground(DiskCachePolicy... policies) {
			try {
				walkDir(cacheRoot, policies[0]);
			}
			catch (Throwable t) {
				Log.e(TAG, "Exception cleaning cache", t);
			}
			
			return(null);
		}

		void walkDir(File dir, DiskCachePolicy policy) {
      if (dir.isDirectory()) {
        String[] children=dir.list();
    
		    for (int i=0; i<children.length; i++) {
          walkDir(new File(dir, children[i]), policy);
        }
      }
			else if (policy.eject(dir)) {
				dir.delete();
      }
    }
	}
}