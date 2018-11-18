package it.albertus.jface.maps.leaflet;

import java.util.Map.Entry;

import org.eclipse.swt.widgets.Shell;

import it.albertus.jface.maps.MapDialog;
import it.albertus.jface.maps.MapMarker;
import it.albertus.net.httpserver.html.HtmlUtils;
import it.albertus.util.NewLine;

public class LeafletMapDialog extends MapDialog {

	private final LeafletMapOptions options = new LeafletMapOptions();

	public LeafletMapDialog(final Shell parent) {
		super(parent);
	}

	public LeafletMapDialog(final Shell parent, final int style) {
		super(parent, style);
	}

	@Override
	protected String parseLine(final String line) {
		// Options
		if (line.contains(OPTIONS_PLACEHOLDER)) {
			final StringBuilder optionsBlock = new StringBuilder();
			optionsBlock.append(String.format("map.setView([%s, %s], %d);", getOptions().getCenterLat(), getOptions().getCenterLng(), getOptions().getZoom()));
			if (!options.getControls().containsKey(LeafletMapControl.LAYERS)) {
				optionsBlock.append(NewLine.SYSTEM_LINE_SEPARATOR);
				optionsBlock.append("L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19, attribution: '&copy; <a href=\"http://www.openstreetmap.org/copyright\">OpenStreetMap</a>' }).addTo(map);");
			}
			for (final Entry<LeafletMapControl, String> control : options.getControls().entrySet()) {
				optionsBlock.append(NewLine.SYSTEM_LINE_SEPARATOR);
				optionsBlock.append(String.format("map.addControl(L.control.%s(%s));", control.getKey().getConstructor(), control.getValue() == null ? "" : control.getValue().trim()));
			}
			return optionsBlock.toString().trim();
		}
		// Markers
		else if (line.contains(MARKERS_PLACEHOLDER)) {
			if (getMarkers().isEmpty()) {
				return null;
			}
			else {
				final StringBuilder markersBlock = new StringBuilder();
				for (final MapMarker marker : getMarkers()) {
					markersBlock.append(String.format("L.marker([%s, %s]).addTo(map).bindPopup('%s');", marker.getLatitude(), marker.getLongitude(), marker.getTitle() == null ? "" : HtmlUtils.escapeEcmaScript(marker.getTitle().replace(NewLine.SYSTEM_LINE_SEPARATOR, "<br />").trim())));
					markersBlock.append(NewLine.SYSTEM_LINE_SEPARATOR);
				}
				return markersBlock.toString().trim();
			}
		}
		else {
			return line;
		}
	}

	@Override
	public LeafletMapOptions getOptions() {
		return options;
	}

}
