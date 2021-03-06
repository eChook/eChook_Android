package com.ben.drivenbluetooth.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ben.drivenbluetooth.Global;
import com.ben.drivenbluetooth.R;
import com.ben.drivenbluetooth.events.ArduinoEvent;
import com.ben.drivenbluetooth.events.SnackbarEvent;
import com.ben.drivenbluetooth.util.ColorHelper;
import com.ben.drivenbluetooth.util.CustomLabelFormatter;
import com.ben.drivenbluetooth.util.UnitHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class FourGraphsBars extends Fragment {

	private TextView Amps;
	private TextView Volts;
	private TextView RPM;
	private TextView Speed;
	private TextView WattHours;
	private TextView AmpHours;

	private BarChart AmpsBarChart;
	private BarChart VoltsBarChart;
	private BarChart SpeedBarChart;
	private BarChart RPMBarChart;

	private LineChart VoltsLineChart;
	private LineChart AmpsLineChart;
	private LineChart RPMLineChart;
	private LineChart SpeedLineChart;


	/*===================*/
	/* FOURGRAPHSBARS
	/*===================*/
	public FourGraphsBars() {
		// Required empty public constructor
	}

	/*===================*/
	/* INITIALIZERS
	/*===================*/
	private void InitializeDataFields() {
		View v = getView();
		Amps 		= v.findViewById(R.id.current);
		Volts 		= v.findViewById(R.id.voltage);
		RPM 		= v.findViewById(R.id.rpm);
		Speed 		= v.findViewById(R.id.speed);
		AmpHours	= v.findViewById(R.id.ampHours);
		WattHours 	= v.findViewById(R.id.wattHours);
	}

	private void InitializeGraphs() {
		View v = getView();
		VoltsBarChart   = v.findViewById(R.id.VoltsBarChart);
		AmpsBarChart    = v.findViewById(R.id.AmpsBarChart);
		RPMBarChart     = v.findViewById(R.id.RPMBarChart);
		SpeedBarChart   = v.findViewById(R.id.SpeedBarChart);

		VoltsLineChart  = v.findViewById(R.id.VoltsLineChart);
		AmpsLineChart   = v.findViewById(R.id.AmpsLineChart);
		RPMLineChart    = v.findViewById(R.id.RPMGraph);
		SpeedLineChart  = v.findViewById(R.id.SpeedGraph);

		BarChart barCharts[] = new BarChart[] {
						VoltsBarChart,
						AmpsBarChart,
						RPMBarChart,
						SpeedBarChart
		};

		LineChart lineCharts[] = new LineChart[] {
						VoltsLineChart,
						AmpsLineChart,
						RPMLineChart,
						SpeedLineChart
		};

		LineData lineDatas[] = new LineData[] {
						Global.VoltsHistory,
						Global.AmpsHistory,
						Global.MotorRPMHistory,
						Global.SpeedHistory
		};

		IAxisValueFormatter labelFormats[] = new IAxisValueFormatter[] {
						new CustomLabelFormatter("", "0", "V"),
						new CustomLabelFormatter("", "0", "A"),
						new LargeValueFormatter(),
						new CustomLabelFormatter("", "0", "")
		};

		float minMax[][] = new float[][] {
						new float[] {0, 30},	// volts
						new float[] {0, 50},	// amps
						new float[] {0, 2100},	// motor rpm
						new float[] {0, UnitHelper.getMaxSpeed(Global.Unit)}	// speed
		};

		for (int i = 0; i < lineCharts.length; i++) {
			LineChart chart = lineCharts[i];
			if (Global.EnableGraphs) {
				chart.setData(lineDatas[i]);
				chart.setVisibleXRangeMaximum(Global.MAX_GRAPH_DATA_POINTS);
				chart.setNoDataText("");

				YAxis leftAxis = chart.getAxisLeft();
				leftAxis.setAxisMinimum(minMax[i][0]);
				leftAxis.setAxisMaximum(minMax[i][1]);
				leftAxis.setLabelCount(3, true);
				leftAxis.setValueFormatter(labelFormats[i]);

				chart.setDescription(new Description());
				chart.setDrawGridBackground(false);

				YAxis rightAxis = chart.getAxisRight();
				rightAxis.setEnabled(false);

				Legend l = chart.getLegend();
				l.setEnabled(false);

				XAxis bottomAxis = chart.getXAxis();
				bottomAxis.setEnabled(false);

				// Remove padding
				chart.setViewPortOffsets(0f, 0f, 0f, 0f);
			} else {
				chart.setNoDataText("");
			}
			chart.invalidate();
		}

		String legend[] = new String[] {
						"Volts",
						"Amps",
						"RPM",
						Global.Unit == Global.UNIT.KPH ? "kph" : "mph"
		};

		for (int i = 0; i < barCharts.length; i++) {
			BarChart chart = barCharts[i];

			if (Global.EnableGraphs) {
				// Disable right-hand y-axis
				chart.getAxisRight().setEnabled(false);

				// Disable legend
				chart.getLegend().setEnabled(false);
				chart.setDescription(new Description());

				chart.setDrawGridBackground(false);

				// Set y-axis limits
				YAxis yAxis = chart.getAxisLeft();
				yAxis.setAxisMinimum(minMax[i][0]);
				yAxis.setAxisMaximum(minMax[i][1]);

				// Create data
				BarEntry entry = new BarEntry(0, 0);
				BarDataSet dataSet = new BarDataSet(new ArrayList<BarEntry>(), legend[i]);
				BarData data = new BarData();
				data.setDrawValues(false);

				// Attach data
				dataSet.addEntry(entry);
				data.addDataSet(dataSet);
				chart.setData(data);

				chart.setHardwareAccelerationEnabled(true);
			} else {
				chart.setNoDataText("");
			}

			// Refresh chart
			chart.invalidate();
		}
	}

	/*===================*/
	/* LIFECYCLE
	/*===================*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
													 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_four_graphs_bars, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		InitializeDataFields();
		InitializeGraphs();
		UpdateFragmentUI();

		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Amps                = null;
		Volts               = null;
		RPM					= null;
		Speed				= null;

		AmpsBarChart		= null;
		VoltsBarChart		= null;
		SpeedBarChart	    = null;
		RPMBarChart 	    = null;

		VoltsLineChart      = null;
		AmpsLineChart       = null;
		SpeedLineChart      = null;
		RPMLineChart        = null;

		AmpHours			= null;

		EventBus.getDefault().unregister(this);
	}

	/*===================*/
	/* FRAGMENT UPDATE
	/*===================*/
	private void UpdateFragmentUI() {
		UpdateVolts();
		UpdateAmps();
		UpdateAmpHours();
		UpdateSpeed();
		UpdateMotorRPM();
		UpdateWattHours();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onArduinoEvent(ArduinoEvent event) {
        try {
            switch (event.eventType) {

                case Volts:
                    UpdateVolts();
                    break;
                case Amps:
                    UpdateAmps();
                    break;
                case AmpHours:
                    UpdateAmpHours();
                    break;
                case WheelSpeedMPS:
                    UpdateSpeed();
                    break;
                case MotorSpeedRPM:
                    UpdateMotorRPM();
                    break;
            }
        } catch (Exception e) {
            EventBus.getDefault().post(new SnackbarEvent(e));
e.printStackTrace();
        }
    }

	private void UpdateVolts() {
		try {
			Volts.setText(String.format("%.2f", Global.Volts));
			Volts.setTextColor(ColorHelper.GetVoltsColor(Global.Volts));
			if (Global.EnableGraphs) {
				UpdateBarChart(VoltsBarChart, Global.Volts, ColorHelper.GetVoltsColor(Global.Volts));
				UpdateLineChart(VoltsLineChart);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void UpdateAmps() {
		try {
			Amps.setText(String.format("%.1f", Global.Amps));
			Amps.setTextColor(ColorHelper.GetAmpsColor(Global.Amps));
			if (Global.EnableGraphs) {
				UpdateBarChart(AmpsBarChart, Global.Amps, ColorHelper.GetAmpsColor(Global.Amps));
				UpdateLineChart(AmpsLineChart);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void UpdateAmpHours() {
		try {
			AmpHours.setText(String.format("%.2f", Global.AmpHours) + " Ah");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void UpdateSpeed() {
		try {
			Double speed = UnitHelper.getSpeed(Global.SpeedMPS, Global.Unit);
			Speed.setText(UnitHelper.getSpeedText(Global.SpeedMPS, Global.Unit));

			if (Global.EnableGraphs) {
				UpdateBarChart(SpeedBarChart, speed, Color.BLACK);
				UpdateLineChart(SpeedLineChart);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void UpdateMotorRPM() {
		try {
			RPM.setText(String.format("%.0f", Global.MotorRPM));
			RPM.setTextColor(ColorHelper.GetRPMColor(Global.MotorRPM));
			if (Global.EnableGraphs) {
				UpdateBarChart(RPMBarChart, Global.MotorRPM, ColorHelper.GetRPMColor(Global.MotorRPM));
				UpdateLineChart(RPMLineChart);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void UpdateWattHours() {
		try {
			WattHours.setText(String.format("%.2f Wh/km", Global.WattHoursPerMeter * 1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void UpdateBarChart(BarChart chart, Double value, int color) {
		chart.getBarData().getDataSetByIndex(0).getEntryForIndex(0).setY(value.floatValue());
		DataSet set = (DataSet) chart.getData().getDataSetByIndex(0);
		set.setColor(color);
		chart.invalidate();
	}

	private void UpdateLineChart(LineChart graph) {
		graph.notifyDataSetChanged();
		graph.setVisibleXRangeMaximum(Global.MAX_GRAPH_DATA_POINTS);
		graph.moveViewToX(graph.getData().getEntryCount() - Global.MAX_GRAPH_DATA_POINTS - 1);
	}
}
