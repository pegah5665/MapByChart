package com.pegahahvapp.mapbychart;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener,GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;

    LinearLayout lineareChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lineareChart=findViewById(R.id.LineareChart);

        chart = (PieChartView) findViewById(R.id.chart);
        //ghabee click kardane chart
        chart.setOnValueTouchListener(new ValueTouchListener());
        //dadane data be chart:
        toggleLabelForSelected();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng kanda = new LatLng(35.719593, 51.386943);
        mMap.addMarker(new MarkerOptions().position(kanda).title("Marker in Kanda"));


        CameraPosition position = CameraPosition.builder()
                .target(kanda)
                .zoom(16)
                .tilt(googleMap.getCameraPosition().tilt)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        CameraPosition position = CameraPosition.builder()
                .target(marker.getPosition())
                .bearing(180)
                .zoom(16)
                .tilt(mMap.getCameraPosition().tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        lineareChart.setVisibility(View.VISIBLE);

    }


    private void generateData() {
        //te@dad bakhsh haye chart
        int numValues = 6;
//sakhtane yek list ba aghadire tasadofi
        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < numValues; ++i) {
            SliceValue sliceValue = new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor());
            values.add(sliceValue);
        }
//dadane maghadire list be bakhsh haye chart
        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);


        if (isExploded) {
            //agar isExploded true shavad bakhshha chart ra az ham fasele midahd har che adad(24) bozorgtar fasel ghata@at bishtar mishavad
            data.setSlicesSpacing(24);
        }

        if (hasCenterText1) {
            //agar hasCenterText1 ra true konim yek text vasate chart namayesh midahad
            data.setCenterText1("Kanda");

            // Get roboto-italic font.
            Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "Roboto-Italic.ttf");
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }
        //agar hasCenterText1 ra true konim 2 khat text vasate chart namayesh midahad
        if (hasCenterText2) {
            data.setCenterText2("Kanda");

            Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "Roboto-Italic.ttf");

            data.setCenterText2Typeface(tf);
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        }

        chart.setPieChartData(data);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        lineareChart.setVisibility(View.GONE);
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            Toast.makeText(getBaseContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

    private void toggleLabelForSelected() {

        //baraye inke ba click rooye chart, ghesmate click shode birintar buyad va meghdaresh roosh neveshte beshe:
        hasLabelForSelected = !hasLabelForSelected;

        chart.setValueSelectionEnabled(hasLabelForSelected);

        if (hasLabelForSelected) {
            hasLabels = false;
            hasLabelsOutside = false;

            if (hasLabelsOutside) {
                chart.setCircleFillRatio(0.7f);
            } else {
                chart.setCircleFillRatio(1.0f);
            }
        }

//sakhte chart va dadane maghadir be an:
        generateData();
    }


}
