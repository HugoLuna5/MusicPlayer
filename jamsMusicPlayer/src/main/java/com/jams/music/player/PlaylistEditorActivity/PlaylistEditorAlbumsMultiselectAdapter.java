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
package com.jams.music.player.PlaylistEditorActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jams.music.player.R;
import com.jams.music.player.DBHelpers.DBAccessHelper;
import com.jams.music.player.Helpers.TypefaceHelper;
import com.jams.music.player.Utils.Common;

public class PlaylistEditorAlbumsMultiselectAdapter extends SimpleCursorAdapter {
	
	private Context mContext;
	private Common mApp;
	
    public PlaylistEditorAlbumsMultiselectAdapter(Context context, Cursor cursor) {
        super(context, -1, cursor, new String[] {}, new int[] {}, 0);
        mContext = context;
        mApp = (Common) mContext.getApplicationContext();
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
        final Cursor c = (Cursor) getItem(position);
	    SongsListViewHolder holder = null;

		if (convertView == null) {
			
			convertView = LayoutInflater.from(mContext).inflate(R.layout.music_library_editor_albums_layout, parent, false);
			holder = new SongsListViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.albumThumbnailMusicLibraryEditor);
			holder.title = (TextView) convertView.findViewById(R.id.albumNameMusicLibraryEditor);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.albumCheckboxMusicLibraryEditor);
			holder.subText = (TextView) convertView.findViewById(R.id.albumArtistNameMusicLibraryEditor);

			convertView.setTag(holder);
		} else {
		    holder = (SongsListViewHolder) convertView.getTag();
		}
		
		final View finalConvertView = convertView;
		final String songId = c.getString(c.getColumnIndex(DBAccessHelper._ID));
		final String songArtist = c.getString(c.getColumnIndex(DBAccessHelper.SONG_ARTIST));
		final String songAlbum = c.getString(c.getColumnIndex(DBAccessHelper.SONG_ALBUM));
		final String songAlbumArtPath = c.getString(c.getColumnIndex(DBAccessHelper.SONG_ALBUM_ART_PATH));
		
		//Set the album's name and artist as the row's tag.
		convertView.setTag(R.string.album, songAlbum);
		convertView.setTag(R.string.artist, songArtist);
		
		holder.title.setTypeface(TypefaceHelper.getTypeface(mContext, "RobotoCondensed-Light"));
		holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		holder.subText.setTypeface(TypefaceHelper.getTypeface(mContext, "RobotoCondensed-Light"));
		holder.subText.setPaintFlags(holder.subText.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		
		//Set the song title.
		holder.title.setText(songAlbum);
		holder.subText.setText(songArtist);
        mApp.getImageLoader().displayImage(songAlbumArtPath, holder.image, PlaylistEditorActivity.displayImageOptions);

        //Check if the song's DB ID exists in the HashSet and set the appropriate checkbox status.
        if (PlaylistEditorActivity.songDBIdsList.contains(songId)) {
        	holder.checkBox.setChecked(true);
        	convertView.setBackgroundColor(0xCC0099CC);
        } else {
        	convertView.setBackgroundColor(0x00000000);
        	holder.checkBox.setChecked(false);
        }
        
        holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
				
				if (isChecked==true) {
					
					//Only receive inputs by the user and ignore any system-made changes to the checkbox state.
					if (checkbox.isPressed()) {
						finalConvertView.setBackgroundColor(0xCC0099CC);
						AsyncGetAlbumSongIds task = new AsyncGetAlbumSongIds(mContext, songAlbum, songArtist);
						task.execute(new String[] {"ADD"});
					}
					
				} else if (isChecked==false) {
					
					//Only receive inputs by the user and ignore any system-made changes to the checkbox state.
					if (checkbox.isPressed()) {
						finalConvertView.setBackgroundColor(0x00000000);
						AsyncGetAlbumSongIds task = new AsyncGetAlbumSongIds(mContext, songAlbum, songArtist);
						task.execute(new String[] {"REMOVE"});
						
					}

				}
				
			}
			
        });
 
		return convertView;
	}
    
	static class SongsListViewHolder {
	    public ImageView image;
	    public TextView title;
	    public TextView subText;
	    public CheckBox checkBox;
	}
	
	/***************************************************************
	 * This AsyncTask goes through a specified album and retrieves 
	 * every song in the album and its ID. It then inserts the ID(s) 
	 * into a HashSet.
	 ***************************************************************/
	static class AsyncGetAlbumSongIds extends AsyncTask<String, String, String> {

		private Common mApp;
		private String mAlbumName;
		private String mArtistName;
		
		public AsyncGetAlbumSongIds(Context context, String albumName, String artistName) {
			mApp = (Common) context.getApplicationContext();
			mAlbumName = albumName;
			mArtistName = artistName;
		}
		
		@Override
		protected String doInBackground(String... params) {
			//Check if the user is adding or removing an album from the list.
			String operation = params[0];
			if (operation.equals("ADD")) {
				addAlbumToLibrary();
			} else {
				removeAlbumFromLibrary();
			}
			
			return null;
		}
		
		public void addAlbumToLibrary() {
			
			Cursor cursor = mApp.getDBAccessHelper().getAllSongsInAlbum(mAlbumName, mArtistName);
			if (cursor!=null && cursor.getCount() > 0) {

				for (int i=0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					PlaylistEditorActivity.songDBIdsList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper._ID)));
				}
				cursor.close();
				cursor = null;
				
			}
			
		}
		
		public void removeAlbumFromLibrary() {
			
			Cursor cursor = mApp.getDBAccessHelper().getAllSongsInAlbum(mAlbumName, mArtistName);
			if (cursor!=null && cursor.getCount() > 0) {

				for (int i=0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					PlaylistEditorActivity.songDBIdsList.remove(cursor.getString(cursor.getColumnIndex(DBAccessHelper._ID)));
				}
				cursor.close();
				cursor = null;
				
			}
			
		}
		
	}
	
}
