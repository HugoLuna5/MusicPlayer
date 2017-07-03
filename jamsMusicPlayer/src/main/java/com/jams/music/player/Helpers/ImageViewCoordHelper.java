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
package com.jams.music.player.Helpers;

import android.graphics.Bitmap;

/**
 * Helper class used to animate a thumbnail to a
 * larger, scaled-in version during an activity
 * transition.
 *
 * @author Saravan Pantham
 */
public class ImageViewCoordHelper {

    public String mAlbumArtPath;
    public Bitmap mThumbnail;

    public ImageViewCoordHelper(String albumArtPath, Bitmap thumbnail) {
        mAlbumArtPath = albumArtPath;
        mThumbnail = thumbnail;

    }

}
