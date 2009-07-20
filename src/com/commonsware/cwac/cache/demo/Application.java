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

package com.commonsware.cwac.cache.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import java.io.File;
import com.commonsware.cwac.bus.SimpleBus;
import com.commonsware.cwac.cache.AsyncCache;
import com.commonsware.cwac.cache.WebImageCache;

public class Application extends android.app.Application {
	private static String TAG="CacheDemo";
	private WebImageCache cache=null;
	private SimpleBus bus=new SimpleBus();
	private Drawable placeholder=null;
	
	public Application() {
		super();
		
		Thread.setDefaultUncaughtExceptionHandler(onBlooey);
	}
	
	void goBlooey(Throwable t) {
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		
		builder
			.setTitle(R.string.exception)
			.setMessage(t.toString())
			.setPositiveButton(R.string.ok, null)
			.show();
	}
	
	synchronized WebImageCache getCache() {
		if (cache==null) {
			placeholder=getResources()
												.getDrawable(R.drawable.placeholder);
			cache=new WebImageCache(getCacheDir(), bus, policy,
															placeholder);
		}
		
		return(cache);
	}
	
	boolean isPlaceholder(Drawable d) {
		return(d==placeholder);
	}
	
	SimpleBus getBus() {
		return(bus);
	}
	
	private Thread.UncaughtExceptionHandler onBlooey=
		new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			Log.e(TAG, "Uncaught exception", ex);
			goBlooey(ex);
		}
	};
	
	private AsyncCache.DiskCachePolicy policy=new AsyncCache.DiskCachePolicy() {
		public boolean eject(File file) {
			return(System.currentTimeMillis()-file.lastModified()>1000*60*60*24*7);
		}
	};
}