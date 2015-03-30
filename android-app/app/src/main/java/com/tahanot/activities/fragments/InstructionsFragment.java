package com.tahanot.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tahanot.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstructionsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.stop_sign, container, false);		
		((TextView) v.findViewById(R.id.stopName)).setText(R.string.howToUse);		
		fillListView(v);
		
		return v;
	}


	private void fillListView(View v) {
		ListView routesListView = (ListView) v.findViewById(R.id.routesListView);

		List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map = new HashMap<String, String>();
		data.add(new HashMap<String, String>() {
			{
				put("number", "1");
				put("big text", "עבור למסך הבית.");
				put("small text", "(לחץ HOME)");
			}
		});
		data.add(new HashMap<String, String>() {
			{
				put("number", "2");
				put("big text", "צור ווידג'ט חדש.");
				put("small text", "(תפריט -> הוסף -> וידג'ט)");
			}
		});
		data.add(new HashMap<String, String>() {
			{
				put("number", "3");
				put("big text", "בחר בווידג'ט \"תחנות\".");
				put("small text", "(מתחיל ב-ת', ולכן בד\"כ אחרון)");
			}
		});
		data.add(new HashMap<String, String>() {
			{
				put("number", "4");
				put("big text", "בחר תחנה.");
				put("small text", "התחנה תופיע קבוע במסך הבית.");
			}
		});
		data.add(new HashMap<String, String>() {
			{
				put("number", "5");
				put("big text", "ניתן ליצור כמה ווידג'טים!");
				put("small text", "התחנה ליד הבית, התחנה ליד הלימודים, התחנה ליד העבודה...");
			}
		});

		String[] from = new String[] { "number", "big text", "small text" };
		int[] to = new int[] { R.id.textInsteadOfCompanyLogo, R.id.routeTextLine1, R.id.routeTextLine2 };
		routesListView.setAdapter(new SimpleAdapter(getActivity(), data, R.layout.stop_sign_route, from, to));
	}
}
