package com.moblong.flipped;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.moblong.flipped.R;

import android.app.Fragment;
import android.app.ListFragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public final class DatingFragment extends Fragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dating_layout, null);
		PullToRefreshListView listView = (PullToRefreshListView) root.findViewById(R.id.interesting);
		BaseAdapter adapter = new BaseAdapter(){

			@Override
			public int getCount() {
				return 1;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Rect rect = new Rect();
				View item = inflater.inflate(R.layout.dating_item, null);
				
				item.getDrawingRect(rect);
				int w = item.getLeft() - item.getRight();
				View timeDatingBar = item.findViewById(R.id.time_dating);
				timeDatingBar.getDrawingRect(rect);
				
				return item;
			}
			
		};
		
		listView.setAdapter(adapter);
		return root;
	}


}
