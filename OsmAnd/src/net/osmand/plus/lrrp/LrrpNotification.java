package net.osmand.plus.lrrp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.BigTextStyle;
import androidx.core.app.NotificationCompat.Builder;

import net.osmand.plus.NavigationService;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.notifications.OsmandNotification;
import net.osmand.plus.plugins.OsmandPlugin;

import static net.osmand.plus.NavigationService.USED_BY_LRRP;

public class LrrpNotification extends OsmandNotification {
	public final static String GROUP_NAME = "LRRP";
	public final static String LRRP_PAUSE_PLUGIN_ACTION = "LRRP_PAUSE_PLUGIN_ACTION";
	public final static String LRRP_DELETE_PLUGIN_ACTION = "LRRP_DELETE_PLUGIN_ACTION";
	public final static String LRRP_REPLAY_PLUGIN_ACTION = "LRRP_REPLAY_PLUGIN_ACTION";

	private boolean wasNoDataDismissed;
	private boolean lastBuiltNoData;

	public LrrpNotification(OsmandApplication app) {
		super(app, GROUP_NAME);
	}

	@Override
	public void init() {
		app.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final LrrpOsmandPlugin plugin = OsmandPlugin.getActivePlugin(LrrpOsmandPlugin.class);
				if (plugin != null) {
					OsmandPlugin.enablePlugin(null, app, plugin, false);
					plugin.disable(app);
				}
			}
		}, new IntentFilter(LRRP_DELETE_PLUGIN_ACTION));
	}

	@Override
	public NotificationType getType() {
		return NotificationType.LRRP;
	}

	@Override
	public int getPriority() {
		return NotificationCompat.PRIORITY_DEFAULT;
	}

	@Override
	public boolean isActive() {
		NavigationService service = app.getNavigationService();
		return isEnabled()
				&& service != null
				&& (service.getUsedBy() & USED_BY_LRRP) != 0;
	}

	@Override
	public boolean isEnabled() {
		LrrpOsmandPlugin plugin = OsmandPlugin.getEnabledPlugin(LrrpOsmandPlugin.class);
		return plugin != null && plugin.isEnabledPlayer();
	}

	@Override
	public Intent getContentIntent() {
		return new Intent(app, MapActivity.class);
	}

	@Override
	public void onNotificationDismissed() {
		if (!wasNoDataDismissed) {
			wasNoDataDismissed = lastBuiltNoData;
		}
	}

	@Override
	public Builder buildNotification(boolean wearable) {
		if (!isEnabled()) {
			return null;
		}
		String notificationTitle;
		String notificationText;
		color = 0;
		icon = R.drawable.ic_notification_track;
		ongoing = true;
		lastBuiltNoData = false;

		notificationTitle = "Sound";
		notificationText = "Enabled";
		LrrpOsmandPlugin plugin = OsmandPlugin.getPlugin(LrrpOsmandPlugin.class);

		if (null != plugin) {
			notificationText = "";
			LrrpRequestHelper helper = plugin.getLrrpRequestHelper();
			long currentTime = System.currentTimeMillis()/1000;
			long delay = currentTime - helper.getWsClientTimeout();
			if (delay < 60) {
				notificationText = "del. " + delay + " s.";
			}

			LrrpPoint point = helper.getMinDist();
			if (null != point) {
				notificationText += " D:" + Math.round(point.dist/10) * 10;
			}

			if (helper.getNotificationTitle() != null) {
				notificationTitle = helper.getNotificationTitle();
			}
			if (helper.getNotificationDescription() != null) {
				notificationText = helper.getNotificationDescription();
			}
		}

		Intent intent = new Intent(LRRP_DELETE_PLUGIN_ACTION);
		PendingIntent delIntent = PendingIntent.getBroadcast(app, 0, intent, 0);

		final Builder notificationBuilder = createBuilder(wearable)
				.setContentTitle(notificationTitle)
				.setDeleteIntent(delIntent)
				.setStyle(new BigTextStyle().bigText(notificationText))
				.setContentText(notificationText);

		return notificationBuilder;
	}

	@Override
	public int getOsmandNotificationId() {
		return LRRP_NOTIFICATION_SERVICE_ID;
	}

	@Override
	public int getOsmandWearableNotificationId() {
		return WEAR_LRRP_NOTIFICATION_SERVICE_ID;
	}
}
