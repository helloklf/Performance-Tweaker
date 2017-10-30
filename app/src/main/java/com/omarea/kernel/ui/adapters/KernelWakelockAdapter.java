package com.omarea.kernel.ui.adapters;

import com.asksven.android.common.privateapiproxies.NativeKernelWakelock;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.omarea.kernel.R;
import com.omarea.kernel.utils.BatteryStatsUtils;
import com.omarea.kernel.utils.SysUtils;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class KernelWakelockAdapter extends BaseAdapter {

    private ArrayList<NativeKernelWakelock> kernelWakelocks;
    private Context context;
    private LayoutInflater inflater;
    private long totaltime = 0;

    public KernelWakelockAdapter(Context ctx) {
        this.context = ctx;
        kernelWakelocks = BatteryStatsUtils.getInstance(context).getNativeKernelWakelocks(true);
//        for (NativeKernelWakelock element : kernelWakelocks) {
//            totaltime += (int) element.getDuration() / 1000;
//        }

        totaltime = SystemClock.elapsedRealtime();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.kernel_wakelock_row, parent, false);
        TextView mKernelWakelock = rowView.findViewById(R.id.kernel_wakelock_name);
        TextView WakeupInfo = rowView.findViewById(R.id.wakelock_duration);
        TextView wakeUpCount = rowView.findViewById(R.id.kernel_wakelock_count);
        DonutProgress progress = rowView.findViewById(R.id.donut_progress);

        NativeKernelWakelock nativeWakeLock = kernelWakelocks.get(position);
        String kernelWakelock = kernelWakelocks.get(position).getName();
        mKernelWakelock.setText(kernelWakelock);
        WakeupInfo.setText(SysUtils.secToString(nativeWakeLock.getDuration() / 1000));
        progress.setMax(100);
        progress.setProgress(nativeWakeLock.getDuration() * 100 / totaltime);
        wakeUpCount.setText("x" + nativeWakeLock.getCount() + " " + context.getString(R.string.times));

        return rowView;
    }

    @Override
    public int getCount() {
        if (kernelWakelocks != null) {
            return kernelWakelocks.size();
        } else {
            return 0;
        }
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
