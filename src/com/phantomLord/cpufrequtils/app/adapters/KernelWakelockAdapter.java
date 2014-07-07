package com.phantomLord.cpufrequtils.app.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asksven.android.common.kernelutils.NativeKernelWakelock;
import com.asksven.android.common.kernelutils.Wakelocks;
import com.asksven.android.common.privateapiproxies.StatElement;
import com.phantomLord.cpufrequtils.app.R;
import com.phantomLord.cpufrequtils.app.utils.MiscUtils;

public class KernelWakelockAdapter extends BaseAdapter {
	ArrayList<StatElement> kernelWakelocks;
	Context context;
	LayoutInflater inflator;

	public KernelWakelockAdapter(Context ctx) {
		this.context = ctx;
		kernelWakelocks = Wakelocks.parseProcWakelocks(ctx);
		kernelWakelocks = MiscUtils.removeZeroValues(kernelWakelocks);

		Collections.sort(kernelWakelocks, new Comparator<StatElement>() {
			@Override
			public int compare(StatElement arg0, StatElement arg1) {
				return (int) (arg1.getMaxValue() - arg0.getMaxValue());
			}
		});
		inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		View rowView = inflator.inflate(
				R.layout.alarm_trigger_custom_list_item1, arg2, false);
		NativeKernelWakelock nativeWakeLock = (NativeKernelWakelock) kernelWakelocks
				.get(position);
		TextView mKernelWakelock = (TextView) rowView
				.findViewById(R.id.alarm_package_name);
		TextView WakeupInfo = (TextView) rowView
				.findViewById(R.id.wakeup_count);

		String kernelWakelock = kernelWakelocks.get(position).getName();
		mKernelWakelock.setText(kernelWakelock.substring(1,
				kernelWakelock.length() - 1));

		WakeupInfo
				.setText(MiscUtils.secToString(nativeWakeLock.getDuration() / 1000));
		return rowView;
	}

	@Override
	public int getCount() {
		return kernelWakelocks.size();
	}

	@Override
	public Object getItem(int pos) {
		return kernelWakelocks.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return kernelWakelocks.indexOf(pos);
	}

}
