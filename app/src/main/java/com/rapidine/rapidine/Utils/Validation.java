package com.rapidine.rapidine.Utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * Created by ravi on 27/6/18.
 */

public class Validation {


    private Context context;

    public Validation(Context context) {
        this.context = context;
    }


    /*.................................isValidName......................................*/
    public boolean isValidName(String input) {
        if (input.equals("") || input.isEmpty() || input.length() < 2 || input.length() > 20) {
            return false;
        }
        return true;
    }

    //image null
    public boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable) drawable).getBitmap() != null;
        }

        return hasImage;
    }

    /*.................................isValidPassword......................................*/
    public boolean isValidPassword(String input) {
        if (input.equals("") || input.isEmpty() || input.length() < 2 || input.length() > 12) {
            return false;
        }
        return true;
    }

    /*.................................isConfirmPassword......................................*/
    public boolean isConfirmPassword(String input, String confirm) {
        if (input.equals("") || input.equals(confirm) || input.isEmpty() || input.length() < 6 || input.length() > 12) {
            return false;
        }
        return true;
    }

    /*.................................isEmpty......................................*/
    public boolean isEmpty(String textView) {
        if (textView.equals("") || textView.length() == 0) {
            return false;
        }
        return true;
    }


    public boolean isValidNo(String textView) {
        if (textView.length() < 10) {
            return false;
        }
        return true;
    }


    /*.................................isValidEmail......................................*/
    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

}
