package name.monwf.customiuizer.prefs;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.DropDownPreference;
import androidx.preference.PreferenceViewHolder;

import name.monwf.customiuizer.R;
import name.monwf.customiuizer.utils.Helpers;

public class DropDownPreferenceEx extends DropDownPreference implements PreferenceState {

	private CharSequence sValue;
	private final Resources res = getContext().getResources();
	private final int primary = res.getColor(R.color.preference_primary_text, getContext().getTheme());
	private final int secondary = res.getColor(R.color.preference_secondary_text, getContext().getTheme());
	private final int childpadding = res.getDimensionPixelSize(R.dimen.preference_item_child_padding);

	private final int indentLevel;
	private final boolean dynamic;
	private boolean newmod = false;
    private boolean highlight = false;
	private boolean unsupported = false;
	private final boolean valueAsSummary;

	public DropDownPreferenceEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		final TypedArray xmlAttrs = context.obtainStyledAttributes(attrs, R.styleable.ListPreferenceEx);
		indentLevel = xmlAttrs.getInt(R.styleable.ListPreferenceEx_indentLevel, 0);
		dynamic = xmlAttrs.getBoolean(R.styleable.ListPreferenceEx_dynamic, false);
		valueAsSummary = xmlAttrs.getBoolean(R.styleable.ListPreferenceEx_valueAsSummary, false);
		xmlAttrs.recycle();
		setIconSpaceReserved(false);
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		int index = findIndexOfValue(value);
		if (index < 0 || index > getEntries().length - 1) return;
		sValue = getEntries()[index];
	}

	public void setUnsupported(boolean value) {
		unsupported = value;
		setEnabled(!value);
	}

	public void getView(View finalView) {
		TextView title = finalView.findViewById(android.R.id.title);
		TextView summary = finalView.findViewById(android.R.id.summary);
		TextView valSummary = finalView.findViewById(android.R.id.hint);

		summary.setVisibility(valueAsSummary || getSummary() == null || getSummary().toString().isEmpty() ? View.GONE : View.VISIBLE);
		valSummary.setVisibility(valueAsSummary ? View.VISIBLE : View.GONE);
		valSummary.setText(valueAsSummary ? sValue : "");
//		if (valueAsSummary) valSummary.setTextColor(Helpers.isNightMode(getContext()) ? secondary : primary);
//		title.setTextColor(isEnabled() ? primary : secondary);
		title.setText(getTitle() + (unsupported ? " ⨯" : (dynamic ? " ⟲" : "")));
		if (newmod) Helpers.applyNewMod(title);
		if (highlight) {
			Helpers.applySearchItemHighlight(finalView);
		}
		int hrzPadding = (indentLevel) * childpadding + (int) (res.getDisplayMetrics().density + 0.5f);
		finalView.setPadding(hrzPadding, 0, (indentLevel + 1) * childpadding, 0);
		ViewGroup vg = (ViewGroup) finalView;
		for (int i = 0; i < vg.getChildCount(); i++) {
			View v = vg.getChildAt(i);
			if (v.getClass() == LinearLayout.class) {
				v.setBackground(null);
				Log.w("TAG", "DropDownPreference: remove excess Background of LinearLayout: " + v + " " + i + "," + vg.getChildCount());
				break;
			}
		}
	}

	@Override
	public void onBindViewHolder(PreferenceViewHolder view) {
		super.onBindViewHolder(view);
		TextView title = (TextView) view.findViewById(android.R.id.title);
		title.setMaxLines(3);

		TextView summary = (TextView) view.findViewById(android.R.id.summary);
		summary.setTextColor(secondary);

		TextView valSummary = view.itemView.findViewById(android.R.id.hint);
		if (valSummary == null) {
			valSummary = new TextView(getContext());
			valSummary.setTextSize(TypedValue.COMPLEX_UNIT_PX, summary.getTextSize());
			valSummary.setTextColor(summary.getCurrentTextColor());
			valSummary.setPadding(0, 0, res.getDimensionPixelSize(R.dimen.preference_summary_padding_right), 0);
			valSummary.setId(android.R.id.hint);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER | Gravity.END;
			valSummary.setLayoutParams(lp);
			((ViewGroup) view.itemView).addView(valSummary, 2);
		}
		getView(view.itemView);
	}

	@Override
	public void markAsNew() {
		newmod = true;
	}

	@Override
	public void applyHighlight() {
		highlight = true;
	}
}
