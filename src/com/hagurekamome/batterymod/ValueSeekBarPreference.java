package com.hagurekamome.batterymod;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class ValueSeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener{
	private int mMax;
	private int mMin;
	private int mProgress;
	private int mStep;
	private boolean mTrackingTouch;
	private TextView tvValue;
	private String valueDisplayFormat;

	public ValueSeekBarPreference(Context paramContext)
	{
		this(paramContext, null);
	}

	public ValueSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet)
	{
		this(paramContext, paramAttributeSet, 0x101008e);
	}

	public ValueSeekBarPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
	{
		super(paramContext, paramAttributeSet, paramInt);
		if (paramAttributeSet != null)
		{
			setStep(paramAttributeSet.getAttributeIntValue(null, "step", 1));
			setMin(paramAttributeSet.getAttributeIntValue(null, "min", 0));
			setMax(paramAttributeSet.getAttributeIntValue(null, "max", 100));
			this.valueDisplayFormat = paramAttributeSet.getAttributeValue(null, "displayFormat");
			if (this.valueDisplayFormat == null)
				this.valueDisplayFormat = "%d";
		}
	}

	private void setProgress(int paramInt, boolean paramBoolean)
	{
		if (paramInt > this.mMax)
			paramInt = this.mMax;
		if (paramInt < this.mMin)
			paramInt = this.mMin;
		if (paramInt != this.mProgress)
		{
			this.mProgress = paramInt;
			persistInt(paramInt);
			if (paramBoolean)
				notifyChanged();
		}
	}

	public int getProgress()
	{
		return this.mProgress;
	}

	protected void onBindView(View paramView)
	{
		super.onBindView(paramView);
		SeekBar localSeekBar = (SeekBar)paramView.findViewById(R.id.valueseekbar_preference_seekbar);
		localSeekBar.setOnSeekBarChangeListener(this);
		localSeekBar.setMax((this.mMax - this.mMin) / this.mStep);
		localSeekBar.setProgress((this.mProgress - this.mMin) / this.mStep);
		TextView localTextView = this.tvValue;
		String str = this.valueDisplayFormat;
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(this.mProgress);
		localTextView.setText(String.format(str, arrayOfObject));
		localSeekBar.setEnabled(isEnabled());
	}

	protected View onCreateView(ViewGroup paramViewGroup)
	{
		ViewGroup localViewGroup1 = (ViewGroup)super.onCreateView(paramViewGroup);
		ViewGroup localViewGroup2 = (ViewGroup)((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(R.layout.preference_valueseekbar_extension, null);
		localViewGroup2.addView(localViewGroup1, 0);
		this.tvValue = ((TextView)localViewGroup2.findViewById(R.id.valueseekbar_preference_value));
		return localViewGroup2;
	}

	protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt)
	{
		return Integer.valueOf(paramTypedArray.getInt(paramInt, 0));
	}

	public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
	{
		if ((paramBoolean) && (!this.mTrackingTouch))
			syncProgress(paramSeekBar);
		TextView localTextView = this.tvValue;
		String str = this.valueDisplayFormat;
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = Integer.valueOf(paramInt * this.mStep + this.mMin);
		localTextView.setText(String.format(str, arrayOfObject));
	}

	protected void onRestoreInstanceState(Parcelable paramParcelable)
	{
		if (!paramParcelable.getClass().equals(SavedState.class))
		{
			super.onRestoreInstanceState(paramParcelable);
			return;
		}
		SavedState localSavedState = (SavedState)paramParcelable;
		super.onRestoreInstanceState(localSavedState.getSuperState());
		this.mProgress = localSavedState.progress;
		this.mStep = localSavedState.step;
		this.mMin = localSavedState.min;
		this.mMax = localSavedState.max;
		notifyChanged();
	}

	protected Parcelable onSaveInstanceState()
	{
		Parcelable localParcelable = super.onSaveInstanceState();
		if (isPersistent())
			return localParcelable;
		SavedState localSavedState = new SavedState(localParcelable);
		localSavedState.progress = this.mProgress;
		localSavedState.step = this.mStep;
		localSavedState.min = this.mMin;
		localSavedState.max = this.mMax;
		return localSavedState;
	}

	protected void onSetInitialValue(boolean paramBoolean, Object paramObject)
	{
		int i;

		if (paramBoolean)
			i = getPersistedInt(this.mProgress);
		else
			i = ((Integer)paramObject).intValue();
		setProgress(i);
		return;
	}

	public void onStartTrackingTouch(SeekBar paramSeekBar)
	{
		this.mTrackingTouch = true;
	}

	public void onStopTrackingTouch(SeekBar paramSeekBar)
	{
		this.mTrackingTouch = false;
		if (paramSeekBar.getProgress() * this.mStep + this.mMin != this.mProgress)
			syncProgress(paramSeekBar);
	}

	public void setMax(int paramInt)
	{
		if (paramInt != this.mMax)
		{
			this.mMax = paramInt;
			notifyChanged();
		}
	}

	public void setMin(int paramInt)
	{
		if (paramInt != this.mMin)
		{
			this.mMin = paramInt;
			notifyChanged();
		}
	}

	public void setProgress(int paramInt)
	{
		setProgress(paramInt, true);
	}

	public void setStep(int paramInt)
	{
		if (paramInt != this.mStep)
		{
			this.mStep = paramInt;
			notifyChanged();
		}
	}

	void syncProgress(SeekBar paramSeekBar)
	{
		int i = paramSeekBar.getProgress() * this.mStep + this.mMin;
		if (i != this.mProgress)
		{
			if (callChangeListener(Integer.valueOf(i)))
				setProgress(i, false);
		}
		else
			return;
		paramSeekBar.setProgress((this.mProgress - this.mMin) / this.mStep);
	}

	private static class SavedState extends Preference.BaseSavedState
	{
		private static final class CreatorImplementation implements Parcelable.Creator {
			public ValueSeekBarPreference.SavedState createFromParcel(Parcel paramAnonymousParcel)
			{
				return new ValueSeekBarPreference.SavedState(paramAnonymousParcel);
			}

			public ValueSeekBarPreference.SavedState[] newArray(int paramAnonymousInt)
			{
				return new ValueSeekBarPreference.SavedState[paramAnonymousInt];
			}
		}

		@SuppressWarnings({ "unchecked", "unused" })
		public static final Parcelable.Creator<SavedState> CREATOR = new CreatorImplementation();
		int max;
		int min;
		int progress;
		int step;

		public SavedState(Parcel paramParcel)
		{
			super(paramParcel);
			this.progress = paramParcel.readInt();
			this.step = paramParcel.readInt();
			this.min = paramParcel.readInt();
			this.max = paramParcel.readInt();
		}

		public SavedState(Parcelable paramParcelable)
		{
			super(paramParcelable);
		}

		public void writeToParcel(Parcel paramParcel, int paramInt)
		{
			super.writeToParcel(paramParcel, paramInt);
			paramParcel.writeInt(this.progress);
			paramParcel.writeInt(this.step);
			paramParcel.writeInt(this.min);
			paramParcel.writeInt(this.max);
		}
	}
}
