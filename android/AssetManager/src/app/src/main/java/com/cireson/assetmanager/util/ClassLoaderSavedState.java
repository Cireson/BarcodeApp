
//////////////////////////////////////////////////////////////////////////////
//This file is part of Cireson Barcode Scanner. 
//
//Cireson Barcode Scanner is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Cireson Barcode Scanner is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Cireson Barcode Scanner.  If not, see<https://www.gnu.org/licenses/>.
/////////////////////////////////////////////////////////////////////////////


package com.cireson.assetmanager.util;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * A {@link android.os.Parcelable} implementation that should be used by inheritance
 * hierarchies to ensure the state of all classes along the chain is saved.
 */
public abstract class ClassLoaderSavedState implements Parcelable {
    public static final ClassLoaderSavedState EMPTY_STATE = new ClassLoaderSavedState() {};

    private Parcelable mSuperState = EMPTY_STATE;
    private ClassLoader mClassLoader;

    /**
     * Constructor used to make the EMPTY_STATE singleton
     */
    private ClassLoaderSavedState() {
        mSuperState = null;
        mClassLoader = null;
    }

    /**
     * Constructor called by derived classes when creating their ListSavedState objects
     *
     * @param superState The state of the superclass of this view
     */
    protected ClassLoaderSavedState(Parcelable superState, ClassLoader classLoader) {
        mClassLoader = classLoader;
        if (superState == null) {
            throw new IllegalArgumentException("superState must not be null");
        }
        else {
            mSuperState = superState != EMPTY_STATE ? superState : null;
        }
    }

    /**
     * Constructor used when reading from a parcel. Reads the state of the superclass.
     *
     * @param source
     */
    protected ClassLoaderSavedState(Parcel source) {
        // ETSY : we're using the passed super class loader unlike AbsSavedState
        Parcelable superState = source.readParcelable(mClassLoader);
        mSuperState = superState != null ? superState : EMPTY_STATE;
    }

    final public Parcelable getSuperState() {
        return mSuperState;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(mSuperState, flags);
    }

    public static final Parcelable.Creator<ClassLoaderSavedState> CREATOR
            = new Parcelable.Creator<ClassLoaderSavedState>() {

        public ClassLoaderSavedState createFromParcel(Parcel in) {
            Parcelable superState = in.readParcelable(null);
            if (superState != null) {
                throw new IllegalStateException("superState must be null");
            }
            return EMPTY_STATE;
        }

        public ClassLoaderSavedState[] newArray(int size) {
            return new ClassLoaderSavedState[size];
        }
    };
}
