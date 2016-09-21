package it.albertus.jface.preference.field;

import it.albertus.jface.JFaceMessages;
import it.albertus.jface.TextFormatter;
import it.albertus.jface.listener.LongVerifyListener;
import it.albertus.util.Configured;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class DefaultLongFieldEditor extends LongFieldEditor {

	protected static final int DEFAULT_TEXT_LIMIT = Long.toString(Long.MAX_VALUE).length() - 1;

	public DefaultLongFieldEditor(final String name, final String labelText, final Composite parent, final int textLimit) {
		super(name, labelText, parent, textLimit);
		init();
	}

	public DefaultLongFieldEditor(final String name, final String labelText, final Composite parent) {
		this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
	}

	@Override
	public void setValidRange(final long min, final long max) {
		super.setValidRange(min, max);
		setErrorMessage(JFaceMessages.get("err.preferences.integer.range", min, max));
	}

	@Override
	protected void doLoad() {
		final Text text = getTextControl();
		if (text != null && !text.isDisposed()) {
			setToolTipText(getPreferenceStore().getDefaultLong(getPreferenceName()));
			String value;
			try {
				value = Long.toString(Long.parseLong(getPreferenceStore().getString(getPreferenceName()).trim()));
			}
			catch (final Exception e) {
				value = "";
			}
			text.setText(value);
			oldValue = value;
			updateFontStyle();
		}
	}

	@Override
	protected void doStore() {
		if (!isEmptyStringAllowed()) {
			super.doStore();
		}
		else {
			final Text text = getTextControl();
			if (text != null) {
				getPreferenceStore().setValue(getPreferenceName(), text.getText());
			}
		}
	}

	@Override
	protected void doLoadDefault() {
		if (!isEmptyStringAllowed()) {
			super.doLoadDefault();
		}
		else {
			Text text = getTextControl();
			if (text != null) {
				text.setText(getPreferenceStore().getDefaultString(getPreferenceName()));
			}
			valueChanged();
		}
	}

	@Override
	protected boolean checkState() {
		if (!isEmptyStringAllowed()) {
			return super.checkState();
		}
		else {
			boolean state = super.checkState();
			if (!state) {
				final Text text = getTextControl();
				if (text != null && "".equals(text.getText())) {
					clearErrorMessage();
					state = true;
				}
			}
			return state;
		}

	}

	@Override
	protected void valueChanged() {
		super.valueChanged();
		updateFontStyle();
	}

	protected void init() {
		getTextControl().addVerifyListener(new LongVerifyListener(new Configured<Boolean>() {
			@Override
			public Boolean getValue() {
				return getMinValidValue() < 0;
			}	
		}));
		getTextControl().addFocusListener(new LongFocusListener());
		setErrorMessage(JFaceMessages.get("err.preferences.integer"));
	}

	protected void setToolTipText(final long defaultValue) {
		if (defaultValue != 0) {
			getTextControl().setToolTipText(JFaceMessages.get("lbl.preferences.default.value", defaultValue));
		}
	}

	protected void updateFontStyle() {
		final String defaultValue = getPreferenceStore().getDefaultString(getPreferenceName());
		TextFormatter.updateFontStyle(getTextControl(), defaultValue);
	}

	/** Removes trailing zeros when the field loses the focus */
	protected class LongFocusListener extends FocusAdapter {
		@Override
		public void focusLost(final FocusEvent fe) {
			final Text text = (Text) fe.widget;
			final String oldText = text.getText();
			try {
				final String newText = Long.toString(Long.parseLong(oldText));
				if (!oldText.equals(newText)) {
					text.setText(newText);
				}
				valueChanged();
			}
			catch (final Exception e) {}
		}
	}

}