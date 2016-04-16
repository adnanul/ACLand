package com.binarycraft.acland.customcontrols;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.binarycraft.acland.R;

/**
 * 
 * SearchView with Custom Style. Default SearchView at Actionbar cannot be
 * customized through xml as all it's fields are declared as internal or
 * private. This widget uses reflection to change the actual style of the
 * Searchview.
 * 
 * @author fam
 * 
 */
public class CustomSearchView extends SearchView {
	/**
	 * @param context
	 */
	public CustomSearchView(Context context) {
		super(context);
		setCustomStyle(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CustomSearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomStyle(context);
	}

	/**
	 * @param context
	 * @param mSearchView
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 */
	private void setCustomStyle(Context context) {

		try {
			setImeOptions(EditorInfo.IME_ACTION_SEARCH);

			// search plate
			int searchPlateId = context.getResources().getIdentifier(
					"android:id/search_plate", null, null);
			View searchPlate = (View) findViewById(searchPlateId);
			searchPlate
					.setBackgroundResource(R.drawable.texfield_searchview_holo_light);

			// search button
			int searchImgId = context.getResources().getIdentifier(
					"android:id/search_button", null, null);
			ImageView imgSearch = (ImageView) findViewById(searchImgId);
			imgSearch.setImageResource(R.drawable.ic_action_search);

			// close icon
			int closeIconImgId = context.getResources().getIdentifier(
					"android:id/search_close_btn", null, null);
			ImageView imgCloseIco = (ImageView) findViewById(closeIconImgId);
			imgCloseIco.setImageResource(R.drawable.ic_action_cancel);

			// textview
			int textViewId = context.getResources().getIdentifier(
					"android:id/search_src_text", null, null);
			AutoCompleteTextView searchText = (AutoCompleteTextView) findViewById(textViewId);
			searchText.setTextColor(Color.WHITE);

			// cursor
			Field mCursorDrawableRes = TextView.class
					.getDeclaredField("mCursorDrawableRes");
			mCursorDrawableRes.setAccessible(true);
			// sets the cursor resource ID to 0 or @null which will make it
			// visible on white background
			mCursorDrawableRes.set(searchText, 0);

			// hint icon, text
			Class<?> clazz;

			clazz = Class
					.forName("android.widget.SearchView$SearchAutoComplete");

			SpannableStringBuilder stopHint = new SpannableStringBuilder("   ");
			stopHint.append(context.getResources().getString(
					R.string.search_hint));

			// Add the icon as an spannable
			Drawable searchIcon = context.getResources().getDrawable(
					R.drawable.search);
			Method textSizeMethod = clazz.getMethod("getTextSize");
			Float rawTextSize = (Float) textSizeMethod.invoke(searchText);
			int textSize = (int) (rawTextSize * 1.25);
			searchIcon.setBounds(0, 0, textSize, textSize);
			stopHint.setSpan(new ImageSpan(searchIcon), 1, 2,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			// Set the new hint text
			Method setHintMethod = clazz.getMethod("setHint",
					CharSequence.class);
			setHintMethod.invoke(searchText, stopHint);
		} catch (NoSuchMethodException nme) {
			Log.e("", "NoSuchMethodException");
		} catch (IllegalAccessException iae) {
			Log.e("", "IllegalAccessException");
		} catch (InvocationTargetException ite) {
			Log.e("", "InvocationTargetException");
		} catch (NoSuchFieldException nfe) {
			Log.e("", "NoSuchFieldException");
		} catch (ClassNotFoundException cnfe) {
			Log.e("", "ClassNotFoundException");
		}
	}

}
