/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jams.music.player.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jams.music.player.R;
import com.jams.music.player.Helpers.TypefaceHelper;

public class CautionEditArtistsDialog extends DialogFragment {

	private Activity parentActivity;
	private DialogFragment dialogFragment;
	private View rootView;
	private TextView cautionText;
	
	private String EDIT_TYPE;
	private String ARTIST;
	
	private TextView dontShowAgainText;
	private CheckBox dontShowAgainCheckbox;
	private SharedPreferences sharedPreferences;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		parentActivity = getActivity();
		dialogFragment = this;
		
		EDIT_TYPE = this.getArguments().getString("EDIT_TYPE");
		ARTIST = this.getArguments().getString("ARTIST");
		
		rootView = (View) parentActivity.getLayoutInflater().inflate(R.layout.fragment_caution_edit_artists, null);
		
		cautionText = (TextView) rootView.findViewById(R.id.caution_text);
		cautionText.setText(R.string.caution_artists_text);
		cautionText.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
		cautionText.setPaintFlags(cautionText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
		
		sharedPreferences = getActivity().getSharedPreferences("com.jams.music.player", Context.MODE_PRIVATE);
		sharedPreferences.edit().putBoolean("SHOW_ARTIST_EDIT_CAUTION", false).commit();
		
		dontShowAgainText = (TextView) rootView.findViewById(R.id.dont_show_again_text);
		dontShowAgainText.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
		dontShowAgainText.setPaintFlags(dontShowAgainText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
		
		dontShowAgainCheckbox = (CheckBox) rootView.findViewById(R.id.dont_show_again_checkbox);
		dontShowAgainCheckbox.setChecked(true);
		
		dontShowAgainCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				
				if (isChecked==true) {
					sharedPreferences.edit().putBoolean("SHOW_ARTIST_EDIT_CAUTION", false).commit();
				} else {
					sharedPreferences.edit().putBoolean("SHOW_ARTIST_EDIT_CAUTION", true).commit();
				}
				
			}
			
		});
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Set the dialog title.
        builder.setTitle(R.string.caution);
        builder.setView(rootView);
        builder.setNegativeButton(R.string.no, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dialogFragment.dismiss();
				
			}
        	
        });
        
        builder.setPositiveButton(R.string.yes, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dialogFragment.dismiss();
				
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Bundle bundle = new Bundle();
				bundle.putString("EDIT_TYPE", EDIT_TYPE);
				bundle.putString("ARTIST", ARTIST);
				ID3sArtistEditorDialog dialog = new ID3sArtistEditorDialog();
				dialog.setArguments(bundle);
				dialog.show(ft, "id3ArtistEditorDialog");
				
			}
        	
        });

        return builder.create();
    }
	
}
