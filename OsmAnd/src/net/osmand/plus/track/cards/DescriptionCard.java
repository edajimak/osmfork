package net.osmand.plus.track.cards;

import static net.osmand.plus.wikivoyage.WikivoyageUtils.ARTICLE_LANG;
import static net.osmand.plus.wikivoyage.WikivoyageUtils.ARTICLE_TITLE;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import net.osmand.plus.utils.AndroidUtils;
import net.osmand.GPXUtilities.GPXFile;
import net.osmand.GPXUtilities.Metadata;
import net.osmand.plus.utils.PicassoUtils;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.routepreparationmenu.cards.MapBaseCard;
import net.osmand.plus.track.fragments.GpxEditDescriptionDialogFragment;
import net.osmand.plus.track.fragments.GpxReadDescriptionDialogFragment;
import net.osmand.plus.widgets.TextViewEx;
import net.osmand.plus.wikipedia.WikiArticleHelper;
import net.osmand.plus.wikivoyage.WikivoyageUtils;
import net.osmand.plus.wikivoyage.data.TravelArticle.TravelArticleIdentifier;
import net.osmand.util.Algorithms;

import java.util.Map;

public class DescriptionCard extends MapBaseCard {

	private final Fragment targetFragment;
	private final GPXFile gpxFile;

	public DescriptionCard(@NonNull MapActivity mapActivity,
	                       @NonNull Fragment targetFragment,
	                       @NonNull GPXFile gpxFile) {
		super(mapActivity);
		this.gpxFile = gpxFile;
		this.targetFragment = targetFragment;
	}

	@Override
	public int getCardLayoutId() {
		return R.layout.gpx_description_preview_card;
	}

	@Override
	public void updateContent() {
		final String title = gpxFile.metadata.getArticleTitle();
		final String imageUrl = getMetadataImageLink(gpxFile.metadata);
		final String descriptionHtml = gpxFile.metadata.getDescription();

		setupImage(imageUrl);

		if (Algorithms.isBlank(descriptionHtml)) {
			showAddBtn();
		} else {
			showDescription(title, imageUrl, descriptionHtml);
		}
		AndroidUiHelper.updateVisibility(view.findViewById(R.id.shadow), gpxFile.showCurrentTrack);
	}

	private void showAddBtn() {
		LinearLayout descriptionContainer = view.findViewById(R.id.description_container);
		View addBtn = view.findViewById(R.id.btn_add);

		setupButton(addBtn);
		addBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GpxEditDescriptionDialogFragment.showInstance(getMapActivity(), "", null);
			}
		});
		AndroidUiHelper.updateVisibility(descriptionContainer, false);
		AndroidUiHelper.updateVisibility(addBtn, true);
	}

	private void showDescription(final String title, final String imageUrl, final String descriptionHtml) {
		LinearLayout descriptionContainer = view.findViewById(R.id.description_container);
		FrameLayout addBtn = view.findViewById(R.id.btn_add);

		AndroidUiHelper.updateVisibility(descriptionContainer, true);
		AndroidUiHelper.updateVisibility(addBtn, false);

		TextViewEx tvDescription = view.findViewById(R.id.description);
		tvDescription.setText(getFirstParagraph(descriptionHtml));

		View readBtn = view.findViewById(R.id.btn_read_full);
		setupButton(readBtn);
		readBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Map<String, String> extensions = gpxFile.metadata.getExtensionsToRead();
				if (!Algorithms.isEmpty(extensions)) {
					String title = extensions.get(ARTICLE_TITLE);
					String lang = extensions.get(ARTICLE_LANG);
					if (title != null && lang != null) {
						TravelArticleIdentifier articleId = app.getTravelHelper().getArticleId(title, lang);
						if (articleId != null) {
							WikivoyageUtils.openWikivoyageArticle(activity, articleId, lang);
							return;
						}
					}
				}
				GpxReadDescriptionDialogFragment.showInstance(mapActivity, title, imageUrl, descriptionHtml, targetFragment);
			}
		});

		View editBtn = view.findViewById(R.id.btn_edit);
		setupButton(editBtn);
		editBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GpxEditDescriptionDialogFragment.showInstance(mapActivity, descriptionHtml, null);
			}
		});
	}

	private String getFirstParagraph(String descriptionHtml) {
		if (descriptionHtml != null) {
			String firstParagraph = WikiArticleHelper.getPartialContent(descriptionHtml);
			if (!Algorithms.isEmpty(firstParagraph)) {
				return firstParagraph;
			}
		}
		return descriptionHtml;
	}

	private void setupButton(View button) {
		Context ctx = button.getContext();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			AndroidUtils.setBackground(ctx, button, nightMode, R.drawable.ripple_light, R.drawable.ripple_dark);
		} else {
			AndroidUtils.setBackground(ctx, button, nightMode, R.drawable.btn_unstroked_light, R.drawable.btn_unstroked_dark);
		}
	}

	private void setupImage(final String imageUrl) {
		if (imageUrl == null) {
			return;
		}
		final PicassoUtils picasso = PicassoUtils.getPicasso(app);
		RequestCreator rc = Picasso.get().load(imageUrl);
		final AppCompatImageView image = view.findViewById(R.id.main_image);
		rc.into(image, new Callback() {
			@Override
			public void onSuccess() {
				picasso.setResultLoaded(imageUrl, true);
				AndroidUiHelper.updateVisibility(image, true);
			}

			@Override
			public void onError(Exception e) {
				picasso.setResultLoaded(imageUrl, false);
			}
		});
	}

	@Nullable
	public static String getMetadataImageLink(@NonNull Metadata metadata) {
		String link = metadata.link;
		if (!TextUtils.isEmpty(link)) {
			String lowerCaseLink = link.toLowerCase();
			if (lowerCaseLink.contains(".jpg")
					|| lowerCaseLink.contains(".jpeg")
					|| lowerCaseLink.contains(".png")
					|| lowerCaseLink.contains(".bmp")
					|| lowerCaseLink.contains(".webp")) {
				return link;
			}
		}
		return null;
	}
}