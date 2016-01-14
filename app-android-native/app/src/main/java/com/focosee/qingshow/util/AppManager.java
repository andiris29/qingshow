package com.focosee.qingshow.util;

import java.util.Stack;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

/**
 * Activity管理工具
 * 
 * @author Aaron He
 * 
 */
public class AppManager {
	private static final String TAG = "test_i";
	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}

	/**
	 * 单一实例
	 */
	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		//activityStack.addElement(activity);
		if(activityStack.contains(activity) == false){
			activityStack.add(activity);
		}
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = null;
		try {
			activity = activityStack.lastElement();
		} catch (Exception e) {
		}
		
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = currentActivity();
		if (activity != null) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
			activityStack.removeElement(activity);
			activity = null;
		}
		//finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			if(activity.isFinishing()){
				activity.finish();
			}
			activityStack.removeElement(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}


	/**
	 * * 移除全部元素，除了指定类型的以外 * @param cls
	 * */
	public void popAllActivityExceptOne(Class cls) {
		int size = activityStack.size();
		for (int i = 0; i < size; i++) {
			Activity activity = (Activity) activityStack.get(i);
			if (activity != null && activity.getClass().equals(cls)) {
			} else {
				finishActivity(activity);
				size--;
				i--;
			}
		}
	}
	/**
	 * * 移除全部元素，除了指定类型的以外 * @param cls
	 * */
	public void popAllActivityExcept(Class cls,Class cls2) {
		int size = activityStack.size();
		for (int i = 0; i < size; i++) {
			Activity activity = (Activity) activityStack.get(i);
			if (activity != null && activity.getClass().equals(cls) || activity.getClass().equals(cls2)) {
			} else {
				finishActivity(activity);
				size--;
				i--;
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	@SuppressWarnings("deprecation")
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}
}
