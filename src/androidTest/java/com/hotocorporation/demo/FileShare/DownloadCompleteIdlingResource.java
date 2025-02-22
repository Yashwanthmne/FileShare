package com.hotocorporation.demo.FileShare;

import android.app.Activity;
import android.widget.TextView;
import androidx.test.runner.lifecycle.Stage;
import androidx.test.espresso.IdlingResource;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;

public class DownloadCompleteIdlingResource implements IdlingResource {
    private ResourceCallback resourceCallback;
    private boolean isIdle;

    @Override
    public String getName() {
        return DownloadCompleteIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        if (isIdle) return true;

        Activity activity = getCurrentActivity();
        TextView stateView = (TextView) activity.findViewById(R.id.textState);
        isIdle = (stateView != null && stateView.getText().equals("COMPLETED"));
        if (isIdle) {
            resourceCallback.onTransitionToIdle();
        }
        return isIdle;
    }

    private Activity getCurrentActivity() {
        Activity activity;
        java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
        activity = Iterables.getOnlyElement(activities);
        return activity;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}