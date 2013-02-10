/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 *
 */
package com.dgsd.android.ShiftTracker.View;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import org.holoeverywhere.widget.AutoCompleteTextView;

/**
 * Extension of the EditText Spinner which is responsible for saving its own
 * state. This means that individual activities don't need to bother backing up
 * the state separately
 *
 * @author Daniel Grech
 */
public class StatefulAutoCompleteTextView extends AutoCompleteTextView {

    public StatefulAutoCompleteTextView(Context context) {
        super(context);
    }

    public StatefulAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatefulAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        if (getId() != NO_ID) {
            Parcel p = Parcel.obtain();
            p.writeString(getText().toString());
            p.setDataPosition(0);
            container.put(getId(), new EditTextParcelable(p));
        }
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        if (getId() != NO_ID) {

            Parcelable p = container.get(getId());
            if (p != null && p instanceof EditTextParcelable) {
                EditTextParcelable iwbp = (EditTextParcelable) p;
                setText(iwbp.getValue());
            }
        }
    }

    static class EditTextParcelable implements Parcelable {

        private String mValue;

        private EditTextParcelable(Parcel in) {
            mValue = in.readString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mValue);
        }

        String getValue() {
            return mValue;
        }

        public static final Creator<EditTextParcelable> CREATOR = new Creator<EditTextParcelable>() {
            public EditTextParcelable createFromParcel(Parcel source) {
                return new EditTextParcelable(source);
            }

            public EditTextParcelable[] newArray(int size) {
                return new EditTextParcelable[size];
            }
        };

    }
}
