/**
 * 
 */
package com.ebay.socialhub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.flickr2twitter.services.rest.models.UserModel;
import com.googlecode.flickr2twitter.services.rest.models.UserServiceConfigModel;

/**
 * @author yayu
 *
 */
public class UserProfileActivity extends Activity {
	public static final String TAG = "SocialHub";
	private static final Map<String, Integer> ICON_MAP;
	
    private SectionedAdapter sourceAdapter;
	
	private TextView txtUserName;
	private TextView txtUserEmail;
	private ListView sourceServiceListView;
	
	static {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("flickr", R.drawable.flickr_32);
		map.put("youtube", R.drawable.youtube_32);
		map.put("facebook", R.drawable.facebook_32);
		map.put("twitter", R.drawable.twitter_32);
		map.put("picasa", R.drawable.picasa_32);
		map.put("ebay", R.drawable.ebay_32);
		map.put("sina", R.drawable.sina_32);
		
		ICON_MAP = Collections.unmodifiableMap(map);
	}
	
	/**
	 * 
	 */
	public UserProfileActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			
			Bundle extras = getIntent().getExtras();
			UserModel user = null;
			if (extras != null) {
				if (extras.containsKey("user")) {
					Object obj = extras.getSerializable("user");
					if (obj instanceof UserModel) {
						user = (UserModel)obj;
					}
				}
			}
			
			this.txtUserName = (TextView)this.findViewById(R.id.userScreenName);
			this.txtUserEmail = (TextView)this.findViewById(R.id.userEmail);
			
			final OnLongClickListener longClickListener = new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					if (v instanceof TextView) {
						TextView txtV = (TextView)v;
						Toast.makeText(UserProfileActivity.this, txtV.getText(),
								Toast.LENGTH_SHORT).show();
						return true;
					}
					return false;
				}
			};
			this.sourceAdapter = new SectionedAdapter() {

				@Override
				protected View getHeaderView(String caption,
						int index, View convertView, ViewGroup parent) {
					TextView result = (TextView) convertView;

					if (convertView == null) {
						result = (TextView) getLayoutInflater().inflate(
								R.layout.header, null);
					}

					result.setText(caption);
					result.setOnLongClickListener(longClickListener);
					return (result);
				}
			};
			
			sourceAdapter.addSection("Authroized Source Services", new ItemAdapter(
					this, R.layout.row, 
					new ArrayList<UserServiceConfigModel>(user.getSourceServices())));

			sourceAdapter.addSection("Authroized Target Services", new ItemAdapter(
					this, R.layout.row, new ArrayList<UserServiceConfigModel>(user.getTargetServices())));
			
			this.sourceServiceListView = (ListView)this.findViewById(R.id.sourceServiceList);
			sourceServiceListView.setAdapter(this.sourceAdapter);
			this.sourceServiceListView.setTextFilterEnabled(true);
			

			this.sourceServiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view,
			        int position, long id) {
			    	Object obj = sourceAdapter.getItem(position);
			    	Toast.makeText(UserProfileActivity.this, String.valueOf(obj), Toast.LENGTH_SHORT).show();
			    	/*UserServiceConfigModel serviceModel = sourceAdapter.items.get(position);
					if (serviceModel != null && serviceModel.getUserSiteUrl() != null) {
						UserProfileActivity.this.startActivity(
								new Intent(Intent.ACTION_VIEW, Uri.parse(serviceModel.getUserSiteUrl())));
					}*/
			    }
			    
			  });
			
			if (user != null) {
				txtUserName.setText(user.getScreenName());
				txtUserEmail.setText(user.getUserId());
			}
		
		} catch (Exception e) {
			Log.e(TAG, e.toString(), e);
		}
	}
	
	private class ItemAdapter extends ArrayAdapter<UserServiceConfigModel> {
		private List<UserServiceConfigModel> items;

		public ItemAdapter(Context context, int textViewResourceId,
				List<UserServiceConfigModel> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			UserServiceConfigModel serviceModel = items.get(position);
			if (serviceModel != null) {
				ImageView image = (ImageView) v.findViewById(R.id.serviceIcon);
				String providerId = serviceModel.getServiceProviderId();
				
				if (providerId != null && ICON_MAP.containsKey(providerId.toLowerCase(Locale.US))) {
					image.setImageResource(ICON_MAP.get(providerId.toLowerCase(Locale.US)));
				}
				
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Provider: "+serviceModel.getServiceProviderId());                            }
				if(bt != null){
					bt.setText("User ID: "+ serviceModel.getServiceUserName());
				}
			}
			return v;
		}
	}

}
