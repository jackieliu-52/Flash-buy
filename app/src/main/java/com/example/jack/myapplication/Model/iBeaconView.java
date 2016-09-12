package com.example.jack.myapplication.Model;

import android.graphics.PointF;

public class iBeaconView {
	public String mac = "";
	public int rssi = -1;
	public String detailInfo = "";
	public boolean isMultiIDs = false;
	public int uuid = -1;
	public PointF location; //beacon的位置


	public void reset(iBeaconView beacon) {
		this.mac = beacon.mac;
		this.rssi = beacon.rssi;
		this.detailInfo = beacon.detailInfo;
		this.isMultiIDs = beacon.isMultiIDs;
		this.location = beacon.location;
	}
}
