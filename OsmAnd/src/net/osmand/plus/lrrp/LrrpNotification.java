package net.osmand.plus.lrrp;

import android.content.Intent;

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

	private boolean wasNoDataDismissed;
	private boolean lastBuiltNoData;

	public LrrpNotification(OsmandApplication app) {
		super(app, GROUP_NAME);
	}

	@Override
	public void init() {

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
				notificationText = "del. -" + delay + " s.";
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


		final Builder notificationBuilder = createBuilder(wearable)
				.setContentTitle(notificationTitle)
				.setStyle(new BigTextStyle().bigText(notificationText));

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
